package org.mulesoft.als.actions.links

import amf.core.annotations.ReferenceTargets
import amf.core.model.document.BaseUnit
import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.lsp.feature.link.DocumentLink

object FindLinks {
  def getLinks(bu: BaseUnit): Seq[DocumentLink] =
    bu.annotations
      .collect { case rt: ReferenceTargets => rt }
      .map { rt =>
        DocumentLink(LspRangeConverter.toLspRange(PositionRange(rt.originRange)), rt.targetLocation, None)
      }
}
