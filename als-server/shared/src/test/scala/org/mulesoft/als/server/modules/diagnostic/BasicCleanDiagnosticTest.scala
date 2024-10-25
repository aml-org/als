package org.mulesoft.als.server.modules.diagnostic

import amf.core.client.common.validation.ProfileNames
import org.mulesoft.als.server.client.scala.LanguageServerBuilder
import org.mulesoft.als.server.modules.WorkspaceManagerFactoryBuilder
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.{LanguageServerBaseTest, MockDiagnosticClientNotifier}
import org.mulesoft.lsp.feature.diagnostic.DiagnosticSeverity

import scala.concurrent.ExecutionContext

class BasicCleanDiagnosticTest extends LanguageServerBaseTest {

  override implicit def executionContext: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
  override def rootPath: String                            = "diagnostics"



  def buildServer(): LanguageServer = {
    val diagnosticNotifier = new MockDiagnosticClientNotifier()
    val builder            = new WorkspaceManagerFactoryBuilder(diagnosticNotifier)
    val dm                 = builder.buildDiagnosticManagers()
    val factory            = builder.buildWorkspaceManagerFactory()
    val b = new LanguageServerBuilder(
      factory.documentManager,
      factory.workspaceManager,
      factory.configurationManager,
      factory.resolutionTaskManager
    )
    b.addRequestModule(factory.cleanDiagnosticManager)
    dm.foreach(m => b.addInitializableModule(m))
    b.build()
  }
}
