package org.mulesoft.amfintegration.dialect.dialects.oas.nodes

import amf.core.vocabulary.Namespace.XsdTypes.amlAnyNode
import org.mulesoft.amfintegration.dialect.dialects.oas.OAS20Dialect.DialectLocation
import amf.plugins.document.vocabularies.model.domain.PropertyMapping
import amf.plugins.domain.shapes.metamodel.ExampleModel
import amf.plugins.domain.webapi.metamodel.PayloadModel

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
