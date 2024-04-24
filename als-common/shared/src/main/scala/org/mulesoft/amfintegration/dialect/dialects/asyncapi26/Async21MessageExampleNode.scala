package org.mulesoft.amfintegration.dialect.dialects.asyncapi26

import amf.aml.client.scala.model.domain.PropertyMapping
import amf.apicontract.internal.metamodel.domain.MessageModel
import amf.shapes.internal.domain.metamodel.ExampleModel
import org.mulesoft.amfintegration.dialect.dialects.asyncapi20.AsyncMessageExampleNode
import org.mulesoft.amfintegration.dialect.dialects.asyncapi26.Async21MessageExampleNode.location
import org.mulesoft.amfintegration.dialect.dialects.oas.nodes.DialectNode

trait Async21ExampleMessageMappings {
  protected def mappingsMessages21 = Seq(
    PropertyMapping()
      .withId(location + "#/declarations/Message/examples/name")
      .withName("name")
      .withNodePropertyMapping(MessageModel.HeaderExamples.value.iri()),
    PropertyMapping()
      .withId(location + "#/declarations/Message/examples/summary")
      .withName("summary")
      .withNodePropertyMapping(MessageModel.HeaderExamples.value.iri())
  )
}
object Async21MessageExampleNode extends AsyncMessageExampleNode with Async21ExampleMessageMappings {
  override def properties: Seq[PropertyMapping] = super.properties ++ mappingsMessages21
}
