package org.mulesoft.als.server.modules.completion

import java.util.UUID

import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.common.DirectoryResolver
import org.mulesoft.als.common.URIImplicits._
import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.als.server.RequestModule
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.textsync.{TextDocument, TextDocumentContainer}
import org.mulesoft.als.server.workspace.WorkspaceManager
import org.mulesoft.als.suggestions.client.Suggestions
import org.mulesoft.als.suggestions.interfaces.{CompletionProvider, Syntax}
import org.mulesoft.als.suggestions.patcher.{ContentPatcher, PatchedContent}
import org.mulesoft.amfmanager.AmfParseResult
import org.mulesoft.lsp.ConfigType
import org.mulesoft.lsp.feature.RequestHandler
import org.mulesoft.lsp.feature.completion._
import org.mulesoft.lsp.feature.telemetry.{MessageTypes, TelemetryProvider}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SuggestionsManager(val editorEnvironment: TextDocumentContainer,
                         val workspace: WorkspaceManager,
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

  protected def onDocumentCompletion(lspUri: String, position: Position): Future[Seq[CompletionItem]] = {
    val telemetryUUID: String = UUID.randomUUID().toString

    def innerOnDocumentCompletion(): Future[Seq[CompletionItem]] = {
      val uri = lspUri.toAmfUri(editorEnvironment.platform)
      // we need to normalize the URI encoding so we can find it both on RL and memory
      editorEnvironment.get(uri) match {
        case Some(textDocument) =>
          val syntax       = Syntax(textDocument.syntax)
          val originalText = textDocument.text
          val offset       = position.offset(originalText)
          val patchedContent =
            ContentPatcher(originalText, offset, syntax).prepareContent()
          buildCompletionProviderAST(
            new TextDocument(uri, textDocument.version, patchedContent.content, syntax.toString, logger),
            originalText,
            uri,
            offset,
            syntax,
            patchedContent,
            telemetryUUID
          ).flatMap(provider => {
            provider
              .suggest()
          })
        case _ => Future.successful(Seq.empty)
      }
    }

    telemetryProvider.timeProcess(
      "Completion",
      MessageTypes.BEGIN_COMPLETION,
      MessageTypes.END_COMPLETION,
      "SuggestionsManager : onDocumentCompletion",
      lspUri,
      innerOnDocumentCompletion,
      telemetryUUID
    )
  }

  def buildCompletionProviderAST(text: TextDocument,
                                 unmodifiedContent: String,
                                 uri: String,
                                 position: Int,
                                 syntax: Syntax,
                                 patchedContent: PatchedContent,
                                 uuid: String): Future[CompletionProvider] = {

    val eventualUnit = telemetryProvider.timeProcess(
      "Start Patched Parse",
      MessageTypes.BEGIN_PARSE_PATCHED,
      MessageTypes.END_PARSE_PATCHED,
      "SuggestionsManager : buildCompletionProviderAST",
      uri,
      innerParsePatched(uri, text, uuid),
      uuid
    )

    suggestions.buildProviderAsync(
      eventualUnit.map(_.baseUnit),
      position,
      uri,
      patchedContent,
      snippetSupport,
      workspace.getProjectRootOf(uri)
    )
  }
  private def innerParsePatched(uri: String, text: TextDocument, uuid: String)(): Future[AmfParseResult] = {
    val patchedEnvironment: TextDocumentContainer =
      editorEnvironment.patchUri(uri, text)
    workspace.getUnit(uri, uuid).flatMap { bu =>
      editorEnvironment.amfConfiguration.parserHelper.parse(
        uri.toAmfDecodedUri(editorEnvironment.platform),
        patchedEnvironment.environment
          .withResolver(CompletionReferenceResolver(bu.unit))
          .withLoaders(patchedEnvironment.environment.loaders ++ editorEnvironment.environment.loaders)
      )
    }
  }
}
