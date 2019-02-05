package org.mulesoft.language.server.core.connections

import org.mulesoft.als.suggestions.interfaces.ISuggestion
import org.mulesoft.language.common.dtoTypes._
import org.mulesoft.language.outline.structure.structureInterfaces.StructureNodeJSON
import org.mulesoft.language.server.common.configuration.IServerConfiguration

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.Future

trait AbstractServerConnection extends IServerConnection {

  protected var openDocumentListeners: mutable.Buffer[IOpenedDocument => Unit] = ArrayBuffer()

  protected var changeDocumentListeners: mutable.Buffer[IChangedDocument => Unit] = ArrayBuffer()

  protected var closeDocumentListeners: mutable.Buffer[String => Unit] = ArrayBuffer()

  protected var documentStructureListeners: mutable.Buffer[String => Future[Map[String, StructureNodeJSON]]] =
    ArrayBuffer()

  protected var documentCompletionListeners: mutable.Buffer[(String, Int) => Future[Seq[ISuggestion]]] = ArrayBuffer()

  protected var openDeclarationListeners: mutable.Buffer[(String, Int) => Future[Seq[ILocation]]] = ArrayBuffer()

  protected var findReferencesListeners: mutable.Buffer[(String, Int) => Future[Seq[ILocation]]] = ArrayBuffer()

  protected var markOccurrencesListeners: mutable.Buffer[(String, Int) => Future[Seq[IRange]]] = ArrayBuffer()

  protected var renameListeners: mutable.Buffer[(String, Int, String) => Future[Seq[IChangedDocument]]] = ArrayBuffer()

  protected var documentDetailsListeners: mutable.Buffer[(String, Int) => Future[Seq[ISuggestion]]] = ArrayBuffer()

  protected var changeDetailValueListeners
    : mutable.Buffer[(String, Int, String, AnyVal) => Future[Seq[IChangedDocument]]] =
    ArrayBuffer()

  protected var changePositionListeners: mutable.Buffer[(String, Int) => Unit] = ArrayBuffer()

  protected var serverConfigurationListeners: mutable.Buffer[IServerConfiguration => Unit] = ArrayBuffer()

  protected var calculateEditorContextActionsListeners
    : mutable.Buffer[(String, Int) => Future[Seq[IExecutableAction]]] =
    ArrayBuffer()

  protected var getAllEditorContextActionsListeners: mutable.Buffer[() => Future[Seq[IExecutableAction]]] =
    ArrayBuffer()

  protected var executeContextActionListeners: mutable.Buffer[(String, String, Int) => Future[Seq[IChangedDocument]]] =
    ArrayBuffer()

  protected var executeDetailsActionListeners: mutable.Buffer[(String, String, Int) => Future[Seq[IChangedDocument]]] =
    ArrayBuffer()

  /**
    * Adds a listener to document open notification. Must notify listeners in order of registration.
    *
    * @param listener    (document: IOpenedDocument) => Unit
    * @param unsubscribe - if true, existing listener will be removed. False by default.
    */
  def onOpenDocument(listener: IOpenedDocument => Unit, unsubscribe: Boolean = false): Unit = {

    this.addListener(this.openDocumentListeners, listener, unsubscribe)
  }

  /**
    * Adds a listener to document change notification. Must notify listeners in order of registration.
    *
    * @param listener    (document: IChangedDocument) => Unit
    * @param unsubscribe - if true, existing listener will be removed. False by default.
    */
  def onChangeDocument(listener: IChangedDocument => Unit, unsubscribe: Boolean = false): Unit = {

    this.addListener(this.changeDocumentListeners, listener, unsubscribe)
  }

  /**
    * Adds a listener to document close notification. Must notify listeners in order of registration.
    *
    * @param listener    (uri: String) => Unit
    * @param unsubscribe - if true, existing listener will be removed. False by default.
    */
  def onCloseDocument(listener: String => Unit, unsubscribe: Boolean = false): Unit = {

    this.addListener(this.closeDocumentListeners, listener, unsubscribe)
  }

  /**
    * Adds a listener to document completion request. Must notify listeners in order of registration.
    *
    * @param listener    (uri: String, position: Int) => Future[Seq[Suggestion] ]
    * @param unsubscribe - if true, existing listener will be removed. False by default.
    */
  def onDocumentCompletion(listener: (String, Int) => Future[Seq[ISuggestion]], unsubscribe: Boolean = false): Unit = {

    this.addListener(this.documentCompletionListeners, listener, unsubscribe)
  }

  /**
    * Adds a listener to document structure request. Must notify listeners in order of registration.
    *
    * @param listener    (uri: String) => Future[Map[String, StructureNodeJSON] ]
    * @param unsubscribe - if true, existing listener will be removed. False by default.
    */
  def onDocumentStructure(listener: (String) => Future[Map[String, StructureNodeJSON]],
                          unsubscribe: Boolean = false): Unit = {

    this.addListener(this.documentStructureListeners, listener, unsubscribe)
  }

  /**
    * Adds a listener to document open declaration request.  Must notify listeners in order of registration.
    *
    * @param listener    (uri: String, position: Int) => Future[Seq[ILocation] ]
    * @param unsubscribe - if true, existing listener will be removed. False by default.
    */
  def onOpenDeclaration(listener: (String, Int) => Future[Seq[ILocation]], unsubscribe: Boolean): Unit = {

    this.addListener(this.openDeclarationListeners, listener, unsubscribe)
  }

  /**
    * Adds a listener to document find references request.  Must notify listeners in order of registration.
    *
    * @param listener    (uri: string, position: number) => Future[Seq[ILocation] ]
    * @param unsubscribe - if true, existing listener will be removed. False by default.
    */
  def onFindReferences(listener: (String, Int) => Future[Seq[ILocation]], unsubscribe: Boolean): Unit = {

    this.addListener(this.findReferencesListeners, listener, unsubscribe)
  }

  /**
    * Marks occurrences of a symbol under the cursor in the current document.
    *
    * @param listener    (uri: String, position: Int) => Future[Seq[IRange] ]
    * @param unsubscribe - if true, existing listener will be removed. False by default.
    */
  def onMarkOccurrences(listener: (String, Int) => Future[Seq[IRange]], unsubscribe: Boolean = false): Unit = {

    this.addListener(this.markOccurrencesListeners, listener, unsubscribe)
  }

  /**
    * Finds the set of document (and non-document files) edits to perform the requested rename.
    *
    * @param listener    (uri: String, position: Int, newName: String) => Seq[IChangedDocument]
    * @param unsubscribe - if true, existing listener will be removed. False by default.
    */
  def onRename(listener: (String, Int, String) => Future[Seq[IChangedDocument]], unsubscribe: Boolean = false): Unit = {

    this.addListener(this.renameListeners, listener, unsubscribe)
  }

  /**
    * Adds a listener to document details value change request.
    *
    * @param listener    (uri: String, position: Int, itemID: String, value: String | Int | Boolean) => Future[Seq[IChangedDocument] ]
    * @param unsubscribe - if true, existing listener will be removed. False by default.
    */
  def onChangeDetailValue(listener: (String, Int, String, AnyVal) => Future[Seq[IChangedDocument]],
                          unsubscribe: Boolean = false): Unit = {

    this.addListener(this.changeDetailValueListeners, listener, unsubscribe)
  }

  /**
    * Adds a listener to document cursor position change notification.
    * Must notify listeners in order of registration.
    *
    * @param listener    (uri: string, position: number) => Unit
    * @param unsubscribe - if true, existing listener will be removed. False by default.
    */
  def onChangePosition(listener: (String, Int) => Unit, unsubscribe: Boolean = false): Unit = {

    this.addListener(this.changePositionListeners, listener, unsubscribe)
  }

  /**
    * Adds a listener for specific details action execution.
    *
    * @param listener    (uri: string, actionId: string, position: number)  => Future[Seq[IChangedDocument] ]. Position is optional, -1 if not available.
    * @param unsubscribe - if true, existing listener will be removed. False by default.
    *                    If not provided, the last reported by positionChanged method will be used.
    */
  def onExecuteDetailsAction(listener: (String, String, Int) => Future[Seq[IChangedDocument]],
                             unsubscribe: Boolean = false): Unit = {

    this.addListener(this.executeDetailsActionListeners, listener, unsubscribe)
  }

  /**
    * Calculates the list of executable actions available in the current context.
    *
    * @param listener    (uri: string, position?: number) => Future[Seq[IExecutableAction] ]. Position is optional, -1 if not available.
    * @param unsubscribe - if true, existing listener will be removed. False by default.
    */
  def onCalculateEditorContextActions(listener: (String, Int) => Future[Seq[IExecutableAction]],
                                      unsubscribe: Boolean = false): Unit = {

    this.addListener(this.calculateEditorContextActionsListeners, listener, unsubscribe)
  }

  /**
    * Calculates the list of all available executable actions.
    *
    * @param listener    () => Future[Seq[IExecutableAction] ]
    * @param unsubscribe - if true, existing listener will be removed. False by default.
    */
  def onAllEditorContextActions(listener: () => Future[Seq[IExecutableAction]], unsubscribe: Boolean = false): Unit = {

    this.addListener(this.getAllEditorContextActionsListeners, listener, unsubscribe)
  }

  /**
    * Adds a listener for specific action execution.
    *
    * @param listener    (uri: string, actionId: string, position?: number). Position is optional, -1 if not available.
    * @param unsubscribe - if true, existing listener will be removed. False by default.
    */
  def onExecuteContextAction(listener: (String, String, Int) => Future[Seq[IChangedDocument]],
                             unsubscribe: Boolean): Unit = {

    this.addListener(this.executeContextActionListeners, listener, unsubscribe)
  }

  /**
    * Sets server configuration.
    *
    * @param listener    (IServerConfiguration) => Unit
    * @param unsubscribe - if true, existing listener will be removed. False by default.
    */
  def onSetServerConfiguration(listener: IServerConfiguration => Unit, unsubscribe: Boolean = false): Unit = {

    this.addListener(this.serverConfigurationListeners, listener, unsubscribe)
  }

  def addListener[T](memberListeners: mutable.Buffer[T], listener: T, unsubscribe: Boolean = false): Unit = {

    if (unsubscribe) {

      val index = memberListeners.indexOf(listener)
      if (index != -1) {
        memberListeners.remove(index)
      }

    } else {

      memberListeners += listener

    }

  }

}
