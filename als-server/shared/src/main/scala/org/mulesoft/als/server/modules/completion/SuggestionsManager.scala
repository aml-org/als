package org.mulesoft.als.server.modules.completion

import org.mulesoft.als.common.DirectoryResolver
import org.mulesoft.als.common.URIImplicits._
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.als.server.RequestModule
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.modules.configuration.ConfigurationProvider
import org.mulesoft.als.server.textsync.{TextDocument, TextDocumentContainer}
import org.mulesoft.als.server.workspace.WorkspaceManager
import org.mulesoft.als.suggestions.client.Suggestions
import org.mulesoft.als.suggestions.interfaces.{CompletionProvider, Syntax}
import org.mulesoft.als.suggestions.patcher.{ContentPatcher, PatchedContent}
import org.mulesoft.lsp.ConfigType
import org.mulesoft.lsp.feature.TelemeteredRequestHandler
import org.mulesoft.lsp.feature.completion._
import org.mulesoft.lsp.feature.telemetry.MessageTypes.MessageTypes
import org.mulesoft.lsp.feature.telemetry.{MessageTypes, TelemetryProvider}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SuggestionsManager(val editorEnvironment: TextDocumentContainer,
                         val workspace: WorkspaceManager,
                         private val telemetryProvider: TelemetryProvider,
                         val directoryResolver: DirectoryResolver,
                         private val logger: Logger,
                         private val configurationProvider: ConfigurationProvider)
    extends RequestModule[CompletionClientCapabilities, CompletionOptions] {

  private var conf: Option[CompletionClientCapabilities] = None

  private val suggestions = new Suggestions(editorEnvironment.platform,
                                            editorEnvironment.environment,
                                            configurationProvider.getConfiguration,
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

  override val getRequestHandlers: Seq[TelemeteredRequestHandler[_, _]] = Seq(
    new TelemeteredRequestHandler[CompletionParams, Either[Seq[CompletionItem], CompletionList]] {
      override def `type`: CompletionRequestType.type = CompletionRequestType

      override def task(params: CompletionParams): Future[Either[Seq[CompletionItem], CompletionList]] =
        onDocumentCompletion(params.textDocument.uri, LspRangeConverter.toPosition(params.position), uuid(params))
          .map(Left.apply)

      override protected def telemetry: TelemetryProvider = telemetryProvider

      override protected def code(params: CompletionParams): String =
        "SuggestionsManager"

      override protected def beginType(params: CompletionParams): MessageTypes =
        MessageTypes.BEGIN_COMPLETION

      override protected def endType(params: CompletionParams): MessageTypes =
        MessageTypes.END_COMPLETION

      override protected def msg(params: CompletionParams): String =
        s"SuggestionsManager : onDocumentCompletion for uri ${params.textDocument.uri}"

      override protected def uri(params: CompletionParams): String =
        params.textDocument.uri
    }
  )

  override def applyConfig(config: Option[CompletionClientCapabilities]): CompletionOptions = {
    conf = config
    CompletionOptions(None, Some(Set('[')))
  }

  override def initialize(): Future[Unit] = suggestions.init()

  protected def onDocumentCompletion(lspUri: String,
                                     position: Position,
                                     telemetryUUID: String): Future[Seq[CompletionItem]] = {
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
          uri,
          offset,
          patchedContent,
          telemetryUUID
        ).flatMap(provider => {
          provider
            .suggest()
        })
      case _ => Future.successful(Seq.empty)
    }
  }

  def buildCompletionProviderAST(text: TextDocument,
                                 uri: String,
                                 position: Int,
                                 patchedContent: PatchedContent,
                                 uuid: String): Future[CompletionProvider] =
    suggestions.buildProviderAsync(
      patchedParse(text, uri, position, patchedContent, uuid).map(_.baseUnit),
      position,
      uri,
      patchedContent,
      snippetSupport,
      workspace.getProjectRootOf(uri)
    )

  private def patchedParse(text: TextDocument,
                           uri: String,
                           position: Int,
                           patchedContent: PatchedContent,
                           uuid: String) =
    new TelemeteredPatchedParse(telemetryProvider)
      .run(PatchedParseParams(text, uri, position, patchedContent, editorEnvironment, workspace, uuid))
}
