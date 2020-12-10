package org.mulesoft.als.suggestions.plugins.aml.webapi.async

import amf.plugins.domain.webapi.models.Payload
import org.mulesoft.als.suggestions.plugins.aml.webapi.WebApiTypeFacetsCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.webapi.jsonSchema.draft7.JsonSchemeDraft7TypeFacetsCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.webapi.oas.oas30.Oas30TypeFacetsCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.webapi.raml.raml10.Raml10TypeFacetsCompletionPlugin

import scala.util.matching.Regex

trait AsyncMediaTypePluginFinder {
  val asyncApiRegex: Regex =
    "(application\\/vnd\\.aai\\.asyncapi(\\+yaml|\\+json)?;?(version=2\\.[0-9]\\.[0-9])?)".r
  val jsonRegex: Regex =
    "(application\\/schema(\\+json|\\+yaml)?;?(version=draft-07)?)".r
  val oas3Regex: Regex =
    "(application\\/vnd\\.oai\\.openapi(\\+json|\\+yaml)?;?(version=3\\.[0-9]\\.[0-9])?)".r
  val raml10Regex: Regex =
    "(application\\/raml(\\+yaml)?;?(version=1(\\.[0-9])?)?)".r

  def findPluginForMediaType(payload: Payload): Option[WebApiTypeFacetsCompletionPlugin] = {
    payload.schemaMediaType.value() match {
      case asyncApiRegex(_ *) => Some(Async20TypeFacetsCompletionPlugin)
      case jsonRegex(_ *)     => Some(JsonSchemeDraft7TypeFacetsCompletionPlugin)
      case oas3Regex(_ *)     => Some(Oas30TypeFacetsCompletionPlugin)
      case raml10Regex(_ *)   => Some(Raml10TypeFacetsCompletionPlugin)
      case _                  => None
    }
  }
}
