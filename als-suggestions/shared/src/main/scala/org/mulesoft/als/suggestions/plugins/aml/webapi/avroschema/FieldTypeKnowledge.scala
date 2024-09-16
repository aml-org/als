package org.mulesoft.als.suggestions.plugins.aml.webapi.avroschema
import amf.apicontract.client.scala.model.domain.Payload
import amf.core.client.scala.model.domain.extensions.PropertyShape
import amf.shapes.client.scala.model.domain.AnyShape
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.amfintegration.AmfImplicits.AmfAnnotationsImp
import org.mulesoft.amfintegration.dialect.dialects.avro.AvroDialect

trait FieldTypeKnowledge {

  protected def isFieldType(request: AmlCompletionRequest): Boolean =
    request.amfObject match {
      case _: PropertyShape =>
        request.astPartBranch.parentKey
          .contains("type") && request.amfObject.annotations.avroSchemaType().exists(_.avroType.isEmpty)
//      case _: AnyShape =>
//        request.astPartBranch.parentKey.contains("type") && isAvroFormat(request)
      case _ => false
    }

//  private def isAvroFormat(implicit request: AmlCompletionRequest): Boolean =
//    request.branchStack.head match {
//      case p: Payload => isAvroFormatFromPayload(p)
//      case _          => false
//    }

//  protected def isAvroFormatFromPayload(payloadObject: Payload) =
//    AvroDialect.avro190MediaType.equalsIgnoreCase(payloadObject.schemaMediaType.value())
}
