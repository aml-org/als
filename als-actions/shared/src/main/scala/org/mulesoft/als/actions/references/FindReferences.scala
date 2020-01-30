package org.mulesoft.als.actions.references

import amf.core.model.document.BaseUnit
import org.mulesoft.als.common.dtoTypes.{ReferenceOrigins, ReferenceStack}
import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.lsp.feature.common.Location

object FindReferences {
  def getReferences(bu: BaseUnit, references: Seq[ReferenceStack]): Seq[Location] =
    references
      .flatMap(
        _.stack.headOption
          .map(referenceOriginToLocation))

  private def referenceOriginToLocation(ro: ReferenceOrigins) =
    Location(ro.originUri, LspRangeConverter.toLspRange(ro.originRange))
}
