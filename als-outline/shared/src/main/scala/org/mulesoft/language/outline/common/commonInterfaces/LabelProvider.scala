package org.mulesoft.language.outline.common.commonInterfaces

import org.mulesoft.high.level.interfaces.IParseResult

/**
  * Constructs node text for high-level node.
  */
trait LabelProvider {

  /**
    * Gets label (text) for a high-level node.
    *
    * @param node
    */
  def getLabelText(node: IParseResult): String

  /**
    * Gets type text for a high-level node.
    *
    * @param node
    */
  def getTypeText(node: IParseResult): Option[String]
}
