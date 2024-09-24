package org.mulesoft.als.suggestions.util

import amf.aml.client.scala.model.document.Dialect
import amf.apicontract.client.scala.model.domain.Payload
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
  def findDialectFromPayload(payload: Payload): Dialect = {
    payload.schemaMediaType.value() match {
      case asyncApiRegex(_*) => AsyncApi20Dialect.dialect
      case jsonRegex07(_*)     => JsonSchemaDraft7Dialect.dialect
      case jsonRegex04(_*)     => JsonSchemaDraft4Dialect.dialect
      case oas3Regex(_*)     => OAS30Dialect.dialect
      case raml10Regex(_*)   => Raml10TypesDialect.dialect
      case avroRegex(_*)     => AvroDialect.dialect
      case _                 => AsyncApi20Dialect.dialect
    }
  }
}
