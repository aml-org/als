package org.mulesoft.language.server.modules.validationManager

import amf.core.client.ParserConfig
import amf.core.model.document.BaseUnit
import amf.core.services.RuntimeValidator
import amf.core.validation.{AMFValidationReport, AMFValidationResult}
import org.mulesoft.language.common.dtoTypes._
import org.mulesoft.language.server.common.reconciler.Reconciler
import org.mulesoft.language.server.common.utils.PathRefine
import org.mulesoft.language.server.core.AbstractServerModule
import org.mulesoft.language.server.modules.astManager.{ASTListener, ASTManagerModule}
import org.mulesoft.language.server.modules.editorManager.EditorManagerModule

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

class ValidationManager extends AbstractServerModule {

  val moduleId: String = "VALIDATION_MANAGER"

  private val reconciler: Reconciler = new Reconciler(connection, 1000)

  val moduleDependencies: Array[String] = Array(EditorManagerModule.moduleId, ASTManagerModule.moduleId)

  val onNewASTAvailableListener: ASTListener = (uri: String, version: Int, ast: BaseUnit) => {
    ValidationManager.this.newASTAvailable(uri, version, ast)
  }

  protected def getEditorManager(): EditorManagerModule = this.getDependencyById(EditorManagerModule.moduleId).get

  protected def getASTManager(): ASTManagerModule = this.getDependencyById(ASTManagerModule.moduleId).get

  override def launch(): Future[Unit] =
    super.launch()
      .map(_ => {
        this.getASTManager().onNewASTAvailable(this.onNewASTAvailableListener)
      })

  override def stop() {
    super.stop()

    this.getASTManager().onNewASTAvailable(this.onNewASTAvailableListener, true)
  }

  def newASTAvailable(uri: String, version: Int, ast: BaseUnit) {
    connection.debug("Got new AST:\n" + ast.toString, "ValidationManager", "newASTAvailable")

    reconciler.shedule(new ValidationRunnable(uri, () => gatherValidationErrors(uri, version, ast))).future andThen {
      case Success(report) =>
        connection.debug("Number of errors is:\n" + report.issues.length, "ValidationManager", "newASTAvailable")
        connection.validated(report)

      case Failure(exception) =>
        exception.printStackTrace()
        connection.error("Error on validation: " + exception.toString, "ValidationManager", "newASTAvailable")
        connection.validated(ValidationReport(uri, 0, Seq()))
    }
  }

  private def gatherValidationErrors(docUri: String, docVersion: Int, astNode: BaseUnit): Future[ValidationReport] = {
    val uri = PathRefine.refinePath(docUri, platform)
    val editorOption = this.getEditorManager().getEditor(uri)

    if (editorOption.isDefined) {
      val startTime = System.currentTimeMillis()

      this
        .report(uri, astNode)
        .map(report => {
          val endTime = System.currentTimeMillis()

          this.connection.debugDetail(s"It took ${endTime - startTime} milliseconds to validate",
            "ValidationManager",
            "gatherValidationErrors")

          val issues = report.results.map(validationResult =>
            this.amfValidationResultToIssue(docUri, validationResult))

          ValidationReport(docUri, docVersion, issues)
        })
    } else {
      Future.failed(new Exception("Cant find the editor for uri " + uri))
    }
  }

  def amfValidationResultToIssue(uri: String, validationResult: AMFValidationResult): ValidationIssue = {
    val messageText = validationResult.message
    val range = validationResult.position
      .map(position => PositionRange(position.range))
      .getOrElse(EmptyPositionRange)

    ValidationIssue("PROPERTY_UNUSED", ValidationSeverity(validationResult.level), uri, messageText, range, List())
  }

  private def report(uri: String, baseUnit: BaseUnit): Future[AMFValidationReport] = {
    val language = getEditorManager().getEditor(uri).map(_.language).getOrElse("OAS 2.0")

    val config = new ParserConfig(Some(ParserConfig.VALIDATE),
      Some(uri),
      Some(language),
      Some("application/yaml"),
      None,
      Some(language),
      Some("application/yaml"),
      false,
      true)

    val customProfileLoaded = if (config.customProfile.isDefined) {
      RuntimeValidator.loadValidationProfile(config.customProfile.get)
    } else {
      Future.successful(config.profile)
    }

    customProfileLoaded.flatMap(profile => RuntimeValidator(baseUnit, profile))
  }
}

object ValidationManager {
  val moduleId: String = "VALIDATION_MANAGER"
}
