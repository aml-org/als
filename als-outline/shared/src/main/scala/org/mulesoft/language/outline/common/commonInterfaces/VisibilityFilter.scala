package org.mulesoft.language.outline.common.commonInterfaces

import org.mulesoft.high.level.interfaces.IParseResult

/**
  * Can hide nodes from the resulting tree.
  */
trait VisibilityFilter {

  /**
    * Allows blocking some nodes from being added to the structure tree, on top of what
    * StructureBuilder returns.
    * @param node
    */
  def apply(node: IParseResult): Boolean
}
