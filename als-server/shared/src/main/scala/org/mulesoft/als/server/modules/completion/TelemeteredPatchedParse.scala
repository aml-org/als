package org.mulesoft.als.server.modules.completion

import org.mulesoft.als.common.URIImplicits._
import org.mulesoft.als.server.textsync.{TextDocument, TextDocumentContainer}
import org.mulesoft.als.server.workspace.WorkspaceManager
import org.mulesoft.als.suggestions.patcher.PatchedContent
import org.mulesoft.amfintegration.AmfParseResult
import org.mulesoft.lsp.feature.telemetry.MessageTypes.MessageTypes
import org.mulesoft.lsp.feature.telemetry.{MessageTypes, TelemeteredTask, TelemetryProvider}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TelemeteredPatchedParse(telemetryProvider: TelemetryProvider)
    extends TelemeteredTask[PatchedParseParams, AmfParseResult] {
  override protected def telemetry: TelemetryProvider = telemetryProvider

  override protected def task(params: PatchedParseParams): Future[AmfParseResult] = {
    val patchedEnvironment: TextDocumentContainer =
      params.editorEnvironment.patchUri(params.uri, params.text)
    params.workspace.getUnit(params.uri, params.uuid).flatMap { bu =>
      params.editorEnvironment.amfConfiguration
        .modelBuilder()
        .parse(
          params.uri.toAmfDecodedUri(params.editorEnvironment.platform),
          patchedEnvironment.environment
            .withResolver(CompletionReferenceResolver(bu.unit))
            .withLoaders(patchedEnvironment.environment.loaders ++ params.editorEnvironment.environment.loaders),
          bu.workspaceConfiguration
        )
    }
  }

  override protected def code(params: PatchedParseParams): String = "PatchedProvider"

  override protected def beginType(params: PatchedParseParams): MessageTypes = MessageTypes.BEGIN_PARSE_PATCHED

  override protected def endType(params: PatchedParseParams): MessageTypes = MessageTypes.END_PARSE_PATCHED

  override protected def msg(params: PatchedParseParams): String = s"Patching content for ${params.uri}"

  override protected def uri(params: PatchedParseParams): String = params.uri

  override protected def uuid(params: PatchedParseParams): String = params.uuid
}

case class PatchedParseParams(text: TextDocument,
                              uri: String,
                              position: Int,
                              patchedContent: PatchedContent,
                              editorEnvironment: TextDocumentContainer,
                              workspace: WorkspaceManager,
                              uuid: String)
