package org.mulesoft.als.suggestions.plugins.aml.webapi.raml.raml08

import amf.plugins.domain.webapi.models.Payload
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.plugins.aml.webapi.raml.raml10.ResolveShapeAndSecurity
import org.mulesoft.als.suggestions.plugins.aml.{ResolveIfApplies, StructureCompletionPlugin}

import scala.concurrent.Future

object Raml08StructureCompletionPlugin extends StructureCompletionPlugin {
  override protected val resolvers: List[ResolveIfApplies] = List(
    ResolveShapeAndSecurity,
    ResolvePayload,
    ResolveDefault
  )

  object ResolvePayload extends ResolveIfApplies {
    override def resolve(request: AmlCompletionRequest): Option[Future[Seq[RawSuggestion]]] = {
      request.amfObject match {
        case _: Payload =>
          applies(emptySuggestion)
        case _ => notApply
      }
    }
  }
}
