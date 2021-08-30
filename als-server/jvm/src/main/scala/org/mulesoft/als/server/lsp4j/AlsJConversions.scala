package org.mulesoft.als.server.lsp4j

import com.google.common.collect.Lists
import org.eclipse.lsp4j._
import org.mulesoft.als.server.feature.configuration.workspace.GetWorkspaceConfigurationResult
import org.mulesoft.als.server.feature.renamefile.RenameFileActionResult
import org.mulesoft.als.server.feature.serialization.{SerializationResult, SerializedDocument}
import org.mulesoft.als.server.protocol.configuration.{AlsInitializeResult, AlsServerCapabilities}
import org.mulesoft.lsp.Lsp4JConversions._
import org.mulesoft.lsp.textsync.DidChangeConfigurationNotificationParams

import java.io.StringWriter
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
    result.setDefinitionProvider(lsp4JEitherWorkDoneProgressOptions(capabilities.definitionProvider)(opt => {
      val ret  = new DefinitionOptions()
      val bool = opt.workDoneProgress.getOrElse(false)
      ret.setWorkDoneProgress(bool)
      ret
    }))

    result.setReferencesProvider(lsp4JEitherWorkDoneProgressOptions(capabilities.referencesProvider)(opt => {
      val ret  = new ReferenceOptions()
      val bool = opt.workDoneProgress.getOrElse(false)
      ret.setWorkDoneProgress(bool)
      ret
    }))

    result.setDocumentSymbolProvider(lsp4JEitherWorkDoneProgressOptions(capabilities.documentSymbolProvider)(opt => {
      val ret  = new DocumentSymbolOptions()
      val bool = opt.workDoneProgress.getOrElse(false)
      ret.setWorkDoneProgress(bool)
      ret
    }))

    result.setRenameProvider(capabilities.renameProvider)
    result.setCodeActionProvider(capabilities.codeActionProvider)

    result.setImplementationProvider(lsp4JEitherWorkDoneProgressOptions(capabilities.implementationProvider)(opt => {
      val ret  = new ImplementationRegistrationOptions()
      val bool = opt.workDoneProgress.getOrElse(false)
      ret.setWorkDoneProgress(bool)
      ret
    }))

    result.setTypeDefinitionProvider(lsp4JEitherWorkDoneProgressOptions(capabilities.typeDefinitionProvider)(opt => {
      val ret  = new TypeDefinitionRegistrationOptions()
      val bool = opt.workDoneProgress.getOrElse(false)
      ret.setWorkDoneProgress(bool)
      ret
    }))

    result.setSelectionRangeProvider(lsp4JEitherWorkDoneProgressOptions(capabilities.selectionRange)(opt => {
      val ret  = new SelectionRangeRegistrationOptions()
      val bool = opt.workDoneProgress.getOrElse(false)
      ret.setWorkDoneProgress(bool)
      ret
    }))

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

    result.setDocumentFormattingProvider(
      lsp4JEitherWorkDoneProgressOptions(capabilities.documentFormattingProvider)(opt => {
        val ret  = new DocumentFormattingOptions()
        val bool = opt.workDoneProgress.getOrElse(false)
        ret.setWorkDoneProgress(bool)
        ret
      }))
    result.setDocumentRangeFormattingProvider(
      lsp4JEitherWorkDoneProgressOptions(capabilities.documentRangeFormattingProvider)(opt => {
        val ret  = new DocumentRangeFormattingOptions()
        val bool = opt.workDoneProgress.getOrElse(false)
        ret.setWorkDoneProgress(bool)
        ret
      }))
    result
  }

  implicit def serializedDocument(serializedDocument: SerializedDocument): extension.SerializedDocument =
    new extension.SerializedDocument(serializedDocument.uri, serializedDocument.document)

  implicit def serializationSerializedDocument(
      serializationMessage: SerializationResult[StringWriter]): extension.SerializedDocument =
    new extension.SerializedDocument(serializationMessage.uri, serializationMessage.model.toString)

  implicit def renameFileActionResult(result: RenameFileActionResult): extension.RenameFileActionResult =
    new extension.RenameFileActionResult(lsp4JWorkspaceEdit(result.edits))

  implicit def workspaceConfigurationParams(
      shared: DidChangeConfigurationNotificationParams): extension.WorkspaceConfigurationParams =
    new extension.WorkspaceConfigurationParams(shared.mainUri,
                                               shared.dependencies.asJava,
                                               shared.customValidationProfiles.asJava,
                                               shared.semanticExtensions.asJava)

  implicit def getWorkspaceConfigurationResult(
      result: GetWorkspaceConfigurationResult): extension.GetWorkspaceConfigurationResult =
    new extension.GetWorkspaceConfigurationResult(result.workspace, result.configuration)
}
