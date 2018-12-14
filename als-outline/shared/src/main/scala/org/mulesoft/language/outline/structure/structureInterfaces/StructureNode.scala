package org.mulesoft.language.outline.structure.structureInterfaces

import org.mulesoft.high.level.interfaces.IParseResult

/**
  * Arbitrary structure tree node.
  * Depending on the highly customizable filters and
  * look-and-feel providers results in a read-to-display structure tree
  * for the document.
  */
trait StructureNode extends StructureNodeJSON {

  /**
    * Node children.
    */
  def children: Seq[StructureNode]

  /**
    * Returns structure node source.
    */
  def getSource: IParseResult

  /**
    * Converts structure node and its children recursivelly into JSON, containing
    * text, icon and children fields.
    */
  def toJSON: StructureNodeJSON
}
