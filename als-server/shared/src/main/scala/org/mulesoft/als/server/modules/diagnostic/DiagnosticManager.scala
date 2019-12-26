package org.mulesoft.als.server.modules.diagnostic

import amf.ProfileName
import amf.core.annotations.LexicalInformation
import amf.core.model.document.BaseUnit
import amf.core.remote.Aml
import amf.core.services.RuntimeValidator
import amf.core.validation.{AMFValidationReport, AMFValidationResult}
import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.als.server.ClientNotifierModule
import org.mulesoft.als.server.client.ClientNotifier
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.modules.ast._
import org.mulesoft.als.server.modules.common.reconciler.Reconciler
import org.mulesoft.als.server.modules.workspace.DiagnosticsBundle
import org.mulesoft.amfmanager.BaseUnitImplicits._
import org.mulesoft.lsp.ConfigType
import org.mulesoft.lsp.common.Location
import org.mulesoft.lsp.convert.LspRangeConverter
import org.mulesoft.lsp.feature.diagnostic.{
  DiagnosticClientCapabilities,
  DiagnosticConfigType,
  DiagnosticRelatedInformation
}
import org.mulesoft.lsp.feature.telemetry.{MessageTypes, TelemetryProvider}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

class DiagnosticManager(private val telemetryProvider: TelemetryProvider,
                        private val clientNotifier: ClientNotifier,
                        private val logger: Logger)
    extends BaseUnitListener
    with ClientNotifierModule[DiagnosticClientCapabilities, Unit] {

  override val `type`: ConfigType[DiagnosticClientCapabilities, Unit] = DiagnosticConfigType

  override def applyConfig(config: Option[DiagnosticClientCapabilities]): Unit = {}

  private val reconciler: Reconciler = new Reconciler(logger, 1000)

  override def initialize(): Future[Unit] = {
    Future.successful()
  }

  /**
    * Called on new AST available
    *
    * @param tuple - (AST, References)
    * @param uuid  - telemetry UUID
    */
  override def onNewAst(tuple: (BaseUnit, Map[String, DiagnosticsBundle]), uuid: String): Unit = {
    val ast        = tuple._1
    val references = tuple._2
    logger.debug("Got new AST:\n" + ast.toString, "ValidationManager", "newASTAvailable")
    val uri = ast.location().getOrElse(ast.id)
    telemetryProvider.addTimedMessage("Start report",
                                      "DiagnosticManager",
                                      "onNewAst",
                                      MessageTypes.BEGIN_DIAGNOSTIC,
                                      uri,
                                      uuid)

    reconciler
      .shedule(new ValidationRunnable(uri, () => gatherValidationErrors(uri, ast, references, uuid)))
      .future andThen {
      case Success(reports: Seq[ValidationReport]) =>
        logger.debug("Number of errors is:\n" + reports.flatMap(_.issues).length,
                     "ValidationManager",
                     "newASTAvailable")
        reports.foreach { r =>
          telemetryProvider.addTimedMessage(s"Got reports: ${r.publishDiagnosticsParams.uri}",
                                            "DiagnosticManager",
                                            "onNewAst",
                                            MessageTypes.GOT_DIAGNOSTICS,
                                            uri,
                                            uuid)
          clientNotifier.notifyDiagnostic(r.publishDiagnosticsParams)
        }
        telemetryProvider.addTimedMessage("End report",
                                          "DiagnosticManager",
                                          "onNewAst",
                                          MessageTypes.END_DIAGNOSTIC,
                                          uri,
                                          uuid)

      case Failure(exception) =>
        telemetryProvider.addTimedMessage(s"End report: ${exception.getMessage}",
                                          "DiagnosticManager",
                                          "onNewAst",
                                          MessageTypes.END_DIAGNOSTIC,
                                          uri,
                                          uuid)
        logger.warning("Error on validation: " + exception.toString, "ValidationManager", "newASTAvailable")
        clientNotifier.notifyDiagnostic(ValidationReport(uri, Set.empty).publishDiagnosticsParams)
    }
  }

  private def gatherValidationErrors(uri: String,
                                     baseUnit: BaseUnit,
                                     references: Map[String, DiagnosticsBundle],
                                     uuid: String): Future[Seq[ValidationReport]] = {
    val clonedUnit = baseUnit //.clone()
    val startTime  = System.currentTimeMillis()

    this
      .report(uri, telemetryProvider, clonedUnit, uuid)
      .map(report => {
        val endTime = System.currentTimeMillis()

        this.logger.debugDetail(s"It took ${endTime - startTime} milliseconds to validate",
                                "ValidationManager",
                                "gatherValidationErrors")

        buildIssueResults(uri, report, baseUnit, references)
      })
  }

  def buildIssueResults(root: String,
                        report: AMFValidationReport,
                        baseUnit: BaseUnit,
                        references: Map[String, DiagnosticsBundle]): Seq[ValidationReport] = {
    val issuesWithStack: Seq[ValidationIssue] = buildIssues(report, references)

    extractAllReferences(baseUnit)
      .map { dependency =>
        ValidationReport(
          dependency,
          issuesWithStack.filter(_.filePath == dependency).toSet
        )
      }
      .toSeq
      .sortBy(_.publishDiagnosticsParams.uri)
  }

  private def buildIssues(report: AMFValidationReport,
                          references: Map[String, DiagnosticsBundle]): Seq[ValidationIssue] = {
    report.results.flatMap { r =>
      references.get(r.location.getOrElse("")) match {
        case Some(t)
            if !t.isExternal && t.references.nonEmpty => // Has stack, ain't ExternalFragment todo: check if it's a syntax error?
          t.references.map { stackContainer =>
            buildIssue(
              r,
              stackContainer.stack
                .map(
                  s =>
                    DiagnosticRelatedInformation(Location(s.originUri, LspRangeConverter.toLspRange(s.originRange)),
                                                 s"at ${s.originUri} ${s.originRange}"))
            )
          }
        case Some(t) if t.references.nonEmpty =>
          // invert order of stack, put root as last element of the trace
          val range = LspRangeConverter.toLspRange(
            r.position
              .map(position => PositionRange(position.range))
              .getOrElse(PositionRange(Position(0, 0), Position(0, 0))))
          val rootAsRelatedInfo: DiagnosticRelatedInformation = DiagnosticRelatedInformation(
            Location(
              r.location.getOrElse(""),
              range
            ),
            s"from ${r.location.getOrElse("")} ${range}"
          )

          t.references.map { stackContainer =>
            val newHead = stackContainer.stack.last

            buildIssue(
              newHead.originUri,
              newHead.originRange,
              r.message,
              r.level,
              stackContainer.stack.reverse
                .drop(1)
                .map(s =>
                  DiagnosticRelatedInformation(Location(s.originUri, LspRangeConverter.toLspRange(s.originRange)),
                                               s"from ${s.originUri}")) :+
                rootAsRelatedInfo
            )
          }
        case _ =>
          Seq(buildIssue(r, Nil))
      }
    }
  }

  // check if DialectInstance <- nameAndVersion ?
  // check if .raml (and force RAML vendor)
  private def checkProfileName(baseUnit: BaseUnit): String =
    baseUnit.sourceVendor.map(_.name).getOrElse(Aml.toString)

  private def report(uri: String,
                     telemetryProvider: TelemetryProvider,
                     baseUnit: BaseUnit,
                     uuid: String): Future[AMFValidationReport] = {
    telemetryProvider.addTimedMessage("Start AMF report",
                                      "DiagnosticManager",
                                      "report",
                                      MessageTypes.BEGIN_REPORT,
                                      uri,
                                      uuid)
    val eventualReport = RuntimeValidator(baseUnit.cloneUnit(), ProfileName(checkProfileName(baseUnit)))
    eventualReport.foreach(
      r =>
        telemetryProvider
          .addTimedMessage("End AMF report", "DiagnosticManager", "report", MessageTypes.END_REPORT, uri, uuid))
    eventualReport
  }

  override def onRemoveFile(uri: String): Unit =
    clientNotifier.notifyDiagnostic(AlsPublishDiagnosticsParams(uri, Nil))

  // todo: include old tree references
  private def extractAllReferences(baseUnit: BaseUnit): Set[String] =
    baseUnit.flatRefs.map(bu => bu.location().getOrElse(bu.id)).toSet + baseUnit.location().getOrElse(baseUnit.id)

  private def buildIssue(r: AMFValidationResult, stack: Seq[DiagnosticRelatedInformation]): ValidationIssue = {
    ValidationIssue("PROPERTY_UNUSED",
                    ValidationSeverity(r.level),
                    r.location.getOrElse(""),
                    r.message,
                    lexicalToPosition(r.position),
                    stack)
  }

  private def buildIssue(path: String,
                         range: PositionRange,
                         message: String,
                         level: String,
                         stack: Seq[DiagnosticRelatedInformation]): ValidationIssue = {
    ValidationIssue("PROPERTY_UNUSED", ValidationSeverity(level), path, message, range, stack)
  }

  private def lexicalToPosition(maybeLi: Option[LexicalInformation]): PositionRange =
    maybeLi.map(position => PositionRange(position.range)).getOrElse(PositionRange(Position(0, 0), Position(0, 0)))
}
