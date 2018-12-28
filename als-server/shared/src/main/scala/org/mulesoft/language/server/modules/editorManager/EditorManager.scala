package org.mulesoft.language.server.modules.editorManager

import amf.core.remote.Raml08
import amf.core.remote.Raml10
import amf.core.remote.Oas20
import amf.core.remote.Aml
import org.mulesoft.als.suggestions.interfaces.Syntax
import org.mulesoft.language.common.dtoTypes.{IChangedDocument, IDocumentChangeExecutor, IOpenedDocument, ITextEdit}
import org.mulesoft.language.server.core.{AbstractServerModule, IServerModule}
import org.mulesoft.language.server.modules.commonInterfaces.IAbstractTextEditorWithCursor
import org.mulesoft.platform.PathRefine

import scala.collection.mutable
import scala.collection.mutable.Buffer
import scala.collection.mutable.ArrayBuffer
import scala.util.{Success, Try}
import scala.language.experimental.macros

class EditorManager extends AbstractServerModule with IEditorManagerModule {

  val moduleDependencies: Array[String] = Array()

  var uriToEditor: mutable.Map[String, TextEditorInfo] = mutable.HashMap()

  var documentChangeListeners: Buffer[(IChangedDocument) => Unit] = ArrayBuffer()

  var documentChangeExecutor: Option[IDocumentChangeExecutor] = None

  override def launch(): Try[IServerModule] = {

    val superLaunch = super.launch()

    if (superLaunch.isSuccess) {

      this.connection.onOpenDocument(this.onOpenDocument _)

      this.connection.onChangeDocument(this.documentWasChanged _)

      this.connection.onChangePosition(this.onChangePosition _)

      this.connection.onCloseDocument(this.onCloseDocument _)

      Success(this)
    } else {
      superLaunch
    }
  }

  def onChangeDocument(listener: ((IChangedDocument) => Unit), unsubscribe: Boolean = false): Unit = {

    if (unsubscribe) {

      val index = documentChangeListeners.indexOf(listener)
      if (index != -1) {
        documentChangeListeners.remove(index)
      }

    } else {

      documentChangeListeners += listener
    }
  }

//  def getEditor(uri: String): Option[IAbstractTextEditorWithCursor] = {
//
//    this.connection.debugDetail(s"Asked for uri ${uri}, while having following editors registered: " +
//      this.uriToEditor.keys.mkString(","),
//      "EditorManager", "onOpenDocument")
//
//    val directResult = this.uriToEditor.get(uri)
//
//    if (directResult.isDefined) {
//
//      directResult
//    } else if (uri.startsWith("file://") || uri.startsWith("FILE://")) {
//
//      val path = uri.substring("file://".length)
//      val result = this.uriToEditor.get(path)
//      println(s"Checking uri $path and getting result: $result")
//      result
//    } else {
//
//      None
//    }
//  }

  def getEditor(_uri: String): Option[IAbstractTextEditorWithCursor] = {
    var uri = _uri
    uri = PathRefine.refinePath(uri, platform)
    this.connection.debugDetail(s"Asked for uri ${uri}, while having following editors registered: " +
                                  this.uriToEditor.keys.mkString(","),
                                "EditorManager",
                                "onOpenDocument")

    val directResult = this.uriToEditor.get(uri)

    if (directResult.isDefined) {

      directResult
    } else if (uri.startsWith("file://") || uri.startsWith("FILE://")) {

      var found: Option[TextEditorInfo] = None

      if (uri.startsWith("file:///") || uri.startsWith("FILE:///")) {
        val path: String = uri.substring("file:///".length).replace("%5C", "\\")
        val result       = this.uriToEditor.get(path)
        if (result.isDefined) {
          found = result
        }
      }

      if (found.isEmpty) {
        val path: String = uri.substring("file://".length).replace("%5C", "\\")
        val result       = this.uriToEditor.get(path)
        if (result.isDefined) {
          found = result
        }
      }

      found
    } else {

      None
    }
  }

  def onOpenDocument(document: IOpenedDocument): Unit = {

    this.connection.debug("Document is opened", "EditorManager", "onOpenDocument")

    val language = this.determineLanguage(document.uri, document.text)
    val syntax   = this.determineSyntax(document.uri, document.text)

    this.uriToEditor(PathRefine.refinePath(document.uri, platform)) =
      new TextEditorInfo(document.uri, document.version, document.text, language, syntax, /*this,*/ this.connection)

    this.documentChangeListeners.foreach { listener =>
      listener(
        new IChangedDocument(
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

    val refinedUri = PathRefine.refinePath(document.uri, platform)
    val current    = this.uriToEditor.get(refinedUri);

    if (current.isDefined) {
      val currentVersion = current.get.version;

      val currentText = current.get.text;

      if (currentVersion == document.version) {
        this.connection.debugDetail("Version of the reported change is equal to the previous one",
                                    "EditorManager",
                                    "onChangeDocument");

        return;
      }

      if (document.version < currentVersion && document.text == currentText) {
        this.connection.debugDetail("No changes detected", "EditorManager", "onChangeDocument");

        return;
      }
    }

    val language = this.determineLanguage(refinedUri, document.text.get)
    val syntax   = this.determineSyntax(refinedUri, document.text.get)

    this.uriToEditor(refinedUri) =
      new TextEditorInfo(refinedUri, document.version, document.text.get, language, syntax, this.connection);

    this.documentChangeListeners.foreach(listener => listener(document));
  }

  def onCloseDocument(uri: String): Unit = {

    this.uriToEditor.remove(PathRefine.refinePath(uri, platform))
  }

  def onChangePosition(uri: String, position: Int): Unit = {
    val editorOption = this.getEditor(uri)

    if (editorOption.isDefined) {
      editorOption.get.asInstanceOf[TextEditorInfo].setCursorPosition(position)
    }
  }

  def determineLanguage(url: String, text: String): String = {

    if (url.endsWith(".raml")) {
      if (text.startsWith("#%RAML 1.0")) {
        Raml10.toString
      } else {
        Raml08.toString
      }
    } else if ((url.endsWith(".yaml") || url.endsWith(".yml")) && text.startsWith("#%")) {
      Aml.toString
    } else {
      Oas20.toString
    }
  }

  def determineSyntax(url: String, text: String): String = {

    if (text.trim.startsWith("{")) {
      "JSON"
    } else {
      "YAML"
    }
  }
}
