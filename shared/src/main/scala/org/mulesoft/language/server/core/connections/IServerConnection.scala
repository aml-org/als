package org.mulesoft.language.server.core.connections

import org.mulesoft.als.suggestions.interfaces.ISuggestion
import org.mulesoft.language.server.common.configuration.IServerConfiguration
import org.mulesoft.language.common.logger._
import org.mulesoft.language.common.dtoTypes._
import org.mulesoft.language.outline.structure.structureInterfaces.StructureNodeJSON

import scala.concurrent.Future

/**
  * Connection, server modules use to communicate to the clients.
  */
trait IServerConnection extends ILogger {

  /**
    * Adds a listener to document open notification. Must notify listeners in order of registration.
    * @param listener (document: IOpenedDocument) => Unit
    * @param unsubscribe - if true, existing listener will be removed. False by default.
    */
  def onOpenDocument(listener: (IOpenedDocument) => Unit,
                     unsubscribe: Boolean = false): Unit

  /**
    * Adds a listener to document change notification. Must notify listeners in order of registration.
    * @param listener (document: IChangedDocument) => Unit
    * @param unsubscribe - if true, existing listener will be removed. False by default.
    */
  def onChangeDocument(listener: (IChangedDocument) => Unit,
                       unsubscribe: Boolean = false): Unit

  /**
    * Adds a listener to document close notification. Must notify listeners in order of registration.
    * @param listener (uri: String) => Unit
    * @param unsubscribe - if true, existing listener will be removed. False by default.
    */
  def onCloseDocument(listener: (String) => Unit,
                      unsubscribe: Boolean = false): Unit

  /**
    * Adds a listener to document completion request. Must notify listeners in order of registration.
    * @param listener (uri: String, position: Int) => Future[Seq[Suggestion] ]
    * @param unsubscribe - if true, existing listener will be removed. False by default.
    */
  def onDocumentCompletion(listener: (String, Int) => Future[Seq[ISuggestion]],
                           unsubscribe: Boolean = false): Unit

  /**
    * Adds a listener to document structure request. Must notify listeners in order of registration.
    * @param listener (uri: String) => Future[Map[String, StructureNodeJSON] ]
    * @param unsubscribe - if true, existing listener will be removed. False by default.
    */
  def onDocumentStructure(listener: (String) => Future[Map[String, StructureNodeJSON]],
                          unsubscribe: Boolean = false): Unit

  /**
    * Adds a listener to document open declaration request.  Must notify listeners in order of registration.
    * @param listener (uri: String, position: Int) => Future[Seq[ILocation] ]
    * @param unsubscribe - if true, existing listener will be removed. False by default.
    */
  def onOpenDeclaration(listener: (String, Int) => Future[Seq[ILocation]],
                        unsubscribe: Boolean): Unit

  /**
    * Adds a listener to document find references request.  Must notify listeners in order of registration.
    * @param listener (uri: string, position: number) => Future[Seq[ILocation] ]
    * @param unsubscribe - if true, existing listener will be removed. False by default.
    */
  def onFindReferences(listener: (String, Int) => Future[Seq[ILocation]],
                       unsubscribe: Boolean): Unit

  /**
    * Reports latest validation results
    * @param report
    */
  def validated(report: IValidationReport): Unit

  /**
    * Reports new calculated structure when available.
    * @param report - structure report.
    */
  def structureAvailable(report: IStructureReport): Unit

  /**
    * Marks occurrences of a symbol under the cursor in the current document.
    * @param listener (uri: String, position: Int) => Future[Seq[IRange] ]
    * @param unsubscribe - if true, existing listener will be removed. False by default.
    */
  def onMarkOccurrences(listener: (String, Int) => Future[Seq[IRange]],
                        unsubscribe: Boolean = false): Unit

  /**
    * Finds the set of document (and non-document files) edits to perform the requested rename.
    * @param listener (uri: String, position: Int, newName: String) => Seq[IChangedDocument]
    * @param unsubscribe - if true, existing listener will be removed. False by default.
    */
  def onRename(listener: (String, Int, String) => Future[Seq[IChangedDocument]],
               unsubscribe: Boolean = false): Unit

  /**
    * Returns whether path/url exists.
    * @param path
    */
  def exists(path: String): Future[Boolean]

  /**
    * Returns directory content list.
    * @param path
    */
  def readDir(path: String): Future[Seq[String]]

  /**
    * Returns whether path/url represents a directory
    * @param path
    */
  def isDirectory(path: String): Future[Boolean]

  /**
    * File contents by full path/url.
    * @param fullPath
    */
  def content(fullPath: String): Future[String]

  /**
    * Adds a listener to document details request. Must notify listeners in order of registration.
    * @param listener (uri: String, position: Int) => Future[DetailsItemJSON]
    * @param unsubscribe - if true, existing listener will be removed. False by default.
    */
  def onDocumentDetails(listener: (String, Int) => Future[IDetailsItem],
                        unsubscribe: Boolean = false): Unit

  /**
    * Adds a listener to document details value change request.
    * @param listener (uri: String, position: Int, itemID: String, value: String | Int | Boolean) => Future[Seq[IChangedDocument] ]
    * @param unsubscribe - if true, existing listener will be removed. False by default.
    */
  def onChangeDetailValue(listener: (String, Int, String, AnyVal) => Future[Seq[IChangedDocument]],
                          unsubscribe: Boolean = false): Unit

  /**
    * Adds a listener to document cursor position change notification.
    * Must notify listeners in order of registration.
    * @param listener (uri: string, position: number) => Unit
    * @param unsubscribe - if true, existing listener will be removed. False by default.
    */
  def onChangePosition(listener: (String, Int) => Unit,
                       unsubscribe: Boolean = false): Unit

  /**
    * Reports new calculated details when available.
    * @param report - details report.
    */
  def detailsAvailable(report: IDetailsReport): Unit

  /**
    * Adds a listener for specific details action execution.
    * @param listener (uri: string, actionId: string, position: number)  => Future[Seq[IChangedDocument] ]. Position is optional, -1 if not available.
    * @param unsubscribe - if true, existing listener will be removed. False by default.
    * If not provided, the last reported by positionChanged method will be used.
    */
  def onExecuteDetailsAction(listener: (String, String, Int) => Future[Seq[IChangedDocument]],
                             unsubscribe: Boolean = false): Unit

  /**
    * Calculates the list of executable actions available in the current context.
    *
    * @param listener (uri: string, position?: number) => Future[Seq[IExecutableAction] ]. Position is optional, -1 if not available.
    * @param unsubscribe - if true, existing listener will be removed. False by default.
    */
  def onCalculateEditorContextActions(listener: (String, Int) => Future[Seq[IExecutableAction]],
                                      unsubscribe: Boolean = false): Unit

  /**
    * Calculates the list of all available executable actions.
    * @param listener () => Future[Seq[IExecutableAction] ]
    * @param unsubscribe - if true, existing listener will be removed. False by default.
    */
  def onAllEditorContextActions(listener: () => Future[Seq[IExecutableAction]],
                                unsubscribe: Boolean = false): Unit

  /**
    * Adds a listener for specific action execution.
    * @param listener (uri: string, actionId: string, position?: number). Position is optional, -1 if not available.
    * @param unsubscribe - if true, existing listener will be removed. False by default.
    */
  def onExecuteContextAction(listener: ((String, String, Int) => Future[Seq[IChangedDocument]]),
                             unsubscribe: Boolean): Unit
  /**
    * Adds a listener to display action UI.
    * @param uiDisplayRequest - display request
    * @return final UI state.
    */
  def displayActionUI(uiDisplayRequest: IUIDisplayRequest): Future[Any]

  /**
    * Sets server configuration.
    * @param listener (IServerConfiguration) => Unit
    * @param unsubscribe - if true, existing listener will be removed. False by default.
    */
  def onSetServerConfiguration(listener: (IServerConfiguration) => Unit,
                               unsubscribe: Boolean = false): Unit
}
