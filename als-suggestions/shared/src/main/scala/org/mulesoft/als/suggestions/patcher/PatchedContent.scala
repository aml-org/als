package org.mulesoft.als.suggestions.patcher

abstract class PatchToken(token: String, val size: Int)

object ColonToken extends PatchToken(":", 2)
object QuoteToken extends PatchToken("\"", 1)
object CommaToken extends PatchToken(",", 1)

case class PatchedContent(content: String, original: String, addedTokens: List[PatchToken])
