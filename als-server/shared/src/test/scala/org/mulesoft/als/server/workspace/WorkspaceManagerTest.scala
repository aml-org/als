package org.mulesoft.als.server.workspace

import org.mulesoft.als.server.modules.ManagersFactory
import org.mulesoft.als.server.workspace.command.Commands
import org.mulesoft.als.server.{LanguageServerBaseTest, LanguageServerBuilder}
import org.mulesoft.lsp.common.{Position, Range}
import org.mulesoft.lsp.configuration.{InitializeParams, TraceKind}
import org.mulesoft.lsp.server.{DefaultServerSystemConf, LanguageServer}
import org.mulesoft.lsp.workspace.ExecuteCommandParams
import org.scalatest.Assertion

import scala.concurrent.ExecutionContext

class WorkspaceManagerTest extends LanguageServerBaseTest {

  override implicit val executionContext = ExecutionContext.Implicits.global

  private val factory = ManagersFactory(MockDiagnosticClientNotifier, logger, withDiagnostics = true)

  private val editorFiles = factory.container

  test("Workspace Manager check validations (initializing a tree should validate instantly)") {
    withServer[Assertion] { server =>
      for {
        _ <- server.initialize(InitializeParams(None, Some(TraceKind.Off), rootUri = Some(s"${filePath("ws1")}")))
        a <- MockDiagnosticClientNotifier.nextCall
        b <- MockDiagnosticClientNotifier.nextCall
        c <- MockDiagnosticClientNotifier.nextCall
      } yield {
        val allDiagnostics = Seq(a, b, c)
        assert(allDiagnostics.size == allDiagnostics.map(_.uri).distinct.size)
      }
    }
  }

  test("Workspace Manager search by location rather than uri (workspace)") {
    withServer[Assertion] { server =>
      for {
        _ <- server.initialize(InitializeParams(None, Some(TraceKind.Off), rootUri = Some(s"${filePath("ws3")}")))
        a <- MockDiagnosticClientNotifier.nextCall
        b <- MockDiagnosticClientNotifier.nextCall
      } yield {
        val allDiagnostics = Seq(a, b)
        assert(allDiagnostics.size == allDiagnostics.map(_.uri).distinct.size)
      }
    }
  }

  test("Workspace Manager check validation Stack - Error on external fragment with indirection") {
    withServer[Assertion] { server =>
      val rootFolder = s"${filePath("ws-error-stack-1")}"
      for {
        _ <- server.initialize(InitializeParams(None, Some(TraceKind.Off), rootUri = Some(rootFolder)))
        a <- MockDiagnosticClientNotifier.nextCall
        b <- MockDiagnosticClientNotifier.nextCall
        c <- MockDiagnosticClientNotifier.nextCall
      } yield {
        val allDiagnostics = Seq(a, b, c)
        assert(allDiagnostics.size == allDiagnostics.map(_.uri).distinct.size)
        val main   = allDiagnostics.find(_.uri == s"$rootFolder/api.raml")
        val others = allDiagnostics.filterNot(pd => main.exists(_.uri == pd.uri))
        assert(main.isDefined)
        others.size should be(2)

        main match {
          case Some(m) =>
            m.diagnostics.size should be(1)
            m.diagnostics.head.range should be(Range(Position(3, 5), Position(3, 28)))
            m.diagnostics.head.relatedInformation.size should be(2)
            m.diagnostics.head.relatedInformation.head.location.uri should be(s"$rootFolder/external1.yaml")
            m.diagnostics.head.relatedInformation.head.location.range should be(Range(Position(2, 5), Position(2, 28)))
            m.diagnostics.head.relatedInformation.tail.head.location.uri should be(s"$rootFolder/external2.yaml")
            m.diagnostics.head.relatedInformation.tail.head.location.range should be(
              Range(Position(2, 0), Position(2, 9)))
          case _ => fail("No Main detected")
        }
      }
    }
  }

  test("Workspace Manager check validation Stack - Error on library") {
    withServer[Assertion] { server =>
      val rootFolder = s"${filePath("ws-error-stack-2")}"
      for {
        _ <- server.initialize(InitializeParams(None, Some(TraceKind.Off), rootUri = Some(rootFolder)))
        a <- MockDiagnosticClientNotifier.nextCall
        b <- MockDiagnosticClientNotifier.nextCall
      } yield {
        val allDiagnostics = Seq(a, b)
        assert(allDiagnostics.size == allDiagnostics.map(_.uri).distinct.size)
        val library = allDiagnostics.find(_.uri == s"$rootFolder/library.raml")
        val others  = allDiagnostics.filterNot(pd => library.exists(_.uri == pd.uri))
        assert(library.isDefined)
        others.size should be(1)

        library match {
          case Some(m) =>
            m.diagnostics.size should be(1)
            m.diagnostics.head.range should be(Range(Position(3, 0), Position(3, 6)))
            m.diagnostics.head.relatedInformation.size should be(1)
            m.diagnostics.head.relatedInformation.head.location.uri should be(s"$rootFolder/api.raml")
            m.diagnostics.head.relatedInformation.head.location.range should be(Range(Position(4, 7), Position(4, 19)))
          case _ => fail("No Main detected")
        }
      }
    }
  }

  test("Workspace Manager check validation Stack - Error on typed fragment") {
    withServer[Assertion] { server =>
      val rootFolder = s"${filePath("ws-error-stack-3")}"
      for {
        _ <- server.initialize(InitializeParams(None, Some(TraceKind.Off), rootUri = Some(rootFolder)))
        a <- MockDiagnosticClientNotifier.nextCall
        b <- MockDiagnosticClientNotifier.nextCall
      } yield {
        val allDiagnostics = Seq(a, b)
        assert(allDiagnostics.size == allDiagnostics.map(_.uri).distinct.size)
        val library = allDiagnostics.find(_.uri == s"$rootFolder/external.raml")
        val others  = allDiagnostics.filterNot(pd => library.exists(_.uri == pd.uri))
        assert(library.isDefined)
        others.size should be(1)

        library match {
          case Some(m) =>
            m.diagnostics.size should be(1)
            m.diagnostics.head.range should be(Range(Position(2, 0), Position(2, 7)))
            m.diagnostics.head.relatedInformation.size should be(1)
            m.diagnostics.head.relatedInformation.head.location.uri should be(s"$rootFolder/api.raml")
            m.diagnostics.head.relatedInformation.head.location.range should be(Range(Position(4, 5), Position(4, 27)))
          case _ => fail("No Main detected")
        }
      }
    }
  }

  test("Workspace Manager check validation Stack - Error on External with two stacks") {
    withServer[Assertion] { server =>
      val rootFolder = s"${filePath("ws-error-stack-4")}"
      for {
        _ <- server.initialize(InitializeParams(None, Some(TraceKind.Off), rootUri = Some(rootFolder)))
        a <- MockDiagnosticClientNotifier.nextCall
        b <- MockDiagnosticClientNotifier.nextCall
        c <- MockDiagnosticClientNotifier.nextCall
        d <- MockDiagnosticClientNotifier.nextCall
      } yield {
        val allDiagnostics = Seq(a, b, c, d)
        assert(allDiagnostics.size == allDiagnostics.map(_.uri).distinct.size)
        val root   = allDiagnostics.find(_.uri == s"$rootFolder/api.raml")
        val others = allDiagnostics.filterNot(pd => root.exists(_.uri == pd.uri))
        assert(root.isDefined)
        others.size should be(3)
        assert(others.forall(p => p.diagnostics.isEmpty))

        root match {
          case Some(m) =>
            m.diagnostics.size should be(2)

            m.diagnostics.exists { d =>
              d.range == Range(Position(7, 5), Position(7, 27)) &&
              d.relatedInformation.size == 2 &&
              d.relatedInformation.head.location.uri == s"$rootFolder/external.yaml" &&
              d.relatedInformation.head.location.range == Range(Position(1, 12), Position(1, 36)) &&
              d.relatedInformation.tail.head.location.uri == s"$rootFolder/external-2.yaml" &&
              d.relatedInformation.tail.head.location.range == Range(Position(2, 0), Position(2, 6))
            } should be(true)

            m.diagnostics.exists { d =>
              d.range == Range(Position(4, 7), Position(4, 19))
              d.relatedInformation.size == 3
              d.relatedInformation.head.location.uri == s"$rootFolder/library.raml" &&
              d.relatedInformation.head.location.range == Range(Position(3, 5), Position(3, 27)) &&
              d.relatedInformation.tail.head.location.uri == s"$rootFolder/external.yaml" &&
              d.relatedInformation.tail.head.location.range == Range(Position(1, 12), Position(1, 36)) &&
              d.relatedInformation.tail.tail.head.location.uri == s"$rootFolder/external-2.yaml" &&
              d.relatedInformation.tail.tail.head.location.range == Range(Position(2, 0), Position(2, 6))
            } should be(true)

            succeed
          case _ => fail("No Main detected")
        }
      }
    }
  }

  test("Workspace Manager check change in Config [changing exchange.json] - Should notify validations of new tree") {
    withServer[Assertion] { server =>
      val root           = s"${filePath("ws4")}"
      val changedConfig  = """{"main": "api2.raml"}"""
      val originalConfig = """{"main": "api.raml"}"""

      for {
        _ <- server.initialize(InitializeParams(None, Some(TraceKind.Off), rootUri = Some(root)))
        // api.raml, fragment.raml
        a <- MockDiagnosticClientNotifier.nextCall
        b <- MockDiagnosticClientNotifier.nextCall
        _ <- changeNotification(server)(s"$root/exchange.json", changedConfig, 2)
        // api2.raml
        c <- MockDiagnosticClientNotifier.nextCall
        _ <- changeNotification(server)(s"$root/exchange.json", originalConfig, 3)
        // api.raml, fragment.raml
        d <- MockDiagnosticClientNotifier.nextCall
        e <- MockDiagnosticClientNotifier.nextCall

      } yield {
        val first = Seq(a, b)
        assert(first.exists(_.uri == s"$root/api.raml"))
        assert(first.exists(_.uri == s"$root/fragment.raml"))

        c.uri should be(s"$root/api2.raml")
        c.diagnostics.isEmpty should be(false)

        val last = Seq(d, e)
        assert(last.exists(_.uri == s"$root/api.raml"))
        assert(last.exists(_.uri == s"$root/fragment.raml"))

      }
    }
  }

  test("Workspace Manager check change in Config [using Command] - Should notify validations of new tree") {
    withServer[Assertion] { server =>
      val root        = s"${filePath("ws4")}"
      val apiRoot     = s"$root/api.raml"
      val api2Root    = s"$root/api2.raml"
      val apiFragment = s"$root/fragment.raml"

      for {
        _ <- server.initialize(InitializeParams(None, Some(TraceKind.Off), rootUri = Some(root)))
        // api.raml, fragment.raml
        a <- MockDiagnosticClientNotifier.nextCall
        b <- MockDiagnosticClientNotifier.nextCall
        _ <- server.workspaceService.executeCommand(
          ExecuteCommandParams(Commands.DID_CHANGE_CONFIGURATION,
                               List(s"""{"mainUri": "$api2Root", "dependencies": []}""")))
        // api2.raml
        c <- MockDiagnosticClientNotifier.nextCall
        _ <- server.workspaceService.executeCommand(
          ExecuteCommandParams(Commands.DID_CHANGE_CONFIGURATION,
                               List(s"""{"mainUri": "$apiRoot", "dependencies": []}""")))
        // api.raml, fragment.raml
        d <- MockDiagnosticClientNotifier.nextCall
        e <- MockDiagnosticClientNotifier.nextCall

      } yield {
        val first = Seq(a, b)
        assert(first.exists(_.uri == apiRoot))
        assert(first.exists(_.uri == apiFragment))

        c.uri should be(api2Root)
        c.diagnostics.isEmpty should be(false)

        val last = Seq(d, e)
        assert(last.exists(_.uri == apiRoot))
        assert(last.exists(_.uri == apiFragment))

      }
    }
  }

  override def buildServer(): LanguageServer =
    new LanguageServerBuilder(factory.documentManager, factory.workspaceManager, DefaultServerSystemConf)
      .addRequestModule(factory.structureManager)
      .build()

  override def rootPath: String = "workspace"

}
