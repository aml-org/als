package org.mulesoft.language.test.clientConnection

import common.dtoTypes.Position
import org.mulesoft.language.client.client.{AbstractClientConnection, VersionedDocumentManager}
import org.mulesoft.language.common.dtoTypes.{
  IDetailsItem,
  IDetailsReport,
  IExecutableAction,
  ILocation,
  ChangedDocument => SharedChangedDocument,
  OpenedDocument => SharedOpenedDocument,
  Range => SharedRange,
  StructureReport => SharedStructureReport,
  ValidationReport => SharedValidationReport
}
import org.mulesoft.language.common.logger.MutedLogger
import org.mulesoft.language.entryPoints.common.{MessageDispatcher, ProtocolMessage, ProtocolSeqMessage}
import org.mulesoft.language.outline.structure.structureImpl.DocumentSymbol
import org.mulesoft.language.outline.structure.structureInterfaces.{StructureNodeJSON => SharedStructureNode}
import org.mulesoft.language.test.dtoTypes._
import org.mulesoft.language.test.serverConnection.{NodeMsgTypeMeta, TestServerConnection}
import org.mulesoft.als.suggestions.interfaces.{Suggestion => SuggestionInterface}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TestClientConnection(serverProcess: Seq[TestServerConnection])
    extends MutedLogger
    with MessageDispatcher[ProtocolMessagePayload, NodeMsgTypeMeta]
    with AbstractClientConnection {

  val versionManager: VersionedDocumentManager = new VersionedDocumentManager(this)

  /**
    * Performs actual message sending.
    * Not intended to be called directly, instead is being used by
    * send() and sendWithResponse() methods
    * Called by the trait.
    *
    * @param message - message to send
    */
  override def internalSendMessage(message: ProtocolMessage[ProtocolMessagePayload]): Unit = {
    this.serverProcess.foreach(_.internalHandleReceivedMessage(message))
  }

  /**
    * Performs actual message sending.
    * Not intended to be called directly, instead is being used by
    * send() and sendWithResponse() methods
    * Called by the trait.
    *
    * @param message - message to send
    */
  override def internalSendSeqMessage(message: ProtocolSeqMessage[ProtocolMessagePayload]): Unit = {}

  /**
    * Stops the server.
    */
  override def stop(): Unit = {}

  /**
    * Notifies the server that document is opened.
    *
    * @param document
    */
  override def documentOpened(document: SharedOpenedDocument): Unit = {
    val commonOpenedDocument = this.versionManager.registerOpenedDocument(document)
    commonOpenedDocument.foreach(doc => {
      this.send("OPEN_DOCUMENT", OpenedDocument.sharedToTransport(doc))
    })
  }

  /**
    * Notified the server that document is closed.
    *
    * @param uri
    */
  override def documentClosed(uri: String): Unit = this.send("CLOSE_DOCUMENT", ClosedDocument(uri))

  /**
    * Notifies the server that document is changed.
    *
    * @param document
    */
  override def documentChanged(document: SharedChangedDocument): Unit = {
    val commonChangeddDocument = this.versionManager.registerChangedDocument(document)
    commonChangeddDocument.foreach(doc => {
      this.send("CHANGE_DOCUMENT", ChangedDocument.sharedToTransport(doc))
    })
  }

  /**
    * Requests server for the document structure.
    *
    * @param uri
    */
  override def getStructure(uri: String): Future[List[DocumentSymbol]] =
    this
      .sendWithResponse[GetStructureResponse]("GET_STRUCTURE", GetStructureRequest(uri))
      .map(x => x.wrapped)

  /**
    * Requests server for the suggestions.
    *
    * @param uri      - document uri
    * @param position - offset in the document, starting from 0
    */
  override def getSuggestions(uri: String, position: Position): Future[Seq[SuggestionInterface]] =
    this
      .sendWithResponse[GetCompletionResponse]("GET_SUGGESTIONS", GetCompletionRequest(uri, position))
      .map(_.suggestions.map(Suggestion.transportToShared))

  /**
    * Requests server for the positions of the declaration of the element defined
    * at the given document position.
    *
    * @param uri      - document uri
    * @param position - position in the document
    */
  override def openDeclaration(uri: String, position: Position): Future[Seq[ILocation]] =
    this
      .sendWithResponse[LocationsResponse]("OPEN_DECLARATION", FindDeclarationRequest(uri, position))
      .map(r => r.wrapped.map(Location.transportToShared))

  /**
    * Requests server for the positions of the references of the element defined
    * at the given document position.
    *
    * @param uri      - document uri
    * @param position - position in the document
    */
  override def findReferences(uri: String, position: Position): Future[Seq[ILocation]] =
    this
      .sendWithResponse[LocationsResponse]("FIND_REFERENCES", FindReferencesRequest(uri, position))
      .map(r => r.wrapped.map(Location.transportToShared))

  /**
    * Requests server for the occurrences of the element defined
    * at the given document position.
    *
    * @param uri      - document uri
    * @param position - position in the document
    */
  override def markOccurrences(uri: String, position: Int): Future[Seq[SharedRange]] = Future.successful(Seq())

  /**
    * Requests server for rename of the element
    * at the given document position.
    *
    * @param uri      - document uri
    * @param position - position in the document
    */
  override def rename(uri: String, position: Int, newName: String): Future[Seq[SharedChangedDocument]] =
    this
      .sendWithResponse[RenameResponse]("RENAME", RenameRequest(uri, newName, position))
      .map(r => r.wrapped.map(ChangedDocument.transportToShared))

  def VALIDATION_REPORT(report: SharedValidationReport): Unit = validationReportListeners.foreach(x => x(report))

  def STRUCTURE_REPORT(report: SharedStructureReport): Unit = structureReportListeners.foreach(x => x(report))

  def EXISTS(path: String): Future[Boolean] =
    Future.sequence(onExistsListeners.map(x => x(path))).map(_.exists(x => x))

  def READ_DIR(path: String): Future[Seq[String]] = {
    val iterable = onReadDirListeners.map(x => x(path)).toList
    Future.find(iterable)(x => x != null && x.nonEmpty).map(_.getOrElse(Seq()))
  }

  def IS_DIRECTORY(path: String): Future[Boolean] =
    Future.sequence(onIsDirectoryListeners.map(x => x(path))).map(_.exists(x => x))

  def CONTENT(path: String): Future[String] = {
    val iterable = onContentListeners.map(x => x(path)).toList
    Future.find(iterable)(x => x != null && x.nonEmpty).map(_.orNull)
  }

  def DETAILS_REPORT(report: IDetailsReport): Unit = onDetailsReportListeners.foreach(x => x(report))

  /**
    * Gets latest document version.
    *
    * @param uri
    */
  override def getLatestVersion(uri: String): Future[Int] = ???

  /**
    * Requests server for the document+position details.
    *
    * @param uri
    */
  override def getDetails(uri: String, position: Int): Future[IDetailsItem] = {
    Future.successful(null)
    // this.sendWithResponse("GET_DETAILS", ChangedPosition(uri,position))
  }

  /**
    * Reports to the server the position (cursor) change on the client.
    *
    * @param uri      - document uri.
    * @param position - curtsor position, starting from 0.
    */
  override def positionChanged(uri: String, position: Int): Unit = {
    this.send("CHANGE_POSITION", ChangedPosition(uri, position))
  }

  /**
    * Executes the specified details action.
    *
    * @param uri      - document uri
    * @param actionID - ID of the action to execute.
    * @param position - optional position in the document.
    *                 If not provided, the last reported by positionChanged method will be used.
    */
  override def executeDetailsAction(uri: String, actionID: String, position: Int): Future[Seq[SharedChangedDocument]] = {
    Future.successful(Seq())
  }

  /**
    * Calculates the list of executable actions avilable in the current context.
    *
    * @param uri      - document uri.
    * @param position - optional position in the document.
    *                 If not provided, the last reported by positionChanged method will be used.
    */
  override def calculateEditorContextActions(uri: String, position: Int): Future[Seq[IExecutableAction]] = {
    Future.successful(Seq())
  }

  /**
    * Calculates the list of all available actions.
    */
  override def allAvailableActions(): Future[Seq[IExecutableAction]] = {
    Future.successful(Seq())
  }

  /**
    * Executes the specified action. If action has UI, causes a consequent
    * server->client UI message resulting in onDisplayActionUI listener call.
    *
    * @param uri      - document uri
    * @param action   - action to execute.
    * @param position - optional position in the document.
    *                 If not provided, the last reported by positionChanged method will be used.
    */
  override def executeContextAction(uri: String,
                                    action: IExecutableAction,
                                    position: Int): Future[Seq[SharedChangedDocument]] = {
    Future.successful(Seq())
  }

  /**
    * Executes the specified action. If action has UI, causes a consequent
    * server->client UI message resulting in onDisplayActionUI listener call.
    *
    * @param uri      - document uri
    * @param actionID - actionID to execute.
    * @param position - optional position in the document.
    *                 If not provided, the last reported by positionChanged method will be used.
    */
  override def executeContextActionByID(uri: String,
                                        actionID: String,
                                        position: Int): Future[Seq[SharedChangedDocument]] = {
    Future.successful(Seq())
  }

  /**
    * Changes value of details item.
    *
    * @param uri      - uri of the document to change
    * @param position - position of the value to change
    * @param itemID   - identifier of the value to change
    * @param value    - new value
    */
  override def changeDetailValue(uri: String,
                                 position: Int,
                                 itemID: String,
                                 value: AnyVal): Future[Seq[SharedChangedDocument]] = {
    //this.send("CHANGE_DETAIL_VALUE", StructureReport.sharedToTransport(report))
    Future.successful(Seq())
  }
}
