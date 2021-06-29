package org.mulesoft.als.actions.rangeFormatting

import amf.core.client.common.position.{Range => AmfRange}
import amf.core.client.scala.validation.AMFValidationResult
import amf.core.internal.annotations.LexicalInformation
import org.mulesoft.als.actions.formatting.RangeFormatting
import org.mulesoft.als.common.diff.WorkspaceEditsTest
import org.mulesoft.als.common.{ByDirectoryTest, MarkerFinderTest, NodeBranchBuilder}
import org.mulesoft.amfintegration.ErrorsCollected
import org.mulesoft.common.io.{Fs, SyncFile}
import org.mulesoft.lexer.SourceLocation
import org.mulesoft.lsp.configuration.FormattingOptions
import org.mulesoft.lsp.edit.{TextEdit, WorkspaceEdit}
import org.yaml.model.{ParseErrorHandler, SyamlException, YDocument, YPart}

import scala.collection.mutable
import scala.concurrent.ExecutionContext

trait RangeFormattingTest extends ByDirectoryTest with MarkerFinderTest with WorkspaceEditsTest {

  override implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global

  def basePath: String

  def dir: SyncFile = Fs.syncFile(basePath)

  def parse(content: String)(implicit eh: ParseErrorHandler): YDocument

  def isJson: Boolean

  def filePath(path: String): String = {
    s"file://$basePath/$path"
      .replace('\\', '/')
      .replace("null/", "")
  }

  override def testFile(rawContent: String, file: SyncFile, parent: String): Unit = {
    s"Range formatting on ${file.name} at dir $parent${dir.name}" in {
      implicit val errorHandler: FormattingTestErrorHandler = new FormattingTestErrorHandler()
      val expectedUri                                       = file.parent + "/expected/" + file.name
      val markers                                           = findMarkers(rawContent)
      val content                                           = markers.headOption.map(_.content).getOrElse(rawContent)

      val ypart: YPart = markers.headOption
        .map(start => {
          assert(markers.length == 2)
          val ast = parse(start.content)
          val end = markers.tail.head
          NodeBranchBuilder.getAstForRange(ast, start.position.toAmfPosition, end.position.toAmfPosition, isJson)
        })
        .getOrElse(parse(content))

      val formattingOption = FormattingOptions(2, insertSpaces = true)
      val edits: Seq[TextEdit] =
        RangeFormatting(ypart, formattingOption, isJson, errorHandler.getErrors, Some(content)).format()

      writeTemporaryFile(expectedUri)(applyEdits(WorkspaceEdit(Some(Map(file.name -> edits)), None), Option(content)))
        .flatMap(tmp => assertDifferences(tmp, expectedUri))
    }
  }

  protected class FormattingTestErrorHandler() extends ParseErrorHandler {

    private val errors: mutable.LinkedHashSet[AMFValidationResult] = mutable.LinkedHashSet()

    override def handle(location: SourceLocation, e: SyamlException): Unit = synchronized {
      val result = AMFValidationResult(e.getMessage, "test", "-", None, "", lexical(location), None, None)
      if (!errors.contains(result)) {
        errors += result
        true
      } else false
    }

    private def lexical(loc: SourceLocation): Option[LexicalInformation] =
      Some(LexicalInformation(AmfRange(loc.inputRange)))

    def getErrors: ErrorsCollected = ErrorsCollected(errors.toList)

  }
}
