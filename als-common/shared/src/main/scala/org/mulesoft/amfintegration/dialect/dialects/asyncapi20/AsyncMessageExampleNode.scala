package org.mulesoft.amfintegration.dialect.dialects.asyncapi20

import amf.plugins.document.vocabularies.model.domain.PropertyMapping
import amf.plugins.domain.shapes.metamodel.ExampleModel
import amf.plugins.domain.webapi.metamodel.MessageModel
import org.mulesoft.amfintegration.dialect.dialects.oas.nodes.DialectNode

class AsyncMessageExampleNode extends DialectNode {
  override def name: String = "AsyncMessageExampleNode"

  override def nodeTypeMapping: String = ExampleModel.`type`.head.iri()

  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(location + "#/declarations/Message/examples/payload")
      .withName("payload")
      .withNodePropertyMapping(MessageModel.Examples.value.iri()),
    PropertyMapping()
      .withId(location + "#/declarations/Message/examples/headers")
      .withName("headers")
      .withNodePropertyMapping(MessageModel.HeaderExamples.value.iri())
  )
}

object AsyncMessageExampleNode extends AsyncMessageExampleNode
