package org.mulesoft.language.server.modules.suggestions

import amf.core.remote.{Raml10, Vendor}
import org.mulesoft.language.common.dtoTypes._
import org.mulesoft.language.server.core.{AbstractServerModule, IServerModule}
import org.mulesoft.language.server.modules.hlastManager.{HLASTmanager, IHLASTListener, IHLASTManagerModule}
import org.mulesoft.language.server.modules.commonInterfaces.{IAbstractTextEditorWithCursor, IEditorTextBuffer, IPoint}
import org.mulesoft.language.server.modules.editorManager.IEditorManagerModule

import scala.collection.mutable.Buffer
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success, Try}
import scala.concurrent.ExecutionContext.Implicits.global
import org.mulesoft.high.level.Core
import org.mulesoft.high.level.interfaces.IProject
import org.mulesoft.language.common.dtoTypes.IStructureReport
import org.mulesoft.als.suggestions.implementation.CompletionConfig
import org.mulesoft.als.suggestions.{CompletionProvider, PlatformBasedExtendedFSProvider}
import org.mulesoft.als.suggestions.interfaces.{ISuggestion, Syntax}
import org.mulesoft.language.server.common.utils.PathRefine

import scala.collection.Map

class SuggestionsManager extends AbstractServerModule {

  /**
    * Module ID
    */
  val moduleId: String = "SUGGESTIONS_MANAGER"

  val moduleDependencies: Array[String] = Array(
    IEditorManagerModule.moduleId,
    IHLASTManagerModule.moduleId
  )

  val onDocumentCompletionListener: (String, Int) => Future[Seq[ISuggestion]] = onDocumentCompletion

  protected def getEditorManager: IEditorManagerModule = {

    this.getDependencyById(IEditorManagerModule.moduleId).get
  }

  protected def getHLASTManager: HLASTmanager = {

    this.getDependencyById(HLASTmanager.moduleId).get
  }

  override def launch(): Try[IServerModule] = {

    val superLaunch = super.launch()

    if (superLaunch.isSuccess) {

      org.mulesoft.als.suggestions.Core.init()

      this.connection.onDocumentCompletion(this.onDocumentCompletionListener)

      Success(this)
    } else {

      superLaunch
    }
  }

  override def stop(): Unit = {

    super.stop()

    this.connection.onDocumentCompletion(this.onDocumentCompletionListener, true)
  }

  protected def onDocumentCompletion(_url: String, position: Int): Future[Seq[ISuggestion]] = {

    val url = PathRefine.refinePath(_url, platform)
    this.connection.debug(s"Calling for completion for uri ${url} and position ${position}",
                          "SuggestionsManager",
                          "onDocumentCompletion")

    val editorOption: Option[IAbstractTextEditorWithCursor] =
      this.getEditorManager.getEditor(url)

    if (editorOption.isDefined) {
      val editor = editorOption.get

      val syntax = if (editor.syntax == "YAML") Syntax.YAML else Syntax.JSON

      val startTime = System.currentTimeMillis()

      val text = org.mulesoft.als.suggestions.Core.prepareText(editor.text, position, syntax)

      val vendorOption   = Vendor.unapply(editor.language)
      val vendor: Vendor = vendorOption.getOrElse(Raml10)
//      this.connection.debug("Vendor is: " + vendor,
//        "SuggestionsManager", "onDocumentCompletion")

      //TODO add unapply to suggestion's Syntax

//      this.connection.debug(s"TEXT:",
//        "SuggestionsManager", "onDocumentCompletion")
//      this.connection.debug(text,
//        "SuggestionsManager", "onDocumentCompletion")
//
//      this.connection.debug("Completion substring: " + text.substring(position-10, position),
//        "SuggestionsManager", "onDocumentCompletion")

      this
        .buildCompletionProviderAST(text, editor.text, url, position, vendor, syntax)
        .flatMap(provider => {

          provider.suggest.map(result => {
            this.connection.debug(s"Got ${result.length} proposals", "SuggestionsManager", "onDocumentCompletion")

            val endTime = System.currentTimeMillis()

            this.connection.debugDetail(s"It took ${endTime - startTime} milliseconds to complete",
                                        "ASTMaSuggestionsManagernager",
                                        "onDocumentCompletion")

            result
          })

        })
    } else {
      Promise.successful(Seq.empty[ISuggestion]).future
    }
  }

  def buildCompletionProviderAST(text: String,
                                 unmodifiedContent: String,
                                 url: String,
                                 position: Int,
                                 vendor: Vendor,
                                 syntax: Syntax): Future[CompletionProvider] = {

    this.getHLASTManager
      .forceBuildNewAST(url, text)
      .map(hlAST => {

        val baseName = url.substring(url.lastIndexOf('/') + 1)

        val astProvider = new ASTProvider(hlAST.rootASTUnit.rootNode, vendor, syntax, position)

        val editorStateProvider = new EditorStateProvider(text, url, baseName, position)

        val platformFSProvider = new PlatformBasedExtendedFSProvider(this.platform)

        val completionConfig = new CompletionConfig()
          .withEditorStateProvider(editorStateProvider)
          .withAstProvider(astProvider)
          .withFsProvider(platformFSProvider)
          .withOriginalContent(unmodifiedContent)

        CompletionProvider().withConfig(completionConfig)
      })

  }
}

object SuggestionsManager {

  /**
    * Module ID
    */
  val moduleId: String = "SUGGESTIONS_MANAGER"
}
