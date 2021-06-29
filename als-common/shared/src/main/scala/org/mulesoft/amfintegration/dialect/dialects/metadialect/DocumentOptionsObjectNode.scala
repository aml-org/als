package org.mulesoft.amfintegration.dialect.dialects.metadialect

import amf.aml.client.scala.model.domain.PropertyMapping
import amf.aml.internal.metamodel.domain.DocumentsModelModel
import amf.core.client.scala.vocabulary.Namespace.XsdTypes.{xsdBoolean, xsdString}
import org.mulesoft.amfintegration.dialect.dialects.oas.nodes.DialectNode

object DocumentOptionsObjectNode extends DialectNode {
  override def name: String = "DocumentOptionsObjectNode"

  override def nodeTypeMapping: String = "NonExisting"

  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(location + s"#/declarations/$name/selfEncoded")
      .withNodePropertyMapping(DocumentsModelModel.SelfEncoded.value.iri())
      .withName("selfEncoded")
      .withLiteralRange(xsdBoolean.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/declarationsPath")
      .withNodePropertyMapping(DocumentsModelModel.DeclarationsPath.value.iri())
      .withName("declarationsPath")
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/keyProperty")
      .withNodePropertyMapping(DocumentsModelModel.KeyProperty.value.iri())
      .withName("keyProperty")
      .withLiteralRange(xsdBoolean.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/referenceStyle")
      .withNodePropertyMapping(DocumentsModelModel.ReferenceStyle.value.iri())
      .withName("referenceStyle")
      .withLiteralRange(xsdString.iri())
      .withEnum(Seq("RamlStyle", "JsonSchemaStyle"))
  )
}
