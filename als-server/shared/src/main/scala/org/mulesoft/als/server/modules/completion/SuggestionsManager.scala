package org.mulesoft.als.server.modules.completion

import amf.core.model.document.BaseUnit
import amf.core.remote.{Platform, Raml10, Vendor}
import org.mulesoft.als.common.DirectoryResolver
import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.als.server.RequestModule
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.modules.common.LspConverter
import org.mulesoft.als.server.modules.hlast.HlAstManager
import org.mulesoft.als.server.textsync.TextDocumentManager
import org.mulesoft.als.suggestions
import org.mulesoft.als.suggestions.client.Suggestions
import org.mulesoft.als.suggestions.interfaces.{CompletionProvider, Suggestion, Syntax}
import org.mulesoft.lsp.ConfigType
import org.mulesoft.lsp.edit.TextEdit
import org.mulesoft.lsp.feature.RequestHandler
import org.mulesoft.lsp.feature.completion._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SuggestionsManager(private val textDocumentManager: TextDocumentManager,
                         val hlAstManager: HlAstManager,
                         private val directoryResolver: DirectoryResolver,
                         private val platform: Platform,
                         private val logger: Logger)
    extends RequestModule[CompletionClientCapabilities, CompletionOptions] {
  override val `type`: ConfigType[CompletionClientCapabilities, CompletionOptions] =
    CompletionConfigType

  def completionItem(suggestion: Suggestion, position: Position): CompletionItem = {
    val range: PositionRange = suggestion.range
      .getOrElse(PositionRange(position.moveColumn(-suggestion.prefix.length), position))

    CompletionItem(
      suggestion.displayText,
      textEdit = Some(TextEdit(LspConverter.toLspRange(range), suggestion.text)),
      detail = Some(suggestion.description),
      insertTextFormat = Some(suggestion.insertTextFormat)
    )
  }

  override val getRequestHandlers: Seq[RequestHandler[_, _]] = Seq(
    new RequestHandler[CompletionParams, Either[Seq[CompletionItem], CompletionList]] {
      override def `type`: CompletionRequestType.type = CompletionRequestType

      override def apply(params: CompletionParams): Future[Either[Seq[CompletionItem], CompletionList]] = {
        onDocumentCompletion(params.textDocument.uri, LspConverter.toPosition(params.position))
          .map(_.map(completionItem(_, LspConverter.toPosition(params.position))))
          .map(Left.apply)
      }
    }
  )

  override def applyConfig(config: Option[CompletionClientCapabilities]): CompletionOptions =
    CompletionOptions(None, Some(Set('[')))

  val onDocumentCompletionListener: (String, Position) => Future[Seq[Suggestion]] = onDocumentCompletion

  override def initialize(): Future[Unit] =
    suggestions.Core.init()

  protected def onDocumentCompletion(uri: String, position: Position): Future[Seq[Suggestion]] = {
    val refinedUri = platform.decodeURI(platform.resolvePath(uri))

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

        buildCompletionProviderAST(text, originalText, uri, refinedUri, offset, vendor, syntax)
          .flatMap(provider => {
            provider
              .suggest()
              .map(result => {
                this.logger.debug(s"Got ${result.length} proposals", "SuggestionsManager", "onDocumentCompletion")

                val endTime = System.currentTimeMillis()

                this.logger.debugDetail(s"It took ${endTime - startTime} milliseconds to complete",
                                        "ASTSuggestionsManager",
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
                                 syntax: Syntax): Future[CompletionProvider] = {

    val eventualUnit: Future[BaseUnit] = hlAstManager.astManager.forceBuildNewAST(uri, text)
    Suggestions.buildProviderAsync(eventualUnit, position, directoryResolver, platform, uri, unmodifiedContent)
  }
}
