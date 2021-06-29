package org.mulesoft.amfintegration.dialect.dialects.oas.nodes

import amf.aml.client.scala.model.domain.PropertyMapping
import amf.core.client.scala.vocabulary.Namespace.XsdTypes.{amlLink, xsdBoolean, xsdString}
import amf.shapes.internal.domain.metamodel.XMLSerializerModel
import org.mulesoft.amfintegration.dialect.dialects.oas.OasBaseDialect

object XmlObject extends DialectNode {

  override def name: String            = "XMLObject"
  override def nodeTypeMapping: String = XMLSerializerModel.`type`.head.iri()
  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(OasBaseDialect.DialectLocation + "#/declarations/XMLObject/name")
      .withNodePropertyMapping(XMLSerializerModel.Name.value.iri())
      .withName("name")
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(OasBaseDialect.DialectLocation + "#/declarations/XMLObject/namespace")
      .withNodePropertyMapping(XMLSerializerModel.Namespace.value.iri())
      .withName("namespace")
      .withLiteralRange(amlLink.iri()),
    PropertyMapping()
      .withId(OasBaseDialect.DialectLocation + "#/declarations/XMLObject/prefix")
      .withNodePropertyMapping(XMLSerializerModel.Prefix.value.iri())
      .withName("prefix")
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(OasBaseDialect.DialectLocation + "#/declarations/XMLObject/attribute")
      .withNodePropertyMapping(XMLSerializerModel.Attribute.value.iri())
      .withName("attribute")
      .withLiteralRange(xsdBoolean.iri()),
    PropertyMapping()
      .withId(OasBaseDialect.DialectLocation + "#/declarations/XMLObject/wrapped")
      .withNodePropertyMapping(XMLSerializerModel.Wrapped.value.iri())
      .withName("wrapped")
      .withLiteralRange(xsdBoolean.iri())
  )
}
