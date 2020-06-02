package org.mulesoft.als.actions.links

import amf.core.annotations.ReferenceTargets
import amf.core.model.document.BaseUnit
import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.lsp.feature.link.DocumentLink
import org.mulesoft.amfmanager.AmfImplicits._

object FindLinks {
  def getLinks(bu: BaseUnit): Seq[DocumentLink] =
    bu.annotations
      .targets()
      .map {
        case (targetLocation, originRange) =>
          DocumentLink(LspRangeConverter.toLspRange(PositionRange(originRange)), targetLocation, None)
      }
      .toSeq
}
