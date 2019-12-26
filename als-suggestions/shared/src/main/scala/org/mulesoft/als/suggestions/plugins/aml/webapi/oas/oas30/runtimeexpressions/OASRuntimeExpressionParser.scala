package org.mulesoft.als.suggestions.plugins.aml.webapi.oas.oas30.runtimeexpressions

import scala.collection.mutable
import scala.util.matching.Regex

case class OASRuntimeExpressionParser(override val value: String) extends RuntimeExpressionParser {
  override val rx: Regex = "" r
  override val followedBy = Seq(
    URLBaseExpressionToken,
    MethodBaseExpressionToken,
    StatusCodeBaseExpressionToken,
    ResponseCodeBaseExpressionToken,
    RequestCodeBaseExpressionToken
  )

  lazy val completeStack: Seq[RuntimeParsingToken] = {
    val aux: mutable.ListBuffer[RuntimeParsingToken] = mutable.ListBuffer()
    var token: Option[RuntimeParsingToken]           = Some(this)
    do {
      aux.append(token.get)
      token = token.get.next
    } while (token.isDefined)
    aux
  }
}

case class URLBaseExpressionToken(override val value: String) extends BaseLabeledExpressionToken {
  override val label: String = "$url"
  override val rx: Regex     = "(\\$url)" r
}

case class MethodBaseExpressionToken(override val value: String) extends BaseLabeledExpressionToken {
  override val label: String = "$method"
  override val rx: Regex     = "(\\$method)" r
}

case class StatusCodeBaseExpressionToken(override val value: String) extends BaseLabeledExpressionToken {
  override val label: String = "$statusCode"
  override val rx: Regex     = "(\\$statusCode)" r

}

case class NameExpressionToken(override val value: String) extends RuntimeParsingToken {
  override val rx: Regex                                      = "(.+)" r
  override val followedBy: Seq[String => RuntimeParsingToken] = Nil
}

case class FragmentExpressionToken(override val value: String) extends RuntimeParsingToken {
  override val rx: Regex                                      = "(.+)" r
  override val followedBy: Seq[String => RuntimeParsingToken] = Nil
}

case class TokenExpressionToken(override val value: String) extends RuntimeParsingToken {
  override val rx: Regex                                      = "(.+)" r
  override val followedBy: Seq[String => RuntimeParsingToken] = Nil
}

trait WithNameExpression extends LabeledExpressionToken {
  override val followedBy = Seq(NameExpressionToken)
}

case class QueryExpression(override val value: String) extends WithNameExpression {
  override val label: String = "query."
  override val rx: Regex     = "(query\\.).*" r

}

case class PathExpression(override val value: String) extends WithNameExpression {
  override val label: String = "path."
  override val rx: Regex     = "(path\\.).*" r
}

case class HeaderCodeBaseExpressionToken(override val value: String) extends LabeledExpressionToken {
  override val rx: Regex                                      = "(header\\.).*" r
  override val label: String                                  = "header."
  override val followedBy: Seq[String => RuntimeParsingToken] = Seq(TokenExpressionToken)
}

case class BodyNoFragmentCodeBaseExpressionToken(override val value: String) extends LabeledExpressionToken {
  override val label: String                                  = "body"
  override val rx: Regex                                      = "(body)" r
  override val followedBy: Seq[String => RuntimeParsingToken] = Nil
}

case class BodyWithFragmentCodeBaseExpressionToken(override val value: String) extends LabeledExpressionToken {
  override val label: String                                  = "body#"
  override val rx: Regex                                      = "(body#).*" r
  override val followedBy: Seq[String => RuntimeParsingToken] = Seq(FragmentExpressionToken)
}

trait WithSourceBaseExpression extends LabeledExpressionToken {
  override val followedBy: Seq[String => RuntimeParsingToken] = Seq(
    HeaderCodeBaseExpressionToken,
    PathExpression,
    BodyNoFragmentCodeBaseExpressionToken,
    BodyWithFragmentCodeBaseExpressionToken,
    QueryExpression
  )
}

case class ResponseCodeBaseExpressionToken(override val value: String) extends WithSourceBaseExpression {
  override val rx: Regex     = "(\\$response\\.).*" r
  override val label: String = "$response."
}

case class RequestCodeBaseExpressionToken(override val value: String) extends WithSourceBaseExpression {
  override val label: String = "$request."
  override val rx: Regex     = "(\\$request\\.).*" r
}
