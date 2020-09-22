package org.mulesoft.amfintegration.relationships

import org.mulesoft.als.common.cache.YPartBranchCached
import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.lsp.feature.common.Location

case class AliasInfo(tag: String, declaration: Location, target: String) {
  def keyRange(yPartBranchCached: YPartBranchCached): Option[PositionRange] =
    yPartBranchCached
      .getCachedOrNew(PositionRange(declaration.range).start, declaration.uri)
      .parentEntry
      .map(_.key)
      .map(_.range)
      .map(r => PositionRange(r))
}
