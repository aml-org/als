package org.mulesoft.als.suggestions.plugins.aml.webapi.raml

import amf.core.model.domain.{AmfObject, ObjectNode, ScalarNode}
import amf.core.model.domain.templates.AbstractDeclaration
import amf.core.parser.FieldEntry
import org.mulesoft.als.suggestions.{RawSuggestion, SuggestionOptions}
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import amf.core.utils._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object DefaultVariablesAbstractDefinition extends AMLCompletionPlugin {
  override def id: String = "DefaultVariablesAbstractDefinition"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    Future {
      if (request.branchStack.exists(_.isInstanceOf[AbstractDeclaration])) {
        valSuggestions(request.amfObject, request.fieldEntry, request.position.column).map(s =>
          RawSuggestion(s._1, s._2, s._2, Nil, "", options = SuggestionOptions(isKey = request.yPartBranch.isKey)))
      } else Nil
    }
  }

  private def valSuggestions(amfObject: AmfObject, fe: Option[FieldEntry], column: Int): Seq[(String, String)] = {
    amfObject match {
      case s: ScalarNode =>
        buildFromString(s.value.option(), column)
      case o: ObjectNode if fe.isDefined =>
        buildFromString(fe.map(f => f.field.value.name.urlComponentDecoded.stripSuffix("k")), column)
      case _ => Nil
    }
  }

  private def buildFromString(value: Option[String], column: Int) = {
    value match {
      case None                 => commonNames.map(c => (c, c))
      case Some(t) if t.isEmpty => commonNames.map(c => (c, c))
      case Some(t) if t.contains("<<") =>
        val (pre, content, pos) = partitionVar(t)
        val sugg = if (content.contains('|') && content.indexOf('|') < column) { // suggesting function
          val funcPrefix = content.split('|').last
          val prefix     = content.split('|').head
          if (funcPrefix.trim.isEmpty) functions.map(f => (prefix + "|" + funcPrefix + f, f)) // empty spaces
          else functions.filter(_.startsWith(funcPrefix)).map(f => (prefix + "|" + f, f))
        } else { // suggestions variable
          if (content.contains('|')) {
            val prefix = content.split('|').head
            val sufix  = content.split('|').last
            commonNames.filter(_.startsWith(prefix)).map(c => (c + "|" + sufix, c))
          } else
            commonNames.filter(_.startsWith(content)).map(c => (c, c))
        }
        sugg.map(s => (s"$pre${s._1}$pos", s._2))
      case _ => Nil
    }
  }

  private def partitionVar(value: String): (String, String, String) = {
    val (pre, tempContent) = value.split("<<").toList match {
      case head :: Nil  => (head + "<<", "")
      case head :: tail => (head + "<<", tail.head)
      case _            => ("<<", "")
    }
    val (content, pos) = if (tempContent.contains(">>")) {
      tempContent.split(">>").toList match {
        case head :: Nil  => (head.toString, ">>")
        case head :: tail => (head.toString, ">>" + tail.head)
        case _            => (tempContent.toString, ">>")
      }
    } else (tempContent.toString, ">>")

    (pre.toString, content.toString, pos.toString)
  }

  private val commonNames = Seq("resourcePathName", "resourcePath", "methodName")

  private val functions =
    Seq("!singularize", "!pluralize", "!uppercase", "!lowercase", "!lowercamelcase", "!uppercamelcase")

}
