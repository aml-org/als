package org.mulesoft.als.suggestions.plugins.aml.webapi.avroschema.structure

import amf.shapes.client.scala.model.domain.AnyShape
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.ResolveIfApplies
import org.mulesoft.als.suggestions.plugins.aml._
import org.mulesoft.amfintegration.AmfImplicits.AmfAnnotationsImp
import org.mulesoft.amfintegration.amfconfiguration.DocumentDefinition
import org.mulesoft.amfintegration.dialect.dialects.avro.AvroFieldNode

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object ResolveField extends ResolveIfApplies {
  override def resolve(request: AmlCompletionRequest): Option[Future[Seq[RawSuggestion]]] = {
    request.amfObject match {
      case _: AnyShape if isField(request) && !newerTypeDefined(request) =>
        applies(fieldNodeSuggestions(request.actualDocumentDefinition))
      case _ =>
        notApply
    }
  }

  private def newerTypeDefined(request: AmlCompletionRequest) = request.amfObject.annotations.avroSchemaType().isDefined

  private def isField(request: AmlCompletionRequest) =
    request.astPartBranch.parentEntryIs("fields") &&
      request.branchStack.tail.headOption
        .flatMap(_.annotations.avroSchemaType())
        .map(_.avroType)
        .contains("record")

  private def fieldNodeSuggestions(d: DocumentDefinition): Future[Seq[RawSuggestion]] =
    Future(AvroFieldNode.Obj.propertiesRaw(Some("parameters"), d))
}
