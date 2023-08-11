package org.mulesoft.als.common

import org.mulesoft.als.common.diff.FileAssertionTest
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.logger.{EmptyLogger, Logger}
import org.mulesoft.lsp.feature.hover.Hover
import org.scalatest.compatible.Assertion
import org.yaml.model.YDocument
import org.yaml.model.YDocument.EntryBuilder
import org.yaml.render.YamlRender

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait BaseHoverTest extends FileAssertionTest {
  protected val printRange: Boolean = false
  protected def renderHoverResult(hovers: Seq[PositionedHover]): String = {
    Logger.withLogger(EmptyLogger)
    val doc = YDocument.objFromBuilder(e => {
      e.entry(
        "hovers",
        p => {
          p.obj(eb =>
            hovers.foreach { hp =>
              if (printRange) hp.addEntryWithRange(eb) else hp.addEntry(eb)
            }
          )
        }
      )
    })
    YamlRender.render(doc)
  }

  protected def compareResults(goldenPath: String, hovers: Seq[PositionedHover]): Future[Assertion] = {
    for {
      str <- Future { renderHoverResult(hovers) }
      tmp <- writeTemporaryFile(goldenPath)(str)
      r   <- assertDifferences(tmp, goldenPath)
    } yield r
  }
}

case class PositionedHover(position: Position, h: Hover) {
  def addEntry(b: EntryBuilder): Unit = {
    b.entry(
      position.toAmfPosition.toString,
      pb => {
        pb.list(inner => h.contents.foreach(c => inner += c))
      }
    )
  }

  def addEntryWithRange(b: EntryBuilder): Unit = {
    b.entry(
      position.toAmfPosition.toString,
      pb => {
        pb.obj(eb => {
          h.range.foreach(r => eb.entry("range", r.toString))
          eb.entry(
            "contents",
            lb => {
              lb.list(inner => h.contents.foreach(c => inner += c))
            }
          )
        })
      }
    )
  }
}
