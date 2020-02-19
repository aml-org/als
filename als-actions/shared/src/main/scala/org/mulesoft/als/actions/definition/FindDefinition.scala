package org.mulesoft.als.actions.definition

import amf.core.model.document.BaseUnit
import amf.core.remote.Platform
import org.mulesoft.als.common.{NodeBranchBuilder, ObjectInTree, ObjectInTreeBuilder, YPartBranch, YamlUtils}
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.lsp.feature.common.LocationLink

/**
  * Cases in which to return a Location:
  * * root key "uses" indicates there is a map with libraries (whose values are relative paths)
  * * SYAML MutRefs will be returned as location
  * *  [X] if clicked on a URI it will also return this as location
  */
object FindDefinition {
  def getDefinition(bu: BaseUnit, position: Position, platform: Platform): Seq[LocationLink] = {
    val objectInTree: ObjectInTree = ObjectInTreeBuilder.fromUnit(bu, position.toAmfPosition)
    val yPartBranch: YPartBranch =
      NodeBranchBuilder.build(bu, position.toAmfPosition, YamlUtils.isJson(bu))

    AllFinders.get.flatMap(_.find(bu, position, objectInTree, yPartBranch, platform))
  }
}
