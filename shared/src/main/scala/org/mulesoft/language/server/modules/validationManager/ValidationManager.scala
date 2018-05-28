package org.mulesoft.language.server.server.modules.validationManager

import amf.core.client.ParserConfig
import amf.core.model.document.BaseUnit
import amf.core.services.RuntimeValidator
import amf.core.unsafe.TrunkPlatform
import amf.core.validation.{AMFValidationReport, AMFValidationResult}
import org.mulesoft.language.common.dtoTypes.{IRange, IValidationIssue, IValidationReport}
import org.mulesoft.language.server.core.{AbstractServerModule, IServerModule}
import org.mulesoft.language.server.server.modules.astManager.{IASTListener, IASTManagerModule, ParserHelper}
import org.mulesoft.language.server.server.modules.commonInterfaces.{IEditorTextBuffer, IPoint}
import org.mulesoft.language.server.server.modules.editorManager.IEditorManagerModule

import scala.collection.mutable.Buffer
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}
import scala.concurrent.ExecutionContext.Implicits.global



class ValidationManager extends AbstractServerModule {

  /**
    * Module ID
    */
  val moduleId: String = "VALIDATION_MANAGER"

  val moduleDependencies: Array[String] = Array(
    IEditorManagerModule.moduleId,
    IASTManagerModule.moduleId)

  val onNewASTAvailableListener: IASTListener = new IASTListener {

    override def apply(uri: String, version: Int, ast: BaseUnit): Unit = {
      ValidationManager.this.newASTAvailable(uri, version, ast)
    }
  }

  protected def getEditorManager: IEditorManagerModule = {

    this.getDependencyById(IEditorManagerModule.moduleId).get
  }

  protected def getASTManager: IASTManagerModule = {

    this.getDependencyById(IASTManagerModule.moduleId).get
  }

  override def launch(): Try[IServerModule] = {

    val superLaunch = super.launch()

    if (superLaunch.isSuccess) {

      this.getASTManager.onNewASTAvailable(this.onNewASTAvailableListener)

      Success(this)
    } else {

      superLaunch
    }
  }


  override def stop(): Unit = {

    super.stop()

    this.getASTManager.onNewASTAvailable(this.onNewASTAvailableListener, true)
  }

  def newASTAvailable(uri: String, version: Int, ast: BaseUnit): Unit = {

    this.connection.debug("Got new AST:\n" + ast.toString,
      "ValidationManager", "newASTAvailable")

    val errors = this.gatherValidationErrors(uri, version, ast).onComplete{
      case Success(report) => {

        this.connection.debug("Number of errors is:\n" + report.issues.length,
          "ValidationManager", "newASTAvailable")

        this.connection.validated(report)
      }
      case Failure(exception) => this.connection.error("Error on validation: " + exception.toString,
        "ValidationManager", "newASTAvailable")
    }

  }

  def gatherValidationErrors(docUri: String, docVersion: Int, astNode: BaseUnit): Future[IValidationReport] = {

    val editorOption = this.getEditorManager.getEditor(docUri)

    if (editorOption.isDefined){

      this.report(docUri, astNode).map(report=>{

        val issues = report.results.map(validationResult=>{

          this.amfValidationResultToIssue(docUri, validationResult, editorOption.get.buffer)

        })

        IValidationReport(
          docUri,
          docVersion,
          issues
        )
      })
    } else {

      Future.failed(new Exception("Cant find the editor for uri " + docUri))
    }
  }

  def amfValidationResultToIssue(uri: String, validationResult: AMFValidationResult,
                                 buffer: IEditorTextBuffer): IValidationIssue = {

    val messageText = validationResult.message

    var resultRange = IRange(
      0, 0
    )
    if (validationResult.position.isDefined) {
      val startLine = validationResult.position.get.range.start.line - 1
      val startColumn = validationResult.position.get.range.start.column

      val startOffset = buffer.characterIndexForPosition(IPoint (
        startLine,
        startColumn
      ))

      val endLine = validationResult.position.get.range.end.line - 1
      val endColumn = validationResult.position.get.range.end.column

      val endOffset = buffer.characterIndexForPosition(IPoint (
        endLine,
        endColumn
      ))

      resultRange = IRange(
        startOffset, endOffset
      )
    }

    println("Found error with message: " + messageText)

    IValidationIssue(
      "PROPERTY_UNUSED",
      "Error",
      uri,
      messageText,
      resultRange,
      List()
    )
  }

  def report(uri: String, baseUnit: BaseUnit): Future[AMFValidationReport] = {

    val language = if (uri.endsWith(".raml")) "RAML 1.0" else "OAS 2.0";

    //move config initialization elsewhere
    var config = new ParserConfig(
      Some(ParserConfig.PARSE),
      Some(uri),
      Some(language),
      Some("application/yaml"),
      None,
      Some("AMF Graph"),
      Some("application/ld+json")
    )
//
//    val text = this.getEditorManager.getEditor(uri).get.text
//
//    val platform = TrunkPlatform(text)
//
//    val helper = ParserHelper(platform)
//
//    helper.report(baseUnit, config)

    val customProfileLoaded = if (config.customProfile.isDefined) {
      RuntimeValidator.loadValidationProfile(config.customProfile.get) map { profileName =>
        profileName
      }
    } else {
      Future {
        config.validationProfile
      }
    }

    customProfileLoaded.flatMap(profileName => {

        val result = RuntimeValidator(baseUnit, profileName)
        result
      }
    )
  }
}

object ValidationManager {
  /**
    * Module ID
    */
  val moduleId: String = "VALIDATION_MANAGER"
}
