package org.mulesoft.als.suggestions.plugins.aml.webapi.jsonschema

import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.ResolveIfApplies
import org.mulesoft.als.suggestions.plugins.aml.webapi.IsInsideRequired
import org.mulesoft.als.suggestions.plugins.aml.webapi.oas.OASLikeRequiredObjectCompletionPlugin

import scala.concurrent.Future

object JsonSchemaRequiredObjectCompletionPlugin extends OASLikeRequiredObjectCompletionPlugin

object ResolveJsonSchemaRequiredProperties extends ResolveIfApplies with IsInsideRequired{
  override def resolve(request: AmlCompletionRequest): Option[Future[Seq[RawSuggestion]]] =
    if (isInsideRequired(request.astPartBranch))
      Some(Future.successful(Nil))
    else notApply
}