package org.mulesoft.language.outline.common.commonInterfaces

import org.mulesoft.high.level.interfaces.IParseResult

trait ParserResultFilter {

  def apply(node: IParseResult): Boolean
}
