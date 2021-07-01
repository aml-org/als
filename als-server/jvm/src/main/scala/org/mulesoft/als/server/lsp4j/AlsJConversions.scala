package org.mulesoft.als.server.lsp4j

import java.io.StringWriter

import com.google.common.collect.Lists
import org.eclipse.lsp4j.ExecuteCommandOptions
import org.mulesoft.als.server.feature.renamefile.RenameFileActionResult
import org.mulesoft.als.server.feature.serialization.{SerializationResult, SerializedDocument}
import org.mulesoft.als.server.protocol.configuration.{AlsInitializeResult, AlsServerCapabilities}
import org.mulesoft.lsp.Lsp4JConversions._

import scala.collection.JavaConverters._
import scala.language.implicitConversions

object AlsJConversions {

  implicit def alsInitializeResult(result: AlsInitializeResult): extension.AlsInitializeResult = {
    val clientR = new extension.AlsInitializeResult
    clientR.setServerCapabilities(result.capabilities)
    clientR
  }

  implicit def alsServerCapabilities(capabilities: AlsServerCapabilities): extension.AlsServerCapabilities = {
    val result = new extension.AlsServerCapabilities()

    result.setTextDocumentSync(
      capabilities.textDocumentSync
        .map(jEither(_, lsp4JTextDocumentSyncKind, lsp4JTextDocumentSyncOptions))
        .orNull)
    result.setCompletionProvider(
      capabilities.completionProvider
        .map(lsp4JCompletionOptions)
        .orNull)
    result.setDefinitionProvider(capabilities.definitionProvider)
    result.setReferencesProvider(capabilities.referencesProvider)
    result.setDocumentSymbolProvider(capabilities.documentSymbolProvider)
    result.setRenameProvider(capabilities.renameProvider)
    result.setCodeActionProvider(capabilities.codeActionProvider)

    result.setImplementationProvider(lsp4JEitherStaticregistrationOptions(capabilities.implementationProvider))
    result.setTypeDefinitionProvider(lsp4JEitherStaticregistrationOptions(capabilities.typeDefinitionProvider))
    result.setSelectionRangeProvider(lsp4JEitherStaticregistrationOptions(capabilities.selectionRange))

    capabilities.hoverProvider.foreach(h => result.setHoverProvider(h))
    capabilities.documentLinkProvider.foreach(dlp => result.setDocumentLinkProvider(dlp))

    result.setExperimental(capabilities.experimental)
    result.setExecuteCommandProvider(new ExecuteCommandOptions(Lists.newArrayList("didFocusChange")))

    capabilities.documentHighlightProvider.foreach(d => result.setDocumentHighlightProvider(d))

    capabilities.fileUsage.foreach(fu => result.setFileUsage(new extension.FileUsageServerOptions(fu.supported)))
    capabilities.cleanDiagnostics.foreach(cd =>
      result.setCleanDiagnosticTree(new extension.CleanDiagnosticTreeServerOptions(cd.supported)))
    capabilities.serialization.foreach(s =>
      result.setSerialization(new extension.SerializationServerOptions(s.supportsSerialization)))
    capabilities.conversion.foreach { c =>
      result.setConversion(
        new extension.ConversionServerOptions(
          c.supported
            .map(s => new extension.ConversionConf(s.from, s.to))
            .asJava))
    }
    capabilities.foldingRangeProvider.foreach(p => result.setFoldingRangeProvider(p))
    capabilities.renameFileAction.foreach(r =>
      result.setRenameFileAction(new extension.RenameFileActionServerOptions(r.supported)))

    result.setDocumentFormattingProvider(capabilities.documentFormattingProvider)
    result.setDocumentRangeFormattingProvider(capabilities.documentRangeFormattingProvider)
    result
  }

  implicit def serializedDocument(serializedDocument: SerializedDocument): extension.SerializedDocument =
    new extension.SerializedDocument(serializedDocument.uri, serializedDocument.document)

  implicit def serializationSerializedDocument(
      serializationMessage: SerializationResult[StringWriter]): extension.SerializedDocument =
    new extension.SerializedDocument(serializationMessage.uri, serializationMessage.model.toString)

  implicit def renameFileActionResult(result: RenameFileActionResult): extension.RenameFileActionResult =
    new extension.RenameFileActionResult(lsp4JWorkspaceEdit(result.edits))
}
