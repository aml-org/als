package org.mulesoft.language.server.modules.suggestions

import amf.core.remote.{Raml10, Vendor}
import common.dtoTypes.Position
import org.mulesoft.als.suggestions
import org.mulesoft.als.suggestions.CompletionProvider
import org.mulesoft.als.suggestions.implementation.CompletionConfig
import org.mulesoft.als.suggestions.interfaces.{Suggestion, Syntax}
import org.mulesoft.high.level.implementation.AlsPlatform
import org.mulesoft.language.server.common.utils.PathRefine
import org.mulesoft.language.server.core.AbstractServerModule
import org.mulesoft.language.server.modules.editorManager.EditorManagerModule
import org.mulesoft.language.server.modules.hlastManager.{HlAstManager, IHLASTManagerModule}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SuggestionsManager extends AbstractServerModule {

  /**
    * Module ID
    */
  val moduleId: String = "SUGGESTIONS_MANAGER"

  val moduleDependencies: Array[String] = Array(
    EditorManagerModule.moduleId,
    IHLASTManagerModule.moduleId
  )

  val onDocumentCompletionListener: (String, Position) => Future[Seq[Suggestion]] = onDocumentCompletion

  protected def getEditorManager: EditorManagerModule = {

    this.getDependencyById(EditorManagerModule.moduleId).get
  }

  protected def getHLASTManager: HlAstManager = {
    getDependencyById(HlAstManager.moduleId).get
  }

  override def launch(): Future[Unit] =
    super
      .launch()
      .flatMap(_ => {
        this.connection.onDocumentCompletion(this.onDocumentCompletionListener)

        suggestions.Core.init()
      })

  override def stop(): Unit = {

    super.stop()

    this.connection.onDocumentCompletion(this.onDocumentCompletionListener, true)
  }

  protected def onDocumentCompletion(uri: String, position: Position): Future[Seq[Suggestion]] = {
    val refinedUri = PathRefine.refinePath(uri, platform)

    connection.debug(s"Calling for completion for uri $refinedUri and position $position",
                     "SuggestionsManager",
                     "onDocumentCompletion")

    getEditorManager
      .getEditor(refinedUri)
      .map(editor => {
        val syntax = if (editor.syntax == "YAML") Syntax.YAML else Syntax.JSON

        val startTime = System.currentTimeMillis()

        val originalText = editor.text
        val offset       = position.offset(originalText)
        val text         = suggestions.Core.prepareText(originalText, offset, syntax)

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

        buildCompletionProviderAST(text, originalText, refinedUri, offset, vendor, syntax, AlsPlatform.default) // todo find a way to instanciate some platform usings als protocol (initialization maybe?)
          .flatMap(provider => {
            provider.suggest
              .map(result => {
                this.connection.debug(s"Got ${result.length} proposals", "SuggestionsManager", "onDocumentCompletion")

                val endTime = System.currentTimeMillis()

                this.connection.debugDetail(s"It took ${endTime - startTime} milliseconds to complete",
                                            "ASTMaSuggestionsManagernager",
                                            "onDocumentCompletion")
                result
              })
          })
      })
      .getOrElse(Future.successful(Seq.empty[Suggestion]))
  }

  def buildCompletionProviderAST(text: String,
                                 unmodifiedContent: String,
                                 url: String,
                                 position: Int,
                                 vendor: Vendor,
                                 syntax: Syntax,
                                 platform: AlsPlatform): Future[CompletionProvider] = {

    getHLASTManager
      .forceBuildNewAST(url, text)
      .map(hlAST => {

        val baseName = url.substring(url.lastIndexOf('/') + 1)

        val astProvider = new ASTProvider(hlAST.rootASTUnit.rootNode, vendor, syntax, position)

        val editorStateProvider = new EditorStateProvider(text, url, baseName, position)

        val completionConfig = new CompletionConfig(platform)
          .withEditorStateProvider(editorStateProvider)
          .withAstProvider(astProvider)
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
