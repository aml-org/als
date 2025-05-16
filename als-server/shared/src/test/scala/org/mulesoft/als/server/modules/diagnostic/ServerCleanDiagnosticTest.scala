package org.mulesoft.als.server.modules.diagnostic

import amf.core.client.common.validation.ProfileNames
import amf.core.client.scala.AMFGraphConfiguration
import org.mulesoft.als.server.feature.diagnostic.CustomValidationClientCapabilities
import org.mulesoft.als.server.protocol.configuration.{AlsClientCapabilities, AlsInitializeParams}
import org.mulesoft.als.server.workspace.ChangesWorkspaceConfiguration
import org.mulesoft.als.server.{Flaky, MockDiagnosticClientNotifier}
import org.mulesoft.lsp.configuration.TraceKind

import scala.concurrent.ExecutionContext

class ServerCleanDiagnosticTest extends DiagnosticServerImpl with ChangesWorkspaceConfiguration {

  override implicit val executionContext = ExecutionContext.Implicits.global

  test("Test resource loader invocation from clean diagnostic with encoded uri") {
    val diagnosticNotifier: MockDiagnosticClientNotifier = new MockDiagnosticClientNotifier
    withServer(buildServer(diagnosticNotifier)) { server =>
      val apiPath = s"file://file%20with%20spaces.raml"

      for {
        d <- requestCleanDiagnostic(server)(apiPath)
      } yield {
        server.shutdown()
        assert(d.length == 1)
      }
    }
  }

  test("Test clean validation with invalid vendor inclusions") {
    val diagnosticNotifier: MockDiagnosticClientNotifier = new MockDiagnosticClientNotifier
    withServer(buildServer(diagnosticNotifier)) { server =>
      val apiPath = s"file://api.raml"
      for {
        d <- requestCleanDiagnostic(server)(apiPath)
      } yield {
        server.shutdown()
        assert(d.exists(_.diagnostics.nonEmpty))
      }
    }
  }

  test("Clean diagnostic test, compare notification against clean", Flaky) {
    val diagnosticNotifier: MockDiagnosticClientNotifier = new MockDiagnosticClientNotifier(5000)
    withServer(buildServer(diagnosticNotifier)) { s =>
      val mainFilePath = s"file://api.raml"

      val mainContent =
        """#%RAML 1.0
          |title: Recursive
          |types:
          |  Recursive:
          |    type: object
          |    properties:
          |      myP:
          |        type: Recursive
          |/recursiveType:
          |  post:
          |    responses:
          |      201:
          |        body:
          |          application/json:
          |            type: Recursive
        """.stripMargin

      for {
        _  <- openFileNotification(s)(mainFilePath, mainContent)
        d  <- diagnosticNotifier.nextCall
        v1 <- requestCleanDiagnostic(s)(mainFilePath)

      } yield {
        s.shutdown()

        d.diagnostics.size should be(1)
        v1.length should be(1)
        val fileDiagnostic = v1.head
        fileDiagnostic.diagnostics.size should be(1)
      }
    }
  }

  test("Clean diagnostic test - ASYNC20 vendor", Flaky) {
    val diagnosticNotifier: MockDiagnosticClientNotifier = new MockDiagnosticClientNotifier(5000)
    withServer(buildServer(diagnosticNotifier)) { s =>
      val mainFilePath = s"file://async.yaml"

      val mainContent =
        """asyncapi: "2.0.0"
          |""".stripMargin

      for {
        _ <- openFileNotification(s)(mainFilePath, mainContent)
        _ <- diagnosticNotifier.nextCall
        d <- requestCleanDiagnostic(s)(mainFilePath)
      } yield {
        s.shutdown()
        assert(d.nonEmpty)
        assert(d.forall(_.profile.profile == ProfileNames.ASYNC20.profile))
      }
    }
  }

  private val customValidationInitParams =
    AlsInitializeParams(
      Some(AlsClientCapabilities(customValidations = Some(CustomValidationClientCapabilities(true)))),
      Some(TraceKind.Off),
      rootUri = Some("file:///")
    )

  test("Clean diagnostic with negative custom validations") {
    val negativeReportUri = filePath(platform.encodeURI("project/negative.report.jsonld"))
    platform
      .fetchContent(negativeReportUri, AMFGraphConfiguration.predefined())
      .flatMap(negativeReport => {
        val diagnosticNotifier: MockDiagnosticClientNotifier = new MockDiagnosticClientNotifier(10000)
        val validator                                        = FromJsonLdValidatorProvider(negativeReport.toString())
        val server                                           = buildServer(diagnosticNotifier, Some(validator))
        withServer(server, customValidationInitParams) { s =>
          val mainFilePath       = s"file:///api.raml"
          val profileUri: String = filePath(platform.encodeURI("project/profile.yaml"))
          val args               = changeConfigArgs(None, "file:///", Set.empty, Set(profileUri))
          val mainContent =
            """#%RAML 1.0
                |""".stripMargin

          for {
            _ <- openFileNotification(s)(mainFilePath, mainContent)
            _ <- diagnosticNotifier.nextCall
            _ <- validator.jsonLDValidatorExecutor.calledAtLeastNTimes(0) // no profile
            _ <- changeWorkspaceConfiguration(server)(args)
            _ <- diagnosticNotifier.nextCall
            _ <- validator.jsonLDValidatorExecutor.calledAtLeastNTimes(1) // added profile
            d <- requestCleanDiagnostic(s)(mainFilePath)
            _ <- validator.jsonLDValidatorExecutor.calledAtLeastNTimes(2) // clean diagnostic
          } yield {
            s.shutdown()
            assert(d.nonEmpty)
            assert(d.forall(_.diagnostics.nonEmpty))
            assert(
              d.head.diagnostics.head.code
                .exists(_.endsWith("project/profile.yaml#/encodes/validations/validation1"))
            )
          }
        }
      })
  }
  test("Clean diagnostic with positive custom validations", Flaky) {
    val negativeReportUri = filePath(platform.encodeURI("project/positive.report.jsonld"))
    platform
      .fetchContent(negativeReportUri, AMFGraphConfiguration.predefined())
      .flatMap(negativeReport => {
        val diagnosticNotifier: MockDiagnosticClientNotifier = new MockDiagnosticClientNotifier(7000)
        val validator                                        = FromJsonLdValidatorProvider(negativeReport.toString())
        val server                                           = buildServer(diagnosticNotifier, Some(validator))
        withServer(server, customValidationInitParams) { s =>
          val mainFilePath       = s"file:///api.raml"
          val profileUri: String = filePath(platform.encodeURI("project/profile.yaml"))
          val args               = changeConfigArgs(None, "file:///", Set.empty, Set(profileUri))
          val mainContent =
            """#%RAML 1.0
              |""".stripMargin

          for {
            _ <- openFileNotification(s)(mainFilePath, mainContent)
            _ <- diagnosticNotifier.nextCall
            _ <- changeWorkspaceConfiguration(server)(args)
            _ <- diagnosticNotifier.nextCall
            d <- requestCleanDiagnostic(s)(mainFilePath)
            _ <- validator.jsonLDValidatorExecutor.calledAtLeastNTimes(2)
          } yield {
            s.shutdown()
            assert(d.nonEmpty)
            assert(d.forall(_.diagnostics.isEmpty))
          }
        }
      })
  }

  test("Clean diagnostic test for graphql") {
    val diagnosticNotifier: MockDiagnosticClientNotifier = new MockDiagnosticClientNotifier(5000)
    withServer(buildServer(diagnosticNotifier)) { s =>
      val mainFilePath = s"file://api.graphql"

      val mainContent =
        """type Dog {
          |    name: String!
          |    breed: String!
          |}
          |
        """.stripMargin

      for {
        _  <- openFileNotification(s)(mainFilePath, mainContent)
        d  <- diagnosticNotifier.nextCall
        v1 <- requestCleanDiagnostic(s)(mainFilePath)

      } yield {
        s.shutdown()

        d.diagnostics.size should be(1)
        v1.length should be(1)
        val fileDiagnostic = v1.head
        fileDiagnostic.diagnostics.size should be(1)
        fileDiagnostic.diagnostics.head shouldBe (d.diagnostics.head) // should be the same for req and notification
      }
    }
  }

  // TODO: the correct configuration is used, but the report is always empty, lexical errors are print but lost,
  //   while resolution errors don't seem to be supported
  //   the Editing pipeline for resolution is also not implemented, so Default is used (probably shouldn't matter)
  test("Clean diagnostic test for grpc - no validations") {
    val diagnosticNotifier: MockDiagnosticClientNotifier = new MockDiagnosticClientNotifier(5000)
    withServer(buildServer(diagnosticNotifier)) { s =>
      val mainFilePath = s"file://api.proto"

      val mainContent = """syntax = "proto3";
                          |
                          |package greeter;
                          |
                          |// The greeting service definition
                          |service Greeter {
                          |  // Sends a greeting
                          |  rpc SayHello (HelloRequest) returns (HelloReply) {}
                          |}
                          |
                          |// The request message containing the user's name
                          |message HelloRequest {
                          |  string name = 1;
                          |}
                          |
                          |// The response message containing the greeting
                          |message HelloReply {
                          |  string message = 1;
                          |}""".stripMargin
      for {
        _  <- openFileNotification(s)(mainFilePath, mainContent)
        d  <- diagnosticNotifier.nextCall
        v1 <- requestCleanDiagnostic(s)(mainFilePath)

      } yield {
        s.shutdown()

        d.diagnostics.size should be(0)
        v1.length should be(1)
        val fileDiagnostic = v1.head
        fileDiagnostic.diagnostics.size should be(0)
      }
    }
  }

  test("Clean diagnostic test for graphql with invalid token") {
    val diagnosticNotifier: MockDiagnosticClientNotifier = new MockDiagnosticClientNotifier(5000)
    withServer(buildServer(diagnosticNotifier)) { s =>
      val mainFilePath = s"file://api.graphql"

      val mainContent =
        """nonsense
          |
          |type Dog {
          |    name: String!
          |    breed: String!
          |}
          |
        """.stripMargin

      for {
        _ <- openFileNotification(s)(mainFilePath, mainContent)
        d <- diagnosticNotifier.nextCall

      } yield {
        s.shutdown()
        d.diagnostics.size should be(2)
      }
    }
  }

  test("Clean diagnostic test for graphql with empty api", Flaky) {
    val diagnosticNotifier: MockDiagnosticClientNotifier = new MockDiagnosticClientNotifier(5000)
    withServer(buildServer(diagnosticNotifier)) { s =>
      val mainFilePath = s"file://api.graphql"

      val mainContent =
        """ """.stripMargin

      for {
        _  <- openFileNotification(s)(mainFilePath, mainContent)
        d  <- diagnosticNotifier.nextCall
        v1 <- requestCleanDiagnostic(s)(mainFilePath)

      } yield {
        s.shutdown()
        d.diagnostics.size should be(0)
        v1.length should be(1) // couldn't guess spec
      }
    }
  }

  test("Test clean diagnostic with encoded uri for Project Name with spaces") {
    val diagnosticNotifier: MockDiagnosticClientNotifier = new MockDiagnosticClientNotifier
    withServer(buildServer(diagnosticNotifier)) { server =>
      val apiPath = s"file:///American%20Flights%20API/american-flights-api.raml"

      for {
        d <- requestCleanDiagnostic(server)(apiPath)
      } yield {
        server.shutdown()
        assert(d.length == 1)
        d.head.diagnostics.size should be(1)
      }
    }
  }

  test("Clean diagnostic test for async 2.6") {
    val diagnosticNotifier: MockDiagnosticClientNotifier = new MockDiagnosticClientNotifier(5000)
    withServer(buildServer(diagnosticNotifier)) { s =>
      val mainFilePath = s"file://api.yaml"

      val mainContent = """asyncapi: '2.6.0'
                          |info:
                          |  title: async
                          |  version: '1.0.0'
                          |servers:
                          |  server:
                          |    url: s
                          |    protocol: s
                          |    protocolVersion: s
                          |
                          |components:
                          |  servers:
                          |    myServer:
                          |      url: url.com
                          |      protocol: https
                          |      protocolVersion: 2
                          |channels:
                          |  user:
                          |    servers:
                          |      - server
                          |      - myServer
                          |      - asdasd""".stripMargin

      for {
        _  <- openFileNotification(s)(mainFilePath, mainContent)
        v1 <- requestCleanDiagnostic(s)(mainFilePath)

      } yield {
        s.shutdown()
        v1.length should be(1)
        v1.head.diagnostics.size should be(2)
      }
    }
  }
}
