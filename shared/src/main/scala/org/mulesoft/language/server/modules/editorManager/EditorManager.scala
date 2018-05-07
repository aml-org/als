package org.mulesoft.language.server.server.modules.editorManager

import org.mulesoft.language.common.dtoTypes.{IChangedDocument, IDocumentChangeExecutor, IOpenedDocument, ITextEdit}
import org.mulesoft.language.server.core.{AbstractServerModule, IServerModule}

import org.mulesoft.language.server.server.modules.commonInterfaces.IAbstractTextEditorWithCursor

import scala.collection.mutable
import scala.collection.mutable.Buffer
import scala.collection.mutable.ArrayBuffer
import scala.util.{Success, Try}
import scala.language.experimental.macros

class EditorManager extends AbstractServerModule with IEditorManagerModule {

  val moduleDependencies: Array[String] = Array()

  val mainInterfaceName: Option[String] = None//Some(TypeName.get[IEditorManagerModule])

  var uriToEditor: mutable.Map[String, TextEditorInfo] = mutable.HashMap()

  var documentChangeListeners: Buffer[(IChangedDocument) => Unit] = ArrayBuffer()

  var documentChangeExecutor: Option[IDocumentChangeExecutor] = None

  override def launch(): Try[IServerModule] = {

    val superLaunch = super.launch()

    if(superLaunch.isSuccess) {

      this.connection.onOpenDocument(this.onOpenDocument _)

      this.connection.onChangeDocument(this.documentWasChanged _)

      this.connection.onChangePosition(this.onChangePosition _)

      this.connection.onCloseDocument(this.onCloseDocument _)

      Success(this)
    } else {
      superLaunch
    }
  }

  def onChangeDocument(listener: ((IChangedDocument) => Unit),
                       unsubscribe: Boolean = false): Unit = {

    if (unsubscribe) {

      val index = documentChangeListeners.indexOf(listener)
      if (index != -1) {
        documentChangeListeners.remove(index)
      }

    }
    else {

      documentChangeListeners += listener
    }
  }

  def getEditor(uri: String): Option[IAbstractTextEditorWithCursor] = {

    this.uriToEditor.get(uri)
  }

  def onOpenDocument(document: IOpenedDocument): Unit = {

    this.connection.debug("Document is opened", "EditorManager", "onOpenDocument")

    this.uriToEditor(document.uri) =
      new TextEditorInfo(document.uri, document.version,
        document.text, /*this,*/ this.connection)

    this.documentChangeListeners.foreach { listener =>
      listener(new IChangedDocument (

        document.uri,

        document.version,

        Some(document.text),

        None
      ))
    }
  }

  def setDocumentChangeExecutor(executor: IDocumentChangeExecutor): Unit = {
    this.documentChangeExecutor = Some(executor)
  }

  def getDocumentChangeExecutor: Option[IDocumentChangeExecutor] = {

    this.documentChangeExecutor
  }

  def documentWasChanged(document: IChangedDocument) {
	  this.connection.debug("Document is changed", "EditorManager", "onChangeDocument");
	  
	  this.connection.debugDetail("Text is:\n " + document.text, "EditorManager", "onChangeDocument");
	  
	  val current = this.uriToEditor.get(document.uri);
	  
	  if(current.isDefined) {
		  val currentVersion = current.get.version;
		  
		  val currentText = current.get.text;
		  
		  if(currentVersion == document.version) {
			  this.connection.debugDetail("Version of the reported change is equal to the previous one", "EditorManager", "onChangeDocument");
			  
			  return;
		  }
		  
		  if(document.version < currentVersion && document.text == currentText) {
			  this.connection.debugDetail("No changes detected", "EditorManager", "onChangeDocument");
			  
			  return;
		  }
	  }
	  
	  this.uriToEditor(document.uri) = new TextEditorInfo(document.uri, document.version, document.text.get, this.connection);
	  
      this.documentChangeListeners.foreach(listener => listener(document));
  }
  
  def onCloseDocument(uri: String): Unit = {

    this.uriToEditor.remove(uri)
  }

  def onChangePosition(uri: String, position: Int): Unit = {
    val editorOption = this.getEditor(uri)

    if (editorOption.isDefined) {
      editorOption.get.asInstanceOf[TextEditorInfo].setCursorPosition(position)
    }
  }
}
