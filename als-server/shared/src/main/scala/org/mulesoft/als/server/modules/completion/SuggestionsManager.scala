package org.mulesoft.als.server.modules.completion

import amf.core.model.document.BaseUnit
import amf.core.remote.Platform
import amf.internal.environment.Environment
import org.mulesoft.als.common.DirectoryResolver
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.server.RequestModule
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.modules.ast.AstManager
import org.mulesoft.als.server.textsync.TextDocumentManager
import org.mulesoft.als.suggestions
import org.mulesoft.als.suggestions.client.Suggestions
import org.mulesoft.als.suggestions.interfaces.{CompletionProvider, Syntax}
import org.mulesoft.lsp.ConfigType
import org.mulesoft.lsp.convert.LspRangeConverter
import org.mulesoft.lsp.feature.RequestHandler
import org.mulesoft.lsp.feature.completion._
import org.mulesoft.lsp.feature.telemetry.TelemetryProvider

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SuggestionsManager(private val textDocumentManager: TextDocumentManager,
                         val astManager: AstManager,
                         private val telemetryProvider: TelemetryProvider,
                         private val directoryResolver: DirectoryResolver,
                         private val platform: Platform,
                         private val environment: Environment,
                         private val logger: Logger)
    extends RequestModule[CompletionClientCapabilities, CompletionOptions] {

  private var conf: Option[CompletionClientCapabilities] = None

  private def snippetSupport =
    conf
      .getOrElse(CompletionClientCapabilities(contextSupport = None))
      .completionItem
      .flatMap(_.snippetSupport)
      .getOrElse(true)

  override val `type`: ConfigType[CompletionClientCapabilities, CompletionOptions] =
    CompletionConfigType

  override val getRequestHandlers: Seq[RequestHandler[_, _]] = Seq(
    new RequestHandler[CompletionParams, Either[Seq[CompletionItem], CompletionList]] {
      override def `type`: CompletionRequestType.type = CompletionRequestType

      override def apply(params: CompletionParams): Future[Either[Seq[CompletionItem], CompletionList]] = {
        onDocumentCompletion(params.textDocument.uri, LspRangeConverter.toPosition(params.position))
          .map(Left.apply)
      }
    }
  )

  override def applyConfig(config: Option[CompletionClientCapabilities]): CompletionOptions = {
    conf = config
    CompletionOptions(None, Some(Set('[')))
  }

  val onDocumentCompletionListener: (String, Position) => Future[Seq[CompletionItem]] = onDocumentCompletion

  override def initialize(): Future[Unit] =
    suggestions.Core.init()

  protected def onDocumentCompletion(uri: String, position: Position): Future[Seq[CompletionItem]] = {
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

        buildCompletionProviderAST(text, originalText, uri, refinedUri, offset, /* vendor, */ syntax)
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
      .getOrElse(Future.successful(Seq.empty[CompletionItem]))
  }

  def buildCompletionProviderAST(text: String,
                                 unmodifiedContent: String,
                                 uri: String,
                                 refinedUri: String,
                                 position: Int,
                                 syntax: Syntax): Future[CompletionProvider] = {

    val eventualUnit: Future[BaseUnit] = astManager.forceBuildNewAST(uri, text, telemetryProvider)

    Suggestions.buildProviderAsync(eventualUnit,
                                   position,
                                   directoryResolver,
                                   platform,
                                   environment,
                                   uri,
                                   unmodifiedContent,
                                   snippetSupport)
  }
}
