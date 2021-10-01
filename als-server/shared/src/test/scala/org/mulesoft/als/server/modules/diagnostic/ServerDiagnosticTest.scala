package org.mulesoft.als.server.modules.diagnostic

import amf.core.client.scala.AMFResult
import amf.core.client.scala.model.document.BaseUnit
import amf.core.client.scala.model.domain.AmfObject
import amf.core.client.scala.vocabulary.Namespace.{Document => DocumentNamespace}
import amf.core.client.scala.vocabulary.ValueType
import amf.core.internal.metamodel.Field
import amf.core.internal.metamodel.document.BaseUnitModel
import amf.core.internal.parser.domain.{Annotations, Fields}
import org.mulesoft.als.configuration.{ConfigurationStyle, ProjectConfigurationStyle}
import org.mulesoft.als.server.modules.WorkspaceManagerFactoryBuilder
import org.mulesoft.als.server.modules.ast.BaseUnitListenerParams
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.protocol.configuration.AlsInitializeParams
import org.mulesoft.als.server.textsync.TextDocumentContainer
import org.mulesoft.als.server.workspace.command.Commands
import org.mulesoft.als.server.{LanguageServerBaseTest, LanguageServerBuilder, MockDiagnosticClientNotifier}
import org.mulesoft.amfintegration.amfconfiguration.{AmfConfigurationWrapper, AmfParseResult}
import org.mulesoft.amfintegration.dialect.dialects.ExternalFragmentDialect
import org.mulesoft.lsp.configuration.TraceKind
import org.mulesoft.lsp.workspace.ExecuteCommandParams

import scala.concurrent.{ExecutionContext, Future}

class ServerDiagnosticTest extends LanguageServerBaseTest {

  override implicit val executionContext: ExecutionContext =
    ExecutionContext.Implicits.global

  override def rootPath: String = ""

  var container: Option[TextDocumentContainer] = None

  def buildServer(diagnosticNotifier: MockDiagnosticClientNotifier): LanguageServer = {
    val builder = new WorkspaceManagerFactoryBuilder(diagnosticNotifier, logger)
    val dm      = builder.buildDiagnosticManagers()
    val factory = builder.buildWorkspaceManagerFactory()
    container = Option(factory.container)
    val b = new LanguageServerBuilder(factory.documentManager,
                                      factory.workspaceManager,
                                      factory.configurationManager,
                                      factory.resolutionTaskManager)
    dm.foreach(b.addInitializableModule)
    b.build()

  }

  test("diagnostics test 001 - onFocus") {
    val diagnosticNotifier: MockDiagnosticClientNotifier = new MockDiagnosticClientNotifier(10000)
    withServer(buildServer(diagnosticNotifier)) { server =>
      val mainFilePath = s"file://api.raml"
      val libFilePath  = s"file://lib1.raml"

      val mainContent =
        """#%RAML 1.0
          |
          |title: test API
          |uses:
          |  lib1: lib1.raml
          |
          |/resource:
          |  post:
          |    responses:
          |      200:
          |        body:
          |          application/json:
          |            type: lib1.TestType
          |            example:
          |              {"a":"1"}
        """.stripMargin

      val libFileContent =
        """#%RAML 1.0 Library
          |
          |types:
          |  TestType:
          |    properties:
          |      b: string
        """.stripMargin

      /*
        open lib -> open main -> focus lib -> fix lib -> focus main
       */
      for {
        _       <- openFileNotification(server)(libFilePath, libFileContent)
        oLib1   <- diagnosticNotifier.nextCall
        _       <- openFileNotification(server)(mainFilePath, mainContent)
        oMain11 <- diagnosticNotifier.nextCall
        oMain12 <- diagnosticNotifier.nextCall
        _       <- focusNotification(server)(libFilePath, 0)
        oLib21  <- diagnosticNotifier.nextCall
        _       <- changeNotification(server)(libFilePath, libFileContent.replace("b: string", "a: string"), 1)
        oLib31  <- diagnosticNotifier.nextCall
        _       <- focusNotification(server)(mainFilePath, 0)
        oMain21 <- diagnosticNotifier.nextCall
        oMain22 <- diagnosticNotifier.nextCall
      } yield {
        server.shutdown()
        val firstMain  = Seq(oMain11, oMain12)
        val secondMain = Seq(oLib21)
        val thirdMain  = Seq(oLib31)
        val fixedMain  = Seq(oMain21, oMain22)

        assert(oLib1.diagnostics.isEmpty && oLib1.uri == libFilePath)
        assert(firstMain.find(_.uri == mainFilePath).exists(_.diagnostics.length == 1))
        assert(firstMain.contains(oLib1))
        assert(firstMain.find(_.uri == libFilePath).contains(oLib21))
        assert(oLib21 == oLib31)
        assert(oLib1 == oLib31)
        assert(fixedMain.forall(_.diagnostics.isEmpty))
        assert(diagnosticNotifier.promises.isEmpty)
      }
    }
  }

  test("diagnostics test 002 - AML") {
    val diagnosticNotifier: MockDiagnosticClientNotifier = new MockDiagnosticClientNotifier(10000)
    withServer(
      buildServer(diagnosticNotifier),
      AlsInitializeParams(None,
                          Some(TraceKind.Off),
                          projectConfigurationStyle = Some(ProjectConfigurationStyle(ConfigurationStyle.COMMAND)))
    ) { server =>
      val dialectPath  = s"file://dialect.yaml"
      val instancePath = s"file://instance.yaml"

      val dialectContent =
        """#%Dialect 1.0
          |
          |dialect: Diagnostic Test
          |version: 1.1
          |
          |external:
          |  mock: mock.org
          |
          |nodeMappings:
          |  A:
          |    classTerm: mock.A
          |    mapping:
          |      a:
          |        range: boolean
          |
          |documents:
          |  root:
          |    encodes: A
          |
        """.stripMargin

      val instanceContent1 =
        """#%Diagnostic Test 1.1
          |
          |a: t
        """.stripMargin

      val instanceContent2 =
        """#%Diagnostic Test 1.1
          |
          |a: true
        """.stripMargin

      /*
        register dialect -> open invalid instance -> fix -> invalid again
       */
      for {
        _  <- openFileNotification(server)(dialectPath, dialectContent)
        d1 <- diagnosticNotifier.nextCall
        _ <- server.workspaceService.executeCommand(
          ExecuteCommandParams(
            Commands.DID_CHANGE_CONFIGURATION,
            List(s"""{"mainUri": "", "dependencies": [{"file": "$dialectPath", "scope": "dialect"}]}""")))
        _             <- openFileNotification(server)(instancePath, instanceContent1)
        openInvalid   <- diagnosticNotifier.nextCall
        _             <- openFileNotification(server)(instancePath, instanceContent2)
        fixed         <- diagnosticNotifier.nextCall
        _             <- openFileNotification(server)(instancePath, instanceContent1)
        reopenInvalid <- diagnosticNotifier.nextCall
      } yield {
        server.shutdown()
        assert(d1.diagnostics.isEmpty && d1.uri == dialectPath)
        assert(openInvalid.diagnostics.length == 2 && openInvalid.uri == instancePath)
        assert(fixed.diagnostics.isEmpty && fixed.uri == instancePath)
        assert(openInvalid == reopenInvalid)
        assert(diagnosticNotifier.promises.isEmpty)
      }
    }
  }

  test("DiagnosticManager with invalid clone") {
    class MockDialectDomainElementModel extends BaseUnitModel {
      override def modelInstance: AmfObject = throw new Exception("should fail")

      override val `type`: List[ValueType] = List(DocumentNamespace + "MockUnit")

      override def fields: List[Field] = List(ModelVersion, References, Usage, DescribedBy, Root)
    }

    class MockDialectInstance(override val fields: Fields) extends BaseUnit {

      override def meta: BaseUnitModel = new MockDialectDomainElementModel()

      override def references: Seq[BaseUnit] = Nil

      override def componentId: String = ""

      override val annotations: Annotations = Annotations()

      override def location(): Option[String] = Some("location")
    }
    val diagnosticNotifier: MockDiagnosticClientNotifier = new MockDiagnosticClientNotifier(10000)
    val builder                                          = new WorkspaceManagerFactoryBuilder(diagnosticNotifier, logger)
    builder
      .buildDiagnosticManagers()
    val factory = builder.buildWorkspaceManagerFactory()

    val amfBaseUnit: BaseUnit = new MockDialectInstance(new Fields())

    val amfParseResult: AmfParseResult =
      new AmfParseResult(AMFResult(amfBaseUnit, Seq()), ExternalFragmentDialect(), AmfConfigurationWrapper())

    for {
      _ <- Future {
        factory.resolutionTaskManager.onNewAst(
          BaseUnitListenerParams(
            amfParseResult,
            Map.empty,
            tree = false
          ),
          ""
        )
      }
      d <- diagnosticNotifier.nextCall
    } yield {
      assert(d.diagnostics.length == 1)
      assert(d.uri == "location")
      assert(
        d.diagnostics.head.message == "DiagnosticManager suffered an unexpected error while validating: should fail")
    }
  }

  test("Trait resolution with error( test resolution error handler)") {
    val diagnosticNotifier: MockDiagnosticClientNotifier = new MockDiagnosticClientNotifier(10000)
    withServer(buildServer(diagnosticNotifier)) { server =>
      val apiPath = s"file://api.raml"

      val apiContent =
        """#%RAML 1.0
          |
          |title: Example API
          |
          |traits:
          |  secured:
          |    queryParameters:
          |      access_token:
          |        invalid-key: A valid access_token is required
          |
          |/books:
          |  get:
          |    is: [ secured ]
        """.stripMargin

      for {
        _  <- openFileNotification(server)(apiPath, apiContent)
        d1 <- diagnosticNotifier.nextCall
      } yield {
        server.shutdown()
        assert(d1.diagnostics.nonEmpty && d1.uri == apiPath)
        assert(diagnosticNotifier.promises.isEmpty)
      }
    }
  }

  test("Error without location") {
    val diagnosticNotifier: MockDiagnosticClientNotifier = new MockDiagnosticClientNotifier(10000)
    withServer(buildServer(diagnosticNotifier)) { server =>
      val apiPath = s"file://api.json"

      val apiContent =
        """{
          |  "openapi": "3.0.0",
          |  "info": {
          |    "title": "test api",
          |    "version": "1"
          |  }
          |}
        """.stripMargin

      /*
        register dialect -> open invalid instance -> fix -> invalid again
       */
      for {
        _  <- openFileNotification(server)(apiPath, apiContent)
        d1 <- diagnosticNotifier.nextCall
      } yield {
        server.shutdown()
        assert(d1.diagnostics.nonEmpty && d1.uri == apiPath)
        assert(d1.diagnostics.head.relatedInformation.nonEmpty)
      }
    }
  }
}
