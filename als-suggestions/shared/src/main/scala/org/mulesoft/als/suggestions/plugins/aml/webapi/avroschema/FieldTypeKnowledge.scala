package org.mulesoft.als.suggestions.plugins.aml.webapi.avroschema
import amf.core.client.scala.model.domain.extensions.PropertyShape
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.amfintegration.AmfImplicits.AmfAnnotationsImp

trait FieldTypeKnowledge {

  protected def isFieldType(request: AmlCompletionRequest): Boolean =
    request.amfObject match {
      case _: PropertyShape =>
        request.astPartBranch.parentKey
          .contains("type") && request.amfObject.annotations.avroSchemaType().exists(_.avroType.isEmpty)
      case _ => false
    }
}
