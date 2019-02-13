package org.mulesoft.language.server.core.connections

import org.mulesoft.als.suggestions.interfaces.Suggestion
import org.mulesoft.language.common.dtoTypes._
import org.mulesoft.language.outline.structure.structureImpl.DocumentSymbol
import org.mulesoft.language.outline.structure.structureInterfaces.StructureNodeJSON
import org.mulesoft.language.server.common.configuration.IServerConfiguration

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.Future

trait AbstractServerConnection extends ServerConnection with ServerNotifier {

  protected var openDocumentListeners: mutable.Buffer[OpenedDocument => Unit] = ArrayBuffer()

  protected var changeDocumentListeners: mutable.Buffer[ChangedDocument => Unit] = ArrayBuffer()

  protected var closeDocumentListeners: mutable.Buffer[String => Unit] = ArrayBuffer()

  protected var documentStructureListeners: mutable.Buffer[String => Future[List[DocumentSymbol]]] = ArrayBuffer()

  protected var documentCompletionListeners: mutable.Buffer[(String, Position) => Future[Seq[Suggestion]]] = ArrayBuffer()

  protected var documentDetailsListeners: mutable.Buffer[(String, Position) => Future[Seq[Suggestion]]] = ArrayBuffer()

  protected var openDeclarationListeners: mutable.Buffer[(String, Int) => Future[Seq[ILocation]]] = ArrayBuffer()

  protected var findReferencesListeners: mutable.Buffer[(String, Int) => Future[Seq[ILocation]]] = ArrayBuffer()

  protected var markOccurrencesListeners: mutable.Buffer[(String, Int) => Future[Seq[Range]]] = ArrayBuffer()

  protected var renameListeners: mutable.Buffer[(String, Int, String) => Future[Seq[ChangedDocument]]] = ArrayBuffer()

  protected var changeDetailValueListeners: mutable.Buffer[(String, Int, String, AnyVal) => Future[Seq[ChangedDocument]]] =
    ArrayBuffer()

  protected var changePositionListeners: mutable.Buffer[(String, Int) => Unit] = ArrayBuffer()

  protected var serverConfigurationListeners: mutable.Buffer[IServerConfiguration => Unit] = ArrayBuffer()

  protected var calculateEditorContextActionsListeners
    : mutable.Buffer[(String, Int) => Future[Seq[IExecutableAction]]] =
    ArrayBuffer()

  protected var getAllEditorContextActionsListeners: mutable.Buffer[() => Future[Seq[IExecutableAction]]] =
    ArrayBuffer()

  protected var executeContextActionListeners: mutable.Buffer[(String, String, Int) => Future[Seq[ChangedDocument]]] =
    ArrayBuffer()

  protected var executeDetailsActionListeners: mutable.Buffer[(String, String, Int) => Future[Seq[ChangedDocument]]] =
    ArrayBuffer()

  /**
    * Adds a listener to document open notification. Must notify listeners in order of registration.
    *
    * @param listener    (document: IOpenedDocument) => Unit
    * @param unsubscribe - if true, existing listener will be removed. False by default.
    */
  def onOpenDocument(listener: OpenedDocument => Unit, unsubscribe: Boolean = false): Unit =
    addListener(openDocumentListeners, listener, unsubscribe)

  override def notifyDocumentOpened(openedDocument: OpenedDocument): Unit =
    openDocumentListeners.foreach(_.apply(openedDocument))

  /**
    * Adds a listener to document change notification. Must notify listeners in order of registration.
    *
    * @param listener    (document: IChangedDocument) => Unit
    * @param unsubscribe - if true, existing listener will be removed. False by default.
    */
  def onChangeDocument(listener: ChangedDocument => Unit, unsubscribe: Boolean = false): Unit =
    addListener(changeDocumentListeners, listener, unsubscribe)

  override def notifyDocumentChanged(changedDocument: ChangedDocument): Unit =
    changeDocumentListeners.foreach(_.apply(changedDocument))

  /**
    * Adds a listener to document close notification. Must notify listeners in order of registration.
    *
    * @param listener    (uri: String) => Unit
    * @param unsubscribe - if true, existing listener will be removed. False by default.
    */
  def onCloseDocument(listener: String => Unit, unsubscribe: Boolean = false): Unit =
    addListener(closeDocumentListeners, listener, unsubscribe)

  override def notifyDocumentClosed(path: String): Unit =
    closeDocumentListeners.foreach(_.apply(path))

  /**
    * Adds a listener to document completion request. Must notify listeners in order of registration.
    *
    * @param listener    (uri: String, position: Int) => Future[Seq[Suggestion] ]
    * @param unsubscribe - if true, existing listener will be removed. False by default.
    */
  def onDocumentCompletion(listener: (String, Position) => Future[Seq[Suggestion]], unsubscribe: Boolean = false): Unit =
    addListener(documentCompletionListeners, listener, unsubscribe)

  override def notifyDocumentCompletion(uri: String, offset: Position): Future[Seq[Suggestion]] =
    documentCompletionListeners.headOption.map(_.apply(uri, offset)).getOrElse(Future.successful(Seq()))

  /**
    * Adds a listener to document structure request. Must notify listeners in order of registration.
    *
    * @param listener    (uri: String) => Future[Map[String, StructureNodeJSON] ]
    * @param unsubscribe - if true, existing listener will be removed. False by default.
    */
  def onDocumentStructure(listener: (String) => Future[List[DocumentSymbol]], unsubscribe: Boolean = false): Unit = {

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
  def onMarkOccurrences(listener: (String, Int) => Future[Seq[Range]], unsubscribe: Boolean = false): Unit = {
    this.addListener(this.markOccurrencesListeners, listener, unsubscribe)
  }

  /**
    * Finds the set of document (and non-document files) edits to perform the requested rename.
    *
    * @param listener    (uri: String, position: Int, newName: String) => Seq[IChangedDocument]
    * @param unsubscribe - if true, existing listener will be removed. False by default.
    */
  def onRename(listener: (String, Int, String) => Future[Seq[ChangedDocument]], unsubscribe: Boolean = false): Unit = {
    this.addListener(this.renameListeners, listener, unsubscribe)
  }

  /**
    * Adds a listener to document details value change request.
    *
    * @param listener    (uri: String, position: Int, itemID: String, value: String | Int | Boolean) => Future[Seq[IChangedDocument] ]
    * @param unsubscribe - if true, existing listener will be removed. False by default.
    */
  def onChangeDetailValue(listener: (String, Int, String, AnyVal) => Future[Seq[ChangedDocument]],
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
  def onExecuteDetailsAction(listener: (String, String, Int) => Future[Seq[ChangedDocument]],
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
  def onExecuteContextAction(listener: (String, String, Int) => Future[Seq[ChangedDocument]],
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
