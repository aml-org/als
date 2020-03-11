package org.mulesoft.als.suggestions.plugins.aml.webapi.async

import amf.core.model.domain.Shape
import amf.plugins.domain.webapi.models.Payload
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.webapi.jsonSchema.draft7.JsonSchemeDraft7TypeFacetsCompletionPlugin

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.matching.Regex

object Async20PayloadCompletionPlugin extends AMLCompletionPlugin {
  override def id: String = "Async20PayloadCompletionPlugin"

  val asyncApiRegex: Regex =
    raw"application\/vnd\.aai\.asyncapi(\+yaml|\+json)?;?(version=2\.[0-9]\.[0-9])?".r
  val jsonRegex: Regex =
    raw"application\/schema(\+json|\+yaml)?;?(version=draft-07)?".r

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    Future {
      request.branchStack.headOption match {
        case Some(p: Payload) if request.yPartBranch.isKey =>
          request.amfObject match {
            case s: Shape =>
              if (p.schemaMediaType.isNullOrEmpty || asyncApiRegex
                    .findAllMatchIn(p.schemaMediaType.value())
                    .nonEmpty)
                Async20TypeFacetsCompletionPlugin.resolveShape(s, request.branchStack)
              else if (jsonRegex.findAllMatchIn(p.schemaMediaType.value()).nonEmpty)
                JsonSchemeDraft7TypeFacetsCompletionPlugin.resolveShape(s, request.branchStack)
              else Seq()
          }
        case _ => Seq()
      }
    }
  }

}
