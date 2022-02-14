package org.mulesoft.als.actions

import org.mulesoft.als.actions.hover.HoverAction
import org.mulesoft.als.common.YamlWrapper._
import org.mulesoft.als.common.cache.{ObjectInTreeCached, YPartBranchCached}
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.common.{BaseHoverTest, PositionedHover}
import org.mulesoft.amfintegration.AmfImplicits.BaseUnitImp
import org.mulesoft.amfintegration.amfconfiguration.{
  ALSConfigurationState,
  AmfParseResult,
  EditorConfiguration,
  EmptyProjectConfigurationState
}
import org.scalatest.{Assertion, AsyncFunSuite}
import org.yaml.model._

import scala.concurrent.{ExecutionContext, Future}

class HoverActionTest extends AsyncFunSuite with BaseHoverTest {
  override implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global
  val path                                                 = "als-actions/shared/src/test/resources/actions/hover/"

  test("Complete Raml 1.0 test") {
    runTest("raml10-full.raml", "raml10-full-result.yaml")
  }

  test("Complete oas 2.0 test") {
    runTest("oas20-full.yaml", "oas20-full-result.yaml")
  }

  test("Complete oas 3.0 test") {
    runTest("oas30-full.yaml", "oas30-full-result.yaml")
  }

  test("Complete asyncapi 2.0 test") {
    runTest("async-api20-full.yaml", "async-api20-full-result.yaml")
  }

  test("Complete raml 0.8 test") {
    runTest("raml08-full.raml", "raml08-full-result.raml")
  }

  test("raml 1.0 library test") {
    runTest("library.raml", "library-result.yaml")
  }

  private def runTest(file: String, golden: String): Future[Assertion] = {
    val filePath   = s"file://$path$file"
    val goldenPath = s"file://$path$golden"
    for {
      editorState <- EditorConfiguration().getState
      global      <- Future(ALSConfigurationState(editorState, EmptyProjectConfigurationState, None))
      d           <- global.parse(filePath)
      r <- Future {
        getResults(d, global)
      }
      y <- compareResults(goldenPath, r)
    } yield y
  }

  private def getResults(r: AmfParseResult, alsConfigurationState: ALSConfigurationState): List[PositionedHover] = {
    val positions: List[Position] = r.result.baseUnit.ast.map(extract).getOrElse(List.empty)
    val yPart                     = new YPartBranchCached(r.result.baseUnit)
    val value                     = r.definedBy // always is going to exists in this test and if not, the exception is an advice good enough.
    val cached                    = new ObjectInTreeCached(r.result.baseUnit, value)
    positions.map { p =>
      val hover =
        HoverAction(r.result.baseUnit,
                    cached,
                    yPart,
                    p,
                    r.location,
                    alsConfigurationState.editorState.vocabularyRegistry,
                    value).getHover
      PositionedHover(p, hover)
    }
  }

  private def extract(yPart: YPart): List[Position] = {
    yPart match {
      case m: YMap         => extractPositionsMap(m)
      case s: YSequence    => extractPositionsSeq(s)
      case n: YNode        => extractPositionsNode(n)
      case scalar: YScalar => extractPositionsScalar(scalar)
      case e: YMapEntry    => extractPositionsEntries(e)
      case _               => List.empty
    }
  }

  private def extractPositionsMap(map: YMap): List[Position] = map.entries.flatMap(extractPositionsEntries).toList

  private def extractPositionsSeq(seq: YSequence): List[Position] = seq.nodes.flatMap(extractPositionsNode).toList

  private def extractPositionsNode(node: YNode): List[Position] = {
    val range          = node.range.toPositionRange
    val extractedValue = extract(node.value)
    if (node.asScalar.isDefined) extractedValue
    else {
      val withStart =
        if (node.range.lineFrom == node.value.range.lineFrom && node.range.columnFrom == node.value.range.columnFrom)
          extractedValue
        else range.start +: extractedValue
      if (node.range.lineTo == node.value.range.lineTo && node.range.columnTo == node.value.range.columnTo) withStart
      else withStart :+ range.end
    }
  }

  private def extractPositionsScalar(scalar: YScalar): List[Position] = {
    val pos =
      if (scalar.range.lineFrom == scalar.range.lineTo)
        Position(scalar.range.lineFrom - 1, (scalar.range.columnFrom + scalar.range.columnTo) / 2)
      else Position((scalar.range.lineFrom + scalar.range.lineTo) / 2 - 1, scalar.range.columnTo)
    List(pos)
  }
  private def extractPositionsEntries(entry: YMapEntry): List[Position] = {
    extractPositionsNode(entry.key) ++ extractPositionsNode(entry.value)
  }
}
