package org.mulesoft.als.server.modules.diagnostic

import org.mulesoft.als.server.{LanguageServerBaseTest, LanguageServerBuilder}
import org.mulesoft.als.server.modules.ManagersFactory
import org.mulesoft.lsp.server.{DefaultServerSystemConf, LanguageServer}

import scala.concurrent.ExecutionContext

class ServerParsingBeforeDiagnosticTest extends LanguageServerBaseTest {

  override implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global
  override def buildServer(): LanguageServer = {

    val factory = ManagersFactory(MockDiagnosticClientNotifier, logger, notificationKind = Some(PARSING_BEFORE))
    new LanguageServerBuilder(factory.documentManager, factory.workspaceManager, DefaultServerSystemConf)
      .addInitializableModule(factory.diagnosticManager)
      .build()
  }
  override def rootPath: String = ""

  test("Only Parsing error") {
    val api =
      """#%RAML 1.0
        |title: test
        |a b
        |""".stripMargin
    val filePath = "file://api.raml"
    withServer { server =>
      for {
        _       <- openFileNotification(server)(filePath, api)
        parsing <- MockDiagnosticClientNotifier.nextCall
        model   <- MockDiagnosticClientNotifier.nextCall
      } yield {
        server.shutdown()
        assert(parsing.diagnostics.length == 1)
        assert(parsing.diagnostics.length == model.diagnostics.length)
        assert(parsing.diagnostics.head.message == model.diagnostics.head.message)
        assert(parsing.diagnostics.head.message.startsWith("Syntax error in the following text"))

      }
    }
  }

  test("Parsing and Model error") {
    val api =
      """#%RAML 1.0
        |description: a description
        |a b
        |""".stripMargin
    val filePath = "file://api.raml"
    withServer { server =>
      for {
        _       <- openFileNotification(server)(filePath, api)
        parsing <- MockDiagnosticClientNotifier.nextCall
        model   <- MockDiagnosticClientNotifier.nextCall
      } yield {
        server.shutdown()
        assert(parsing.diagnostics.length == 1)
        assert(model.diagnostics.length == 2)
        assert(parsing.diagnostics.head.message.startsWith("Syntax error in the following text"))
        assert(model.diagnostics.head.message.startsWith("Syntax error in the following text"))
        assert(model.diagnostics.last.message.startsWith("API title is mandatory"))
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
    val filePath = "file://api.raml"
    withServer { server =>
      for {
        _            <- openFileNotification(server)(filePath, api)
        parsing      <- MockDiagnosticClientNotifier.nextCall
        model        <- MockDiagnosticClientNotifier.nextCall
        _            <- changeNotification(server)(filePath, apiPatched, 1)
        parsingFixed <- MockDiagnosticClientNotifier.nextCall
        model2       <- MockDiagnosticClientNotifier.nextCall
      } yield {
        server.shutdown()
        assert(parsing.diagnostics.length == 1)
        assert(model.diagnostics.length == 2)
        assert(parsing.diagnostics.head.message.startsWith("Syntax error in the following text"))
        assert(model.diagnostics.head.message.startsWith("Syntax error in the following text"))
        assert(model.diagnostics.last.message.startsWith("API title is mandatory"))

        assert(parsingFixed.diagnostics.length == 1)
        assert(parsingFixed.diagnostics.length == model2.diagnostics.length)
        assert(model2.diagnostics.head.message == "API title is mandatory")
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
    val filePath = "file://api.raml"
    withServer { server =>
      for {
        _           <- openFileNotification(server)(filePath, api)
        parsing     <- MockDiagnosticClientNotifier.nextCall
        model       <- MockDiagnosticClientNotifier.nextCall
        _           <- changeNotification(server)(filePath, apiPatched, 1)
        parsing2    <- MockDiagnosticClientNotifier.nextCall
        modelBroken <- MockDiagnosticClientNotifier.nextCall
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
