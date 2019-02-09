package org.mulesoft.language.server.modules.editorManager

import amf.core.remote.{Aml, Oas20, Raml08, Raml10}
import org.mulesoft.language.common.dtoTypes.{ChangedDocument, IDocumentChangeExecutor, OpenedDocument}
import org.mulesoft.language.server.common.utils.PathRefine
import org.mulesoft.language.server.core.AbstractServerModule

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.experimental.macros


class EditorManager extends AbstractServerModule with EditorManagerModule {

  val moduleDependencies: Array[String] = Array()

  var uriToEditor: mutable.Map[String, TextEditorInfo] = mutable.HashMap()

  var documentChangeListeners: mutable.Buffer[ChangedDocument => Unit] = ArrayBuffer()

  var documentChangeExecutor: Option[IDocumentChangeExecutor] = None

  override def launch(): Future[Unit] =
    super.launch()
      .map(_ => {
        this.connection.onOpenDocument(this.onOpenDocument)

        this.connection.onChangeDocument(this.documentWasChanged)

        this.connection.onChangePosition(this.onChangePosition)

        this.connection.onCloseDocument(this.onCloseDocument)
      })


  def onChangeDocument(listener: ChangedDocument => Unit, unsubscribe: Boolean = false): Unit = {

    if (unsubscribe) {

      val index = documentChangeListeners.indexOf(listener)
      if (index != -1) {
        documentChangeListeners.remove(index)
      }

    } else {

      documentChangeListeners += listener
    }
  }

  def getEditor(_uri: String): Option[TextEditorInfo] = {
    var uri = _uri
    uri = PathRefine.refinePath(uri, platform)
    this.connection.debugDetail(s"Asked for uri $uri, while having following editors registered: " +
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
        val result = this.uriToEditor.get(path)
        if (result.isDefined) {
          found = result
        }
      }

      if (found.isEmpty) {
        val path: String = uri.substring("file://".length).replace("%5C", "\\")
        val result = this.uriToEditor.get(path)
        if (result.isDefined) {
          found = result
        }
      }

      found
    } else {

      None
    }
  }

  def onOpenDocument(document: OpenedDocument): Unit = {

    this.connection.debug("Document is opened", "EditorManager", "onOpenDocument")

    val language = determineLanguage(document.uri, document.text)
    val syntax = determineSyntax(document.uri, document.text)

    this.uriToEditor(PathRefine.refinePath(document.uri, platform)) =
      new TextEditorInfo(document.uri, document.version, document.text, language, syntax, connection)

    this.documentChangeListeners.foreach { listener =>
      listener(ChangedDocument(document.uri, document.version, Some(document.text), None))
    }
  }

  def setDocumentChangeExecutor(executor: IDocumentChangeExecutor): Unit = {
    this.documentChangeExecutor = Some(executor)
  }

  def getDocumentChangeExecutor: Option[IDocumentChangeExecutor] = {

    this.documentChangeExecutor
  }

  def documentWasChanged(document: ChangedDocument) {
    connection.debug("Document is changed", "EditorManager", "onChangeDocument")

    connection.debugDetail("Uri is:\n " + document.uri, "EditorManager", "onChangeDocument")
    connection.debugDetail("Text is:\n " + document.text, "EditorManager", "onChangeDocument")

    val refinedUri = PathRefine.refinePath(document.uri, platform)
    uriToEditor.get(refinedUri)
      .foreach(current => {
        val currentVersion = current.version
        val currentText = current.text

        if (currentVersion == document.version) {
          this.connection.debugDetail("Version of the reported change is equal to the previous one",
            "EditorManager",
            "onChangeDocument")

          return
        }

        if (document.version < currentVersion && document.text.contains(currentText)) {
          this.connection.debugDetail("No changes detected", "EditorManager", "onChangeDocument")

          return
        }

      })

    val language = this.determineLanguage(refinedUri, document.text.get)
    val syntax = this.determineSyntax(refinedUri, document.text.get)

    uriToEditor(refinedUri) =
      new TextEditorInfo(refinedUri, document.version, document.text.get, language, syntax, connection)

    documentChangeListeners.foreach(listener => listener(document))
  }

  def onCloseDocument(uri: String): Unit = {
    uriToEditor.remove(PathRefine.refinePath(uri, platform))
  }

  def onChangePosition(uri: String, position: Int): Unit = {
    val editorOption = this.getEditor(uri)

    if (editorOption.isDefined) {
      editorOption.get.setCursorPosition(position)
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

  def determineSyntax(url: String, text: String): String =
    if (text.trim.startsWith("{")) "JSON" else "YAML"
}
