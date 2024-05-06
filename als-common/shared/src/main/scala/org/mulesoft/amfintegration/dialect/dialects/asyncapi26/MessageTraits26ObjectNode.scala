package org.mulesoft.amfintegration.dialect.dialects.asyncapi26

import amf.aml.client.scala.model.domain.PropertyMapping
import amf.apicontract.internal.metamodel.domain.{MessageModel, PayloadModel}
import amf.core.client.scala.vocabulary.Namespace.XsdTypes.xsdString
import org.mulesoft.amfintegration.dialect.dialects.asyncapi20.MessageAbstractObjectNode
import org.mulesoft.amfintegration.dialect.dialects.asyncapi20.bindings.MessageBindingsObjectNode
import org.mulesoft.amfintegration.dialect.dialects.asyncapi20.schema.NodeShapeAsync2Node
import org.mulesoft.amfintegration.dialect.dialects.oas.nodes.{
  AMLExternalDocumentationObject,
  AMLTagObject,
  DialectNode
}
object MessageTraits26ObjectNode extends MessageAbstractObjectNode {
  override val specVersion: String = "2.6.0"
  override def name: String = "MessageTraitsObjectNode"

  override def isAbstract: Boolean = true

  override def nodeTypeMapping: String = MessageModel.`type`.head.iri()

  override val exampleProperty: PropertyMapping = PropertyMapping()
    .withId(location + "#/declarations/Message/examples")
    .withName("examples")
    .withNodePropertyMapping(MessageModel.Examples.value.iri())
    .withObjectRange(Seq(Async21MessageExampleNode.id))
}
