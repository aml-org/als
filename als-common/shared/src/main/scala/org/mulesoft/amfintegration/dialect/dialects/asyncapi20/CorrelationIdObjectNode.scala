package org.mulesoft.amfintegration.dialect.dialects.asyncapi20

import amf.core.vocabulary.Namespace.XsdTypes._
import amf.plugins.document.vocabularies.model.domain.PropertyMapping
import amf.plugins.domain.webapi.metamodel.CorrelationIdModel
import org.mulesoft.amfintegration.dialect.dialects.oas.nodes.DialectNode

object CorrelationIdObjectNode extends DialectNode {
  override def name: String = "CorrelationIdObjectNode"

  override def nodeTypeMapping: String = CorrelationIdModel.`type`.head.iri()

  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(location + "#/declarations/CorrelationId/description")
      .withName("description")
      .withNodePropertyMapping(CorrelationIdModel.Description.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + "#/declarations/CorrelationId/location")
      .withName("location")
      .withNodePropertyMapping(CorrelationIdModel.Location.value.iri())
      .withLiteralRange(xsdString.iri())
  )
}
