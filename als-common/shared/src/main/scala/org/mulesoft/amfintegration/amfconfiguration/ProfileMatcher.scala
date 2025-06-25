package org.mulesoft.amfintegration.amfconfiguration

import amf.core.client.common.validation.{ProfileName, ProfileNames}
import amf.core.client.scala.model.document.BaseUnit
import amf.core.internal.remote.Spec
import amf.shapes.internal.spec.common.{
  JSONSchemaDraft201909SchemaVersion,
  JSONSchemaDraft4SchemaVersion,
  JSONSchemaDraft7SchemaVersion
}
import org.mulesoft.amfintegration.DefinitionWithVendor
import org.mulesoft.amfintegration.dialect.dialects.asyncapi20.AsyncApi20Dialect
import org.mulesoft.amfintegration.dialect.dialects.asyncapi26.AsyncApi26Dialect
import org.mulesoft.amfintegration.dialect.dialects.avro.AvroDialect
import org.mulesoft.amfintegration.dialect.dialects.graphql.GraphQLDialect
import org.mulesoft.amfintegration.dialect.dialects.jsonschema.draft2019.JsonSchemaDraft2019Dialect
import org.mulesoft.amfintegration.dialect.dialects.jsonschema.draft4.JsonSchemaDraft4Dialect
import org.mulesoft.amfintegration.dialect.dialects.jsonschema.draft7.JsonSchemaDraft7Dialect
import org.mulesoft.amfintegration.dialect.dialects.metadialect.MetaDialect
import org.mulesoft.amfintegration.dialect.dialects.oas.{OAS20Dialect, OAS30Dialect}
import org.mulesoft.amfintegration.dialect.dialects.raml.raml08.Raml08TypesDialect
import org.mulesoft.amfintegration.dialect.dialects.raml.raml10.Raml10TypesDialect

object ProfileMatcher {
  def profile(model: BaseUnit): ProfileName = model.sourceSpec.map(profile).getOrElse(ProfileNames.AMF)

  def profile(spec: Spec): ProfileName =
    spec match {
      case Spec.RAML10 => ProfileNames.RAML10
      case Spec.RAML08 => ProfileNames.RAML08
      case Spec.OAS20  => ProfileNames.OAS20
      case Spec.OAS30  => ProfileNames.OAS30
      // TODO Implement OAS31 dialect
      case Spec.OAS31       => ProfileNames.OAS31
      case Spec.ASYNC20     => ProfileNames.ASYNC20
      case Spec.ASYNC21     => ProfileNames.ASYNC20
      case Spec.ASYNC22     => ProfileNames.ASYNC20
      case Spec.ASYNC23     => ProfileNames.ASYNC20
      case Spec.ASYNC24     => ProfileNames.ASYNC20
      case Spec.ASYNC25     => ProfileNames.ASYNC20
      case Spec.ASYNC26     => ProfileNames.ASYNC26
      case Spec.AML         => ProfileNames.AML
      case Spec.GRAPHQL     => ProfileNames.GRAPHQL
      case Spec.GRPC        => ProfileNames.GRPC
      case Spec.JSONSCHEMA  => ProfileNames.JSONSCHEMA
      case Spec.AVRO_SCHEMA => ProfileNames.AVROSCHEMA
      case _                => ProfileNames.AMF
    }

  private lazy val webApiDefinitions: Set[DefinitionWithVendor] = Set(
    DefinitionWithVendor(DocumentDefinition(Raml08TypesDialect()), Spec.RAML08),
    DefinitionWithVendor(DocumentDefinition(Raml10TypesDialect()), Spec.RAML10),
    DefinitionWithVendor(DocumentDefinition(OAS20Dialect()), Spec.OAS20),
    DefinitionWithVendor(DocumentDefinition(OAS30Dialect()), Spec.OAS30),
    DefinitionWithVendor(DocumentDefinition(AsyncApi20Dialect()), Spec.ASYNC20),
    DefinitionWithVendor(DocumentDefinition(AsyncApi26Dialect()), Spec.ASYNC26),
    DefinitionWithVendor(DocumentDefinition(GraphQLDialect()), Spec.GRAPHQL),
    DefinitionWithVendor(DocumentDefinition(JsonSchemaDraft4Dialect()), Spec.JSONSCHEMA, JSONSchemaDraft4SchemaVersion.name),
    DefinitionWithVendor(DocumentDefinition(JsonSchemaDraft7Dialect()), Spec.JSONSCHEMA, JSONSchemaDraft7SchemaVersion.name),
    DefinitionWithVendor(DocumentDefinition(JsonSchemaDraft2019Dialect()), Spec.JSONSCHEMA, JSONSchemaDraft201909SchemaVersion.name),
    DefinitionWithVendor(DocumentDefinition(AvroDialect()), Spec.AVRO_SCHEMA),
    DefinitionWithVendor(DocumentDefinition(MetaDialect()), Spec.AML)
  )

  def dialect(spec: Spec): Option[DocumentDefinition] =
    webApiDefinitions.find(_.spec == spec).map(_.documentDefinition)

  def spec(documentDefinition: DocumentDefinition): Option[Spec] =
    webApiDefinitions.find(_.documentDefinition == documentDefinition).map(_.spec)
}
