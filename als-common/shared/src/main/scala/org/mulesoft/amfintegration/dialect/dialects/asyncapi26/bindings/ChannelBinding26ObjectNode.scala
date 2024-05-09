package org.mulesoft.amfintegration.dialect.dialects.asyncapi26.bindings

import amf.aml.client.scala.model.domain.PropertyMapping
import amf.apicontract.internal.metamodel.domain.bindings.{
  ChannelBindingModel,
  GooglePubSubChannelBindingModel,
  IBMMQChannelBindingModel,
  IBMMQChannelQueueModel,
  IBMMQChannelTopicModel
}
import amf.core.client.scala.vocabulary.Namespace.XsdTypes.{xsdBoolean, xsdString}
import org.mulesoft.amfintegration.dialect.dialects.oas.nodes.DialectNode

object ChannelBinding26ObjectNode extends BindingObjectNode26 {
  override protected def keys: Seq[String] = super.keys ++ Seq(
    "ibmmq",
    "googlepubsub",
    "anypointmq",
    "pulsar"
  )
  override def name: String = "ChannelBindingObjectNode"

  override def nodeTypeMapping: String = ChannelBindingModel.`type`.head.iri()
}

object IBMMQChannelBindingObject extends DialectNode {
  override def name: String = "IBMMQChannelBindingObject"

  override def nodeTypeMapping: String = IBMMQChannelBindingModel.`type`.head.iri()

  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(location + s"#/declarations/$name/destinationType")
      .withName("destinationType")
      .withNodePropertyMapping(IBMMQChannelBindingModel.DestinationType.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/queue")
      .withName("queue")
      .withNodePropertyMapping(IBMMQChannelBindingModel.Queue.value.iri())
      .withObjectRange(Seq(IBMMQChannelQueueObject.id)),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/topic")
      .withName("topic")
      .withNodePropertyMapping(IBMMQChannelBindingModel.Topic.value.iri())
      .withObjectRange(Seq(IBMMQChannelTopicObject.id))
  )
}

object IBMMQChannelQueueObject extends DialectNode {
  override def name: String = "IBMMQChannelQueueObject"

  override def nodeTypeMapping: String = IBMMQChannelQueueModel.`type`.head.iri()

  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(location + s"#/declarations/$name/objectName")
      .withName("objectName")
      .withNodePropertyMapping(IBMMQChannelQueueModel.ObjectName.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/isPartitioned")
      .withName("isPartitioned")
      .withNodePropertyMapping(IBMMQChannelQueueModel.IsPartitioned.value.iri())
      .withLiteralRange(xsdBoolean.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/exclusive")
      .withName("exclusive")
      .withNodePropertyMapping(IBMMQChannelQueueModel.Exclusive.value.iri())
      .withLiteralRange(xsdBoolean.iri())
  )
}

object IBMMQChannelTopicObject extends DialectNode {
  override def name: String = "IBMMQChannelTopicObject"

  override def nodeTypeMapping: String = IBMMQChannelTopicModel.`type`.head.iri()

  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(location + s"#/declarations/$name/string")
      .withName("string")
      .withNodePropertyMapping(IBMMQChannelTopicModel.String.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/objectName")
      .withName("objectName")
      .withNodePropertyMapping(IBMMQChannelTopicModel.ObjectName.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/durablePermitted")
      .withName("durablePermitted")
      .withNodePropertyMapping(IBMMQChannelTopicModel.DurablePermitted.value.iri())
      .withLiteralRange(xsdBoolean.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/lastMsgRetained")
      .withName("lastMsgRetained")
      .withNodePropertyMapping(IBMMQChannelTopicModel.LastMsgRetained.value.iri())
      .withLiteralRange(xsdBoolean.iri())
  )
}
