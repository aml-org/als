package org.mulesoft.als.server.modules.diagnostic

import amf.core.client.common.validation.ProfileNames
import org.mulesoft.als.server.client.scala.LanguageServerBuilder
import org.mulesoft.als.server.modules.WorkspaceManagerFactoryBuilder
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.{LanguageServerBaseTest, MockDiagnosticClientNotifier}

import scala.concurrent.ExecutionContext

class BasicCleanDiagnosticTest extends LanguageServerBaseTest {

  override implicit def executionContext: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global
  override def rootPath: String                            = "diagnostics"

  test("async 2.6 with only one error") {
    withServer(buildServer()) { server =>
      for {
        d <- requestCleanDiagnostic(server)(filePath("async26/async-api26-full.yaml"))
      } yield {
        server.shutdown()
        assert(d.size == 1)
        assert(d.head.diagnostics.size == 1)
        assert(d.head.diagnostics.head.message == "Property 'error' not supported in a ASYNC 2.6 webApi node")
        assert(d.head.profile == ProfileNames.ASYNC26)
      }
    }
  }

  test("Avro with only one error") {
    withServer(buildServer()) { server =>
      for {
        d <- requestCleanDiagnostic(server)(filePath("avro/union-type-payload-error.avsc"))
      } yield {
        server.shutdown()
        assert(d.size == 1)
        assert(d.head.diagnostics.size == 1)
        assert(d.head.profile == ProfileNames.AVROSCHEMA)
        // todo: AMF will handle this error, so the message will change
        assert(d.head.diagnostics.head.message == "Error in AVRO Schema: org.apache.avro.SchemaParseException: No type: {\"name\":\"age\",\"type\":[\"null\",\"int\"],\"default\":null}")
      }
    }
  }


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
