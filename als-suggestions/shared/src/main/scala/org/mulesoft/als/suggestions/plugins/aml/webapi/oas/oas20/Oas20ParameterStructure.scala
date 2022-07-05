package org.mulesoft.als.suggestions.plugins.aml.webapi.oas.oas20

import amf.apicontract.client.scala.model.domain.{EndPoint, Parameter, Request}
import amf.apicontract.internal.annotations.FormBodyParameter
import amf.apicontract.internal.metamodel.domain.{EndPointModel, ParameterModel, RequestModel}
import amf.core.client.scala.model.domain.AmfObject
import amf.core.internal.parser.domain.FieldEntry
import amf.shapes.client.scala.model.domain.AnyShape
import org.mulesoft.als.common.YPartBranch
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml._
import org.mulesoft.als.suggestions.plugins.aml.categories.CategoryRegistry
import org.mulesoft.amfintegration.dialect.dialects.oas.OAS20Dialect
import org.mulesoft.amfintegration.dialect.dialects.oas.nodes.Oas20ParamObject
import org.mulesoft.amfintegration.dialect.dialects.oas.nodes.Oas20ParamObject.allowEmptyValue

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Oas20ParameterStructure extends AMLCompletionPlugin {
  override def id: String = "ParameterStructure"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    Future {
      resolveParameterStructure(request.yPartBranch, request.amfObject, request.fieldEntry)
    }
  }

  def resolveParameterStructure(
      yPartBranch: YPartBranch,
      amfObject: AmfObject,
      fieldEntry: Option[FieldEntry]
  ): Seq[RawSuggestion] =
    if (isWritingFacet(yPartBranch))
      amfObject match {
        case p: Parameter if p.binding.option().contains("body") =>
          bodySuggestions(yPartBranch, p)
        case p: Parameter if p.binding.option().contains("query") || p.binding.option().contains("formData") =>
          suggestions(amfObject) :+ allowEmptyValueSuggestion(p)
        case o if o.annotations.contains(classOf[FormBodyParameter]) =>
          suggestions(amfObject) :+ allowEmptyValueSuggestion(o)
        case p: Parameter if fieldEntry.isEmpty && yPartBranch.stringValue != p.name.value() =>
          suggestions(amfObject)
        case _: EndPoint if fieldEntry.exists(_.field == EndPointModel.Parameters) =>
          suggestions(amfObject)
        case _: Request if fieldEntry.exists(_.field == RequestModel.QueryParameters) =>
          suggestions(amfObject)
        case _ => Nil
      }
    else Nil

  def bodySuggestions(yPartBranch: YPartBranch, p: Parameter): Seq[RawSuggestion] =
    if (p.name.value() != yPartBranch.stringValue)
      Oas20TypeFacetsCompletionPlugin.resolveShape(Option(p.schema).getOrElse(AnyShape()), Nil, OAS20Dialect())
    else Nil

  def suggestions(amfObject: AmfObject): Seq[RawSuggestion] =
    Oas20ParamObject.properties.map { p =>
      p.toRaw(CategoryRegistry(amfObject.meta.`type`.head.iri(), p.name().value(), OAS20Dialect.dialect.id))
    }

  def allowEmptyValueSuggestion(amfObject: AmfObject): RawSuggestion =
    allowEmptyValue.toRaw(
      CategoryRegistry(amfObject.meta.`type`.head.iri(), allowEmptyValue.name().value(), OAS20Dialect.dialect.id)
    )

  private def isWritingFacet(yPartBranch: YPartBranch): Boolean =
    yPartBranch.isKeyLike
}
