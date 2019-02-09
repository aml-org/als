package org.mulesoft.language.server.modules.editorManager

import org.mulesoft.language.common.dtoTypes.{ChangedDocument, IDocumentChangeExecutor}
import org.mulesoft.language.server.core.ServerIOCModule

/**
  * Editor manager
  */
trait EditorManagerModule extends ServerIOCModule {

  val moduleId: String = "EDITOR_MANAGER"

  /**
    * Gets editor for URI
    *
    * @param uri
    * @return
    */
  def getEditor(uri: String): Option[TextEditorInfo]

  /**
    * Subscribes to document changes (including initial open document event)
    *
    * @param listener
    * @param unsubscribe
    * @return
    */
  def onChangeDocument(listener: ChangedDocument => Unit, unsubscribe: Boolean = false): Unit

  /**
    * Sets document change executor.
    *
    * @param executor
    */
  def setDocumentChangeExecutor(executor: IDocumentChangeExecutor): Unit
}

object EditorManagerModule {
  val moduleId: String = "EDITOR_MANAGER"
}