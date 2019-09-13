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

  val onNewASTAvailableListener: AstListener = (uri: String, version: Int, ast: BaseUnit) => {
    newASTAvailable(uri, version, ast)
  }

  override def initialize(): Future[Unit] = {
    astManager.onNewASTAvailable(onNewASTAvailableListener)
    Future.successful()
  }

  def newASTAvailable(uri: String, version: Int, ast: BaseUnit) {
    logger.debug("Got new AST:\n" + ast.toString, "ValidationManager", "newASTAvailable")

    telemetryProvider.addTimedMessage("Start report", MessageTypes.BEGIN_DIAGNOSTIC)

    reconciler.shedule(new ValidationRunnable(uri, () => gatherValidationErrors(uri, version, ast))).future andThen {
      case Success(reports: Seq[ValidationReport]) =>
        telemetryProvider.addTimedMessage("Got reports", MessageTypes.GOT_DIAGNOSTICS)
        logger.debug("Number of errors is:\n" + reports.flatMap(_.issues).length,
                     "ValidationManager",
                     "newASTAvailable")
        reports.foreach { r =>
          clientNotifier.notifyDiagnostic(r.publishDiagnosticsParams)
        }
        telemetryProvider.addTimedMessage("End report", MessageTypes.END_DIAGNOSTIC)

      case Failure(exception) =>
        exception.printStackTrace()
        logger.error("Error on validation: " + exception.toString, "ValidationManager", "newASTAvailable")
        clientNotifier.notifyDiagnostic(ValidationReport(uri, 0, Set.empty).publishDiagnosticsParams)
    }
  }

  private def gatherValidationErrors(uri: String, docVersion: Int, astNode: BaseUnit): Future[Seq[ValidationReport]] = {
    val editorOption = textDocumentManager.getTextDocument(uri)

    if (editorOption.isDefined) {
      val startTime = System.currentTimeMillis()

      this
        .report(uri, telemetryProvider, astNode)
        .map(report => {
          val endTime = System.currentTimeMillis()

          this.logger.debugDetail(s"It took ${endTime - startTime} milliseconds to validate",
                                  "ValidationManager",
                                  "gatherValidationErrors")

          buildIssueResults(uri, docVersion, report)
        })
    } else {
      Future.failed(new Exception("Cant find the editor for uri " + uri))
    }
  }

  def buildIssueResults(root: String, docVersion: Int, report: AMFValidationReport): Seq[ValidationReport] = {
    val located: Map[String, Seq[AMFValidationResult]] = report.results.groupBy(_.location.getOrElse(root))

    val (rootReport, sonsAmfReport) = located.partition(_._1 == root)

    val collectedFirstErrors: ListBuffer[(String, ValidationIssue)] = ListBuffer[(String, ValidationIssue)]()
    val sonsReports = astManager.fileDependencies
      .dependenciesFor(root)
      .map { son =>
        sonsAmfReport.get(son) match {
          case Some(results) if results.nonEmpty =>
            val r      = results.map(amfValidationResultToIssue).toSet
            val report = ValidationReport(son, textDocumentManager.versionOf(son), r)
            collectedFirstErrors ++= r.collectFirst({ case v if v.`type` == ValidationSeverity.Error => (son, v) })
            report
          case _ => ValidationReport(son, textDocumentManager.versionOf(son), Set.empty)
        }
      }

    val rootIssues = rootReport.get(root).map(rr => rr.map(amfValidationResultToIssue).toSet).getOrElse(Nil)
    val finalRoot =
      ValidationReport(root, docVersion, (buildTopFiveIssues(root, collectedFirstErrors.toList) ++ rootIssues).toSet)

    Seq(finalRoot) ++ sonsReports
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
                     baseUnit: BaseUnit): Future[AMFValidationReport] = {
    telemetryProvider.addTimedMessage("Start AMF report", MessageTypes.BEGIN_REPORT)
    val eventualReport = RuntimeValidator(baseUnit, ProfileName(checkProfileName(baseUnit)))
    eventualReport.foreach(r => telemetryProvider.addTimedMessage("End AMF report", MessageTypes.END_REPORT))
    eventualReport
  }
}
