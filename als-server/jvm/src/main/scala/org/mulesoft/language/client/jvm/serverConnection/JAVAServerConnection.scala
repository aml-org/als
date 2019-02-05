// $COVERAGE-OFF$
package org.mulesoft.language.client.jvm.serverConnection

import java.util
import java.util.function.Consumer

import org.mulesoft.als.suggestions.interfaces.ISuggestion
import org.mulesoft.language.client.jvm.dtoTypes.{GetCompletionRequest, GetStructureRequest, GetStructureResponse}
import org.mulesoft.language.client.jvm.{FS, ValidationHandler}
import org.mulesoft.language.common.dtoTypes._
import org.mulesoft.language.common.logger.{ILoggerSettings, MessageSeverity}
import org.mulesoft.language.server.core.connections.AbstractServerConnection

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}

class JAVAServerConnection extends JAVAMessageDispatcher with AbstractServerConnection {
  var lastStructureReport: Option[IStructureReport] = None
  var fs: FS                                        = null
  var validationHandler: ValidationHandler          = null

  var logger: JAVALogger = (_: String, _: MessageSeverity.Value, _: String, _: String) => {}

  initialize()

  protected def initialize() {
    //		this.newMeta("READ_DIR", Option(NodeMsgTypeMeta("org.mulesoft.language.client.js.dtoTypes.ClientStringSeqResponse", true)));
    //		this.newMeta("IS_DIRECTORY", Option(NodeMsgTypeMeta("org.mulesoft.language.client.js.dtoTypes.ClientBoolResponse", true)));
    //		this.newMeta("CONTENT", Option(NodeMsgTypeMeta("org.mulesoft.language.client.js.dtoTypes.ClientStringResponse", true)));
    //
    //		this.newVoidHandler("CHANGE_POSITION", handleChangedPosition _, Option(NodeMsgTypeMeta("org.mulesoft.language.client.js.dtoTypes.ChangedPosition")));
    //
    //		this.newVoidHandler("OPEN_DOCUMENT", this.handleOpenDocument _,
    //			Option(NodeMsgTypeMeta("org.mulesoft.language.client.js.dtoTypes.OpenedDocument")))
    //
    //		this.newVoidHandler("CLOSE_DOCUMENT", (document: ClosedDocument) => Unit, Option(NodeMsgTypeMeta("org.mulesoft.language.client.js.dtoTypes.ClosedDocument", true)));
    //
    //		this.newVoidHandler("CHANGE_DOCUMENT", this.handleChangedDocument _,
    //			Option(NodeMsgTypeMeta("org.mulesoft.language.client.js.dtoTypes.ChangedDocument")))
    //
    //		//    this.newFutureHandler("GET_STRUCTURE", this.handleGetStructure _,
    //		//      Option(NodeMsgTypeMeta("org.mulesoft.language.client.js.dtoTypes.GetStructureRequest", true, true)))
    //
    //		this.newVoidHandler("SET_LOGGER_CONFIGURATION", this.handleSetLoggerConfiguration _,
    //			Option(NodeMsgTypeMeta("org.mulesoft.language.client.js.dtoTypes.LoggerSettings")))
    //
    //		this.newFutureSeqHandler("GET_SUGGESTIONS", this.handleGetSuggestions _,
    //			Option(NodeMsgTypeMeta("org.mulesoft.language.client.js.dtoTypes.GetCompletionRequest")))
    //
    //		this.newFutureHandler[FindDeclarationRequest, LocationsResponse]("OPEN_DECLARATION", (request: FindDeclarationRequest) => openDeclarationListeners.head(request.uri, request.position).map(result => {
    //			var m = Map[String, Location]();
    //
    //			var i = 0;
    //
    //			result.foreach(r => {
    //				m += (("a" + i) -> Location.sharedToTransport(r));
    //
    //				i = i + 1;
    //			});
    //
    //			new LocationsResponse(m);
    //		}), Option(NodeMsgTypeMeta("org.mulesoft.language.client.js.dtoTypes.FindDeclarationRequest")));
    //		//this.newFutureHandler[FindReferencesRequest, LocationsResponse]("FIND_REFERENCES", (request: FindReferencesRequest) => findReferencesListeners.head(request.uri, request.position).map(result => new LocationsResponse(result.map(location => Location.sharedToTransport(location)))), Option(NodeMsgTypeMeta("org.mulesoft.language.client.js.dtoTypes.FindReferencesRequest")));this.newMeta("EXISTS", Option(NodeMsgTypeMeta("org.mulesoft.language.client.js.dtoTypes.ClientBoolResponse", true)));
  }

  def handleCloseDocument(uri: String) {
    val firstOpt = this.closeDocumentListeners.find(_ => true)

    firstOpt match {
      case Some(listener) => listener(uri)
      case _              => Future.failed(new Exception("No close document providers found"))
    }
  }

  def findReferences(uri: String, position: Int): Future[Seq[ILocation]] = {
    findReferencesListeners.head(uri, position)
  }

  def findDeclaration(uri: String, position: Int): Future[Seq[ILocation]] = {
    openDeclarationListeners.head(uri, position)
  }

  def rename(uri: String, position: Int, newName: String): Future[Seq[IChangedDocument]] = {
    renameListeners.head(uri, position, newName)
  }

  def handleGetStructure(getStructure: GetStructureRequest): Future[GetStructureResponse] = {
    val firstOpt = this.documentStructureListeners.find(_ => true)

    firstOpt match {
      case Some(listener) => listener(getStructure.url).map(resultMap => GetStructureResponse(resultMap))

      case _ => Future.failed(new Exception("No structure providers found"))
    }
  }

  def handleGetSuggestions(getCompletion: GetCompletionRequest): Future[Seq[ISuggestion]] = {
    val firstOpt = this.documentCompletionListeners.find(_ => true)

    firstOpt match {
      case Some(listener) => listener(getCompletion.uri, getCompletion.position)

      case _ => Future.failed(new Exception("No structure providers found"))
    }
  }

  def handleOpenDocument(document: IOpenedDocument) {
    val firstOpt = this.openDocumentListeners.find(_ => true)

    firstOpt match {
      case Some(listener) => listener(document)

      case _ => Future.failed(new Exception("No open document providers found"))
    }
  }

  def handleChangedDocument(document: IChangedDocument) {
    val firstOpt = this.changeDocumentListeners.find(_ => true)

    firstOpt match {
      case Some(listener) => listener(document)

      case _ => Future.failed(new Exception("No change document providers found"))
    }
  }

  def handleSetLoggerConfiguration(loggerSettings: ILoggerSettings) {
    this.setLoggerConfiguration(loggerSettings)
  }

  def structureAvailable(report: IStructureReport) {
    //this.send("STRUCTURE_REPORT", StructureReport.sharedToTransport(report))
  }

  override def validated(report: IValidationReport) {
    if (validationHandler == null) {
      return
    }

    var list = new util.ArrayList[IValidationIssue]()

    report.issues.foreach(reportIssue =>
      collectIssues(reportIssue).foreach(collectedIssue => list.add(collectedIssue)))

    validationHandler.success(report.pointOfViewUri, list)
  }

  private def collectIssues(issue: IValidationIssue): Seq[IValidationIssue] = {
    var result = mutable.MutableList(issue)

    issue.trace.foreach(traceIssue => {
      collectIssues(traceIssue).foreach(collected => result += collected)
    })

    result
  }

  override def exists(path: String): Future[Boolean] =
    callFs(path, fs.exists, false, (data: java.lang.Boolean) => data)

  override def readDir(path: String): Future[Seq[String]] =
    callFs(path, fs.readDir, Seq(), (data: java.util.List[String]) => {
      var result: ArrayBuffer[String] = ArrayBuffer()

      data.forEach(item => {
        result += item
      })

      result
    })

  override def isDirectory(path: String): Future[Boolean] =
    callFs(path, fs.isDirectory, false, (data: java.lang.Boolean) => data)

  override def content(fullPath: String): Future[String] = callFs(fullPath, fs.content, "", (data: String) => data)

  private def callFs[T, S](path: String,
                           method: (String, Consumer[S]) => Unit,
                           default: T,
                           converter: S => T): Future[T] =
    if (fs == null) {
      Future.successful(default)
    } else {
      var promise = Promise[T]()

      method(path, (data: S) => {
        promise.success(converter(data))
      })

      promise.future
    }

  override def onDocumentDetails(listener: (String, Int) => Future[IDetailsItem], unsubscribe: Boolean) {}

  override def detailsAvailable(report: IDetailsReport) {}

  override def displayActionUI(uiDisplayRequest: IUIDisplayRequest): Future[Any] = Future.successful(null)

  override def setLoggerConfiguration(loggerSettings: ILoggerSettings) {}

  override def log(message: String, severity: MessageSeverity.Value, component: String, subcomponent: String) {
    logger.log(message, severity, component, subcomponent)
  }

  override def debugDetail(message: String, component: String, subcomponent: String) {
    log(message, MessageSeverity.DEBUG_DETAIL, component, subcomponent)
  }

  override def warning(message: String, component: String, subcomponent: String) {
    log(message, MessageSeverity.WARNING, component, subcomponent)
  }

  override def debugOverview(message: String, component: String, subcomponent: String) {
    log(message, MessageSeverity.DEBUG_OVERVIEW, component, subcomponent)
  }

  override def error(message: String, component: String, subcomponent: String) {
    log(message, MessageSeverity.ERROR, component, subcomponent)
  }

  override def debug(message: String, component: String, subcomponent: String) {
    log(message, MessageSeverity.DEBUG, component, subcomponent)
  }

  def setLogger(logger: JAVALogger) {
    this.logger = logger
  }
}

// $COVERAGE-ON$
