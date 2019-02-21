package org.mulesoft.language.test.serverConnection

import org.mulesoft.language.common.dtoTypes.{
  IDetailsItem,
  IDetailsReport,
  IUIDisplayRequest,
  ChangedDocument => SharedChangedDocument,
  OpenedDocument => SharedOpenedDocument,
  StructureReport => SharedStructureReport,
  ValidationReport => SharedValidationReport
}
import org.mulesoft.language.common.logger.MutedLogger
import org.mulesoft.language.entryPoints.common.{
  MessageDispatcher,
  ProtocolMessage => SharedProtocolMessage,
  ProtocolSeqMessage => SharedProtocolSeqMessage
}
import org.mulesoft.language.server.core.connections.AbstractServerConnection
import org.mulesoft.language.server.modules.editorManager.EditorManagerModule
import org.mulesoft.language.test.clientConnection.TestClientConnection
import org.mulesoft.language.test.dtoTypes._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

//@ScalaJSDefined
class WrappedPayload { //extends js.Object {
  //  var wrapped: js.Object = null;
}

//
//@ScalaJSDefined
class WrappedMessage { //extends js.Object {
  //  var payload: js.Object = null;
}

class TestServerConnection(clientProcess: Seq[TestClientConnection])
    extends MutedLogger
    with MessageDispatcher[ProtocolMessagePayload, NodeMsgTypeMeta]
    with AbstractServerConnection {

  var lastStructureReport: Option[StructureReport] = None

  var editorManager: Option[EditorManagerModule] = None

  initialize()

  protected def initialize(): Unit = {
    this.newMeta("EXISTS", Option(NodeMsgTypeMeta("org.mulesoft.language.test.dtoTypes.ClientBoolResponse", true)))
    this.newMeta("READ_DIR",
                 Option(NodeMsgTypeMeta("org.mulesoft.language.test.dtoTypes.ClientStringSeqResponse", true)))
    this
      .newMeta("IS_DIRECTORY", Option(NodeMsgTypeMeta("org.mulesoft.language.test.dtoTypes.ClientBoolResponse", true)))
    this.newMeta("CONTENT", Option(NodeMsgTypeMeta("org.mulesoft.language.test.dtoTypes.ClientStringResponse", true)))

    this.newVoidHandler("CHANGE_POSITION",
                        handleChangedPosition,
                        Option(NodeMsgTypeMeta("org.mulesoft.language.test.dtoTypes.ChangedPosition")))

    this.newVoidHandler("OPEN_DOCUMENT",
                        this.handleOpenDocument,
                        Option(NodeMsgTypeMeta("org.mulesoft.language.test.dtoTypes.OpenedDocument")))

    this.newVoidHandler("CLOSE_DOCUMENT",
                        (document: ClosedDocument) => Unit,
                        Option(NodeMsgTypeMeta("org.mulesoft.language.test.dtoTypes.ClosedDocument", true)))

    this.newVoidHandler("CHANGE_DOCUMENT",
                        this.handleChangedDocument,
                        Option(NodeMsgTypeMeta("org.mulesoft.language.test.dtoTypes.ChangedDocument")))

    this.newFutureHandler(
      "GET_STRUCTURE",
      this.handleGetStructure,
      Option(NodeMsgTypeMeta("org.mulesoft.language.test.dtoTypes.GetStructureRequest", true, true)))

    this.newFutureHandler("RENAME",
                          this.handleRename,
                          Option(NodeMsgTypeMeta("org.mulesoft.language.test.dtoTypes.RenameRequest")))

    this.newVoidHandler("SET_LOGGER_CONFIGURATION",
                        this.handleSetLoggerConfiguration,
                        Option(NodeMsgTypeMeta("org.mulesoft.language.test.dtoTypes.LoggerSettings")))

    this.newFutureHandler("GET_SUGGESTIONS",
                          this.handleGetSuggestions,
                          Option(NodeMsgTypeMeta("org.mulesoft.language.test.dtoTypes.GetCompletionRequest")))

    this.newFutureHandler[FindDeclarationRequest, LocationsResponse](
      "OPEN_DECLARATION",
      (request: FindDeclarationRequest) =>
        openDeclarationListeners
          .head(request.uri, request.position)
          .map(result => new LocationsResponse(result.map(location => Location.sharedToTransport(location)))),
      Option(NodeMsgTypeMeta("org.mulesoft.language.test.dtoTypes.FindDeclarationRequest"))
    )

    this.newFutureHandler[FindReferencesRequest, LocationsResponse](
      "FIND_REFERENCES",
      this.handleFindReferences,
      Option(NodeMsgTypeMeta("org.mulesoft.language.test.dtoTypes.FindReferencesRequest"))
    )
  }

  def handleChangedPosition(changedPosition: ChangedPosition): Unit = {}

  def handleGetStructure(getStructure: GetStructureRequest): Future[GetStructureResponse] = {
    val firstOpt = this.documentStructureListeners.find(_ => true)
    firstOpt match {
      case Some(listener) =>
        listener(getStructure.wrapped).map(resultMap => {
          GetStructureResponse(resultMap.toList)
        })
      case _ => Future.failed(new Exception("No structure providers found"))
    }
  }

  def handleRename(getStructure: RenameRequest): Future[RenameResponse] = {
    val firstOpt = this.renameListeners.find(_ => true)
    firstOpt match {
      case Some(listener) =>
        listener(getStructure.uri, getStructure.position, getStructure.newName).map(resultMap => {
          RenameResponse(resultMap.map(ChangedDocument.sharedToTransport))
        })
      case _ => Future.failed(new Exception("No rename modules found"))
    }
  }

  def handleGetSuggestions(getCompletion: GetCompletionRequest): Future[GetCompletionResponse] = {
    val firstOpt = this.documentCompletionListeners.find(_ => true)
    firstOpt match {
      case Some(listener) =>
        listener(getCompletion.uri, getCompletion.position)
          .map(result => {
            result.map(suggestion => Suggestion.sharedToTransport(suggestion))
          })
          .map(GetCompletionResponse)
      case _ => Future.failed(new Exception("No structure providers found"))
    }
  }

  def handleFindReferences(findReferences: FindReferencesRequest): Future[LocationsResponse] = {
    val firstOpt = this.findReferencesListeners.find(_ => true)
    firstOpt match {
      case Some(listener) =>
        listener(findReferences.uri, findReferences.position)
          .map(result => {
            result.map(reference => Location.sharedToTransport(reference))
          })
          .map(LocationsResponse(_))
      case _ => Future.failed(new Exception("No structure providers found"))
    }
  }

  def handleOpenDocument(document: OpenedDocument): Unit = {
    val firstOpt = this.openDocumentListeners.find(_ => true)
    firstOpt match {
      case Some(listener) =>
        listener(document)
      case _ => Future.failed(new Exception("No open document providers found"))
    }
  }

  def handleChangedDocument(document: ChangedDocument): Unit = {
    val firstOpt = this.changeDocumentListeners.find(_ => true)
    firstOpt match {
      case Some(listener) =>
        listener(document)
      case _ => Future.failed(new Exception("No change document providers found"))
    }
  }

  def handleSetLoggerConfiguration(loggerSettings: LoggerSettings): Unit = {
    this.withSettings(LoggerSettings.transportToShared(loggerSettings))

  }

  /**
    * Reports new calculated structure when available.
    *
    * @param report - structure report.
    */
  def structureAvailable(report: SharedStructureReport): Unit = {
    this.send("STRUCTURE_REPORT", StructureReport.sharedToTransport(report))
  }

  /**
    * Reports latest validation results
    *
    * @param report
    */
  override def validated(report: SharedValidationReport): Unit = {
    this.send("VALIDATION_REPORT", ValidationReport.sharedToTransport(report))
  }

  /**
    * Returns whether path/url exists.
    *
    * @param path
    */
  override def exists(path: String): Future[Boolean] = FS.exists(path)

  /**
    * Returns directory content list.
    *
    * @param path
    */
  override def readDir(path: String): Future[Seq[String]] = FS.readDir(path).map(_.toSeq)

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
  override def content(fullPath: String): Future[String] = {

    if (editorManager.isDefined) {

      val editor = editorManager.get.getEditor(fullPath)
      if (editor.isDefined) {

        Future.successful(editor.get.text)
      } else {

        FS.content(fullPath)
      }
    } else {

      FS.content(fullPath)
    }

  }

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

  def internalSendMessage(message: SharedProtocolMessage[ProtocolMessagePayload]): Unit = {

    this.debugDetail("Sending message of type: " + message.`type`, "NodeMessageDispatcher", "internalSendMessage")

    //val protocolMessage = this.serializeMessage(message)

    //this.debugDetail("Serialized message: " + JSON.stringify(protocolMessage),
    //  "NodeMessageDispatcher", "internalSendMessage")

    //this.internalSendJSONMessage(protocolMessage.asInstanceOf[js.Object])
    clientProcess.foreach(_.internalHandleReceivedMessage(message))
  }

  /**
    * Performs actual message sending.
    * Not intended to be called directly, instead is being used by
    * send() and sendWithResponse() methods
    * Called by the trait.
    *
    * @param message - message to send
    */
  def internalSendSeqMessage(message: SharedProtocolSeqMessage[ProtocolMessagePayload]): Unit = {

    this.debugDetail("Sending message of type: " + message.`type`, "NodeMessageDispatcher", "internalSendMessage")

    //val protocolMessage = this.serializeSeqMessage(message)

    //this.debugDetail("Serialized message: " + JSON.stringify(protocolMessage),
    //  "NodeMessageDispatcher", "internalSendMessage")

    //this.internalSendJSONMessage(protocolMessage.asInstanceOf[js.Object])
  }

  def rename(uri: String, position: Int, newName: String): Future[Seq[SharedChangedDocument]] = {
    renameListeners.head(uri, position, newName)
  }
}
