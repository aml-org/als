package org.mulesoft.language.outline.structure.structureDefaultInterfaces

import org.mulesoft.language.outline.structure.structureInterfaces.StructureNode

/**
  * Node having a type.
  */
trait TypedStructureNode extends StructureNode {

  /**
    * Node type.
    * @return
    */
  def nodeType: String
}
