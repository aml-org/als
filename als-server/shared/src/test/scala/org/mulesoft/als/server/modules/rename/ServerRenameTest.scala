package org.mulesoft.als.server.modules.rename

import amf.core.remote.Platform
import amf.internal.environment.Environment
import org.mulesoft.als.common.DirectoryResolver
import org.mulesoft.als.common.dtoTypes.{DescendingPositionOrdering, Position}
import org.mulesoft.als.server.modules.ast.AstManager
import org.mulesoft.als.server.modules.common.LspConverter
import org.mulesoft.als.server.modules.common.LspConverter.toPosition
import org.mulesoft.als.server.textsync.TextDocumentManager
import org.mulesoft.als.server.{LanguageServerBaseTest, LanguageServerBuilder}
import org.mulesoft.als.suggestions.interfaces.Syntax.YAML
import org.mulesoft.lsp.common.TextDocumentIdentifier
import org.mulesoft.lsp.feature.rename.{RenameParams, RenameRequestType}
import org.scalatest.Assertion

import scala.concurrent.{ExecutionContext, Future}

abstract class ServerRenameTest extends LanguageServerBaseTest {

  override implicit val executionContext = ExecutionContext.Implicits.global

  override def addModules(documentManager: TextDocumentManager,
                          platform: Platform,
                          directoryResolver: DirectoryResolver,
                          baseEnvironment: Environment,
                          builder: LanguageServerBuilder): LanguageServerBuilder = {

    val astManager = new AstManager(documentManager, baseEnvironment, platform, logger)

    builder
      .addInitializable(astManager)
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
        content = Option(markerInfo.rawContent)
        val position = markerInfo.position

        val filePath = s"file:///$path"
        openFile(server)(filePath, markerInfo.rawContent)
        val handler = server.resolveHandler(RenameRequestType).value

        handler(RenameParams(TextDocumentIdentifier(filePath), LspConverter.toLspPosition(position), newName))
          .map(workspaceEdit => {
            closeFile(server)(filePath)

            val edits = workspaceEdit.changes.flatMap { case (_, textEdits) => textEdits }.toList

            var newText = content.get
            edits
              .sortBy(edit => toPosition(edit.range.start))(DescendingPositionOrdering)
              .foreach(edit =>
                newText = newText.substring(0, toPosition(edit.range.start).offset(newText)) +
                  edit.newText +
                  newText.substring(toPosition(edit.range.end).offset(newText)))
            val result = renamedContent.contains(newText.trim)

            if (result) succeed
            else fail(s"Difference for $path: got [$newText] while expecting [${renamedContent.get}]")
          })
      })
  }

  def findMarker(str: String, label: String = "*", cut: Boolean = true): MarkerInfo = {

    val offset = str.indexOf(label)

    if (offset < 0) {
      new MarkerInfo(str, Position(str.length, str), str)
    } else {
      val rawContent = str.substring(0, offset) + str.substring(offset + 1)
      val preparedContent =
        org.mulesoft.als.suggestions.Core.prepareText(rawContent, offset, YAML)
      new MarkerInfo(preparedContent, Position(offset, str), rawContent)
    }

  }

  class MarkerInfo(val content: String, val position: Position, val rawContent: String)
}
