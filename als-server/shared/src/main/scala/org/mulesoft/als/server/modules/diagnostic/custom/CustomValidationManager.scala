package org.mulesoft.als.server.modules.diagnostic.custom

import amf.core.client.common.validation.{ProfileName, ProfileNames}
import amf.core.client.scala.config.RenderOptions
import amf.core.client.scala.model.document.BaseUnit
import amf.core.client.scala.validation.AMFValidationReport
import amf.core.internal.remote.Spec
import org.mulesoft.als.server.client.ClientNotifier
import org.mulesoft.als.logger.Logger
import org.mulesoft.als.server.modules.ast.ResolvedUnitListener
import org.mulesoft.als.server.modules.common.reconciler.Runnable
import org.mulesoft.als.server.modules.diagnostic._
import org.mulesoft.amfintegration.AmfImplicits.BaseUnitImp
import org.mulesoft.amfintegration.amfconfiguration.AmfConfigurationWrapper
import org.mulesoft.amfintegration.{AmfResolvedUnit, DiagnosticsBundle}
import org.mulesoft.lsp.feature.telemetry.{MessageTypes, TelemetryProvider}
import org.yaml.builder.JsonOutputBuilder

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success}
class CustomValidationManager(override protected val telemetryProvider: TelemetryProvider,
                              override protected val clientNotifier: ClientNotifier,
                              override protected val logger: Logger,
                              override protected val validationGatherer: ValidationGatherer,
                              val platformValidator: AMFOpaValidator,
                              val amfC: AmfConfigurationWrapper)
    extends DiagnosticManager
    with ResolvedUnitListener {
  override protected val managerName: DiagnosticManagerKind = CustomDiagnosticKind
  override type RunType = CustomValidationRunnable

  private def tree(baseUnit: BaseUnit): Set[String] =
    baseUnit.flatRefs
      .map(bu => bu.identifier)
      .toSet + baseUnit.identifier

  private def gatherValidationErrors(uri: String,
                                     resolved: AmfResolvedUnit,
                                     references: Map[String, DiagnosticsBundle],
                                     uuid: String): Future[Unit] = {
    val startTime = System.currentTimeMillis()
    logger.debug(s"Starting custom validation for $uri", "CustomValidationManager", "3")
    resolved.amfConfiguration.workspaceConfiguration match {
      case Some(config) if config.profiles.nonEmpty =>
        for {
          unit <- resolved.resolvedUnit
          serialized <- Future {
            val builder = JsonOutputBuilder(false)
            resolved.amfConfiguration.asJsonLD(unit, builder, RenderOptions().withCompactUris.withSourceMaps)
            builder.result.toString
          }
          reports <- Future.sequence(
            config.profiles.map(profile => validateWithProfile(profile, serialized, resolved.amfConfiguration)))
        } yield {
          val results = reports.flatMap { r =>
            r.results
          }
          validationGatherer
            .indexNewReport(ErrorsWithTree(uri, results.toSeq, Some(tree(resolved.baseUnit))), managerName, uuid)
          notifyReport(uri, resolved.baseUnit, references, managerName, ProfileName("CustomValidation"))
          val endTime = System.currentTimeMillis()
          this.logger.debug(s"It took ${endTime - startTime} milliseconds to validate with Go env",
                            "CustomValidationDiagnosticManager",
                            "gatherValidationErrors")
        }
      case _ =>
        Future.successful {
          validationGatherer.removeFile(uri, managerName)
          notifyReport(uri, resolved.baseUnit, references, managerName, ProfileName("CustomValidation"))
        }
    }
  }

  private def validateWithProfile(profileUri: String,
                                  serializedUnit: String,
                                  amfConfiguration: AmfConfigurationWrapper): Future[AMFValidationReport] =
    for {
      _ <- Future(
        logger.debug(s"Validate with profile: $profileUri", "CustomValidationManager", "validateWithProfile"))
      content   <- amfConfiguration.fetchContent(profileUri).map(_.toString())
      rawResult <- platformValidator.validateWithProfile(content, serializedUnit)
      report    <- new OPAValidatorReportLoader().load(rawResult)
    } yield report

  class CustomValidationRunnable(var uri: String, ast: AmfResolvedUnit, uuid: String) extends Runnable[Unit] {
    private var canceled = false

    private val kind = "CustomValidationRunnable"

    def run(): Promise[Unit] = {
      val promise = Promise[Unit]()

      def innerRunGather() =
        gatherValidationErrors(ast.baseUnit.identifier, ast, ast.diagnosticsBundle, uuid) andThen {
          case Success(report) => promise.success(report)
          case Failure(error)  => promise.failure(error)
        }

      telemetryProvider.timeProcess(
        "End report",
        MessageTypes.BEGIN_CUSTOM_DIAGNOSTIC,
        MessageTypes.END_CUSTOM_DIAGNOSTIC,
        s"CustomValidationRunnable : gatherValidationErrors for ${ast.baseUnit.identifier}",
        uri,
        innerRunGather,
        uuid
      )

      promise
    }

    def conflicts(other: Runnable[Any]): Boolean =
      other.asInstanceOf[CustomValidationRunnable].kind == kind && uri == other
        .asInstanceOf[CustomValidationRunnable]
        .uri

    def cancel() {
      canceled = true
    }

    def isCanceled(): Boolean = canceled
  }

  override protected val amfConfiguration: AmfConfigurationWrapper = amfC

  override protected def runnable(ast: AmfResolvedUnit, uuid: String): CustomValidationRunnable =
    new CustomValidationRunnable(ast.baseUnit.identifier, ast, uuid)

  protected override def onFailure(uuid: String, uri: String, exception: Throwable): Unit = {
    logger.error(s"Error on validation: ${exception.toString}", "CustomValidationDiagnosticManager", "newASTAvailable")
    exception.printStackTrace()
    clientNotifier.notifyDiagnostic(ValidationReport(uri, Set.empty, ProfileNames.AMF).publishDiagnosticsParams)
  }

  protected override def onSuccess(uuid: String, uri: String): Unit =
    logger.debug(s"End report: $uuid", "CustomValidationRunnable", "newASTAvailable")

  /**
    * Meant just for logging
    *
    * @param resolved
    * @param uuid
    */
  override protected def onNewAstPreprocess(resolved: AmfResolvedUnit, uuid: String): Unit =
    logger.debug("Running custom validations on:\n" + resolved.baseUnit.id,
                 "CustomValidationDiagnosticManager",
                 "newASTAvailable")

  override def onRemoveFile(uri: String): Unit = {
    validationGatherer.removeFile(uri, managerName)
    clientNotifier.notifyDiagnostic(AlsPublishDiagnosticsParams(uri, Nil, ProfileNames.AMF))
  }
}
