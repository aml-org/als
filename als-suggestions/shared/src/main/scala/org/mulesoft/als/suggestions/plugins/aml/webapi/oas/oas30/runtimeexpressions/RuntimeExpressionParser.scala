package org.mulesoft.als.suggestions.plugins.aml.webapi.oas.oas30.runtimeexpressions

import scala.util.matching.Regex

trait RuntimeExpressionParser extends RuntimeParsingToken {
  val completeStack: Seq[RuntimeParsingToken]
}

trait RuntimeParsingToken {
  val value: String
  val rx: Regex
  val followedBy: Seq[String => RuntimeParsingToken]
  lazy val extraValue: String = value match {
    case rx(e) => value.stripPrefix(e)
    case _     => value
  }

  lazy val possibleApplications: Seq[String] = followedBy.flatMap(pa =>
    pa("") match {
      case l: LabeledExpressionToken => Some(l.label)
      case _                         => None
  })

  lazy val next: Option[RuntimeParsingToken] =
    if (extraValue.isEmpty) None
    else {
      Some(
        followedBy
          .find(rp => rp(extraValue).nodeIsValid)
          .map(rp => rp(extraValue))
          .getOrElse(InvalidExpressionToken(extraValue)))
    }

  def nodeIsValid: Boolean = {
    value match {
      case rx(e) => true
      case _     => false
    }
  }

  def completelyValid: Boolean = {
    if (followedBy.isEmpty) nodeIsValid
    else next.exists(_.completelyValid)
  }
}

case class InvalidExpressionToken(override val value: String) extends RuntimeParsingToken {
  override val rx: Regex                                      = "(.*)" r
  override val followedBy: Seq[String => RuntimeParsingToken] = Nil

  override lazy val next: Option[RuntimeParsingToken] = None

  override def nodeIsValid() = false

  override def completelyValid: Boolean = false
}

trait LabeledExpressionToken extends RuntimeParsingToken {
  val label: String
}

trait BaseLabeledExpressionToken extends LabeledExpressionToken {
  override val followedBy: Seq[String => RuntimeParsingToken] = Nil
}
