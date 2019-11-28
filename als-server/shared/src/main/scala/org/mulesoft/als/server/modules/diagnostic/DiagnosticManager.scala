package org.mulesoft.als.server.modules.diagnostic

import amf.ProfileName
import amf.core.model.document.BaseUnit
import amf.core.remote.Aml
import amf.core.services.RuntimeValidator
import amf.core.validation.{AMFValidationReport, AMFValidationResult}
import org.mulesoft.als.common.dtoTypes.{EmptyPositionRange, Position, PositionRange}
import org.mulesoft.als.server.ClientNotifierModule
import org.mulesoft.als.server.client.ClientNotifier
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.modules.ast._
import org.mulesoft.als.server.modules.common.reconciler.Reconciler
import org.mulesoft.als.server.modules.workspace.ReferenceOrigin
import org.mulesoft.lsp.ConfigType
import org.mulesoft.lsp.feature.diagnostic.{DiagnosticClientCapabilities, DiagnosticConfigType}
import org.mulesoft.lsp.feature.telemetry.{MessageTypes, TelemetryProvider}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
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
  override def onNewAst(tuple: (BaseUnit, Map[String, Set[ReferenceOrigin]]), uuid: String): Unit = {
    val ast        = tuple._1
    val references = tuple._2
    logger.debug("Got new AST:\n" + ast.toString, "ValidationManager", "newASTAvailable")
    val uri = ast.id
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
                                     references: Map[String, Set[ReferenceOrigin]],
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

  private def extractLocations(baseUnit: BaseUnit): Set[String] = {
    baseUnit.location().toSet ++ baseUnit.references.flatMap(extractLocations)
  }

  def buildIssueResults(root: String,
                        report: AMFValidationReport,
                        baseUnit: BaseUnit,
                        references: Map[String, Set[ReferenceOrigin]]): Seq[ValidationReport] = {
    val located: Map[String, Seq[AMFValidationResult]] = report.results.groupBy(_.location.getOrElse(root))

    val collectedFirstErrors: ListBuffer[(String, ValidationIssue)] = ListBuffer[(String, ValidationIssue)]()
    val dependencyNames: Set[String]                                = extractLocations(baseUnit)

    val withExternalDependency: Map[Boolean, Set[String]] = dependencyNames.groupBy(n => isExternal(n, references))

    val mapReport: mutable.Map[String, mutable.Set[ValidationIssue]] = mutable.Map()

    //todo: if syntax error ignore "external" (validationId: "http://a.ml/vocabularies/amf/core#syaml-error"
    withExternalDependency
      .getOrElse(false, Nil)
      .foreach { dependency =>
        located.get(dependency) match {
          case Some(results) if results.nonEmpty =>
            val r = results.map(amfValidationResultToIssue(_, references)).toSet
            collectedFirstErrors ++= r.collectFirst({
              case v if v.`type` == ValidationSeverity.Error => (dependency, v)
            })
            //              report

            if (mapReport.get(dependency).isEmpty)
              mapReport.update(dependency, mutable.Set.empty)
            r.foreach(mapReport(dependency).add)
          case _ => // ValidationReport(dependency, Set.empty)
        }
      }
    withExternalDependency
      .getOrElse(true, Nil)
      .foreach { dependency =>
        located.get(dependency) match {
          case Some(results) if results.nonEmpty =>
            val r = results
              .map(r => {
                val stack       = amfValidationExternalResultToIssue(r, references)
                val messageText = r.message
                ValidationIssue(
                  "PROPERTY_UNUSED",
                  ValidationSeverity(r.level),
                  stack.headOption.map(_.filePath).getOrElse(""),
                  messageText,
                  stack.headOption.map(_.range).getOrElse(PositionRange(Position(0, 0), Position(0, 0))),
                  stack.tail
                )
              })
              .toSet
            collectedFirstErrors ++= r.collectFirst({
              case v if v.`type` == ValidationSeverity.Error => (dependency, v)
            })
            //              report
            r.foreach(vi => {
              if (mapReport.get(vi.filePath).isEmpty)
                mapReport.update(vi.filePath, mutable.Set.empty)
              mapReport(vi.filePath).add(vi)
            })
          case _ => // ValidationReport(dependency, Set.empty)
        }
      }

    dependencyNames
      .map { name =>
        ValidationReport(name, mapReport.get(name).map(_.toSet).getOrElse(Set.empty))
      }
      //    dependenciesReports
      .toSeq
      .sortBy(_.publishDiagnosticsParams.uri)
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

  def amfValidationExternalResultToIssue(validationResult: AMFValidationResult,
                                         references: Map[String, Set[ReferenceOrigin]]): List[ValidationIssue] = {
    (extractStackTrace(validationResult, references).reverse :+
      ValidationIssue(
        "PROPERTY_UNUSED",
        ValidationSeverity(validationResult.level),
        validationResult.location.getOrElse(""),
        s"at ${validationResult.location.getOrElse("")} ${validationResult.position
          .map(li => PositionRange(li.range))
          .getOrElse(PositionRange(Position(0, 0), Position(0, 0)))}",
        validationResult.position
          .map(li => PositionRange(li.range))
          .getOrElse(PositionRange(Position(0, 0), Position(0, 0))),
        Nil
      ))

  }

  def amfValidationResultToIssue(validationResult: AMFValidationResult,
                                 references: Map[String, Set[ReferenceOrigin]]): ValidationIssue = {
    val messageText = validationResult.message
    val range = validationResult.position
      .map(position => PositionRange(position.range))
      .getOrElse(EmptyPositionRange)

    ValidationIssue(
      "PROPERTY_UNUSED",
      ValidationSeverity(validationResult.level),
      validationResult.location.getOrElse(""),
      messageText,
      range,
      extractStackTrace(validationResult, references)
    )
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

  private def extractStackTrace(amfValidationResult: AMFValidationResult,
                                references: Map[String, Set[ReferenceOrigin]]): List[ValidationIssue] = {
    def inner(uri: String, stack: List[ReferenceOrigin] = Nil): List[ReferenceOrigin] =
      references
        .get(uri)
        .map(_.toList.flatMap(ro => {
          if (stack.contains(ro)) // new
            stack
          else // already there
            ro +: inner(ro.locationOrigin, stack)
        }))
        .getOrElse(stack)

    def buildIssues(value: List[ReferenceOrigin]): List[ValidationIssue] = {
      value.map(
        ro =>
          ValidationIssue("PROPERTY_UNUSED",
                          ValidationSeverity.Error,
                          ro.locationOrigin,
                          s"at ${ro.locationOrigin} ${ro.range}",
                          ro.range,
                          Nil))
    }

    val value: List[ReferenceOrigin] = amfValidationResult.location.map(location => inner(location)).getOrElse(Nil)
    buildIssues(value)
  }

  private def isExternal(location: String, references: Map[String, Set[ReferenceOrigin]]): Boolean =
    references.get(location).exists(ros => ros.forall(ro => ro.isExternal))
}
