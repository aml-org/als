package org.mulesoft.als.suggestions.plugins.aml.webapi.oas.oas30

import amf.dialects.oas.nodes.Oas30ParamObject
import amf.plugins.domain.webapi.metamodel.ParameterModel
import amf.plugins.domain.webapi.models.Parameter
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.AMLEnumCompletionPlugin

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object OAS30EnumCompletionPlugin extends AMLCompletionPlugin {
  override def id = "AMLEnumCompletionPlugin"

  override def resolve(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    params.amfObject match {
      case p: Parameter if params.fieldEntry.exists(_.field == ParameterModel.Style) =>
        Future { forBinding(p) }
      case _ => AMLEnumCompletionPlugin.resolve(params)
    }
  }

  private def forBinding(p: Parameter) = bidingMap.getOrElse(p.binding.option().getOrElse("default"), Nil)

  private val styles: Map[String, RawSuggestion] =
    Oas30ParamObject.styleProp.enum().flatMap(_.option().map(_.toString)).map(n => n -> raw(n)).toMap

  private val bidingMap = Map(
    "default" ->
      Seq("matrix", "label", "form", "simple", "spaceDelimited", "pipeDelimited", "deepObject"),
    "path" ->
      Seq("matrix", "label", "simple"),
    "query" ->
      Seq("form", "spaceDelimited", "pipeDelimited", "deepObject"),
    "cookie" ->
      Seq("form"),
    "header" ->
      Seq("simple")
  ).map(t => t._1 -> t._2.map(styles))

  private def raw(t: String) = RawSuggestion(t, isAKey = false, "parameters", mandatory = true)
}
