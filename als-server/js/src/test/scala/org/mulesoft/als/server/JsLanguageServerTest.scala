package org.mulesoft.als.server

import org.mulesoft.als.configuration.DefaultJsServerSystemConf
import org.mulesoft.als.server.client.AlsClientNotifier
import org.mulesoft.als.server.feature.serialization.SerializationResult
import org.mulesoft.als.server.feature.workspace.FilesInProjectParams

import scala.concurrent.ExecutionContextExecutor
import scala.scalajs.js

class JsLanguageServerTest extends AMFValidatorTest {
  override implicit val executionContext: ExecutionContextExecutor = scala.concurrent.ExecutionContext.global
  override def rootPath: String = ""
  test("Test custom validators plugged from client") {
    val clientConnection = new MockDiagnosticClientNotifier(3000) with AlsClientNotifier[js.Any] {
      override def notifyProjectFiles(params: FilesInProjectParams): Unit = ???

      override def notifySerialization(params: SerializationResult[js.Any]): Unit = ???
    }

    val serializationProps = JsSerializationProps(clientConnection)
    var flag = false
    val server =
      LanguageServerFactory.buildServer(
        clientConnection,
        serializationProps,
        DefaultJsServerSystemConf,
        JsTestLogger(),
        js.undefined,
        Seq(TestValidator(() => flag = true)),
        None
      )
    val content =
      """#%RAML 1.0
        |title: Example of request bodies
        |mediaType: application/json
        |
        |
        |/groups:
        |  post:
        |    body:
        |      application/xml:
        |        type: Person
        |        example: !include person.xml
        |
        |types:
        |  Person:
        |    properties:
        |      age: integer
        |""".stripMargin

    val payload: String = """<Person>
                            |  <age>false</age>
                            |</Person>""".stripMargin
    withServer(server){ s =>
      for {
        _ <- openFile(s)("file:///person.xml", payload)
        _ <- openFile(s)("file:///uri.raml", content)
        _ <- clientConnection.nextCall
        _ <- clientConnection.nextCall
      } yield {
        assert(flag)
      }
    }
  }

  object JsTestLogger {
    def apply(): ClientLogger =
      js.Dynamic
        .literal(
          error = (message: String) => logger.error(message,"", ""),
          warn = (message: String) => logger.warning(message, "", ""),
          info = (message: String) => logger.debug(message, "", ""),
          log = (message: String) => logger.debug(message, "",""),
        )
        .asInstanceOf[ClientLogger]
  }
}