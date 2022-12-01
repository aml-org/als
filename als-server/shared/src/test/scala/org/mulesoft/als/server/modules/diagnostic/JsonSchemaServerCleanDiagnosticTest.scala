package org.mulesoft.als.server.modules.diagnostic

import org.mulesoft.als.server.{Flaky, MockDiagnosticClientNotifier}

import scala.concurrent.ExecutionContext

class JsonSchemaServerCleanDiagnosticTest extends DiagnosticServerImpl {

  override implicit val executionContext: ExecutionContext =
    ExecutionContext.Implicits.global

  test("Clean diagnostic test - JSON Schema draft-03 with errors") {
    val diagnosticNotifier: MockDiagnosticClientNotifier = new MockDiagnosticClientNotifier(5000)
    withServer(buildServer(diagnosticNotifier)) { s =>
      val mainFilePath = s"file://basic-schema-with-error.json"

      val mainContent =
        """{
          |  "$schema": "http://json-schema.org/draft-03/schema#",
          |  "title": "My Schemas",
          |  "definitions": {
          |    "Person": {
          |      "properties": {
          |        "name": {
          |          "type": "string"
          |        },
          |        "age": "true"
          |      }
          |    }
          |  }
          |}
          |
          |""".stripMargin

      for {
        _ <- openFileNotification(s)(mainFilePath, mainContent)
        n <- diagnosticNotifier.nextCall
        r <- requestCleanDiagnostic(s)(mainFilePath)
      } yield {
        s.shutdown()
        assert(r.size == 1)
        assert(r.head.diagnostics.size == 1)
        assert(r.headOption.map(_.diagnostics).contains(n.diagnostics))
      }
    }
  }

  test("Clean diagnostic test - JSON Schema draft-04 with errors", Flaky) {
    val diagnosticNotifier: MockDiagnosticClientNotifier = new MockDiagnosticClientNotifier(5000)
    withServer(buildServer(diagnosticNotifier)) { s =>
      val mainFilePath = s"file://basic-schema-with-error.json"

      val mainContent =
        """{
          |  "$schema": "http://json-schema.org/draft-04/schema#",
          |  "title": "My Schemas",
          |  "definitions": {
          |    "Person": {
          |      "properties": {
          |        "name": {
          |          "type": "string"
          |        },
          |        "age": "true"
          |      }
          |    }
          |  }
          |}
          |
          |""".stripMargin

      for {
        _ <- openFileNotification(s)(mainFilePath, mainContent)
        n <- diagnosticNotifier.nextCall
        r <- requestCleanDiagnostic(s)(mainFilePath)
      } yield {
        s.shutdown()
        assert(r.size == 1)
        assert(r.head.diagnostics.size == 1)
        assert(r.headOption.map(_.diagnostics).contains(n.diagnostics))
      }
    }
  }

  test("Clean diagnostic test - JSON Schema draft-2019 with errors", Flaky) {
    val diagnosticNotifier: MockDiagnosticClientNotifier = new MockDiagnosticClientNotifier(5000)
    withServer(buildServer(diagnosticNotifier)) { s =>
      val mainFilePath = s"file://basic-schema-with-error.json"

      val mainContent =
        """{
          |  "$schema": "http://json-schema.org/draft/2019-09/schema#",
          |  "title": "My Schemas",
          |  "definitions": {
          |    "Person": {
          |      "properties": {
          |        "name": {
          |          "type": "string"
          |        },
          |        "age": {
          |          "type": "integer"
          |        }
          |      }
          |    }
          |  },
          |  "$defs": {
          |    "Animal": {
          |      "properties": {
          |        "breed": {
          |          "type": "string"
          |        },
          |        "age": {
          |          "type": "integer"
          |        }
          |      }
          |    }
          |  }
          |}
          |
          |""".stripMargin

      for {
        _ <- openFileNotification(s)(mainFilePath, mainContent)
        n <- diagnosticNotifier.nextCall
        r <- requestCleanDiagnostic(s)(mainFilePath)
      } yield {
        s.shutdown()
        assert(r.size == 1)
        assert(r.head.diagnostics.size == 1)
        assert(r.headOption.map(_.diagnostics).contains(n.diagnostics))
      }
    }
  }

  // todo: depends on W-11785261 (AMF)
  ignore("Clean diagnostic test - JSON Schema with recursive reference to root") {
    val diagnosticNotifier: MockDiagnosticClientNotifier = new MockDiagnosticClientNotifier(5000)
    withServer(buildServer(diagnosticNotifier)) { s =>
      val mainFilePath = s"file://basic-schema.json"

      val mainContent =
        """{
          |  "$schema": "http://json-schema.org/draft/2019-09/schema#",
          |  "definitions": {
          |    "Person": {
          |      "$ref": "#"
          |     },
          |    "Person": {
          |      "type": "string"
          |     }
          |   }
          | }
          |
          |""".stripMargin

      for {
        _ <- openFileNotification(s)(mainFilePath, mainContent)
        n <- diagnosticNotifier.nextCall
        r <- requestCleanDiagnostic(s)(mainFilePath)
      } yield {
        s.shutdown()
        assert(r.size == 1)
        assert(r.head.diagnostics.size == 1)
        assert(r.headOption.map(_.diagnostics).contains(n.diagnostics))
      }
    }
  }

  // Can't find an example for this kind of tests in Draft-07
}
