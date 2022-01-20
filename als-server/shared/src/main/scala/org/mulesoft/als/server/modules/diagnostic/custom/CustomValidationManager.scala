package org.mulesoft.als.server.modules.diagnostic.custom

import amf.aml.client.scala.model.document.DialectInstance
import amf.core.client.common.validation.{ProfileName, ProfileNames}
import amf.core.client.scala.config.RenderOptions
import amf.core.client.scala.model.document.BaseUnit
import amf.validation.client.scala.BaseProfileValidatorBuilder
import org.mulesoft.als.logger.Logger
import org.mulesoft.als.server.client.platform.ClientNotifier
import org.mulesoft.als.server.feature.diagnostic.{
  CustomValidationClientCapabilities,
  CustomValidationConfigType,
  CustomValidationOptions
}
import org.mulesoft.als.server.modules.ast.ResolvedUnitListener
import org.mulesoft.als.server.modules.common.reconciler.Runnable
import org.mulesoft.als.server.modules.diagnostic._
import org.mulesoft.amfintegration.AmfImplicits.BaseUnitImp
import org.mulesoft.amfintegration.amfconfiguration.AMLSpecificConfiguration
import org.mulesoft.amfintegration.{AmfResolvedUnit, DiagnosticsBundle}
import org.mulesoft.lsp.ConfigType
import org.mulesoft.lsp.feature.telemetry.{MessageTypes, TelemetryProvider}
import org.yaml.builder.JsonOutputBuilder

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success}
class CustomValidationManager(override protected val telemetryProvider: TelemetryProvider,
                              override protected val clientNotifier: ClientNotifier,
                              override protected val logger: Logger,
                              override protected val validationGatherer: ValidationGatherer,
                              val validatorBuilder: BaseProfileValidatorBuilder)
    extends BasicDiagnosticManager[CustomValidationClientCapabilities, CustomValidationOptions]
    with ResolvedUnitListener {

  private var enabled = false

  override protected val managerName: DiagnosticManagerKind = CustomDiagnosticKind
  override type RunType = CustomValidationRunnable

  override val `type`: ConfigType[CustomValidationClientCapabilities, CustomValidationOptions] =
    CustomValidationConfigType

  override def applyConfig(config: Option[CustomValidationClientCapabilities]): CustomValidationOptions = {
    enabled = config.exists(_.enabled)
    logger.debug(s"Custom validation manager enabled? $enabled", "CustomValidationManager", "applyConfig")
    CustomValidationOptions(enabled)
  }

  private def tree(baseUnit: BaseUnit): Set[String] =
    baseUnit.flatRefs
      .map(bu => bu.identifier)
      .toSet + baseUnit.identifier

  private def gatherValidationErrors(uri: String,
                                     resolved: AmfResolvedUnit,
                                     references: Map[String, DiagnosticsBundle],
                                     uuid: String): Future[Unit] = {
    val startTime = System.currentTimeMillis()
    if (resolved.alsConfigurationState.profiles.nonEmpty) {
      for {
        unit    <- resolved.resolvedUnit.map(_.baseUnit)
        results <- validate(uri, unit, resolved.alsConfigurationState.profiles.map(_.model), resolved.configuration)
      } yield {
        validationGatherer
          .indexNewReport(ErrorsWithTree(uri, results.flatten, Some(tree(resolved.baseUnit))), managerName, uuid)
        notifyReport(uri, resolved.baseUnit, references, managerName, ProfileName("CustomValidation"))
        val endTime = System.currentTimeMillis()
        this.logger.debug(s"It took ${endTime - startTime} milliseconds to validate with Go env",
                          "CustomValidationDiagnosticManager",
                          "gatherValidationErrors")
      }
    } else {
      Future.successful {
        validationGatherer.removeFile(uri, managerName)
        notifyReport(uri, resolved.baseUnit, references, managerName, ProfileName("CustomValidation"))
      }
    }
  }

  def validate(uri: String,
               unit: BaseUnit,
               profiles: Seq[DialectInstance],
               config: AMLSpecificConfiguration): Future[Seq[Seq[AlsValidationResult]]] =
    for {
      serialized <- Future {
        val builder = JsonOutputBuilder(false)
        config.asJsonLD(unit, builder, RenderOptions().withCompactUris.withSourceMaps)
        builder.result.toString
      }
      result <- {
        val eventualResults: Seq[Future[Seq[AlsValidationResult]]] = profiles
          .map(profile => {
            logger.debug(s"Validate with profile: ${profile.identifier}",
                         "CustomValidationManager",
                         "validateWithProfile")
            validateWithProfile(profile, uri, serialized)
          })
        Future
          .sequence(eventualResults)
          .map(_.toSeq)
      }
    } yield result

  private def validateWithProfile(profileUnit: DialectInstance,
                                  unitUri: String,
                                  serializedUnit: String): Future[Seq[AlsValidationResult]] = {

    // TODO: compute validator execution could be done just once for each project configuration refreshment
    validatorBuilder
      .validator(profileUnit)
      .validate(serializedUnit, unitUri)
      .map(_.results.map(rr => new AlsValidationResult(rr, Nil)))
  }

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

  override def onNewAst(ast: AmfResolvedUnit, uuid: String): Future[Unit] =
    if (enabled) super.onNewAst(ast, uuid) else Future.successful()
}
