package org.mulesoft.language.client.js.serverConnection

import org.mulesoft.language.client.js.dtoTypes._
import org.mulesoft.language.client.js.{Globals, dtoTypes}
import org.mulesoft.language.common.dtoTypes._
import org.mulesoft.language.common.logger.IPrintlnLogger
import org.mulesoft.language.server.server.core.connectionsImpl.AbstractServerConnection

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}
import scala.scalajs.js
import scala.scalajs.js.annotation.ScalaJSDefined
import scala.util.{Failure, Success}


@ScalaJSDefined
class WrappedPayload extends js.Object {
  var wrapped: js.Object = null;
}

@ScalaJSDefined
class WrappedMessage extends js.Object {
  var payload: js.Object = null;
}

class NodeServerConnection extends IPrintlnLogger
  with NodeMessageDispatcher with AbstractServerConnection {

  var lastStructureReport: Option[StructureReport] = None

  initialize()

  protected def initialize(): Unit = {
    this.newMeta("EXISTS", Option(NodeMsgTypeMeta("org.mulesoft.language.client.js.dtoTypes.ClientBoolResponse", true)));
    this.newMeta("READ_DIR", Option(NodeMsgTypeMeta("org.mulesoft.language.client.js.dtoTypes.ClientStringSeqResponse", true)));
    this.newMeta("IS_DIRECTORY", Option(NodeMsgTypeMeta("org.mulesoft.language.client.js.dtoTypes.ClientBoolResponse", true)));
    this.newMeta("CONTENT", Option(NodeMsgTypeMeta("org.mulesoft.language.client.js.dtoTypes.ClientStringResponse", true)));
    
    this.newVoidHandler("OPEN_DOCUMENT", this.handleOpenDocument _,
      Option(NodeMsgTypeMeta("org.mulesoft.language.client.js.dtoTypes.OpenedDocument")))
    
    this.newVoidHandler("CLOSE_DOCUMENT", (document: ClosedDocument) => Unit, Option(NodeMsgTypeMeta("org.mulesoft.language.client.js.dtoTypes.ClosedDocument", true)));

    this.newVoidHandler("CHANGE_DOCUMENT", this.handleChangedDocument _,
      Option(NodeMsgTypeMeta("org.mulesoft.language.client.js.dtoTypes.ChangedDocument")))

//    this.newFutureHandler("GET_STRUCTURE", this.handleGetStructure _,
//      Option(NodeMsgTypeMeta("org.mulesoft.language.client.js.dtoTypes.GetStructureRequest", true, true)))

    this.newVoidHandler("SET_LOGGER_CONFIGURATION", this.handleSetLoggerConfiguration _,
      Option(NodeMsgTypeMeta("org.mulesoft.language.client.js.dtoTypes.LoggerSettings")))
      
   this.newFutureSeqHandler("GET_SUGGESTIONS", this.handleGetSuggestions _,
      Option(NodeMsgTypeMeta("org.mulesoft.language.client.js.dtoTypes.GetCompletionRequest")))
    
    this.newFutureHandler[FindDeclarationRequest, LocationsResponse]("OPEN_DECLARATION", (request: FindDeclarationRequest) => openDeclarationListeners.head(request.uri, request.position).map(result => new LocationsResponse(result.map(location => location.asInstanceOf[Location]))), Option(NodeMsgTypeMeta("org.mulesoft.language.client.js.dtoTypes.FindDeclarationRequest")));
    this.newFutureHandler[FindReferencesRequest, LocationsResponse]("FIND_REFERENCES", (request: FindReferencesRequest) => findReferencesListeners.head(request.uri, request.position).map(result => new LocationsResponse(result.map(location => location.asInstanceOf[Location]))), Option(NodeMsgTypeMeta("org.mulesoft.language.client.js.dtoTypes.FindReferencesRequest")));
  }

  protected def internalSendJSONMessage(message: js.Object): Unit = {
    if(message.hasOwnProperty("payload") && message.asInstanceOf[WrappedMessage].payload.hasOwnProperty("wrapped")) {
      var payload = message.asInstanceOf[WrappedMessage].payload.asInstanceOf[WrappedPayload];
      
      message.asInstanceOf[WrappedMessage].payload = payload.wrapped;
    }
    
    Globals.process.send(message);
  }

  def handleGetStructure(getStructure: GetStructureRequest) : Future[GetStructureResponse] = {
    val firstOpt = this.documentStructureListeners.find(_=>true)
    firstOpt match  {
      case Some(listener) =>
        listener(getStructure.wrapped).map(resultMap=>{
          GetStructureResponse(resultMap.map{case (key, value) => (key, value.asInstanceOf[dtoTypes.StructureNode])})
        })
      case _ => Future.failed(new Exception("No structure providers found"))
    }
  }
  
    def handleGetSuggestions(getCompletion: GetCompletionRequest) : Future[Seq[Suggestion]] = {
    val firstOpt = this.documentCompletionListeners.find(_=>true)
    firstOpt match  {
      case Some(listener) =>
        listener(getCompletion.uri, getCompletion.position).map(result=>{
          result.map(suggestion=>Suggestion.sharedToTransport(suggestion))
        })
      case _ => Future.failed(new Exception("No structure providers found"))
    }
  }

  def handleOpenDocument(document: OpenedDocument) : Unit = {
    val firstOpt = this.openDocumentListeners.find(_=>true)
    firstOpt match  {
      case Some(listener) =>
        listener(document)
      case _ => Future.failed(new Exception("No open document providers found"))
    }
  }

  def handleChangedDocument(document: ChangedDocument) : Unit = {
    val firstOpt = this.changeDocumentListeners.find(_=>true)
    firstOpt match  {
      case Some(listener) =>
        listener(document)
      case _ => Future.failed(new Exception("No change document providers found"))
    }
  }

  def handleSetLoggerConfiguration(loggerSettings: LoggerSettings) : Unit = {
    this.setLoggerConfiguration(LoggerSettings.transportToShared(loggerSettings))

  }

  /**
    * Reports new calculated structure when available.
    * @param report - structure report.
    */
  def structureAvailable(report: IStructureReport): Unit = {
    this.send("STRUCTURE_REPORT", StructureReport.sharedToTransport(report))
  }

  /**
    * Reports latest validation results
    *
    * @param report
    */
  override def validated(report: IValidationReport): Unit = {
    this.send("VALIDATION_REPORT", ValidationReport.sharedToTransport(report))
  }
  
  /**
    * Returns whether path/url exists.
    *
    * @param path
    */
  override def exists(path: String): Future[Boolean] = FS.exists(path);
  
  /**
    * Returns directory content list.
    *
    * @param path
    */
  override def readDir(path: String): Future[Seq[String]] = FS.readDir(path);
  
  /**
    * Returns whether path/url represents a directory
    *
    * @param path
    */
  override def isDirectory(path: String): Future[Boolean] = FS.isDirectory(path)
  
  /**
    * File contents by full path/url.
    *
    * @param fullPath
    */
  override def content(fullPath: String): Future[String] = FS.content(fullPath);

  /**
    * Adds a listener to document details request. Must notify listeners in order of registration.
    *
    * @param listener    (uri: String, position: Int) => Future[DetailsItemJSON]
    * @param unsubscribe - if true, existing listener will be removed. False by default.
    */
  override def onDocumentDetails(listener: (String, Int) => Future[IDetailsItem], unsubscribe: Boolean): Unit = ???

  /**
    * Reports new calculated details when available.
    *
    * @param report - details report.
    */
  override def detailsAvailable(report: IDetailsReport): Unit = ???

  /**
    * Adds a listener to display action UI.
    *
    * @param uiDisplayRequest - display request
    * @return final UI state.
    */
  override def displayActionUI(uiDisplayRequest: IUIDisplayRequest): Future[Any] = ???
}
