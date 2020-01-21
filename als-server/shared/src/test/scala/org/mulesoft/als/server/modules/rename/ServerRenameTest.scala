package org.mulesoft.als.server.modules.rename

import org.mulesoft.als.common.dtoTypes.{DescendingPositionOrdering, Position}
import org.mulesoft.als.server.modules.WorkspaceManagerFactoryBuilder
import org.mulesoft.als.server.{LanguageServerBaseTest, LanguageServerBuilder, MockDiagnosticClientNotifier}
import org.mulesoft.als.suggestions.interfaces.Syntax.YAML
import org.mulesoft.als.suggestions.patcher.{ContentPatcher, PatchedContent}
import org.mulesoft.lsp.common.TextDocumentIdentifier
import org.mulesoft.lsp.convert.LspRangeConverter
import org.mulesoft.lsp.feature.rename.{RenameParams, RenameRequestType}
import org.mulesoft.lsp.server.LanguageServer
import org.scalatest.Assertion

import scala.concurrent.{ExecutionContext, Future}

abstract class ServerRenameTest extends LanguageServerBaseTest {

  override implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  override def buildServer(): LanguageServer = {
    val factory =
      new WorkspaceManagerFactoryBuilder(new MockDiagnosticClientNotifier, logger).buildWorkspaceManagerFactory()
    new LanguageServerBuilder(factory.documentManager, factory.workspaceManager)
      .addInitializable(factory.documentManager)
      .build()
  }

  def runTest(path: String, newName: String): Future[Assertion] = withServer[Assertion] { server =>
    val resultPath                     = path.replace(".", "-renamed.")
    val resolved                       = filePath(path)
    val resolvedResultPath             = filePath(resultPath)
    var renamedContent: Option[String] = None
    var content: Option[String]        = None

    Future
      .sequence(List(platform.resolve(resolved), platform.resolve(resolvedResultPath)))
      .flatMap(contents => {

        val fileContentsStr        = contents.head.stream.toString
        val renamedFileContentsStr = contents.last.stream.toString
        renamedContent = Option(renamedFileContentsStr.trim)
        val markerInfo = this.findMarker(fileContentsStr)
        content = Option(markerInfo.patchedContent.original)
        val position = markerInfo.position

        val filePath = s"file:///$path"
        openFile(server)(filePath, markerInfo.patchedContent.original)
        val handler = server.resolveHandler(RenameRequestType).value

        handler(RenameParams(TextDocumentIdentifier(filePath), LspRangeConverter.toLspPosition(position), newName))
          .map(workspaceEdit => {
            closeFile(server)(filePath)

            val edits = workspaceEdit.changes.flatMap { case (_, textEdits) => textEdits }.toList

            var newText = content.get
            edits
              .sortBy(edit => LspRangeConverter.toPosition(edit.range.start))(DescendingPositionOrdering)
              .foreach(edit =>
                newText = newText.substring(0, LspRangeConverter.toPosition(edit.range.start).offset(newText)) +
                  edit.newText +
                  newText.substring(LspRangeConverter.toPosition(edit.range.end).offset(newText)))
            val result = renamedContent.contains(newText.trim)

            if (result) succeed
            else fail(s"Difference for $path: got [$newText] while expecting [${renamedContent.get}]")
          })
      })
  }

  def findMarker(str: String, label: String = "*", cut: Boolean = true): MarkerInfo = {

    val offset = str.indexOf(label)

    if (offset < 0) {
      new MarkerInfo(PatchedContent(str, str, Nil), Position(str.length, str))
    } else {
      val rawContent      = str.substring(0, offset) + str.substring(offset + 1)
      val preparedContent = ContentPatcher(rawContent, offset, YAML).prepareContent()
      new MarkerInfo(preparedContent, Position(offset, str))
    }

  }

  class MarkerInfo(val patchedContent: PatchedContent, val position: Position)
}
