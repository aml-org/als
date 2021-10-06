package org.mulesoft.als.server.modules.diagnostic

import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.modules.WorkspaceManagerFactoryBuilder
import org.mulesoft.als.server.{LanguageServerBaseTest, LanguageServerBuilder, MockDiagnosticClientNotifier}

import scala.concurrent.ExecutionContext

class ServerParsingBeforeDiagnosticTest extends LanguageServerBaseTest {

  override implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  def buildServer(clientNotifier: MockDiagnosticClientNotifier): LanguageServer = {
    val builder = new WorkspaceManagerFactoryBuilder(clientNotifier, logger)
      .withNotificationKind(PARSING_BEFORE)

    val dm      = builder.buildDiagnosticManagers()
    val factory = builder.buildWorkspaceManagerFactory()

    val b = new LanguageServerBuilder(factory.documentManager,
                                      factory.workspaceManager,
                                      factory.configurationManager,
                                      factory.resolutionTaskManager)
    dm.foreach(m => b.addInitializableModule(m))
    b.build()
  }

  override def rootPath: String = ""

  test("Only Parsing error") {
    val api =
      """#%RAML 1.0
        |title: test
        |a b
        |""".stripMargin
    val filePath                                     = "file://api.raml"
    val clientNotifier: MockDiagnosticClientNotifier = new MockDiagnosticClientNotifier
    withServer(buildServer(clientNotifier)) { server =>
      for {
        _       <- openFileNotification(server)(filePath, api)
        parsing <- clientNotifier.nextCall
        model   <- clientNotifier.nextCall
      } yield {
        server.shutdown()
        assert(parsing.diagnostics.length == 1)
        assert(parsing.diagnostics.length == model.diagnostics.length)
        assert(parsing.diagnostics.head.message == model.diagnostics.head.message)
        assert(parsing.diagnostics.head.message.startsWith("Syntax error in the following text"))
        assert(clientNotifier.promises.isEmpty)
      }
    }
  }

  test("Parsing and Model error") {
    val api =
      """#%RAML 1.0
        |description: a description
        |a b
        |""".stripMargin
    val filePath                                     = "file://api.raml"
    val clientNotifier: MockDiagnosticClientNotifier = new MockDiagnosticClientNotifier
    withServer(buildServer(clientNotifier)) { server =>
      for {
        _       <- openFileNotification(server)(filePath, api)
        parsing <- clientNotifier.nextCall
        model   <- clientNotifier.nextCall
      } yield {
        server.shutdown()
        assert(parsing.diagnostics.length == 1)
        assert(model.diagnostics.length == 2)
        assert(parsing.diagnostics.head.message.startsWith("Syntax error in the following text"))
        assert(model.diagnostics.exists(_.message.startsWith("Syntax error in the following text")))
        assert(model.diagnostics.exists(_.message.startsWith("API title is mandatory")))
        assert(clientNotifier.promises.isEmpty)
      }
    }
  }

  test("Parsing and Model fix parsing error") {
    val api =
      """#%RAML 1.0
        |description: a description
        |a b
        |""".stripMargin

    val apiPatched =
      """#%RAML 1.0
        |description: a description
        |""".stripMargin
    val filePath                                     = "file://api.raml"
    val clientNotifier: MockDiagnosticClientNotifier = new MockDiagnosticClientNotifier
    withServer(buildServer(clientNotifier)) { server =>
      for {
        _            <- openFileNotification(server)(filePath, api)
        parsing      <- clientNotifier.nextCall
        model        <- clientNotifier.nextCall
        _            <- changeNotification(server)(filePath, apiPatched, 1)
        parsingFixed <- clientNotifier.nextCall
        model2       <- clientNotifier.nextCall
      } yield {
        server.shutdown()
        assert(parsing.diagnostics.length == 1)
        assert(model.diagnostics.length == 2)
        assert(parsing.diagnostics.head.message.startsWith("Syntax error in the following text"))
        assert(model.diagnostics.exists(_.message.startsWith("Syntax error in the following text")))
        assert(model.diagnostics.exists(_.message.startsWith("API title is mandatory")))

        assert(parsingFixed.diagnostics.length == 1)
        assert(parsingFixed.diagnostics.length == model2.diagnostics.length)
        assert(model2.diagnostics.head.message == "API title is mandatory")
        assert(clientNotifier.promises.isEmpty)

      }
    }
  }

  test("Break model") {
    val api =
      """#%RAML 1.0
        |title: test
        |
        |""".stripMargin

    val apiPatched =
      """#%RAML 1.0
        |description: a description
        |""".stripMargin
    val filePath                                     = "file://api.raml"
    val clientNotifier: MockDiagnosticClientNotifier = new MockDiagnosticClientNotifier
    withServer(buildServer(clientNotifier)) { server =>
      for {
        _           <- openFileNotification(server)(filePath, api)
        parsing     <- clientNotifier.nextCall
        model       <- clientNotifier.nextCall
        _           <- changeNotification(server)(filePath, apiPatched, 1)
        parsing2    <- clientNotifier.nextCall
        modelBroken <- clientNotifier.nextCall
      } yield {
        server.shutdown()
        assert(parsing.diagnostics.isEmpty)
        assert(model.diagnostics.isEmpty)
        assert(parsing2.diagnostics.isEmpty)
        assert(modelBroken.diagnostics.length == 1)
      }
    }
  }
}
