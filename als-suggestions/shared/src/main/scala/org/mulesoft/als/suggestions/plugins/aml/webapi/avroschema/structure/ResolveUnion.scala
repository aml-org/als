package org.mulesoft.als.suggestions.plugins.aml.webapi.avroschema.structure

import amf.shapes.client.scala.model.domain.{AnyShape, UnionShape}
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.ResolveIfApplies
import org.mulesoft.amfintegration.dialect.dialects.avro.AvroDialect

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object ResolveUnion extends ResolveIfApplies {
  override def resolve(request: AmlCompletionRequest): Option[Future[Seq[RawSuggestion]]] = {
    request.amfObject match {
      case _: AnyShape if request.branchStack.headOption.exists(_.isInstanceOf[UnionShape]) =>
        applies(unionNodeSuggestions())
      case _ =>
        notApply
    }
  }

  private def unionNodeSuggestions(): Future[Seq[RawSuggestion]] =
    Future(AvroDialect.avroTypes.map { t =>
      RawSuggestion(t, isAKey = false, "schemas", mandatory = false)
    })
}
