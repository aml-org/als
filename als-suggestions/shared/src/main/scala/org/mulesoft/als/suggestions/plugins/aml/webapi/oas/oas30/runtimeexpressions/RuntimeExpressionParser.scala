package org.mulesoft.als.suggestions.plugins.aml.webapi.oas.oas30.runtimeexpressions

import scala.annotation.tailrec
import scala.reflect.ClassTag

case class RuntimeExpressionParser(value: String) {

  /**
    * private val Expression = """(\$url|\$method|\$statusCode|\$request\.|\$response\.).*""".r
    */
  private val Expression = s"(${RuntimeExpressionValues.expressions
    .map(e =>
      e.replaceAllLiterally("$", "\\$")
        .replaceAllLiterally(".", "\\."))
    .mkString("|")}).*".r

  private val HeaderRef = """(header\.).*""" r
  private val QueryRef  = """(query\.).*""" r
  private val PathRef   = """(path\.).*""" r
  private val BodyRef   = """(body#?).*""" r

  /**
    * {
    *   val tokens1 = parse(value, Nil)
    *   assert(tokens1.map(_.value).mkString("") == value)
    *   tokens1
    * }
    */
  lazy val tokens: Seq[RuntimeExpressionToken] = parse(value, Nil)

  def parentIs[T <: RuntimeExpressionToken: ClassTag](stack: Seq[RuntimeExpressionToken]): Boolean = {
    val clazz = implicitly[ClassTag[T]].runtimeClass
    stack.lastOption.exists(p => clazz.isInstance(p))
  }

  lazy val isValid: Boolean = validate(tokens, None)

  @tailrec
  private def validate(tokens: Seq[RuntimeExpressionToken], previous: Option[RuntimeExpressionToken]): Boolean = {
    tokens match {
      case Nil => true
      case (head: ExpressionToken) :: Nil =>
        if (head.value.endsWith(".")) previous.isDefined
        else true
      case head :: tail =>
        (head match {
          case _: FragmentToken =>
            parentIs[BodyRefToken](tail) && tail.head.asInstanceOf[BodyRefToken].hasFragment
          case _: NameToken                                         => parentIs[QueryRefToken](tail) || parentIs[PathRefToken](tail)
          case _: TokenToken                                        => parentIs[HeaderRefToken](tail)
          case _: BodyRefToken | _: QueryRefToken | _: PathRefToken => parentIs[ExpressionToken](tail)
          case _                                                    => false
        }) && validate(tail, Some(head))
    }
  }

  @tailrec
  private def parse(s: String, stack: Seq[RuntimeExpressionToken]): Seq[RuntimeExpressionToken] = {
    if (s.isEmpty) stack
    else {
      s match {
        case Expression(e) =>
          parse(s.stripPrefix(e), stack :+ ExpressionToken(e))
        case HeaderRef(e) if parentIs[ExpressionToken](stack) =>
          parse(s.stripPrefix(e), stack :+ HeaderRefToken(e))
        case QueryRef(e) if parentIs[ExpressionToken](stack) =>
          parse(s.stripPrefix(e), stack :+ QueryRefToken(e))
        case PathRef(e) if parentIs[ExpressionToken](stack) =>
          parse(s.stripPrefix(e), stack :+ PathRefToken(e))
        case BodyRef(e) if parentIs[ExpressionToken](stack) =>
          parse(s.stripPrefix(e), stack :+ BodyRefToken(e))
        case token if parentIs[HeaderRefToken](stack) =>
          stack :+ TokenToken(token)
        case name if parentIs[QueryRefToken](stack) || parentIs[PathRefToken](stack) =>
          stack :+ NameToken(name)
        case fragment if parentIs[BodyRefToken](stack) =>
          stack :+ FragmentToken(fragment)
        case unknown => stack :+ InvalidToken(unknown)
      }
    }
  }
}

object RuntimeExpressionValues {
  val expressions: Seq[String] = Seq("$url", "$method", "$statusCode", "$request.", "$response.")
  val sources: Seq[String]     = Seq("header.", "query.", "path.", "body")
}

abstract class RuntimeExpressionToken(val tokenType: String, val value: String)

case class ExpressionToken(v: String) extends RuntimeExpressionToken("expression", v)

case class HeaderRefToken(v: String) extends RuntimeExpressionToken("headerRef", v)

case class QueryRefToken(v: String) extends RuntimeExpressionToken("queryRef", v)

case class PathRefToken(v: String) extends RuntimeExpressionToken("pathRef", v)

case class BodyRefToken(v: String) extends RuntimeExpressionToken("bodyRef", v) {
  val hasFragment = v.endsWith("#")
}

case class TokenToken(v: String) extends RuntimeExpressionToken("token", v)

case class NameToken(v: String) extends RuntimeExpressionToken("name", v)

case class FragmentToken(v: String) extends RuntimeExpressionToken("fragment", v)

case class InvalidToken(v: String) extends RuntimeExpressionToken("invalid", v)
