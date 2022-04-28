package org.mulesoft.als.server.modules.completion

import org.mulesoft.als.common.DirectoryResolver
import org.mulesoft.als.common.URIImplicits._
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.als.logger.Logger
import org.mulesoft.als.server.RequestModule
import org.mulesoft.als.server.modules.configuration.ConfigurationProvider
import org.mulesoft.als.server.textsync.TextDocumentContainer
import org.mulesoft.als.server.workspace.WorkspaceManager
import org.mulesoft.als.suggestions.client.{Suggestions, UnitBundle}
import org.mulesoft.als.suggestions.interfaces.CompletionProvider
import org.mulesoft.lsp.ConfigType
import org.mulesoft.lsp.feature.TelemeteredRequestHandler
import org.mulesoft.lsp.feature.completion._
import org.mulesoft.lsp.feature.telemetry.MessageTypes.MessageTypes
import org.mulesoft.lsp.feature.telemetry.{MessageTypes, TelemetryProvider}

import java.util.UUID
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

  private val suggestions =
    new Suggestions(configurationProvider.getConfiguration, directoryResolver, accessBundle)

  private def accessBundle: String => Future[UnitBundle] =
    workspace.getLastUnit(_, UUID.randomUUID().toString).map(r => UnitBundle(r.unit, r.definedBy, r.context))

  private def snippetSupport: Boolean =
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

      /**
        * If Some(_), this will be sent as a response as a default for a managed exception
        */
      override protected val empty: Option[Either[Seq[CompletionItem], CompletionList]] = Some(Left(Seq()))
    }
  )

  override def applyConfig(config: Option[CompletionClientCapabilities]): CompletionOptions = {
    conf = config
    CompletionOptions(None, Some(Set('[')))
  }

  override def initialize(): Future[Unit] = Future { suggestions.initialized() }

  protected def onDocumentCompletion(lspUri: String,
                                     position: Position,
                                     telemetryUUID: String): Future[Seq[CompletionItem]] = {
    logger.debug(s"Disable Templates: ${configurationProvider.getConfiguration.getTemplateType}",
                 "SuggestionsManager",
                 "onDocumentCompletion")
    val uri = lspUri.toAmfUri(editorEnvironment.platform)
    // we need to normalize the URI encoding so we can find it both on RL and memory
    editorEnvironment.get(uri) match {
      case Some(textDocument) =>
        val originalText = textDocument.text
        val offset       = position.offset(originalText)
        buildCompletionProviderAST(uri, offset)
          .flatMap(provider => {
            provider
              .suggest()
          })
      case _ => Future.successful(Seq.empty)
    }
  }

  def buildCompletionProviderAST(uri: String, position: Int): Future[CompletionProvider] =
    for {
      wcm     <- workspace.getWorkspace(uri)
      rootUri <- wcm.getRootFolderFor(uri)
      bundle  <- accessBundle(uri)
    } yield
      suggestions.buildProvider(
        bundle,
        position,
        uri,
        snippetSupport,
        rootUri
      )
}
