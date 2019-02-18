package org.mulesoft.language.outline.structure.structureInterfaces

import org.mulesoft.language.outline.structure.structureImpl.DocumentSymbol

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
  def buildChildren(node: StructureNode): Seq[DocumentSymbol]
}
