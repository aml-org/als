package org.mulesoft.amfintegration.dialect.dialects.oas.nodes

import amf.core.vocabulary.Namespace.XsdTypes.xsdString
import org.mulesoft.amfintegration.dialect.dialects.oas.OAS30Dialect
import amf.plugins.document.vocabularies.model.domain.PropertyMapping
import amf.plugins.domain.webapi.metamodel.{IriTemplateMappingModel, TemplatedLinkModel}

object AMLLinkObject extends DialectNode {
  override def name: String            = "LinkObject"
  override def nodeTypeMapping: String = TemplatedLinkModel.`type`.head.iri()
  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(OAS30Dialect.DialectLocation + "#/declarations/LinkObject/name")
      .withName("name")
      .withNodePropertyMapping(TemplatedLinkModel.Name.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(OAS30Dialect.DialectLocation + "#/declarations/LinkObject/operationRef")
      .withName("operationRef")
      .withNodePropertyMapping(TemplatedLinkModel.OperationRef.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(OAS30Dialect.DialectLocation + "#/declarations/LinkObject/operationId")
      .withName("operationId")
      .withNodePropertyMapping(TemplatedLinkModel.OperationId.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(OAS30Dialect.DialectLocation + "#/declarations/LinkObject/parameters")
      .withName("parameters")
      .withNodePropertyMapping(TemplatedLinkModel.Mapping.value.iri())
      .withMapTermKeyProperty(IriTemplateMappingModel.TemplateVariable.value.iri())
      .withMapTermValueProperty(IriTemplateMappingModel.LinkExpression.value.iri())
      .withObjectRange(Seq(AMLIriTemplateMappingObject.id)),
    PropertyMapping()
      .withId(OAS30Dialect.DialectLocation + "#/declarations/LinkObject/requestBody")
      .withName("requestBody")
      .withNodePropertyMapping(TemplatedLinkModel.RequestBody.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(OAS30Dialect.DialectLocation + "#/declarations/LinkObject/description")
      .withName("description")
      .withNodePropertyMapping(TemplatedLinkModel.Description.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(OAS30Dialect.DialectLocation + "#/declarations/LinkObject/server")
      .withName("server")
      .withNodePropertyMapping(TemplatedLinkModel.Server.value.iri())
      .withObjectRange(Seq(Oas30ServerObject.id))
  )
}

object AMLIriTemplateMappingObject extends DialectNode {

  override def name: String            = "IriTemplateMapping"
  override def nodeTypeMapping: String = IriTemplateMappingModel.`type`.head.iri()
  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(OAS30Dialect.DialectLocation + "#/declarations/IriTemplateMapping/variable")
      .withName("parameters")
      .withNodePropertyMapping(IriTemplateMappingModel.TemplateVariable.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(OAS30Dialect.DialectLocation + "#/declarations/IriTemplateMapping/expression")
      .withName("expression")
      .withNodePropertyMapping(IriTemplateMappingModel.LinkExpression.value.iri())
      .withLiteralRange(xsdString.iri())
  )
}
