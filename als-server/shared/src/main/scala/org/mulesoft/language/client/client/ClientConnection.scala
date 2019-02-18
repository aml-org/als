package org.mulesoft.language.client.client

import common.dtoTypes.Position
import org.mulesoft.als.suggestions.interfaces.Suggestion
import org.mulesoft.language.common.dtoTypes._
import org.mulesoft.language.common.logger.Logger
import org.mulesoft.language.outline.structure.structureImpl.DocumentSymbol
import org.mulesoft.language.server.common.configuration.IServerConfiguration

import scala.concurrent.Future

/**
  * Connection for Scala and Java clients
  */
trait ClientConnection extends Logger {

  /**
    * Stops the server.
    */
  def stop(): Unit

  /**
    * Adds a listener for validation report coming from the server.
    *
    * @param listener
    */
  def onValidationReport(listener: ValidationReport => Unit, unsubscribe: Boolean = false): Unit

  /**
    * Instead of calling getStructure to get immediate structure report for the document,
    * this method allows to launch to the new structure reports when those are available.
    *
    * @param listener
    */
  def onStructureReport(listener: StructureReport => Unit, unsubscribe: Boolean = false): Unit

  /**
    * Notifies the server that document is opened.
    *
    * @param document
    */
  def documentOpened(document: OpenedDocument): Unit

  /**
    * Notified the server that document is closed.
    *
    * @param uri
    */
  def documentClosed(uri: String): Unit

  /**
    * Notifies the server that document is changed.
    *
    * @param document
    */
  def documentChanged(document: ChangedDocument): Unit

  /**
    * Requests server for the document structure.
    *
    * @param uri
    */
  def getStructure(uri: String): Future[List[DocumentSymbol]]

  /**
    * Requests server for the suggestions.
    *
    * @param uri      - document uri
    * @param position - offset in the document, starting from 0
    */
  def getSuggestions(uri: String, position: Position): Future[Seq[Suggestion]]

  /**
    * Requests server for the positions of the declaration of the element defined
    * at the given document position.
    *
    * @param uri      - document uri
    * @param position - position in the document
    */
  def openDeclaration(uri: String, position: Int): Future[Seq[ILocation]]

  /**
    * Requests server for the positions of the references of the element defined
    * at the given document position.
    *
    * @param uri      - document uri
    * @param position - position in the document
    */
  def findReferences(uri: String, position: Int): Future[Seq[ILocation]]

  /**
    * Requests server for the occurrences of the element defined
    * at the given document position.
    *
    * @param uri      - document uri
    * @param position - position in the document
    */
  def markOccurrences(uri: String, position: Int): Future[Seq[Range]]

  /**
    * Requests server for rename of the element
    * at the given document position.
    *
    * @param uri      - document uri
    * @param position - position in the document
    */
  def rename(uri: String, position: Int, newName: String): Future[Seq[ChangedDocument]]

  /**
    * Gets latest document version.
    *
    * @param uri
    */
  def getLatestVersion(uri: String): Future[Int]

  /**
    * Listens to the server requests for FS path existence, answering whether
    * a particular path exists on FS.
    */
  def onExists(listener: String => Future[Boolean], unsubscribe: Boolean = false): Unit

  /**
    * Listens to the server requests for directory contents, answering with a list
    * of files in a directory.
    */
  def onReadDir(listener: String => Future[Seq[String]], unsubscribe: Boolean = false): Unit

  /**
    * Listens to the server requests for directory check, answering whether
    * a particular path is a directory.
    */
  def onIsDirectory(listener: String => Future[Boolean], unsubscribe: Boolean = false): Unit

  /**
    * Listens to the server requests for file contents, answering what contents file has.
    */
  def onContent(listener: String => Future[String], unsubscribe: Boolean = false): Unit

  /**
    * Requests server for the document+position details.
    *
    * @param uri
    */
  def getDetails(uri: String, position: Int): Future[IDetailsItem]

  /**
    * Reports to the server the position (cursor) change on the client.
    *
    * @param uri      - document uri.
    * @param position - curtsor position, starting from 0.
    */
  def positionChanged(uri: String, position: Int): Unit

  /**
    * Report from the server that the new details are calculated
    * for particular document and position.
    *
    * @param listener
    */
  def onDetailsReport(listener: IDetailsReport => Unit, unsubscribe: Boolean = false): Unit

  /**
    * Executes the specified details action.
    *
    * @param uri      - document uri
    * @param actionID - ID of the action to execute.
    * @param position - optional position in the document.
    *                 If not provided, the last reported by positionChanged method will be used.
    */
  def executeDetailsAction(uri: String, actionID: String, position: Int): Future[Seq[ChangedDocument]]

  /**
    * Calculates the list of executable actions avilable in the current context.
    *
    * @param uri      - document uri.
    * @param position - optional position in the document.
    *                 If not provided, the last reported by positionChanged method will be used.
    */
  def calculateEditorContextActions(uri: String, position: Int): Future[Seq[IExecutableAction]]

  /**
    * Calculates the list of all available actions.
    */
  def allAvailableActions(): Future[Seq[IExecutableAction]]

  /**
    * Executes the specified action. If action has UI, causes a consequent
    * server->client UI message resulting in onDisplayActionUI listener call.
    *
    * @param uri      - document uri
    * @param action   - action to execute.
    * @param position - optional position in the document.
    *                 If not provided, the last reported by positionChanged method will be used.
    */
  def executeContextAction(uri: String, action: IExecutableAction, position: Int): Future[Seq[ChangedDocument]]

  /**
    * Executes the specified action. If action has UI, causes a consequent
    * server->client UI message resulting in onDisplayActionUI listener call.
    *
    * @param uri      - document uri
    * @param actionID - actionID to execute.
    * @param position - optional position in the document.
    *                 If not provided, the last reported by positionChanged method will be used.
    */
  def executeContextActionByID(uri: String, actionID: String, position: Int): Future[Seq[ChangedDocument]]

  /**
    * Adds a listener to display action UI.
    *
    * @param listener - accepts UI display request, should result in a promise
    *                 returning final UI state to be transferred to the server.
    */
  def onDisplayActionUI(listener: IUIDisplayRequest => Future[Any], unsubscribe: Boolean = false): Unit

  /**
    * Sets server configuration.
    *
    * @param serverSettings
    */
  def setServerConfiguration(serverSettings: IServerConfiguration): Unit

  /**
    * Changes value of details item.
    *
    * @param uri      - uri of the document to change
    * @param position - position of the value to change
    * @param itemID   - identifier of the value to change
    * @param value    - new value
    */
  def changeDetailValue(uri: String, position: Int, itemID: String, value: AnyVal): Future[Seq[ChangedDocument]]
}
