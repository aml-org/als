package org.mulesoft.language.outline.common.commonInterfaces

import org.mulesoft.high.level.interfaces.IParseResult

/**
  * Decorates node
  */
trait Decorator {

  /**
    * Gets node for the icon
    *
    * @param node
    * @return
    */
  def getIcon(node: IParseResult): Option[String]

  /**
    * Gets text style for the icon
    *
    * @param node
    * @return
    */
  def getTextStyle(node: IParseResult): Option[String]
}
