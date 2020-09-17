package org.mulesoft.als.actions

import amf.core.AMFSerializer
import amf.core.emitter.RenderOptions
import amf.core.remote._
import org.mulesoft.als.actions.hover.HoverAction
import org.mulesoft.als.common.YamlWrapper
import org.mulesoft.als.common.YamlWrapper._
import org.mulesoft.als.common.cache.{ObjectInTreeCached, YPartBranchCached}
import org.mulesoft.als.common.diff.FileAssertionTest
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.amfintegration.AmfImplicits.BaseUnitImp
import org.mulesoft.amfintegration.{AmfInstance, AmfParseResult, InitOptions}
import org.mulesoft.lsp.feature.hover.Hover
import org.scalatest.{Assertion, AsyncFunSuite}
import org.yaml.model.YDocument.EntryBuilder
import org.yaml.model._
import org.yaml.render.YamlRender

import scala.concurrent.{ExecutionContext, Future}

class HoverActionTest extends AsyncFunSuite with FileAssertionTest {
  override implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global
  val path                                                 = "als-actions/shared/src/test/resources/actions/hover/"

  test("Complete Raml 1.0 test") {
    runTest("raml10-full.raml", "raml10-full-result.yaml", Raml10)
  }

  test("Complete oas 2.0 test") {
    runTest("oas20-full.yaml", "oas20-full-result.yaml", Oas20)
  }

  test("Complete oas 3.0 test") {
    runTest("oas30-full.yaml", "oas30-full-result.yaml", Oas30)
  }

  test("Complete asyncapi 2.0 test") {
    runTest("async-api20-full.yaml", "async-api20-full-result.yaml", AsyncApi20)
  }

  test("Complete raml 0.8 test") {
    runTest("raml08-full.raml", "raml08-full-result.raml", Raml08)
  }

  test("raml 1.0 library test") {
    runTest("library.raml", "library-result.yaml", Raml10)
  }

  private def runTest(file: String, golden: String, vendor: Vendor): Future[Assertion] = {
    val filePath    = s"file://${path}$file"
    val goldenPath  = s"file://${path}$golden"
    val amfInstance = AmfInstance.default
    for {
      _ <- amfInstance.init(new InitOptions(Set(vendor)))
      d <- amfInstance.parse(filePath)
      r <- Future {
        val hovers = getResults(d, amfInstance)
        val doc = YDocument.objFromBuilder(e => {
          e.entry("hovers", p => {
            p.obj(eb =>
              hovers.foreach { hp =>
                hp.addEntry(eb)
            })
          })
        })
        YamlRender.render(doc)
      }
      tmp <- writeTemporaryFile(goldenPath)(r)
      r   <- assertDifferences(tmp, goldenPath)
    } yield r
  }
  case class PositionedHover(position: Position, h: Hover) {
    def addEntry(b: EntryBuilder): Unit = {
      b.entry(position.toAmfPosition.toString, pb => {
        pb.list(inner => h.contents.foreach(c => inner += c))
      })
    }
  }

  private def getResults(r: AmfParseResult, amfInstance: AmfInstance): List[PositionedHover] = {
    val positions: List[Position] = r.baseUnit.ast.map(extract).getOrElse(List.empty)
    val yPart                     = new YPartBranchCached(r.baseUnit)
    val value                     = r.definedBy // always is going to exists in this test and if not, the exception is an advice good enough.
    val cached                    = new ObjectInTreeCached(r.baseUnit, value)
    positions.map { p =>
      val hover = HoverAction(r.baseUnit, cached, yPart, p, r.location, amfInstance, value).getHover
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
