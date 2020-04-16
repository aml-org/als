package org.mulesoft.als.suggestions.plugins.aml.webapi.async

import amf.core.parser.FieldEntry
import amf.plugins.domain.shapes.metamodel.ScalarShapeModel
import amf.plugins.domain.webapi.metamodel.PayloadModel
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.AMLEnumCompletionPlugin
import org.mulesoft.amfintegration.dialect.dialects.asyncapi20.MessageObjectNode
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case object Async20EnumCompletionPlugin extends AMLCompletionPlugin {
  override def id = "AMLEnumCompletionPlugin"

  override def resolve(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    params.fieldEntry match {
      case Some(FieldEntry(ScalarShapeModel.Format, _)) if params.yPartBranch.isValue => emptySuggestion
      case Some(FieldEntry(PayloadModel.SchemaMediaType, _)) =>
        Future(AMLEnumCompletionPlugin.suggestMapping(MessageObjectNode.schemaFormatProp))
      case _ => AMLEnumCompletionPlugin.resolve(params)
    }
  }
}
