package org.mulesoft.als.suggestions.patcher

abstract class PatchToken(token: String)

object ColonToken extends PatchToken(":")
object QuoteToken extends PatchToken("\"")
object CommaToken extends PatchToken(",")

case class PatchedContent(content: String, original: String, addedTokens: List[PatchToken])
