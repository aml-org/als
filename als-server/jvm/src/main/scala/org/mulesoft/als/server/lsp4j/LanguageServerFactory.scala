package org.mulesoft.als.server.lsp4j

import amf.core.unsafe.PlatformSecrets
import org.mulesoft.als.server.LanguageServerBuilder
import org.mulesoft.als.server.client.ClientNotifier
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.lsp4j.internal.DefaultJvmDirectoryResolver
import org.mulesoft.als.server.modules.ast.AstManager
import org.mulesoft.als.server.modules.completion.SuggestionsManager
import org.mulesoft.als.server.modules.definition.DefinitionModule
import org.mulesoft.als.server.modules.diagnostic.DiagnosticManager
import org.mulesoft.als.server.modules.hlast.HlAstManager
import org.mulesoft.als.server.modules.reference.FindReferencesModule
import org.mulesoft.als.server.modules.rename.RenameModule
import org.mulesoft.als.server.modules.structure.StructureManager
import org.mulesoft.als.server.platform.ServerPlatform
import org.mulesoft.als.server.textsync.TextDocumentManager
import org.mulesoft.high.level.CustomDialects
import org.mulesoft.high.level.implementation.AlsPlatformWrapper
import org.mulesoft.lsp.server.LanguageServer

object LanguageServerFactory extends PlatformSecrets {

  def alsLanguageServer(clientNotifier: ClientNotifier,
                        logger: Logger,
                        dialects: Seq[CustomDialects] = Seq()): LanguageServer = {
    val documentManager = new TextDocumentManager(logger, platform)

    val someDirectoryResolver = Some(DefaultJvmDirectoryResolver)

    val alsPlatform    = new AlsPlatformWrapper(dirResolver = someDirectoryResolver)
    val serverPlatform = new ServerPlatform(logger, documentManager, someDirectoryResolver)

    val astManager        = new AstManager(documentManager, serverPlatform, logger)
    val hlAstManager      = new HlAstManager(documentManager, astManager, serverPlatform, logger, dialects)
    val completionManager = new SuggestionsManager(documentManager, hlAstManager, serverPlatform, logger)
    val definitionModule  = new DefinitionModule(hlAstManager, serverPlatform, logger)
    val diagnosticManager = new DiagnosticManager(documentManager, astManager, clientNotifier, alsPlatform, logger)
    val referenceModule   = new FindReferencesModule(hlAstManager, alsPlatform, logger)
    val renameModule      = new RenameModule(hlAstManager, alsPlatform, logger)
    val structureManager  = new StructureManager(documentManager, hlAstManager, alsPlatform, logger)

    LanguageServerBuilder()
      .withTextDocumentSyncConsumer(documentManager)
      .addInitializable(astManager)
      .addInitializable(hlAstManager)
      .addInitializable(diagnosticManager)
      .addRequestModule(completionManager)
      .addRequestModule(definitionModule)
      .addRequestModule(referenceModule)
      .addRequestModule(renameModule)
      .addRequestModule(structureManager)
      .build()
  }

}
