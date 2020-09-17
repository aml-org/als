package org.mulesoft.amfintegration

import amf.core.annotations.LexicalInformation
import amf.core.parser
import amf.core.parser.FieldEntry
import org.mulesoft.amfintegration.AmfImplicits.AmfAnnotationsImp

object FieldEntryOrdering extends Ordering[FieldEntry] {
  override def compare(x: FieldEntry, y: FieldEntry): Int = {
    val tuple: Option[(parser.Range, parser.Range)] = for {
      xRange <- x.value.annotations
        .lexicalInformation()
        .orElse(x.value.value.annotations.lexicalInformation())
        .map(_.range)
      yRange <- y.value.annotations
        .lexicalInformation()
        .orElse(y.value.value.annotations.lexicalInformation())
        .map(_.range)
    } yield (xRange, yRange)

    tuple match {
      case Some((xRange, yRange)) =>
        val start = xRange.start.compareTo(yRange.start)
        if (start == 0) yRange.end.compareTo(xRange.end)
        else start
      case _ => 0
    }
  }
}
