package org.mulesoft.als.suggestions.plugins.aml.webapi.async

import amf.aml.client.scala.model.domain.PropertyMapping
import amf.apicontract.internal.metamodel.domain.PayloadModel
import amf.core.internal.parser.domain.FieldEntry
import amf.shapes.internal.domain.metamodel.ScalarShapeModel
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.{AMLEnumCompletionPlugin, EnumSuggestions}
import org.mulesoft.amfintegration.dialect.dialects.asyncapi20.MessageObjectNode
import org.mulesoft.amfintegration.dialect.dialects.asyncapi26.Message26ObjectNode

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait AsyncEnumCompletionPlugin extends AMLCompletionPlugin with EnumSuggestions {
  override def id = "AMLEnumCompletionPlugin"

  val schemaFormatProp: PropertyMapping
  override def resolve(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    params.fieldEntry match {
      case Some(FieldEntry(ScalarShapeModel.Format, _)) if params.astPartBranch.isValue => emptySuggestion
      case Some(FieldEntry(PayloadModel.SchemaMediaType, _)) =>
        Future(suggestMappingWithEnum(schemaFormatProp))
      case _ => AMLEnumCompletionPlugin.resolve(params)
    }
  }
}
case object Async20EnumCompletionPlugin extends AsyncEnumCompletionPlugin {
  override val schemaFormatProp: PropertyMapping = MessageObjectNode.schemaFormatProp
}
case object Async26EnumCompletionPlugin extends AsyncEnumCompletionPlugin {
  override val schemaFormatProp: PropertyMapping = Message26ObjectNode.schemaFormatProp
}
