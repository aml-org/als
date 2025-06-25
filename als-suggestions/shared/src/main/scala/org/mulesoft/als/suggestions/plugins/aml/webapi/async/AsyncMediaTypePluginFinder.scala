package org.mulesoft.als.suggestions.plugins.aml.webapi.async

import amf.apicontract.client.scala.model.domain.Payload
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.webapi.avroschema.AvroTypeFacetsCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.webapi.jsonschema.JsonSchemeDraft7TypeFacetsCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.webapi.oas.oas30.Oas30TypeFacetsCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.webapi.raml.raml10.Raml10TypeFacetsCompletionPlugin

import scala.util.matching.Regex

// todo: we are duplicating a lot of code/logic with DialectFinderByMediaType, unify behavior
trait AsyncMediaTypePluginFinder {
  val asyncApiRegex: Regex =
    "(application\\/vnd\\.aai\\.asyncapi(\\+yaml|\\+json)?;?(version=2\\.[0-9]\\.[0-9])?)".r
  val jsonRegex: Regex =
    "(application\\/schema(\\+json|\\+yaml)?;?(version=draft-07)?)".r
  val oas3Regex: Regex =
    "(application\\/vnd\\.oai\\.openapi(\\+json|\\+yaml)?;?(version=3\\.[0-9]\\.[0-9])?)".r
  val raml10Regex: Regex =
    "(application\\/raml(\\+yaml)?;?(version=1(\\.[0-9])?)?)".r
  val avroRegex: Regex =
    "(application\\/vnd\\.apache\\.avro;?(version=1\\.[0-9]\\.[0-9])?)".r

  def findPluginForMediaType(payload: Payload): Option[AMLCompletionPlugin] = {
    payload.schemaMediaType.value() match {
      case asyncApiRegex(_*) => Some(Async20TypeFacetsCompletionPlugin)
      case jsonRegex(_*)     => Some(JsonSchemeDraft7TypeFacetsCompletionPlugin)
      case oas3Regex(_*)     => Some(Oas30TypeFacetsCompletionPlugin)
      case raml10Regex(_*)   => Some(Raml10TypeFacetsCompletionPlugin)
      case avroRegex(_*)     => Some(AvroTypeFacetsCompletionPlugin)
      case _                 => None
    }
  }
}
