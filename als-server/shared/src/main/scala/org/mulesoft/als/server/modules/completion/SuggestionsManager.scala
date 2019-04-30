package org.mulesoft.als.server.modules.completion

import amf.core.remote.{Raml10, Vendor}
import common.dtoTypes.Position
import org.mulesoft.als.server.RequestModule
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.modules.common.LspConverter
import org.mulesoft.als.server.modules.hlast.HlAstManager
import org.mulesoft.als.server.textsync.TextDocumentManager
import org.mulesoft.als.suggestions
import org.mulesoft.als.suggestions.CompletionProvider
import org.mulesoft.als.suggestions.client.Suggestions
import org.mulesoft.als.suggestions.implementation.CompletionConfig
import org.mulesoft.als.suggestions.interfaces.{Suggestion, Syntax}
import org.mulesoft.high.level.implementation.AlsPlatform
import org.mulesoft.als.server.util.PathRefine
import org.mulesoft.lsp.ConfigType
import org.mulesoft.lsp.edit.TextEdit
import org.mulesoft.lsp.feature.RequestHandler
import org.mulesoft.lsp.feature.completion._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SuggestionsManager(private val textDocumentManager: TextDocumentManager,
                         private val hlAstManager: HlAstManager,
                         private val platform: AlsPlatform,
                         private val logger: Logger)
    extends RequestModule[CompletionClientCapabilities, CompletionOptions] {
  override val `type`: ConfigType[CompletionClientCapabilities, CompletionOptions] = CompletionConfigType

  def completionItem(suggestion: Suggestion): CompletionItem = {
    suggestion.range match {
      case Some(r) =>
        CompletionItem(
          suggestion.displayText,
          textEdit = Some(TextEdit(LspConverter.toLspRange(r), suggestion.text)),
          detail = Some(suggestion.description)
        )
      case _ =>
        CompletionItem(
          suggestion.displayText,
          insertText = Some(suggestion.text),
          detail = Some(suggestion.description)
        )
    }
  }

  override val getRequestHandlers: Seq[RequestHandler[_, _]] = Seq(
    new RequestHandler[CompletionParams, Either[Seq[CompletionItem], CompletionList]] {
      override def `type`: CompletionRequestType.type = CompletionRequestType

      override def apply(params: CompletionParams): Future[Either[Seq[CompletionItem], CompletionList]] = {
        onDocumentCompletion(params.textDocument.uri, LspConverter.toPosition(params.position))
          .map(_.map(completionItem))
          .map(Left.apply)
      }
    }
  )

  override def applyConfig(config: Option[CompletionClientCapabilities]): CompletionOptions = CompletionOptions()

  val onDocumentCompletionListener: (String, Position) => Future[Seq[Suggestion]] = onDocumentCompletion

  override def initialize(): Future[Unit] =
    suggestions.Core.init()

  protected def onDocumentCompletion(uri: String, position: Position): Future[Seq[Suggestion]] = {
    val refinedUri = PathRefine.refinePath(uri, platform)

    logger.debug(s"Calling for completion for uri $uri and position $position",
                 "SuggestionsManager",
                 "onDocumentCompletion")

    textDocumentManager
      .getTextDocument(uri)
      .map(editor => {
        val syntax = if (editor.syntax == "YAML") Syntax.YAML else Syntax.JSON

        val startTime = System.currentTimeMillis()

        val originalText = editor.text
        val offset       = position.offset(originalText)
        val text         = suggestions.Core.prepareText(originalText, offset, syntax)

        val vendorOption   = Vendor.unapply(editor.language)
        val vendor: Vendor = vendorOption.getOrElse(Raml10)
        //      this.logger.debug("Vendor is: " + vendor,
        //        "SuggestionsManager", "onDocumentCompletion")

        //TODO add unapply to suggestion's Syntax

        //      this.logger.debug(s"TEXT:",
        //        "SuggestionsManager", "onDocumentCompletion")
        //      this.logger.debug(text,
        //        "SuggestionsManager", "onDocumentCompletion")
        //
        //      this.logger.debug("Completion substring: " + text.substring(position-10, position),
        //        "SuggestionsManager", "onDocumentCompletion")

        buildCompletionProviderAST(text, originalText, uri, refinedUri, offset, vendor, syntax, AlsPlatform.default) // todo find a way to instanciate some platform usings als protocol (initialization maybe?)
          .flatMap(provider => {
            provider.suggest
              .map(result => {
                this.logger.debug(s"Got ${result.length} proposals", "SuggestionsManager", "onDocumentCompletion")

                val endTime = System.currentTimeMillis()

                this.logger.debugDetail(s"It took ${endTime - startTime} milliseconds to complete",
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
                                 uri: String,
                                 refinedUri: String,
                                 position: Int,
                                 vendor: Vendor,
                                 syntax: Syntax,
                                 platform: AlsPlatform): Future[CompletionProvider] = {

    hlAstManager
      .forceBuildNewAST(uri, text)
      .map(hlAST => {

        val baseName = refinedUri.substring(refinedUri.lastIndexOf('/') + 1)

        val astProvider = new ASTProvider(hlAST.rootASTUnit.rootNode, vendor, syntax, position)

        val editorStateProvider = new EditorStateProvider(text, refinedUri, baseName, position)

        val completionConfig = new CompletionConfig(platform)
          .withEditorStateProvider(editorStateProvider)
          .withAstProvider(astProvider)
          .withOriginalContent(unmodifiedContent)

        CompletionProvider().withConfig(completionConfig)
      })
      .recoverWith {
        case e: Throwable =>
          println(e)
          Future.successful(
            Suggestions.buildCompletionProviderNoAST(unmodifiedContent, refinedUri, position, platform))
        case any =>
          println(any)
          Future.failed(new Error("Failed to construct CompletionProvider"))
      }

  }
}
