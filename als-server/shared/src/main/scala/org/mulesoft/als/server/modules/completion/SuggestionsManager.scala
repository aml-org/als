package org.mulesoft.als.server.modules.completion

import java.util.UUID

import amf.core.remote.Platform
import org.mulesoft.als.common.{DirectoryResolver, FileUtils}
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.server.RequestModule
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.textsync.{TextDocument, TextDocumentContainer}
import org.mulesoft.als.suggestions
import org.mulesoft.als.suggestions.client.Suggestions
import org.mulesoft.als.suggestions.interfaces.{CompletionProvider, Syntax}
import org.mulesoft.als.suggestions.patcher.{ContentPatcher, PatchedContent}
import org.mulesoft.amfmanager.ParserHelper
import org.mulesoft.lsp.ConfigType
import org.mulesoft.lsp.convert.LspRangeConverter
import org.mulesoft.lsp.feature.RequestHandler
import org.mulesoft.lsp.feature.completion._
import org.mulesoft.lsp.feature.telemetry.{MessageTypes, TelemetryProvider}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SuggestionsManager(val editorEnvironment: TextDocumentContainer,
                         private val telemetryProvider: TelemetryProvider,
                         private val directoryResolver: DirectoryResolver,
                         private val platform: Platform,
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

  override def initialize(): Future[Unit] =
    suggestions.Core.init()

  protected def onDocumentCompletion(uri: String, position: Position): Future[Seq[CompletionItem]] = {
    val refinedUri            = platform.decodeURI(platform.resolvePath(uri))
    val telemetryUUID: String = UUID.randomUUID().toString

    logger.debug(s"Calling for completion for uri $uri and position $position",
                 "SuggestionsManager",
                 "onDocumentCompletion")

    editorEnvironment.get(uri) match {
      case Some(textDocument) =>
        val startTime      = System.currentTimeMillis()
        val syntax         = Syntax(textDocument.syntax)
        val originalText   = textDocument.text
        val offset         = position.offset(originalText)
        val patchedContent = ContentPatcher(originalText, offset, syntax).prepareContent()
        telemetryProvider.addTimedMessage("Begin Suggestions", MessageTypes.BEGIN_COMPLETION, uri, telemetryUUID)
        buildCompletionProviderAST(
          new TextDocument(uri, textDocument.version, patchedContent.content, syntax.toString, logger),
          originalText,
          uri,
          refinedUri,
          offset,
          syntax,
          patchedContent,
          telemetryUUID
        ).flatMap(provider => {
          provider
            .suggest()
            .map(result => {
              this.logger.debug(s"Got ${result.length} proposals", "SuggestionsManager", "onDocumentCompletion")

              val endTime = System.currentTimeMillis()

              this.logger.debugDetail(s"It took ${endTime - startTime} milliseconds to complete",
                                      "ASTSuggestionsManager",
                                      "onDocumentCompletion")

              telemetryProvider.addTimedMessage("End Suggestions", MessageTypes.END_COMPLETION, uri, telemetryUUID)
              result
            })
        })
      case _ => Future.successful(Seq.empty)

    }

  }

  def buildCompletionProviderAST(text: TextDocument,
                                 unmodifiedContent: String,
                                 uri: String,
                                 refinedUri: String,
                                 position: Int,
                                 syntax: Syntax,
                                 patchedContent: PatchedContent,
                                 uuid: String): Future[CompletionProvider] = {
    val amfRefinedUri      = FileUtils.getDecodedUri(uri, platform)
    val patchedEnvironment = editorEnvironment.patchUri(amfRefinedUri, text)
    val eventualUnit       = ParserHelper(platform).parse(amfRefinedUri, patchedEnvironment.environment) // todo pass other workspace bu's as cache

    Suggestions.buildProviderAsync(eventualUnit,
                                   position,
                                   directoryResolver,
                                   platform,
                                   patchedEnvironment.environment,
                                   uri,
                                   patchedContent,
                                   snippetSupport)
  }
}
