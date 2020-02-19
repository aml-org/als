package org.mulesoft.als.actions.definition.files

import amf.core.model.document.BaseUnit
import amf.core.remote.Platform
import org.mulesoft.als.actions.common.ActionTools
import org.mulesoft.als.actions.definition.FinderPlugin
import org.mulesoft.als.common._
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.lsp.feature.common.LocationLink
import org.yaml.model.YNode

object FindDefinitionFile extends FinderPlugin {
  def find(bu: BaseUnit,
           position: Position,
           objectInTree: ObjectInTree,
           yPartBranch: YPartBranch,
           platform: Platform): Seq[LocationLink] = {
    yPartBranch.node match {
      case alias: YNode.Alias =>
        Seq(
          ActionTools
            .locationToLsp(alias.location, alias.target.location, platform))
      case _ => Nil
    }
  }
}
