package org.mulesoft.als.suggestions.plugins.jsonschema

import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.amfintegration.amfconfiguration.executioncontext.Implicits.global

import scala.concurrent.Future

object JsonSchemaStructureCompletionPlugin extends AMLCompletionPlugin{

  override def id: String = "JsonSchemaStructureCompletionPlugin"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] =
    if(!applies(request)) emptySuggestion
    else Future {
      // todo: need more info from AMF to link amfElements to it definition node (TD needed)
      Nil
    }

  private def applies(request: AmlCompletionRequest): Boolean =
    request.astPartBranch.isKeyLike
}
