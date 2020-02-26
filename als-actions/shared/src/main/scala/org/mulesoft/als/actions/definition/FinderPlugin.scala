package org.mulesoft.als.actions.definition

import amf.core.model.document.BaseUnit
import amf.core.remote.Platform
import org.mulesoft.als.actions.definition.types.FindDefinitionTypes
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.common.{ObjectInTree, YPartBranch}
import org.mulesoft.lsp.feature.common.LocationLink

trait FinderPlugin {
  def find(bu: BaseUnit,
           position: Position,
           objectInTree: ObjectInTree,
           yPartBranch: YPartBranch,
           platform: Platform): Seq[LocationLink]
}

object AllFinders {
  def get: Seq[FinderPlugin] = Seq(FindDefinitionTypes)
}