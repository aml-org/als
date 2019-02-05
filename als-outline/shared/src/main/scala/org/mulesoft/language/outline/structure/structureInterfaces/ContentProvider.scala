package org.mulesoft.language.outline.structure.structureInterfaces

/**
  * Core tree builder.
  */
trait ContentProvider {

  /**
    * Builds node children
    *
    * @param node
    * @return
    */
  def buildChildren(node: StructureNode): Seq[StructureNode]
}
