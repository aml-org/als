package org.mulesoft.als.server.modules.completion

import java.util.UUID

import org.mulesoft.als.common.{DirectoryResolver, FileUtils}
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.server.RequestModule
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.textsync.{TextDocument, TextDocumentContainer}
import org.mulesoft.als.server.workspace.WorkspaceManager
import org.mulesoft.als.suggestions.client.Suggestions
import org.mulesoft.als.suggestions.interfaces.{CompletionProvider, Syntax}
import org.mulesoft.als.suggestions.patcher.{ContentPatcher, PatchedContent}
import org.mulesoft.lsp.ConfigType
import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.lsp.feature.RequestHandler
import org.mulesoft.lsp.feature.completion._
import org.mulesoft.lsp.feature.telemetry.{MessageTypes, TelemetryProvider}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SuggestionsManager(val editorEnvironment: TextDocumentContainer,
                         val workspaceManager: WorkspaceManager,
                         private val telemetryProvider: TelemetryProvider,
                         val directoryResolver: DirectoryResolver,
                         private val logger: Logger)
    extends RequestModule[CompletionClientCapabilities, CompletionOptions] {

  private var conf: Option[CompletionClientCapabilities] = None

  private val suggestions = new Suggestions(editorEnvironment.platform,
                                            editorEnvironment.environment,
                                            directoryResolver,
                                            editorEnvironment.amfConfiguration)
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

  override def initialize(): Future[Unit] = suggestions.init()

  protected def onDocumentCompletion(uri: String, position: Position): Future[Seq[CompletionItem]] = {
    val refinedUri =
      editorEnvironment.platform.decodeURI(editorEnvironment.platform.resolvePath(uri))
    val telemetryUUID: String = UUID.randomUUID().toString

    logger.debug(s"Calling for completion for uri $uri and position $position",
                 "SuggestionsManager",
                 "onDocumentCompletion")

    editorEnvironment.get(uri) match {
      case Some(textDocument) =>
        val startTime    = System.currentTimeMillis()
        val syntax       = Syntax(textDocument.syntax)
        val originalText = textDocument.text
        val offset       = position.offset(originalText)
        telemetryProvider.addTimedMessage("Begin Suggestions",
                                          "SuggestionsManager",
                                          "onDocumentCompletion",
                                          MessageTypes.BEGIN_COMPLETION,
                                          uri,
                                          telemetryUUID)
        telemetryProvider.addTimedMessage("Begin Patching Suggestions",
                                          "SuggestionsManager",
                                          "onDocumentCompletion",
                                          MessageTypes.BEGIN_PATCHING,
                                          uri,
                                          telemetryUUID)
        val patchedContent = ContentPatcher(originalText, offset, syntax).prepareContent()
        telemetryProvider.addTimedMessage("End Patching Suggestions",
                                          "SuggestionsManager",
                                          "onDocumentCompletion",
                                          MessageTypes.END_PATCHING,
                                          uri,
                                          telemetryUUID)
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
              this.logger
                .debug(s"Got ${result.length} proposals for $uri", "SuggestionsManager", "onDocumentCompletion")

              val endTime = System.currentTimeMillis()

              this.logger.debug(s"It took ${endTime - startTime} milliseconds to complete",
                                "ASTSuggestionsManager",
                                "onDocumentCompletion")

              telemetryProvider.addTimedMessage("End Suggestions",
                                                "SuggestionsManager",
                                                "onDocumentCompletion",
                                                MessageTypes.END_COMPLETION,
                                                uri,
                                                telemetryUUID)
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
    val amfRefinedUri = FileUtils.getDecodedUri(uri, editorEnvironment.platform)
    telemetryProvider.addTimedMessage("Start parsing for completion",
                                      "SuggestionsManager",
                                      "buildCompletionProviderAST",
                                      MessageTypes.BEGIN_PARSE_PATCHED,
                                      uri,
                                      uuid)
    val patchedEnvironment: TextDocumentContainer = editorEnvironment.patchUri(amfRefinedUri, text)

    val eventualUnit =
      workspaceManager.getUnit(uri, uuid).flatMap { bu =>
        editorEnvironment.amfConfiguration.parserHelper.parse(
          amfRefinedUri,
          patchedEnvironment.environment
            .withResolver(CompletionReferenceResolver(bu.unit))
            .withLoaders(patchedEnvironment.environment.loaders ++ editorEnvironment.environment.loaders)
        )
      }

    eventualUnit.foreach { _ =>
      telemetryProvider.addTimedMessage("End parsing for completion",
                                        "SuggestionsManager",
                                        "buildCompletionProviderAST",
                                        MessageTypes.END_PARSE_PATCHED,
                                        uri,
                                        uuid)
    }

    suggestions.buildProviderAsync(
      eventualUnit.map(_.baseUnit),
      position,
      uri,
      patchedContent,
      snippetSupport,
      workspaceManager.getWorkspace(uri).workspaceConfiguration.map(c => s"${c.rootFolder}/")
    )
  }
}
