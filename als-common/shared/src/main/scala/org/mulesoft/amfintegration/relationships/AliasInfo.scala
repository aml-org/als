package org.mulesoft.amfintegration.relationships

import org.mulesoft.als.common.{ASTPartBranch, YPartBranch}
import org.mulesoft.als.common.cache.ASTPartBranchCached
import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.lsp.feature.common.Location

case class AliasInfo(tag: String, declaration: Location, target: String) {
  def keyRange(yPartBranchCached: ASTPartBranchCached): Option[PositionRange] =
    yPartBranchCached
      .getCachedOrNew(PositionRange(declaration.range).start, declaration.uri) match {
      case partBranch: YPartBranch =>
        partBranch.parentEntry
          .map(_.key)
          .map(_.range)
          .map(r => PositionRange(r))
      case _ => None
    }

}
