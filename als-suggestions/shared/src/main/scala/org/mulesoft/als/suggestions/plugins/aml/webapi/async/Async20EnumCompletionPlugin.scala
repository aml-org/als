package org.mulesoft.als.suggestions.plugins.aml.webapi.async

import amf.aml.client.scala.model.document.Dialect
import amf.aml.client.scala.model.domain.PropertyMapping
import amf.apicontract.client.scala.model.domain.Payload
import amf.apicontract.internal.metamodel.domain.PayloadModel
import amf.core.internal.parser.domain.FieldEntry
import amf.shapes.internal.domain.metamodel.ScalarShapeModel
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.{AMLEnumCompletionPlugin, EnumSuggestions}
import org.mulesoft.als.suggestions.util.DialectFinderByMediaType
import org.mulesoft.amfintegration.amfconfiguration.DocumentDefinition
import org.mulesoft.amfintegration.dialect.dialects.asyncapi20.MessageObjectNode
import org.mulesoft.amfintegration.dialect.dialects.asyncapi26.Message26ObjectNode

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  *
  */
trait AsyncEnumCompletionPlugin extends AMLCompletionPlugin with EnumSuggestions with DialectFinderByMediaType {
  override def id = "AMLEnumCompletionPlugin"

  val schemaFormatProp: PropertyMapping

  override def resolve(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] =
    params.fieldEntry match {
      case _ if isInsideAvro(params) =>
        // avro is using the internal plugin (all these hacks for other specs would probably be better handled the same as avro)
        emptySuggestion
      case Some(FieldEntry(ScalarShapeModel.Format, _)) if params.astPartBranch.isValue => emptySuggestion
      case Some(FieldEntry(PayloadModel.SchemaMediaType, _)) =>
        Future(suggestMappingWithEnum(schemaFormatProp))
      case _ if params.astPartBranch.parentEntryIs("type") && params.astPartBranch.isInBranchOf("fields") =>
        emptySuggestion
      case _ =>
        AMLEnumCompletionPlugin.resolve(cloneParamsForSchemaMediaType(params))
    }

  private def isInsideAvro(params: AmlCompletionRequest) = {
    // skip cases in the root because they are not handled in the other specific plugins (we should delegate it in the future)
    params.branchStack.tail.collectFirst { case p: Payload => p }.exists { p =>
      findDialectFromPayload(p).name().contains("avro")
    }
  }

  private def cloneParamsForSchemaMediaType(params: AmlCompletionRequest) =
    params.branchStack
      .collectFirst {
        case p: Payload => params.withDefinition(getDialectBySchemaMediaType(p))
      }.getOrElse(params)

  private def getDialectBySchemaMediaType(payload: Payload): DocumentDefinition =
    findDialectFromPayload(payload)
}
case object Async20EnumCompletionPlugin extends AsyncEnumCompletionPlugin {
  override val schemaFormatProp: PropertyMapping = MessageObjectNode.schemaFormatProp
}
case object Async26EnumCompletionPlugin extends AsyncEnumCompletionPlugin {
  override val schemaFormatProp: PropertyMapping = Message26ObjectNode.schemaFormatProp
}
