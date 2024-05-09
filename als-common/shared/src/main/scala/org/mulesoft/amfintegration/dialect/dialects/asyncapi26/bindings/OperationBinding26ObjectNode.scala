package org.mulesoft.amfintegration.dialect.dialects.asyncapi26.bindings

import amf.aml.client.scala.model.domain.PropertyMapping
import amf.apicontract.internal.metamodel.domain.bindings._
import amf.core.client.scala.vocabulary.Namespace.XsdTypes.xsdString
import org.mulesoft.amfintegration.dialect.dialects.asyncapi20.bindings.BindingVersionPropertyMapping
import org.mulesoft.amfintegration.dialect.dialects.oas.nodes.DialectNode

object OperationBinding26ObjectNode extends BindingObjectNode26 {
  override protected def keys: Seq[String] = super.keys ++ Seq(
    "solace"
  )
  override def name: String = "OperationBindingObjectNode"

  override def nodeTypeMapping: String = OperationBindingModel.`type`.head.iri()
}

object SolaceOperationBindingObject extends DialectNode with BindingVersionPropertyMapping {
  override def name: String = "SolaceOperationBindingObject"

  override def nodeTypeMapping: String = SolaceOperationBindingModel.`type`.head.iri()

  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(location + s"#/declarations/$name/destinations")
      .withName("destinations")
      .withNodePropertyMapping(SolaceOperationBindingModel.Destinations.value.iri())
      .withAllowMultiple(true)
      .withObjectRange(Seq(SolaceOperationDestinationObject.id)) // id of schemas
  ) :+ bindingVersion
}

object SolaceOperationDestinationObject extends DialectNode {
  override def name: String = "SolaceOperationDestinationObject"

  override def nodeTypeMapping: String = SolaceOperationDestinationModel.`type`.head.iri()

  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(location + s"#/declarations/$name/destinationType")
      .withName("destinationType")
      .withNodePropertyMapping(SolaceOperationDestinationModel.DestinationType.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/deliveryMode")
      .withName("deliveryMode")
      .withNodePropertyMapping(SolaceOperationDestinationModel.DeliveryMode.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/queue")
      .withName("queue")
      .withNodePropertyMapping(SolaceOperationDestinationModel.Queue.value.iri())
      .withObjectRange(Seq(SolaceOperationQueueObject.id)),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/topic")
      .withName("topic")
      .withNodePropertyMapping(SolaceOperationDestinationModel.Topic.value.iri())
      .withObjectRange(Seq(SolaceOperationTopicObject.id))
  )
}
object SolaceOperationQueueObject extends DialectNode {
  override def name: String = "SolaceOperationQueueObject"

  override def nodeTypeMapping: String = SolaceOperationQueueModel.`type`.head.iri()

  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(location + s"#/declarations/$name/topicSubscriptions")
      .withName("topicSubscriptions")
      .withNodePropertyMapping(SolaceOperationQueueModel.TopicSubscriptions.value.iri())
      .withAllowMultiple(true)
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/accessType")
      .withName("accessType")
      .withNodePropertyMapping(SolaceOperationQueueModel.AccessType.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/maxMsgSpoolSize")
      .withName("maxMsgSpoolSize")
      .withNodePropertyMapping(SolaceOperationQueueModel.MaxMsgSpoolSize.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/maxTtl")
      .withName("maxTtl")
      .withNodePropertyMapping(SolaceOperationQueueModel.MaxTtl.value.iri())
      .withLiteralRange(xsdString.iri())
  )
}

object SolaceOperationTopicObject extends DialectNode {
  override def name: String = "SolaceOperationTopicObject"

  override def nodeTypeMapping: String = SolaceOperationTopicModel.`type`.head.iri()

  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(location + s"#/declarations/$name/topicSubscriptions")
      .withName("topicSubscriptions")
      .withNodePropertyMapping(SolaceOperationTopicModel.TopicSubscriptions.value.iri())
      .withAllowMultiple(true)
      .withLiteralRange(xsdString.iri())
  )
}
