package org.mulesoft.amfintegration.dialect.dialects.jsonschema.draft7

import amf.aml.client.scala.model.domain.PropertyMapping
import amf.apicontract.internal.metamodel.domain.api.WebApiModel
import amf.core.client.scala.vocabulary.Namespace.XsdTypes.{amlAnyNode, xsdString}
import amf.core.internal.metamodel.domain.ShapeModel
import org.mulesoft.amfintegration.dialect.dialects.jsonschema.draft4.Draft4RootNode

object Draft7RootNode extends Draft7RootNode

trait Draft7RootNode extends Draft4RootNode {
  override def identifierMapping(location: String): PropertyMapping =
    PropertyMapping()
      .withId(location + "#/declarations/BaseJsonSchemaDocumentNode/id")
      .withNodePropertyMapping(WebApiModel.Identifier.value.iri())
      .withName("$id")
      .withLiteralRange(xsdString.iri())

  def comment(location: String): PropertyMapping =
    PropertyMapping()
      .withId(location + "#/declarations/BaseJsonSchemaDocumentNode/comment")
      .withNodePropertyMapping(WebApiModel.Documentations.value.iri())
      .withName("$comment")
      .withLiteralRange(xsdString.iri())

  def conditionals(location: String): Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(location + "#/declarations/SchemaObject/if")
      .withNodePropertyMapping(ShapeModel.If.value.iri())
      .withName("if")
      .withObjectRange(Seq(amlAnyNode.iri())),
    PropertyMapping()
      .withId(location + "#/declarations/SchemaObject/then")
      .withNodePropertyMapping(ShapeModel.Then.value.iri())
      .withName("then")
      .withObjectRange(Seq(amlAnyNode.iri())),
    PropertyMapping()
      .withId(location + "#/declarations/SchemaObject/else")
      .withNodePropertyMapping(ShapeModel.Else.value.iri())
      .withName("else")
      .withObjectRange(Seq(amlAnyNode.iri()))
  )
}
