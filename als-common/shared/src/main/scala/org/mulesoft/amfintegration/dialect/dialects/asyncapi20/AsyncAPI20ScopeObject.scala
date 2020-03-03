package org.mulesoft.amfintegration.dialect.dialects.asyncapi20

import amf.dialects.oas.nodes.DialectNode
import amf.plugins.domain.webapi.metamodel.security.ScopeModel
import amf.plugins.document.vocabularies.model.domain.PropertyMapping
import amf.core.vocabulary.Namespace.XsdTypes.xsdString

object AsyncAPI20ScopeObject extends DialectNode {
  override def name: String            = "ScopeObject"
  override def nodeTypeMapping: String = ScopeModel.`type`.head.iri()
  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(AsyncApi20Dialect.DialectLocation + "#/declarations/ScopeObject/name")
      .withName("name")
      .withMinCount(1)
      .withNodePropertyMapping(ScopeModel.Name.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(AsyncApi20Dialect.DialectLocation + "#/declarations/ScopeObject/description")
      .withName("description")
      .withNodePropertyMapping(ScopeModel.Description.value.iri())
      .withLiteralRange(xsdString.iri())
  )
}
