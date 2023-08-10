package org.mulesoft.amfintegration.visitors.links

import amf.core.client.scala.model.document.BaseUnit
import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.amfintegration.AmfImplicits.AmfAnnotationsImp
import org.mulesoft.lsp.feature.link.DocumentLink

object FindLinks {
  def getLinks(bu: BaseUnit): Seq[DocumentLink] =
    bu.annotations
      .targets()
      .flatMap { case (targetLocation, originRange) =>
//        originRange.foreach(or => log.debug(s"FIND LINKS $targetLocation => $or", "FindLinks", "getLinks"))
        originRange.map(r => DocumentLink(LspRangeConverter.toLspRange(PositionRange(r)), targetLocation, None))
      }
      .toSeq
}
