package org.mulesoft.als.server.modules.diagnostic

import amf.ProfileName
import amf.core.model.document.BaseUnit
import amf.core.remote.{Aml, Platform}
import amf.core.services.RuntimeValidator
import amf.core.validation.{AMFValidationReport, AMFValidationResult}
import org.mulesoft.als.common.dtoTypes.{EmptyPositionRange, PositionRange}
import org.mulesoft.als.server.ClientNotifierModule
import org.mulesoft.als.server.client.ClientNotifier
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.modules.ast.{AstListener, AstManager}
import org.mulesoft.als.server.modules.common.reconciler.Reconciler
import org.mulesoft.als.server.textsync.TextDocumentManager
import org.mulesoft.lsp.ConfigType
import org.mulesoft.lsp.feature.diagnostic.{DiagnosticClientCapabilities, DiagnosticConfigType}
import org.mulesoft.lsp.feature.telemetry.{MessageTypes, TelemetryProvider}

import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

class DiagnosticManager(private val textDocumentManager: TextDocumentManager,
                        private val astManager: AstManager,
                        private val telemetryProvider: TelemetryProvider,
                        private val clientNotifier: ClientNotifier,
                        private val platform: Platform,
                        private val logger: Logger)
    extends ClientNotifierModule[DiagnosticClientCapabilities, Unit] {

  override val `type`: ConfigType[DiagnosticClientCapabilities, Unit] = DiagnosticConfigType

  override def applyConfig(config: Option[DiagnosticClientCapabilities]): Unit = {}

  private val reconciler: Reconciler = new Reconciler(logger, 1000)

  val onNewASTAvailableListener: AstListener = (uri: String, version: Int, ast: BaseUnit, uuid: String) => {
    newASTAvailable(uri, version, ast, uuid)
  }

  override def initialize(): Future[Unit] = {
    astManager.onNewASTAvailable(onNewASTAvailableListener)
    Future.successful()
  }

  def newASTAvailable(uri: String, version: Int, ast: BaseUnit, telemetryUUID: String) {
    logger.debug("Got new AST:\n" + ast.toString, "ValidationManager", "newASTAvailable")

    telemetryProvider.addTimedMessage("Start report", MessageTypes.BEGIN_DIAGNOSTIC, uri, telemetryUUID)

    reconciler
      .shedule(new ValidationRunnable(uri, () => gatherValidationErrors(uri, version, ast, telemetryUUID)))
      .future andThen {
      case Success(reports: Seq[ValidationReport]) =>
        logger.debug("Number of errors is:\n" + reports.flatMap(_.issues).length,
                     "ValidationManager",
                     "newASTAvailable")
        reports.foreach { r =>
          telemetryProvider.addTimedMessage(s"Got reports: ${r.publishDiagnosticsParams.uri}",
                                            MessageTypes.GOT_DIAGNOSTICS,
                                            uri,
                                            telemetryUUID)
          clientNotifier.notifyDiagnostic(r.publishDiagnosticsParams)
        }
        telemetryProvider.addTimedMessage("End report", MessageTypes.END_DIAGNOSTIC, uri, telemetryUUID)

      case Failure(exception) =>
        telemetryProvider.addTimedMessage(s"End report: ${exception.getMessage}",
                                          MessageTypes.END_DIAGNOSTIC,
                                          uri,
                                          telemetryUUID)
        logger.warning("Error on validation: " + exception.toString, "ValidationManager", "newASTAvailable")
        clientNotifier.notifyDiagnostic(ValidationReport(uri, 0, Set.empty).publishDiagnosticsParams)
    }
  }

  private def gatherValidationErrors(uri: String,
                                     docVersion: Int,
                                     baseUnit: BaseUnit,
                                     uuid: String): Future[Seq[ValidationReport]] = {
    val editorOption = textDocumentManager.getTextDocument(uri)

    if (editorOption.isDefined) {
      val startTime = System.currentTimeMillis()

      this
        .report(uri, telemetryProvider, baseUnit, uuid)
        .map(report => {
          val endTime = System.currentTimeMillis()

          this.logger.debugDetail(s"It took ${endTime - startTime} milliseconds to validate",
                                  "ValidationManager",
                                  "gatherValidationErrors")

          buildIssueResults(uri, docVersion, report, baseUnit)
        })
    } else {
      Future.failed(new Exception("Cant find the editor for uri " + uri))
    }
  }

  private def extractLocations(baseUnit: BaseUnit): Set[String] = {
    baseUnit.location().toSet ++ baseUnit.references.flatMap(extractLocations)
  }

  def buildIssueResults(root: String,
                        docVersion: Int,
                        report: AMFValidationReport,
                        baseUnit: BaseUnit): Seq[ValidationReport] = {
    val located: Map[String, Seq[AMFValidationResult]] = report.results.groupBy(_.location.getOrElse(root))

    val collectedFirstErrors: ListBuffer[(String, ValidationIssue)] = ListBuffer[(String, ValidationIssue)]()
    val dependencyNames: Set[String] = astManager.fileDependencies
      .dependenciesFor(root) ++ extractLocations(baseUnit)
    val dependenciesReports = dependencyNames
      .map { dependency =>
        located.get(dependency) match {
          case Some(results) if results.nonEmpty =>
            val r      = results.map(amfValidationResultToIssue).toSet
            val report = ValidationReport(dependency, textDocumentManager.versionOf(dependency), r)
            collectedFirstErrors ++= r.collectFirst({
              case v if v.`type` == ValidationSeverity.Error => (dependency, v)
            })
            report
          case _ => ValidationReport(dependency, textDocumentManager.versionOf(dependency), Set.empty)
        }
      }
    dependenciesReports.toSeq.sortBy(_.publishDiagnosticsParams.uri)
  }

  private def buildTopFiveIssues(root: String,
                                 collectedFirstErrors: List[(String, ValidationIssue)]): Option[ValidationIssue] = {
    val messageBuilder: ListBuffer[String] = new ListBuffer[String]
    if (collectedFirstErrors.nonEmpty) {
      messageBuilder += "Dependency errors found:"
      collectedFirstErrors.take(5).foreach { vi =>
        messageBuilder += s"\t${vi._1} at: ${vi._2.range.toString}"
      }
      if (collectedFirstErrors.length > 5) messageBuilder += "and more..."
      Some(
        ValidationIssue("PROPERTY_UNUSED",
                        ValidationSeverity.Error,
                        "",
                        messageBuilder.mkString("\n"),
                        EmptyPositionRange,
                        Nil))
    } else None
  }

  def amfValidationResultToIssue(validationResult: AMFValidationResult): ValidationIssue = {
    val messageText = validationResult.message
    val range = validationResult.position
      .map(position => PositionRange(position.range))
      .getOrElse(EmptyPositionRange)

    ValidationIssue("PROPERTY_UNUSED", ValidationSeverity(validationResult.level), "", messageText, range, List())
  }

  // check if DialectInstance <- nameAndVersion ?
  // check if .raml (and force RAML vendor)
  private def checkProfileName(baseUnit: BaseUnit): String =
    baseUnit.sourceVendor.map(_.name).getOrElse(Aml.toString)

  private def report(uri: String,
                     telemetryProvider: TelemetryProvider,
                     baseUnit: BaseUnit,
                     uuid: String): Future[AMFValidationReport] = {
    telemetryProvider.addTimedMessage("Start AMF report", MessageTypes.BEGIN_REPORT, uri, uuid)
    val eventualReport = RuntimeValidator(baseUnit, ProfileName(checkProfileName(baseUnit)))
    eventualReport.foreach(r =>
      telemetryProvider.addTimedMessage("End AMF report", MessageTypes.END_REPORT, uri, uuid))
    eventualReport
  }
}
