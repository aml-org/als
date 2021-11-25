package org.mulesoft.als.server.modules.diagnostic

import amf.aml.client.scala.model.document.Dialect
import amf.core.client.scala.AMFResult
import amf.core.client.scala.model.document.BaseUnit
import amf.core.client.scala.validation.AMFValidationReport
import org.mulesoft.als.common.URIImplicits._
import org.mulesoft.als.logger.Logger
import org.mulesoft.als.server.RequestModule
import org.mulesoft.als.server.feature.diagnostic._
import org.mulesoft.als.server.modules.configuration.WorkspaceConfigurationProvider
import org.mulesoft.als.server.modules.diagnostic.custom.CustomValidationManager
import org.mulesoft.als.server.workspace.extract.WorkspaceConfig
import org.mulesoft.amfintegration.AmfImplicits.BaseUnitImp
import org.mulesoft.amfintegration.amfconfiguration.{AmfConfigurationWrapper, AmfParseResult}
import org.mulesoft.lsp.ConfigType
import org.mulesoft.lsp.feature.TelemeteredRequestHandler
import org.mulesoft.lsp.feature.diagnostic.PublishDiagnosticsParams
import org.mulesoft.lsp.feature.telemetry.MessageTypes.MessageTypes
import org.mulesoft.lsp.feature.telemetry.{MessageTypes, TelemetryProvider}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CleanDiagnosticTreeManager(telemetryProvider: TelemetryProvider,
                                 baseAmfConfig: AmfConfigurationWrapper,
                                 logger: Logger,
                                 customValidationManager: Option[CustomValidationManager],
                                 workspaceConfigProvider: WorkspaceConfigurationProvider)
    extends RequestModule[CleanDiagnosticTreeClientCapabilities, CleanDiagnosticTreeOptions] {

  private var enabled: Boolean = true

  override def getRequestHandlers: Seq[TelemeteredRequestHandler[_, _]] = Seq(
    new TelemeteredRequestHandler[CleanDiagnosticTreeParams, Seq[PublishDiagnosticsParams]] {
      override def `type`: CleanDiagnosticTreeRequestType.type = CleanDiagnosticTreeRequestType

      override def task(params: CleanDiagnosticTreeParams): Future[Seq[AlsPublishDiagnosticsParams]] =
        validate(params.textDocument.uri)

      override protected def telemetry: TelemetryProvider = telemetryProvider

      override protected def code(params: CleanDiagnosticTreeParams): String = "CleanDiagnosticTreeManager"

      override protected def beginType(params: CleanDiagnosticTreeParams): MessageTypes =
        MessageTypes.BEGIN_CLEAN_VALIDATION

      override protected def endType(params: CleanDiagnosticTreeParams): MessageTypes =
        MessageTypes.END_CLEAN_VALIDATION

      override protected def msg(params: CleanDiagnosticTreeParams): String =
        s"Clean validation request for: ${params.textDocument.uri}"

      override protected def uri(params: CleanDiagnosticTreeParams): String = params.textDocument.uri

      /**
        * If Some(_), this will be sent as a response as a default for a managed exception
        */
      override protected val empty: Option[Seq[PublishDiagnosticsParams]] = None
    }
  )

  override val `type`: ConfigType[CleanDiagnosticTreeClientCapabilities, CleanDiagnosticTreeOptions] =
    CleanDiagnosticTreeConfigType

  override def applyConfig(config: Option[CleanDiagnosticTreeClientCapabilities]): CleanDiagnosticTreeOptions = {
    config.foreach(c => enabled = c.enableCleanDiagnostic)
    CleanDiagnosticTreeOptions(true)
  }

  override def initialize(): Future[Unit] = Future.successful()

  def validate(uri: String): Future[Seq[AlsPublishDiagnosticsParams]] = {
    val helper = getCleanAmfWrapper
    for {
      maybeConfig   <- getWorkspaceConfig(uri)
      _             <- maybeConfig.map(c => registerSemantics(helper, c)).getOrElse(Future.unit)
      pr            <- doParse(uri, helper)
      t             <- partialResultForResolved(uri, helper, pr)
      partialResult <- partialResultForCustom(uri, helper, t)
    } yield {
      val profile = partialResult.resolutionResult.profile
      val list    = partialResult.parseResult.tree
      val ge: Map[String, Seq[AlsValidationResult]] =
        partialResult.parseResult.groupedErrors.map(t => (t._1, t._2.map(new AlsValidationResult(_))))
      val report = partialResult.resolutionResult
      val grouped: Map[String, Seq[AlsValidationResult]] =
        (report.results ++ partialResult.resolvedUnit.results ++ partialResult.customValidationResult.map(_.result))
          .groupBy(r => r.location.getOrElse(uri))
          .map(t => (t._1, t._2.map(new AlsValidationResult(_))))

      val merged = list.map(uri => uri -> (ge.getOrElse(uri, Nil) ++ grouped.getOrElse(uri, Nil))).toMap
      logger.debug(s"Report conforms: ${report.conforms}", "CleanDiagnosticTreeManager", "validate")

      DiagnosticConverters.buildIssueResults(merged, Map.empty, profile).map(_.publishDiagnosticsParams)
    }
  }

  protected def getWorkspaceConfig(uri: String): Future[Option[WorkspaceConfig]] =
    workspaceConfigProvider.getWorkspaceConfiguration(uri).map(_._2)

  protected def getCleanAmfWrapper: AmfConfigurationWrapper =
    baseAmfConfig.branch

  private def partialResultForCustom(uri: String, helper: AmfConfigurationWrapper, t: CleanValidationPartialResult) =
    runCustomValidations(uri, t.resolvedUnit.baseUnit, helper)
      .map(r => CleanValidationPartialResult(t.parseResult, t.resolutionResult, t.resolvedUnit, r))

  private def partialResultForResolved(uri: String, helper: AmfConfigurationWrapper, pr: AmfParseResult) = {
    logger.debug(s"About to report: $uri", "CleanDiagnosticTreeManager", "validate")
    val resolved = helper.fullResolution(pr.result.baseUnit)
    helper.report(resolved.baseUnit).map(r => CleanValidationPartialResult(pr, r, resolved))
  }

  private def doParse(uri: String, helper: AmfConfigurationWrapper): Future[AmfParseResult] = {
    val refinedUri = uri.toAmfDecodedUri(baseAmfConfig.platform)
    val eventualResult = helper
      .parse(refinedUri)
    eventualResult
  }

  private def registerSemantics(helper: AmfConfigurationWrapper, c: WorkspaceConfig): Future[Unit] =
    Future
      .sequence(c.semanticExtensions.map(doParse(_, helper)) ++ c.dialects.map(doParse(_, helper)))
      .map(_.map(_.result.baseUnit).map {
        case d: Dialect => helper.registerDialect(d)
        case b =>
          logger.warning(s"tried to register invalid dialect: ${b.identifier}",
                         "CleanDiagnosticTreeManager",
                         "registerSemantics")
      })

  private def runCustomValidations(uri: String,
                                   resolvedUnit: BaseUnit,
                                   helper: AmfConfigurationWrapper): Future[Seq[AlsValidationResult]] =
    for {
      config <- getWorkspaceConfig(uri)
      profiles <- config match {
        case Some(c) => Future.sequence(c.profiles.map(helper.parse).toSeq)
        case _       => Future(Seq.empty)
      }
      result <- customValidationManager match {
        case Some(cvm) if cvm.isActive && config.isDefined =>
          cvm.validate(uri, resolvedUnit, helper.branch.withProfiles(profiles)).map(_.flatten)
        case _ => Future(Seq.empty)
      }
    } yield result

  case class CleanValidationPartialResult(parseResult: AmfParseResult,
                                          resolutionResult: AMFValidationReport,
                                          resolvedUnit: AMFResult,
                                          customValidationResult: Seq[AlsValidationResult] = Seq())
}
