package org.mulesoft.amfintegration.dialect.dialects.oas.nodes

import amf.aml.client.scala.model.domain.PropertyMapping
import amf.apicontract.internal.metamodel.domain.TagModel
import amf.core.client.scala.vocabulary.Namespace.XsdTypes.xsdString
import org.mulesoft.amfintegration.dialect.dialects.oas.OasBaseDialect

object AMLTagObject extends DialectNode {

  override def name: String            = "TagObject"
  override def nodeTypeMapping: String = TagModel.`type`.head.iri()
  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(OasBaseDialect.DialectLocation + "#/declarations/TagObject/name")
      .withNodePropertyMapping(TagModel.Name.value.iri())
      .withName("name")
      .withMinCount(1)
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(OasBaseDialect.DialectLocation + "#/declarations/TagObject/description")
      .withName("description")
      .withNodePropertyMapping(TagModel.Description.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(OasBaseDialect.DialectLocation + "#/declarations/TagObject/externalDocs")
      .withName("externalDocs")
      .withNodePropertyMapping(TagModel.Documentation.value.iri())
      .withObjectRange(
        Seq(
          AMLExternalDocumentationObject.Obj.id
        )
      )
  )
}
