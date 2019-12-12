package org.mulesoft.als.actions.links

import amf.core.annotations.ReferenceTargets
import amf.core.model.document.BaseUnit
import amf.core.remote.Platform
import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.lsp.convert.LspRangeConverter
import org.mulesoft.lsp.feature.link.DocumentLink

object FindLinks {
  def getLinks(bu: BaseUnit, platform: Platform): Seq[DocumentLink] =
    bu.annotations
      .collect { case rt: ReferenceTargets => rt }
      .map { rt =>
        DocumentLink(LspRangeConverter.toLspRange(PositionRange(rt.originRange)), rt.targetLocation, None)
      }
}
