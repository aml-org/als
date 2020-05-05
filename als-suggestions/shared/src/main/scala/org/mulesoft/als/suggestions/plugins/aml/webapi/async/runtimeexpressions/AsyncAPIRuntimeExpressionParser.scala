package org.mulesoft.als.suggestions.plugins.aml.webapi.async.runtimeexpressions

import org.mulesoft.als.suggestions.plugins.aml.webapi.runtimeexpression.{
  LabeledExpressionToken,
  RuntimeExpressionParser,
  RuntimeParsingToken,
  WithoutConstraint
}

import scala.util.matching.Regex

case class AsyncAPIRuntimeExpressionParser(override val value: String) extends RuntimeExpressionParser {
  override val rx: Regex  = "" r
  override val followedBy = Seq(MessageBaseExpressionToken)
}

case class MessageBaseExpressionToken(override val value: String) extends LabeledExpressionToken {
  override val label: String = "$message."
  override val rx: Regex     = "(\\$message\\.).*" r
  override val followedBy    = Seq(HeaderExpressionToken, PayloadExpressionToken)
}

case class HeaderExpressionToken(override val value: String) extends LabeledExpressionToken {
  override val label: String                                  = "header#"
  override val rx: Regex                                      = "(header#).*" r
  override val followedBy: Seq[String => RuntimeParsingToken] = Seq(FragmentExpressionToken)
}

case class PayloadExpressionToken(override val value: String) extends LabeledExpressionToken {
  override val label: String                                  = "payload#"
  override val rx: Regex                                      = "(payload#).*" r
  override val followedBy: Seq[String => RuntimeParsingToken] = Seq(FragmentExpressionToken)
}

case class FragmentExpressionToken(override val value: String) extends WithoutConstraint
