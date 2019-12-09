package org.mulesoft.als.suggestions.plugins.aml.webapi.oas.oas30

import amf.core.model.domain.{AmfObject, Shape}
import amf.plugins.domain.shapes.models.NodeShape
import amf.plugins.domain.webapi.metamodel.{EncodingModel, PayloadModel}
import amf.plugins.domain.webapi.models.{Encoding, Payload}
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
object EncodingPropertyName extends AMLCompletionPlugin {
  override def id: String = "EncodingPropertyName"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    request.amfObject match {
      case e: Encoding if request.fieldEntry.exists(_.field == EncodingModel.PropertyName) =>
        Future { fromParentPayload(request) }
      case _ => emptySuggestion
    }
  }

  private def fromParentPayload(request: AmlCompletionRequest): Seq[RawSuggestion] = {
    request.branchStack.headOption.map(parentShapeProperties).getOrElse(Nil)
  }

  private def parentShapeProperties(parent: AmfObject): Seq[RawSuggestion] = {
    parent match {
      case p: Payload if p.fields.exists(PayloadModel.Schema) =>
        shapeProperties(p.schema).map(toRaw)
      case _ => Nil
    }
  }

  private def toRaw(name: String): RawSuggestion = RawSuggestion.forKey(name, mandatory = false)

  private def shapeProperties(shape: Shape): Seq[String] = {
    shape match {
      case n: NodeShape => n.properties.flatMap(_.name.option())
      case _            => Nil
    }
  }
}
