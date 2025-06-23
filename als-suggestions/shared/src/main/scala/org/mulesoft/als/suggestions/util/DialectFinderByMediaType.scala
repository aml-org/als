package org.mulesoft.als.suggestions.util

import amf.apicontract.client.scala.model.domain.Payload
import org.mulesoft.amfintegration.amfconfiguration.DocumentDefinition
import org.mulesoft.amfintegration.dialect.dialects.asyncapi20.AsyncApi20Dialect
import org.mulesoft.amfintegration.dialect.dialects.avro.AvroDialect
import org.mulesoft.amfintegration.dialect.dialects.jsonschema.draft4.JsonSchemaDraft4Dialect
import org.mulesoft.amfintegration.dialect.dialects.jsonschema.draft7.JsonSchemaDraft7Dialect
import org.mulesoft.amfintegration.dialect.dialects.oas.OAS30Dialect
import org.mulesoft.amfintegration.dialect.dialects.raml.raml10.Raml10TypesDialect

import scala.util.matching.Regex

trait DialectFinderByMediaType {
  val asyncApiRegex: Regex =
    "(application\\/vnd\\.aai\\.asyncapi(\\+yaml|\\+json)?;?(version=2\\.[0-9]\\.[0-9])?)".r
  val jsonRegex07: Regex =
    "(application\\/schema(\\+json|\\+yaml)?;?(version=draft-07)?)".r
  val jsonRegex04: Regex =
    "(application\\/schema(\\+json|\\+yaml)?;?(version=draft-04)?)".r
  val oas3Regex: Regex =
    "(application\\/vnd\\.oai\\.openapi(\\+json|\\+yaml)?;?(version=3\\.[0-9]\\.[0-9])?)".r
  val raml10Regex: Regex =
    "(application\\/raml(\\+yaml)?;?(version=1(\\.[0-9])?)?)".r
  val avroRegex: Regex =
    "(application\\/vnd\\.apache\\.avro;?(version=1\\.[0-9]\\.[0-9])?)".r

  // TODO: Add all Dialects to this util
  def findDialectFromPayload(payload: Payload): DocumentDefinition = {
    payload.schemaMediaType.value() match {
      case asyncApiRegex(_*) => DocumentDefinition(AsyncApi20Dialect.dialect)
      case jsonRegex07(_*)     => DocumentDefinition(JsonSchemaDraft7Dialect.dialect)
      case jsonRegex04(_*)     => DocumentDefinition(JsonSchemaDraft4Dialect.dialect)
      case oas3Regex(_*)     => DocumentDefinition(OAS30Dialect.dialect)
      case raml10Regex(_*)   => DocumentDefinition(Raml10TypesDialect.dialect)
      case avroRegex(_*)     => DocumentDefinition(AvroDialect.dialect)
      case _                 => DocumentDefinition(AsyncApi20Dialect.dialect)
    }
  }
}
