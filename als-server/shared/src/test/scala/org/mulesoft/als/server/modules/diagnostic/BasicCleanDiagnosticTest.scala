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

  test("Async importing valid Avro") {
    withServer(buildServer()) { server =>
      for {
        d <- requestCleanDiagnostic(server)(filePath("avro/async26-imports-valid-avro.yaml"))
      } yield {
        server.shutdown()
        d.foreach(filediag => assert(filediag.diagnostics.isEmpty))
        assert(d.size == 2)
        assert(d.head.profile == ProfileNames.ASYNC26)
      }
    }
  }

  test("Async importing invalid Avro") {
    withServer(buildServer()) { server =>
      for {
        d <- requestCleanDiagnostic(server)(filePath("avro/async26-imports-invalid-avro.yaml"))
      } yield {
        server.shutdown()
        assert(d.size == 2)
        assert(d.head.diagnostics.length == 1)
        assert(d.head.diagnostics.head.message == "Exception thrown in validation: Duplicate field zipcode in record Address: zipcode type:BOOLEAN pos:1 and zipcode type:INT pos:0.")
        assert(d.head.diagnostics.head.severity.get == DiagnosticSeverity.Error)
        assert(d.head.profile == ProfileNames.ASYNC26)
      }
    }
  }


  test("avro containing inline avro") {
    withServer(buildServer()) { server =>
      for {
        d <- requestCleanDiagnostic(server)(filePath("avro/schemas/avro-user/avrotoavro.avsc"))
      } yield {
        server.shutdown()
        assert(d.size == 1)
        assert(d.head.diagnostics.isEmpty)
        assert(d.head.profile == ProfileNames.AVROSCHEMA)
      }
    }
  }

  test("Avro with no errors") {
    withServer(buildServer()) { server =>
      for {
        d <- requestCleanDiagnostic(server)(filePath("avro/schemas/union-type-payload-error.avsc"))
      } yield {
        server.shutdown()
        assert(d.size == 1)
        assert(d.head.diagnostics.isEmpty)
        assert(d.head.profile == ProfileNames.AVROSCHEMA)
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
