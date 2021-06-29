package org.mulesoft.amfintegration.dialect.dialects.oas.nodes

import amf.aml.client.scala.model.domain.PropertyMapping
import amf.apicontract.internal.metamodel.domain.PayloadModel
import amf.core.client.scala.vocabulary.Namespace.XsdTypes.amlAnyNode
import amf.shapes.internal.domain.metamodel.ExampleModel
import org.mulesoft.amfintegration.dialect.dialects.oas.OAS20Dialect.DialectLocation

trait Oas30ExampleProperty {
  val example: PropertyMapping = PropertyMapping()
    .withId(DialectLocation + "#/declarations/ContentObject/example")
    .withName("example")
    .withNodePropertyMapping(PayloadModel.Examples.value.iri())
    .withLiteralRange(amlAnyNode.iri())
  val examples: PropertyMapping = PropertyMapping()
    .withId(DialectLocation + "#/declarations/ContentObject/examples")
    .withName("examples")
    .withNodePropertyMapping(PayloadModel.Examples.value.iri())
    .withMapTermKeyProperty(ExampleModel.MediaType.value.iri())
    .withObjectRange(Seq(Oas30ExampleObject.id))
}
