package org.mulesoft.als.suggestions.plugins.aml.webapi.async

import amf.core.model.domain.Shape
import amf.plugins.domain.webapi.models.{Payload, Server}
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.webapi.async.Async20PayloadCompletionPlugin.oas3Regex
import org.mulesoft.als.suggestions.plugins.aml.webapi.jsonSchema.draft7.JsonSchemeDraft7TypeFacetsCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.webapi.oas.oas30.Oas30TypeFacetsCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.webapi.raml.raml10.Raml10TypeFacetsCompletionPlugin

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.matching.Regex

object Async20PayloadCompletionPlugin extends AMLCompletionPlugin {
  override def id: String = "Async20PayloadCompletionPlugin"

  val asyncApiRegex: Regex =
    raw"application\/vnd\.aai\.asyncapi(\+yaml|\+json)?;?(version=2\.[0-9]\.[0-9])?".r
  val jsonRegex: Regex =
    raw"application\/schema(\+json|\+yaml)?;?(version=draft-07)?".r
  val oas3Regex: Regex =
    raw"application\/vnd\.oai\.openapi(\+json|\+yaml)?;?(version=3\.[0-9]\.[0-9])?".r
  val raml10Regex: Regex =
    raw"application\/raml(\+yaml)?;?(version=1(\.[0-9])?)?".r

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    Future {
      val branchStack = request.branchStack
      branchStack.headOption match {
        case Some(p: Payload) if request.yPartBranch.isKey && !branchStack.exists(_.isInstanceOf[Server]) =>
          request.amfObject match {
            case s: Shape =>
              val mediaTypeFormat = p.schemaMediaType.value()
              if (p.schemaMediaType.isNullOrEmpty || asyncApiRegex
                    .findAllMatchIn(mediaTypeFormat)
                    .nonEmpty)
                Async20TypeFacetsCompletionPlugin.resolveShape(s, branchStack)
              else if (jsonRegex.findAllMatchIn(mediaTypeFormat).nonEmpty)
                JsonSchemeDraft7TypeFacetsCompletionPlugin.resolveShape(s, branchStack)
              else if (oas3Regex.findAllMatchIn(mediaTypeFormat).nonEmpty)
                Oas30TypeFacetsCompletionPlugin.resolveShape(s, branchStack)
              else if (raml10Regex.findAllMatchIn(mediaTypeFormat).nonEmpty)
                Raml10TypeFacetsCompletionPlugin.resolveShape(s, branchStack)
              else Seq()
          }
        case _ => Seq()
      }
    }
  }

}
