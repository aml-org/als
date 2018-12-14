package org.mulesoft.language.outline.common.commonInterfaces

import org.mulesoft.high.level.interfaces.IParseResult

/**
  * Filter for actegory applicability
  */
trait CategoryFilter {

  /**
    * Checks whether current node is applicable to a category
    */
  def apply(node: IParseResult): Boolean
}
