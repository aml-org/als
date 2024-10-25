package org.mulesoft.als.server.modules.diagnostic.support.async

import amf.core.client.common.validation.ProfileNames
import org.mulesoft.als.server.client.scala.LanguageServerBuilder
import org.mulesoft.als.server.modules.WorkspaceManagerFactoryBuilder
import org.mulesoft.als.server.modules.diagnostic.BasicCleanDiagnosticTest
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.{LanguageServerBaseTest, MockDiagnosticClientNotifier}

import scala.concurrent.ExecutionContext

class Async26CleanDiagnosticTest extends BasicCleanDiagnosticTest {

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
}
