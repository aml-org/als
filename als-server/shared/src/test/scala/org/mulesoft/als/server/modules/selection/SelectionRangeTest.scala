package org.mulesoft.als.server.modules.selection

import amf.core.client.scala.AMFGraphConfiguration
import org.mulesoft.als.server.client.scala.LanguageServerBuilder
import org.mulesoft.als.server.modules.WorkspaceManagerFactoryBuilder
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.server.{LanguageServerBaseTest, MockDiagnosticClientNotifier}
import org.mulesoft.lsp.feature.RequestHandler
import org.mulesoft.lsp.feature.common.{Range, TextDocumentIdentifier, TextDocumentItem, Position => LspPosition}
import org.mulesoft.lsp.feature.selectionRange.{SelectionRange, SelectionRangeParams, SelectionRangeRequestType}
import org.mulesoft.lsp.textsync.DidOpenTextDocumentParams

import scala.concurrent.{ExecutionContext, Future}

class SelectionRangeTest extends LanguageServerBaseTest {
  override implicit val executionContext: ExecutionContext =
    ExecutionContext.Implicits.global

  override def rootPath: String = "actions/selection"

  def buildServer(): LanguageServer = {
    val factory =
      new WorkspaceManagerFactoryBuilder(new MockDiagnosticClientNotifier, logger).buildWorkspaceManagerFactory()
    new LanguageServerBuilder(
      factory.documentManager,
      factory.workspaceManager,
      factory.configurationManager,
      factory.resolutionTaskManager
    )
      .addRequestModule(factory.selectionRangeManager)
      .build()
  }

  test("Select the range on YAML map") {
    val expected: Seq[SelectionRange] =
      Seq(
        SelectionRangeBuilder(1, 0, 3, 0)
          .andThen(1, 0, 2, 0)
          .andThen(1, 7, 1, 11)
          .build()
      )

    runTest(buildServer(), "basic.raml", Seq(LspPosition(1, 8))).map { result =>
      result should be(expected)
    }
  }

  test("Select the range on JSON map") {
    val expected: Seq[SelectionRange] =
      Seq(
        SelectionRangeBuilder(0, 0, 9, 1)
          .andThen(2, 2, 8, 3)
          .andThen(5, 4, 7, 5)
          .andThen(6, 6, 6, 19)
          .andThen(6, 14, 6, 19)
          .build()
      )

    runTest(buildServer(), "basic.json", Seq(LspPosition(6, 18))).map { result =>
      result should be(expected)
    }
  }

  test("Select the key on YAML map") {
    val expected: Seq[SelectionRange] =
      Seq(
        SelectionRangeBuilder(1, 0, 3, 0)
          .andThen(1, 0, 2, 0)
          .andThen(1, 0, 1, 5)
          .build()
      )

    runTest(buildServer(), "basic.raml", Seq(LspPosition(1, 3))).map { result =>
      result should be(expected)
    }
  }

  test("Select the key on JSON map") {
    val expected: Seq[SelectionRange] =
      Seq(
        SelectionRangeBuilder(0, 0, 9, 1)
          .andThen(2, 2, 8, 3)
          .andThen(5, 4, 7, 5)
          .andThen(6, 6, 6, 19)
          .andThen(6, 6, 6, 12)
          .build()
      )

    runTest(buildServer(), "basic.json", Seq(LspPosition(6, 12))).map { result =>
      result should be(expected)
    }
  }

  test("Select the range on a YAML sequence") {
    val expected: Seq[SelectionRange] =
      Seq(
        SelectionRangeBuilder(0, 0, 21, 0)
          .andThen(15, 0, 19, 0)
          .andThen(16, 0, 19, 0)
          .andThen(17, 4, 17, 6)
          .build()
      )

    runTest(buildServer(), "complex.yaml", Seq(LspPosition(17, 5))).map { result =>
      result should be(expected)
    }
  }

  test("Select the range on a JSON sequence") {
    val expected: Seq[SelectionRange] =
      Seq(
        SelectionRangeBuilder(0, 0, 19, 1)
          .andThen(11, 2, 15, 3)
          .andThen(11, 13, 15, 3)
          .andThen(13, 4, 13, 8)
          .build()
      )

    runTest(buildServer(), "complex.json", Seq(LspPosition(13, 8))).map { result =>
      result should be(expected)
    }
  }

  test("Select the ranges on a YAML sequence with multicursor") {
    val expected: Seq[SelectionRange] =
      Seq(
        SelectionRangeBuilder(0, 0, 21, 0)
          .andThen(1, 0, 13, 0)
          .andThen(3, 2, 4, 0)
          .andThen(3, 2, 3, 7)
          .build(),
        SelectionRangeBuilder(0, 0, 21, 0)
          .andThen(15, 0, 19, 0)
          .andThen(16, 0, 19, 0)
          .andThen(17, 4, 17, 6)
          .build()
      )

    runTest(buildServer(), "complex.yaml", Seq(LspPosition(3, 6), LspPosition(17, 5))).map { result =>
      result should be(expected)
    }
  }

  test("Select the ranges on a JSON sequence with multicursor") {
    val expected: Seq[SelectionRange] =
      Seq(
        SelectionRangeBuilder(0, 0, 19, 1)
          .andThen(11, 2, 15, 3)
          .andThen(11, 13, 15, 3)
          .andThen(13, 4, 13, 8)
          .build(),
        SelectionRangeBuilder(0, 0, 19, 1)
          .andThen(2, 2, 8, 3)
          .andThen(5, 4, 7, 5)
          .andThen(6, 6, 6, 19)
          .andThen(6, 6, 6, 12)
          .build()
      )

    runTest(buildServer(), "complex.json", Seq(LspPosition(13, 8), LspPosition(6, 10))).map { result =>
      result should be(expected)
    }
  }

  test("Should stop on MutRef") {
    val expected: Seq[SelectionRange] =
      Seq(
        SelectionRangeBuilder(1, 0, 5, 0)
          .andThen(1, 0, 5, 0)
          .andThen(3, 2, 5, 0)
          .andThen(4, 4, 5, 0)
          .andThen(4, 8, 4, 29)
          .build(),
        SelectionRangeBuilder(1, 0, 5, 0)
          .andThen(1, 0, 5, 0)
          .andThen(3, 2, 5, 0)
          .andThen(4, 4, 5, 0)
          .andThen(4, 8, 4, 29)
          .andThen(4, 17, 4, 29)
          .build()
      )

    runTest(buildServer(), "import.raml", Seq(LspPosition(4, 13), LspPosition(4, 22))).map { result =>
      result should be(expected)
    }
  }

  def runTest(server: LanguageServer, fileName: String, positions: Seq[LspPosition]): Future[Seq[SelectionRange]] = {
    val fileUri = filePath(platform.encodeURI(fileName))

    for {
      content <- this.platform.fetchContent(fileUri, AMFGraphConfiguration.predefined())
      _ <- server.textDocumentSyncConsumer.didOpen(
        DidOpenTextDocumentParams(
          TextDocumentItem(
            filePath(fileName),
            "",
            0,
            content.stream.toString
          )
        )
      )
      selectionRange <- {
        val dhHandler: RequestHandler[SelectionRangeParams, Seq[SelectionRange]] =
          server.resolveHandler(SelectionRangeRequestType).get
        dhHandler(SelectionRangeParams(TextDocumentIdentifier(fileUri), positions))
      }
    } yield {
      selectionRange
    }
  }

  case class SelectionRangeBuilder(fromLine: Int, fromCol: Int, toLine: Int, toCol: Int) {
    private var internal: Option[SelectionRangeBuilder] = None
    def build(): SelectionRange =
      SelectionRange(Range(LspPosition(fromLine, fromCol), LspPosition(toLine, toCol)), internal.map(_.build()))

    protected def setParent(selectionRangeBuilder: SelectionRangeBuilder): Unit =
      internal = Some(selectionRangeBuilder)

    def andThen(fromLine: Int, fromCol: Int, toLine: Int, toCol: Int): SelectionRangeBuilder = {
      val builder = SelectionRangeBuilder(fromLine, fromCol, toLine, toCol)
      builder.setParent(this)
      builder
    }
  }
}
