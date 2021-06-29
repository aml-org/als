package org.mulesoft.amfintegration.dialect.dialects.asyncapi20

import amf.aml.client.scala.model.domain.PropertyMapping
import amf.apicontract.internal.metamodel.domain.CorrelationIdModel
import amf.core.client.scala.vocabulary.Namespace.XsdTypes.xsdString
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
