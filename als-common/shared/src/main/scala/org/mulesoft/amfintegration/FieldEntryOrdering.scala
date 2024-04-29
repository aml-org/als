package org.mulesoft.amfintegration

import org.mulesoft.common.client.lexical.{PositionRange => AmfPositionRange}
import amf.core.internal.parser.domain.FieldEntry
import org.mulesoft.amfintegration.AmfImplicits.AmfAnnotationsImp

object FieldEntryOrdering extends Ordering[FieldEntry] {
  override def compare(x: FieldEntry, y: FieldEntry): Int = {
    val tuple: Option[(AmfPositionRange, AmfPositionRange)] = for {
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
