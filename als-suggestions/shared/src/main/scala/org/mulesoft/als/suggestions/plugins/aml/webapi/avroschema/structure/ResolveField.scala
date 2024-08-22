package org.mulesoft.als.suggestions.plugins.aml.webapi.avroschema.structure

import amf.aml.client.scala.model.document.Dialect
import amf.shapes.client.scala.model.domain.AnyShape
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.ResolveIfApplies
import org.mulesoft.als.suggestions.plugins.aml._
import org.mulesoft.amfintegration.AmfImplicits.AmfAnnotationsImp
import org.mulesoft.amfintegration.dialect.dialects.avro.AvroFieldNode

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object ResolveField extends ResolveIfApplies {
  override def resolve(request: AmlCompletionRequest): Option[Future[Seq[RawSuggestion]]] = {
    request.amfObject match {
      case _: AnyShape if isField(request) =>
        applies(fieldNodeSuggestions(request.actualDialect))
      case _ =>
        notApply
    }
  }

  private def isField(request: AmlCompletionRequest) =
    request.astPartBranch.parentEntryIs("fields") &&
      request.branchStack.tail.headOption
        .flatMap(_.annotations.avroSchemaType())
        .map(_.avroType)
        .contains("record")

  private def fieldNodeSuggestions(d: Dialect): Future[Seq[RawSuggestion]] =
    Future(AvroFieldNode.Obj.propertiesRaw(Some("parameters"), d))
}
