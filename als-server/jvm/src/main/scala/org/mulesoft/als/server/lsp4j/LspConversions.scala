package org.mulesoft.als.server.lsp4j

import java.util.{List => JList}

import org.eclipse.lsp4j
import org.eclipse.lsp4j.jsonrpc.messages.{Either => JEither}
import org.mulesoft.als.server.protocol.configuration.{
  AlsClientCapabilities,
  AlsInitializeParams,
  AlsInitializeResult,
  AlsServerCapabilities
}
import org.mulesoft.lsp.LspConversions.{
  completionOptions,
  documentLinkOptions,
  eitherCodeActionProviderOptions,
  eitherRenameOptions,
  textDocumentClientCapabilities,
  textDocumentSyncKind,
  textDocumentSyncOptions,
  traceKind,
  workspaceClientCapabilities,
  workspaceFolder,
  workspaceServerCapabilities
}
import org.mulesoft.lsp.feature.diagnostic._
import org.mulesoft.lsp.feature.serialization.SerializationClientCapabilities

import scala.collection.JavaConverters._
import scala.language.implicitConversions

object LspConversions {

  implicit def either[A, B, C, D](either: JEither[A, B], leftTo: A => C, rightTo: B => D): Either[C, D] =
    if (either.isLeft) Left(leftTo(either.getLeft)) else Right(rightTo(either.getRight))

  implicit def seq[A, B](list: JList[A], mapper: A => B): Seq[B] = list.asScala.map(mapper)

  def booleanOrFalse(value: java.lang.Boolean): Boolean = !(value == null) && value

  implicit def clientCapabilities(capabilities: extension.AlsClientCapabilities): AlsClientCapabilities =
    AlsClientCapabilities(
      Option(capabilities.getWorkspace).map(workspaceClientCapabilities),
      Option(capabilities.getTextDocument).map(textDocumentClientCapabilities),
      Option(capabilities.getExperimental),
      Option(capabilities.getSerialization).map(s => SerializationClientCapabilities(s.getSupportsSerialization)),
      Option(capabilities.getCleanDiagnosticTree).map(s =>
        CleanDiagnosticTreeClientCapabilities(s.getEnabledCleanDiagnostic))
    )

  implicit def alsInitializeParams(params: extension.AlsInitializeParams): AlsInitializeParams =
    Option(params).map { p =>
      AlsInitializeParams(
        Option(p.getCapabilities).map(clientCapabilities),
        Option(p.getTrace).map(traceKind),
        Option(p.getRootUri),
        Option(p.getProcessId),
        Option(p.getWorkspaceFolders).map(wf => seq(wf, workspaceFolder)),
        Option(p.getRootPath),
        Option(p.getInitializationOptions)
      )
    } getOrElse AlsInitializeParams.default

  implicit def serverCapabilities(result: lsp4j.ServerCapabilities): AlsServerCapabilities =
    if (result == null) AlsServerCapabilities.empty
    else
      AlsServerCapabilities(
        Option(result.getTextDocumentSync).map(either(_, textDocumentSyncKind, textDocumentSyncOptions)),
        Option(result.getCompletionProvider).map(completionOptions),
        booleanOrFalse(result.getDefinitionProvider),
        booleanOrFalse(result.getReferencesProvider),
        booleanOrFalse(result.getDocumentSymbolProvider),
        Option(result.getRenameProvider).flatMap(eitherRenameOptions),
        Option(result.getCodeActionProvider).flatMap(eitherCodeActionProviderOptions),
        Option(result.getDocumentLinkProvider),
        Option(result.getWorkspace),
        Option(result.getExperimental)
      )

  implicit def initializeResult(result: lsp4j.InitializeResult): AlsInitializeResult =
    Option(result)
      .map(r => AlsInitializeResult(serverCapabilities(r.getCapabilities)))
      .getOrElse(AlsInitializeResult.empty)
}
