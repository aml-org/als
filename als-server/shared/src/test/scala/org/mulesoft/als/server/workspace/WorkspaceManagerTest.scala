package org.mulesoft.als.server.workspace

import org.mulesoft.als.server.modules.{WorkspaceManagerFactory, WorkspaceManagerFactoryBuilder}
import org.mulesoft.als.server.modules.diagnostic.DiagnosticManager
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.protocol.configuration.AlsInitializeParams
import org.mulesoft.als.server.workspace.command.Commands
import org.mulesoft.als.server.{LanguageServerBaseTest, LanguageServerBuilder, MockDiagnosticClientNotifier}
import org.mulesoft.lsp.configuration.{TraceKind, WorkspaceFolder}
import org.mulesoft.lsp.feature.common.{Position, Range}
import org.mulesoft.lsp.feature.diagnostic.PublishDiagnosticsParams
import org.mulesoft.lsp.workspace.{DidChangeWorkspaceFoldersParams, ExecuteCommandParams, WorkspaceFoldersChangeEvent}
import org.scalatest.Assertion

import scala.concurrent.{ExecutionContext, Future}

class WorkspaceManagerTest extends LanguageServerBaseTest {

  override implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  val diagnosticClientNotifier         = new MockDiagnosticClientNotifier
  val builder                          = new WorkspaceManagerFactoryBuilder(diagnosticClientNotifier, logger)
  val dm: DiagnosticManager            = builder.diagnosticManager()
  val factory: WorkspaceManagerFactory = builder.buildWorkspaceManagerFactory()

  test("Workspace Manager check validations (initializing a tree should validate instantly)") {
    withServer[Assertion] { server =>
      for {
        _ <- server.initialize(AlsInitializeParams(None, Some(TraceKind.Off), rootUri = Some(s"${filePath("ws1")}")))
        a <- diagnosticClientNotifier.nextCall
        b <- diagnosticClientNotifier.nextCall
        c <- diagnosticClientNotifier.nextCall
      } yield {
        val allDiagnostics = Seq(a, b, c)
        assert(allDiagnostics.size == allDiagnostics.map(_.uri).distinct.size)
      }
    }
  }

  test("Workspace Manager search by location rather than uri (workspace)") {
    withServer[Assertion] { server =>
      for {
        _ <- server.initialize(AlsInitializeParams(None, Some(TraceKind.Off), rootUri = Some(s"${filePath("ws3")}")))
        a <- diagnosticClientNotifier.nextCall
        b <- diagnosticClientNotifier.nextCall
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
        _ <- server.initialize(AlsInitializeParams(None, Some(TraceKind.Off), rootUri = Some(rootFolder)))
        a <- diagnosticClientNotifier.nextCall
        b <- diagnosticClientNotifier.nextCall
        c <- diagnosticClientNotifier.nextCall
      } yield {
        val allDiagnostics = Seq(a, b, c)
        verifyWS1ErrorStack(rootFolder, allDiagnostics)
      }
    }
  }

  private def verifyWS1ErrorStack(rootFolder: String, allDiagnostics: Seq[PublishDiagnosticsParams]) = {
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
        m.diagnostics.head.relatedInformation.tail.head.location.range should be(Range(Position(2, 0), Position(2, 9)))
      case _ => fail("No Main detected")
    }
  }

  test("Workspace Manager check validation Stack - Error on library") {
    withServer[Assertion] { server =>
      val rootFolder = s"${filePath("ws-error-stack-2")}"
      for {
        _ <- server.initialize(AlsInitializeParams(None, Some(TraceKind.Off), rootUri = Some(rootFolder)))
        a <- diagnosticClientNotifier.nextCall
        b <- diagnosticClientNotifier.nextCall
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
        _ <- server.initialize(AlsInitializeParams(None, Some(TraceKind.Off), rootUri = Some(rootFolder)))
        a <- diagnosticClientNotifier.nextCall
        b <- diagnosticClientNotifier.nextCall
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
        _ <- server.initialize(AlsInitializeParams(None, Some(TraceKind.Off), rootUri = Some(rootFolder)))
        a <- diagnosticClientNotifier.nextCall
        b <- diagnosticClientNotifier.nextCall
        c <- diagnosticClientNotifier.nextCall
        d <- diagnosticClientNotifier.nextCall
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
        _ <- server.initialize(AlsInitializeParams(None, Some(TraceKind.Off), rootUri = Some(root)))
        // api.raml, fragment.raml
        a <- diagnosticClientNotifier.nextCall
        b <- diagnosticClientNotifier.nextCall
        _ <- changeNotification(server)(s"$root/exchange.json", changedConfig, 2)
        // api2.raml
        c <- diagnosticClientNotifier.nextCall
        _ <- changeNotification(server)(s"$root/exchange.json", originalConfig, 3)
        // api.raml, fragment.raml
        d <- diagnosticClientNotifier.nextCall
        e <- diagnosticClientNotifier.nextCall

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
        _ <- server.initialize(AlsInitializeParams(None, Some(TraceKind.Off), rootUri = Some(root)))
        // api.raml, fragment.raml
        a <- diagnosticClientNotifier.nextCall
        b <- diagnosticClientNotifier.nextCall
        _ <- server.workspaceService.executeCommand(
          ExecuteCommandParams(Commands.DID_CHANGE_CONFIGURATION,
                               List(s"""{"mainUri": "$api2Root", "dependencies": []}""")))
        // api2.raml
        c <- diagnosticClientNotifier.nextCall
        _ <- server.workspaceService.executeCommand(
          ExecuteCommandParams(Commands.DID_CHANGE_CONFIGURATION,
                               List(s"""{"mainUri": "$apiRoot", "dependencies": []}""")))
        // api.raml, fragment.raml
        d <- diagnosticClientNotifier.nextCall
        e <- diagnosticClientNotifier.nextCall

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

  test("Workspace Content Manager - Unit not found (when changing RAML header)") {
    withServer[Assertion] { server =>
      val root     = s"${filePath("ws4")}"
      val title    = s"$root/fragment.raml"
      val content1 = "#%RAML 1.0 DataType\n"
      val content2 = "#%RAML 1.0 Library\n"

      for {
        _ <- server.initialize(AlsInitializeParams(None, Some(TraceKind.Off), rootUri = Some(root)))
        _ <- {
          openFile(server)(title, content1)
          diagnosticClientNotifier.nextCall
        }
        _ <- {
          changeFile(server)(title, content2, 2)
          diagnosticClientNotifier.nextCall
        }
        _ <- requestDocumentSymbol(server)(title)
        _ <- diagnosticClientNotifier.nextCall // There are a couple of diagnostics notifications not used in the test that could potentially bug proceeding tests
        _ <- diagnosticClientNotifier.nextCall // There are a couple of diagnostics notifications not used in the test that could potentially bug proceeding tests
      } yield {
        succeed // if it hasn't blown, it's OK
      }
    }
  }

  test("Workspace Manager multiworkspace support - basic test") {
    withServer[Assertion] { server =>
      val ws1path  = s"${filePath("multiworkspace/ws1")}"
      val filesWS1 = List(s"${ws1path}/api.raml", s"${ws1path}/sub/type.raml", s"${ws1path}/type.json")
      val ws2path  = s"${filePath("multiworkspace/ws2")}"
      val filesWS2 = List(s"${ws2path}/api.raml", s"${ws2path}/sub/type.raml")
      val ws1      = WorkspaceFolder(Some(ws1path), Some("ws1"))
      val ws2      = WorkspaceFolder(Some(ws2path), Some("ws2"))
      val allFiles = (filesWS1 ++ filesWS2)
      val wsList   = List(ws1, ws2)

      for {
        _ <- server.initialize(
          AlsInitializeParams(None, Some(TraceKind.Off), rootUri = Some(ws1path), workspaceFolders = Some(wsList)))
        a <- diagnosticClientNotifier.nextCall
        b <- diagnosticClientNotifier.nextCall
        c <- diagnosticClientNotifier.nextCall
        d <- diagnosticClientNotifier.nextCall
        e <- diagnosticClientNotifier.nextCall
      } yield {
        val allDiagnosticsFolders = List(a, b, c, d, e).map(_.uri)
        assert(allDiagnosticsFolders.size == allDiagnosticsFolders.distinct.size)
        assert(allFiles.map(u => allDiagnosticsFolders.contains(u)).forall(a => a))
      }
    }
  }

  test("Workspace Manager multiworkspace support - add workspace") {
    withServer[Assertion] { server =>
      val ws1path  = s"${filePath("multiworkspace/ws1")}"
      val filesWS1 = List(s"${ws1path}/api.raml", s"${ws1path}/sub/type.raml", s"${ws1path}/type.json")
      val ws2path  = s"${filePath("multiworkspace/ws2")}"
      val filesWS2 = List(s"${ws2path}/api.raml", s"${ws2path}/sub/type.raml")
      val ws1      = WorkspaceFolder(Some(ws1path), Some("ws1"))
      val ws2      = WorkspaceFolder(Some(ws2path), Some("ws2"))

      for {
        _ <- server.initialize(AlsInitializeParams(None, Some(TraceKind.Off), rootUri = Some(ws1path)))
        a <- diagnosticClientNotifier.nextCall
        b <- diagnosticClientNotifier.nextCall
        c <- diagnosticClientNotifier.nextCall
        _ <- addWorkspaceFolder(server)(ws2)
        d <- diagnosticClientNotifier.nextCall
        e <- diagnosticClientNotifier.nextCall
      } yield {
        val firstDiagnostics  = List(a, b, c)
        val secondDiagnostics = List(d, e)

        assert(firstDiagnostics.size == firstDiagnostics.map(_.uri).distinct.size)
        assert(firstDiagnostics.map(_.uri).containsSlice(filesWS1))

        assert(secondDiagnostics.size == secondDiagnostics.map(_.uri).distinct.size)
        assert(secondDiagnostics.map(_.uri).containsSlice(filesWS2))

      }
    }
  }

  test("Workspace Manager multiworkspace support - remove workspace") {
    withServer[Assertion] { server =>
      val root  = s"${filePath("multiworkspace/ws-error-stack-1")}"
      val file1 = s"${filePath("multiworkspace/ws-error-stack-1/api.raml")}"
      val file2 = s"${filePath("multiworkspace/ws-error-stack-1/external1.yaml")}"
      val file3 = s"${filePath("multiworkspace/ws-error-stack-1/external2.yaml")}"

      val rootWSF = WorkspaceFolder(Some(root), Some("ws-error-stack-1"))

      for {
        _ <- server.initialize(AlsInitializeParams(None, Some(TraceKind.Off), rootUri = Some(root)))
        a <- diagnosticClientNotifier.nextCall
        b <- diagnosticClientNotifier.nextCall
        c <- diagnosticClientNotifier.nextCall
        _ <- removeWorkspaceFolder(server)(rootWSF)
        d <- diagnosticClientNotifier.nextCall
        e <- diagnosticClientNotifier.nextCall
        f <- diagnosticClientNotifier.nextCall
      } yield {
        val firstDiagnostics  = Seq(a, b, c)
        val secondDiagnostics = Seq(d, e, f).map(_.uri)

        verifyWS1ErrorStack(root, firstDiagnostics)
        d.diagnostics shouldBe empty
        e.diagnostics shouldBe empty
        f.diagnostics shouldBe empty
        secondDiagnostics should contain(file1)
        secondDiagnostics should contain(file2)
        secondDiagnostics should contain(file3)

      }
    }
  }

  test("Workspace Manager multiworkspace support - included workspace") {
    withServer[Assertion] { server =>
      val root1 = s"${filePath("multiworkspace/containedws")}"
      val root2 = s"${filePath("multiworkspace/containedws/ws1")}"
      val file1 = s"${filePath("multiworkspace/containedws/api.raml")}"
      val file2 = s"${filePath("multiworkspace/containedws/ws1/api.raml")}"
      val file3 = s"${filePath("multiworkspace/containedws/ws1/sub/type.raml")}"

      val root2WSF = WorkspaceFolder(Some(root2), Some("ws-2"))
      val root1WSF = WorkspaceFolder(Some(root1), Some("ws-1"))

      for {
        _ <- server.initialize(AlsInitializeParams(None, Some(TraceKind.Off), rootUri = Some(root2)))
        a <- diagnosticClientNotifier.nextCall
        b <- diagnosticClientNotifier.nextCall
        _ <- didChangeWorkspaceFolders(server)(List(root1WSF), List())
        c <- diagnosticClientNotifier.nextCall
        d <- diagnosticClientNotifier.nextCall
        e <- diagnosticClientNotifier.nextCall
        f <- diagnosticClientNotifier.nextCall

      } yield {
        val firstDiagnostics         = Seq(a, b).map(_.uri)
        val secondDiagnostics        = Seq(c, d, e, f)
        val secondDiagnosticsFolders = Seq(c, d, e, f).map(_.uri)

        firstDiagnostics should contain(file2)
        firstDiagnostics should contain(file3)
        secondDiagnosticsFolders should contain(file1)
        secondDiagnosticsFolders should contain(file2)
        secondDiagnostics.filter(_.uri.equals(file2)).head.diagnostics shouldBe empty
        secondDiagnosticsFolders should contain(file3)

      }
    }
  }

  test("Workspace Manager multiworkspace support - multiple included workspaces") {
    withServer[Assertion] { server =>
      val root1      = s"${filePath("multiworkspace/ws1")}"
      val root2      = s"${filePath("multiworkspace/ws2")}"
      val globalRoot = s"${filePath("multiworkspace")}"

      val filesWS1      = List(s"${root1}/api.raml", s"${root1}/sub/type.raml", s"${root1}/type.json")
      val filesWS2      = List(s"${root2}/api.raml", s"${root2}/sub/type.raml")
      val filesGlobalWS = List(s"${globalRoot}/api.raml")

      val root2WSF  = WorkspaceFolder(Some(root2), Some("ws-2"))
      val root1WSF  = WorkspaceFolder(Some(root1), Some("ws-1"))
      val globalWSF = WorkspaceFolder(Some(globalRoot), Some("global"))
      for {
        _ <- server.initialize(
          AlsInitializeParams(None,
                              Some(TraceKind.Off),
                              rootUri = Some(root2),
                              workspaceFolders = Some(Seq(root1WSF, root2WSF))))
        a <- diagnosticClientNotifier.nextCall
        b <- diagnosticClientNotifier.nextCall
        c <- diagnosticClientNotifier.nextCall
        d <- diagnosticClientNotifier.nextCall
        e <- diagnosticClientNotifier.nextCall
        _ <- didChangeWorkspaceFolders(server)(List(globalWSF), List())
        f <- diagnosticClientNotifier.nextCall
        g <- diagnosticClientNotifier.nextCall
        h <- diagnosticClientNotifier.nextCall
        i <- diagnosticClientNotifier.nextCall
        j <- diagnosticClientNotifier.nextCall
        k <- diagnosticClientNotifier.nextCall
      } yield {
        val firstDiagnostics         = Seq(a, b, c, d, e).map(_.uri)
        val secondDiagnostics        = Seq(f, g, h, i, j, k)
        val secondDiagnosticsFolders = secondDiagnostics.map(_.uri)

        firstDiagnostics should contain allElementsOf filesWS1
        firstDiagnostics should contain allElementsOf filesWS2
        secondDiagnosticsFolders should contain allElementsOf filesWS1
        secondDiagnosticsFolders should contain allElementsOf filesWS2
        secondDiagnosticsFolders should contain allElementsOf filesGlobalWS
        secondDiagnostics.flatMap(d => d.diagnostics) shouldBe empty

      }
    }
  }

  override def buildServer(): LanguageServer = {
    new LanguageServerBuilder(factory.documentManager, factory.workspaceManager)
      .addRequestModule(factory.structureManager)
      .build()
  }

  override def rootPath: String = "workspace"
}
