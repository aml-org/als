package org.mulesoft.als.server.lsp4j

import com.google.common.collect.Lists
import org.eclipse.lsp4j._
import org.eclipse.lsp4j.jsonrpc.messages.{Either => JEither}
import org.mulesoft.als.server.feature.configuration.workspace.GetWorkspaceConfigurationResult
import org.mulesoft.als.server.feature.renamefile.RenameFileActionResult
import org.mulesoft.als.server.feature.serialization.{SerializationResult, SerializedDocument}
import org.mulesoft.als.server.lsp4j.extension.CustomValidationOptions
import org.mulesoft.als.server.protocol.configuration.{AlsInitializeResult, AlsServerCapabilities}
import org.mulesoft.lsp.Lsp4JConversions._
import org.mulesoft.lsp.{configuration, textsync}
import org.mulesoft.lsp.textsync.DidChangeConfigurationNotificationParams
import org.mulesoft.als.server.feature.fileusage.filecontents.FileContentsResponse
import org.mulesoft.lsp.configuration.FileOperationRegistrationOptions

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
        .orNull
    )
    result.setCompletionProvider(
      capabilities.completionProvider
        .map(lsp4JCompletionOptions)
        .orNull
    )
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
      result.setCleanDiagnosticTree(new extension.CleanDiagnosticTreeServerOptions(cd.supported))
    )
    capabilities.serialization.foreach(s =>
      result.setSerialization(new extension.SerializationServerOptions(s.supportsSerialization))
    )
    capabilities.conversion.foreach { c =>
      result.setConversion(
        new extension.ConversionServerOptions(
          c.supported
            .map(s => new extension.ConversionConf(s.from, s.to))
            .asJava
        )
      )
    }
    capabilities.foldingRangeProvider.foreach(p => result.setFoldingRangeProvider(p))
    capabilities.renameFileAction.foreach(r =>
      result.setRenameFileAction(new extension.RenameFileActionServerOptions(r.supported))
    )

    result.setDocumentFormattingProvider(
      lsp4JEitherWorkDoneProgressOptions(capabilities.documentFormattingProvider)(opt => {
        val ret  = new DocumentFormattingOptions()
        val bool = opt.workDoneProgress.getOrElse(false)
        ret.setWorkDoneProgress(bool)
        ret
      })
    )
    result.setDocumentRangeFormattingProvider(
      lsp4JEitherWorkDoneProgressOptions(capabilities.documentRangeFormattingProvider)(opt => {
        val ret  = new DocumentRangeFormattingOptions()
        val bool = opt.workDoneProgress.getOrElse(false)
        ret.setWorkDoneProgress(bool)
        ret
      })
    )
    capabilities.customValidations.foreach(r => result.setCustomValidations(new CustomValidationOptions(r.enabled)))
    capabilities.workspace.foreach(w => {
      val wsc: WorkspaceServerCapabilities = ClientWorkspaceServerCapabilities(w)
      result.setWorkspace(wsc)
    })
    result
  }

  private def toClient(o: configuration.FileOperationPatternOptions): FileOperationPatternOptions = {
    val options = new FileOperationPatternOptions()
    o.ignoreCase.foreach(options.setIgnoreCase(_))
    options
  }

  private def toClient(pattern: configuration.FileOperationPattern): FileOperationPattern = {
    val pattern1 = new FileOperationPattern(pattern.glob)
    pattern.matches.foreach(pattern1.setMatches)
    pattern.options.foreach(o => pattern1.setOptions(toClient(o)))
    pattern1
  }

  private def toClient(filter: configuration.FileOperationFilter): FileOperationFilter = {
    val filter1 = new FileOperationFilter()
    filter.scheme.foreach(filter1.setScheme)
    filter1.setPattern(toClient(filter.pattern))
    filter1
  }

  private def toClient(d: FileOperationRegistrationOptions): FileOperationOptions = {
    val options = new FileOperationOptions(
      d.filters.map(toClient).asJava
    )
    options
  }

  private def toClient(fo: configuration.FileOperationsServerCapabilities): FileOperationsServerCapabilities = {
    val capabilities = new FileOperationsServerCapabilities()
    fo.didCreate.foreach(d => { capabilities.setDidCreate(toClient(d)) })
    fo.willCreate.foreach(d => { capabilities.setWillCreate(toClient(d)) })
    fo.didRename.foreach(d => { capabilities.setDidRename(toClient(d)) })
    fo.willRename.foreach(d => { capabilities.setWillRename(toClient(d)) })
    fo.didDelete.foreach(d => { capabilities.setDidDelete(toClient(d)) })
    fo.willDelete.foreach(d => { capabilities.setWillDelete(toClient(d)) })
    capabilities
  }

  private def ClientWorkspaceServerCapabilities(
      w: configuration.WorkspaceServerCapabilities
  ): WorkspaceServerCapabilities = {
    val workspaceFolders: WorkspaceFoldersOptions = w.workspaceFolders
      .map(wfsc => {
        val options = new WorkspaceFoldersOptions()
        wfsc.supported.foreach(options.setSupported(_))
        wfsc.changeNotifications.foreach {
          case Left(value)  => options.setChangeNotifications(value)
          case Right(value) => options.setChangeNotifications(value)
        }
        options
      })
      .orNull
    val capabilities = new WorkspaceServerCapabilities(workspaceFolders)

    w.fileOperations.foreach(fo => {
      capabilities.setFileOperations(toClient(fo))
    })
    capabilities
  }

  implicit def serializedDocument(serializedDocument: SerializedDocument): extension.SerializedDocument =
    new extension.SerializedDocument(serializedDocument.uri, serializedDocument.model)

  implicit def fileContentsResponse(fileContentsResponse: FileContentsResponse): extension.FileContentsResponse =
    new extension.FileContentsResponse(fileContentsResponse.fs.asJava)

  implicit def serializationSerializedDocument(
      serializationMessage: SerializationResult[StringWriter]
  ): extension.SerializedDocument =
    new extension.SerializedDocument(serializationMessage.uri, serializationMessage.model.toString)

  implicit def renameFileActionResult(result: RenameFileActionResult): extension.RenameFileActionResult =
    new extension.RenameFileActionResult(lsp4JWorkspaceEdit(result.edits))

  implicit def workspaceConfigurationParams(
      shared: DidChangeConfigurationNotificationParams
  ): extension.WorkspaceConfigurationParams =
    new extension.WorkspaceConfigurationParams(
      shared.mainPath.getOrElse(""),
      shared.folder,
      shared.dependencies.map {
        eitherDependencyConfigurationToJava
      }.asJava
    )

  private def eitherDependencyConfigurationToJava(
      e: Either[String, textsync.DependencyConfiguration]
  ): JEither[String, extension.DependencyConfiguration] = {
    e match {
      case Left(str) =>
        JEither.forLeft[String, extension.DependencyConfiguration](str)
      case Right(dc) =>
        JEither.forRight[String, extension.DependencyConfiguration](
          new extension.DependencyConfiguration(dc.file, dc.scope)
        )
    }
  }

  implicit def getWorkspaceConfigurationResult(
      result: GetWorkspaceConfigurationResult
  ): extension.GetWorkspaceConfigurationResult =
    new extension.GetWorkspaceConfigurationResult(result.workspace, result.configuration)
}
