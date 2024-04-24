package org.mulesoft.amfintegration.dialect.dialects.asyncapi20

import amf.aml.client.scala.model.domain.PropertyMapping
import amf.apicontract.internal.metamodel.domain.MessageModel
import amf.shapes.internal.domain.metamodel.ExampleModel
import org.mulesoft.amfintegration.dialect.dialects.oas.nodes.DialectNode

trait AsyncMessageExampleNode extends DialectNode {
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

object Async20MessageExampleNode extends AsyncMessageExampleNode
