package org.mulesoft.language.server.server.modules.editorManager

import org.mulesoft.language.common.dtoTypes.{IChangedDocument, IDocumentChangeExecutor}
import org.mulesoft.language.server.core.IServerIOCModule
import org.mulesoft.language.server.server.modules.commonInterfaces.IAbstractTextEditorWithCursor

/**
  * Editor manager
  */
trait IEditorManagerModule extends IServerIOCModule {

  val moduleId: String = "EDITOR_MANAGER"

  /**
    * Gets editor for URI
    * @param uri
    * @return
    */
  def getEditor(uri: String): Option[IAbstractTextEditorWithCursor]

  /**
    * Subscribes to document changes (including initial open document event)
    * @param listener
    * @param unsubscribe
    * @return
    */
  def onChangeDocument(listener: ((IChangedDocument) => Unit), unsubscribe: Boolean = false): Unit

  /**
    * Sets document change executor.
    * @param executor
    */
  def setDocumentChangeExecutor(executor: IDocumentChangeExecutor): Unit
}

object IEditorManagerModule {
  val moduleId: String = "EDITOR_MANAGER"
}