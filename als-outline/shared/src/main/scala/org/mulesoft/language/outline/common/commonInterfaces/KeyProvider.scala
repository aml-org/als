package org.mulesoft.language.outline.common.commonInterfaces

import org.mulesoft.high.level.interfaces.IParseResult

/**
  * Provides key for the node
  */
trait KeyProvider {

  /**
    * Returns key for the node
    * @param node
    * @return
    */
  def getKey(node: IParseResult): String
}
