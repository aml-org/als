package org.mulesoft.als.suggestions.plugins.aml.webapi.async.structure

import amf.plugins.domain.shapes.models.ScalarShape
import amf.plugins.domain.webapi.models.{Server, WebApi}
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.ResolveIfApplies
import org.mulesoft.als.suggestions.plugins.aml._
import org.mulesoft.amfintegration.dialect.dialects.asyncapi20.AsyncApiVariableObject

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object ResolveServers extends ResolveIfApplies {
  override def resolve(request: AmlCompletionRequest): Option[Future[Seq[RawSuggestion]]] =
    request.amfObject match {
      case _: ScalarShape if request.branchStack.exists(_.isInstanceOf[Server]) && request.fieldEntry.isEmpty =>
        applies(Future(AsyncApiVariableObject.Obj.propertiesRaw(None, request.actualDialect)))
      case _ => notApply
    }
}
