package org.mulesoft.als.server.modules.diagnostic

import amf.core.errorhandling.ErrorCollector
import amf.core.metamodel.Obj
import amf.core.model.document.BaseUnit
import amf.core.model.domain.AmfObject
import amf.core.parser.{Annotations, Fields}
import amf.plugins.document.vocabularies.metamodel.domain.DialectDomainElementModel
import org.mulesoft.als.server.modules.WorkspaceManagerFactoryBuilder
import org.mulesoft.als.server.modules.ast.BaseUnitListenerParams
import org.mulesoft.als.server.modules.workspace.DummyResolvedUnit
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.textsync.TextDocumentContainer
import org.mulesoft.als.server.{LanguageServerBaseTest, LanguageServerBuilder, MockDiagnosticClientNotifier}
import org.mulesoft.amfmanager.AmfParseResult

import scala.concurrent.{ExecutionContext, Future}

class ServerDiagnosticTest extends LanguageServerBaseTest with DummyResolvedUnit {

  override implicit val executionContext: ExecutionContext =
    ExecutionContext.Implicits.global

  override def rootPath: String = ""

  var container: Option[TextDocumentContainer] = None
  val diagnosticNotifier                       = new MockDiagnosticClientNotifier
  override def buildServer(): LanguageServer = {
    val builder = new WorkspaceManagerFactoryBuilder(diagnosticNotifier, logger)
    val dm      = builder.diagnosticManager()
    val factory = builder.buildWorkspaceManagerFactory()
    container = Option(factory.container)
    new LanguageServerBuilder(factory.documentManager, factory.workspaceManager)
      .addInitializableModule(dm)
      .build()

  }

  test("diagnostics test 001 - onFocus") {
    withServer { server =>
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
        oLib2   <- diagnosticNotifier.nextCall
        _       <- changeNotification(server)(libFilePath, libFileContent.replace("b: string", "a: string"), 1)
        oLib3   <- diagnosticNotifier.nextCall
        _       <- focusNotification(server)(mainFilePath, 0)
        oMain21 <- diagnosticNotifier.nextCall
        oMain22 <- diagnosticNotifier.nextCall
      } yield {
        server.shutdown()
        assert(oLib1.diagnostics.isEmpty && oLib1.uri == libFilePath)
        assert(oMain11.diagnostics.length == 1 && oMain11.uri == mainFilePath)
        assert(oLib1 == oMain12)
        assert(oLib2 == oLib1)
        assert(oLib3 == oLib2)
        assert(oMain22 == oLib1)
        assert(oMain21.diagnostics.isEmpty && oMain21.uri == mainFilePath)
        assert(diagnosticNotifier.promises.isEmpty)
      }
    }
  }

  test("diagnostics test 002 - AML") {
    withServer { server =>
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
      diagnosticNotifier.promises.clear()
      for {
        _  <- openFileNotification(server)(dialectPath, dialectContent)
        d1 <- diagnosticNotifier.nextCall
        _  <- openFileNotification(server)(instancePath, instanceContent1)
        i1 <- diagnosticNotifier.nextCall
        _  <- openFileNotification(server)(instancePath, instanceContent2)
        i2 <- diagnosticNotifier.nextCall
        _  <- openFileNotification(server)(instancePath, instanceContent1)
        i3 <- diagnosticNotifier.nextCall
      } yield {
        server.shutdown()
        assert(d1.diagnostics.isEmpty && d1.uri == dialectPath)
        assert(i1.diagnostics.length == 1 && i1.uri == instancePath)
        assert(i2.diagnostics.isEmpty && i2.uri == instancePath)
        assert(i3 == i1)
        assert(diagnosticNotifier.promises.isEmpty)
      }
    }
  }

  test("DiagnosticManager with invalid clone") {
    class MockDialectDomainElementModel extends DialectDomainElementModel {
      override def modelInstance: AmfObject = throw new Exception("should fail")
    }

    class MockDialectInstance(override val fields: Fields) extends BaseUnit {

      override def meta: Obj = new MockDialectDomainElementModel()

      override def references: Seq[BaseUnit] = Nil

      override def componentId: String = ""

      override val annotations: Annotations = Annotations()

      override def location(): Option[String] = Some("location")
    }

    val builder               = new WorkspaceManagerFactoryBuilder(diagnosticNotifier, logger)
    val dm: DiagnosticManager = builder.diagnosticManager()

    val amfBaseUnit: BaseUnit = new MockDialectInstance(new Fields())

    val eh = new ErrorCollector {}

    val amfParseResult: AmfParseResult = new AmfParseResult(amfBaseUnit, eh)

    dm.onNewAst(
      BaseUnitListenerParams(
        amfParseResult,
        Map.empty,
        () => Future(dummyResolved(amfBaseUnit, container))
      ),
      ""
    )
    for {
      d <- diagnosticNotifier.nextCall
    } yield {
      assert(d.diagnostics.length == 1)
      assert(d.uri == "location")
      assert(
        d.diagnostics.head.message == "DiagnosticManager suffered an unexpected error while cloning unit: should fail")
    }
  }
}
