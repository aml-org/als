package org.mulesoft.amfintegration.dialect.dialects.asyncapi26.bindings

import amf.aml.client.scala.model.domain.PropertyMapping
import amf.apicontract.internal.metamodel.domain.bindings._
import amf.core.client.scala.vocabulary.Namespace.XsdTypes.{xsdBoolean, xsdInteger, xsdString}
import org.mulesoft.amfintegration.dialect.dialects.asyncapi20.bindings.BindingVersionPropertyMapping
import org.mulesoft.amfintegration.dialect.dialects.asyncapi26.bindings.SolaceOperationBinding10Object.{location, name}
import org.mulesoft.amfintegration.dialect.dialects.asyncapi26.bindings.SolaceOperationBinding20Object.{location, name}
import org.mulesoft.amfintegration.dialect.dialects.asyncapi26.bindings.SolaceOperationBinding30Object.{location, name}
import org.mulesoft.amfintegration.dialect.dialects.asyncapi26.bindings.SolaceOperationDestination10Object.{
  location,
  name
}
import org.mulesoft.amfintegration.dialect.dialects.asyncapi26.bindings.SolaceOperationDestination20Object.{
  location,
  name
}
import org.mulesoft.amfintegration.dialect.dialects.asyncapi26.bindings.SolaceOperationDestination30Object.{
  location,
  name
}
import org.mulesoft.amfintegration.dialect.dialects.oas.nodes.DialectNode

object OperationBinding26ObjectNode extends BindingObjectNode26 {
  override protected def keys: Seq[String] = super.keys ++ Seq(
    "solace"
  )
  override def name: String = "OperationBindingObjectNode"

  override def nodeTypeMapping: String = OperationBindingModel.`type`.head.iri()
}

object SolaceOperationBinding10Object extends BaseSolaceOperationBindingObject {
  override def nodeTypeMapping: String = SolaceOperationBinding010Model.`type`.head.iri()
  override def properties: Seq[PropertyMapping] = super.properties ++ Seq(
    PropertyMapping()
      .withId(location + s"#/declarations/$name/destinations")
      .withName("destinations")
      .withNodePropertyMapping(SolaceOperationBindingModel.Destinations.value.iri())
      .withAllowMultiple(true)
      .withObjectRange(Seq(SolaceOperationDestination10Object.id)) // id of schemas
  )
}
object SolaceOperationBinding20Object extends BaseSolaceOperationBindingObject {
  override def nodeTypeMapping: String = SolaceOperationBinding020Model.`type`.head.iri()
  override def properties: Seq[PropertyMapping] = super.properties ++ Seq(
    PropertyMapping()
      .withId(location + s"#/declarations/$name/destinations")
      .withName("destinations")
      .withNodePropertyMapping(SolaceOperationBinding020Model.Destinations.value.iri())
      .withAllowMultiple(true)
      .withObjectRange(Seq(SolaceOperationDestination20Object.id)) // id of schemas
  )
}
object SolaceOperationBinding30Object extends BaseSolaceOperationBindingObject {
  override def nodeTypeMapping: String = SolaceOperationBinding030Model.`type`.head.iri()
  override def properties: Seq[PropertyMapping] = super.properties ++ Seq(
    PropertyMapping()
      .withId(location + s"#/declarations/$name/destinations")
      .withName("destinations")
      .withNodePropertyMapping(SolaceOperationBinding030Model.Destinations.value.iri())
      .withAllowMultiple(true)
      .withObjectRange(Seq(SolaceOperationDestination30Object.id)) // id of schemas
  )
}
object SolaceOperationBinding40Object extends BaseSolaceOperationBindingObject {
  override def nodeTypeMapping: String = SolaceOperationBinding040Model.`type`.head.iri()
  override def properties: Seq[PropertyMapping] = super.properties ++ Seq(
    PropertyMapping()
      .withId(location + s"#/declarations/$name/destinations")
      .withName("destinations")
      .withNodePropertyMapping(SolaceOperationBinding040Model.Destinations.value.iri())
      .withAllowMultiple(true)
      .withObjectRange(Seq(SolaceOperationDestination40Object.id)),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/timeToLive")
      .withName("timeToLive")
      .withNodePropertyMapping(SolaceOperationBinding040Model.TimeToLive.value.iri())
      .withAllowMultiple(true)
      .withLiteralRange(xsdInteger.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/priority")
      .withName("priority")
      .withNodePropertyMapping(SolaceOperationBinding040Model.Priority.value.iri())
      .withAllowMultiple(true)
      .withLiteralRange(xsdInteger.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/dmqEligible")
      .withName("dmqEligible")
      .withNodePropertyMapping(SolaceOperationBinding040Model.DmqEligible.value.iri())
      .withAllowMultiple(true)
      .withLiteralRange(xsdBoolean.iri())
  )
}
trait BaseSolaceOperationBindingObject extends DialectNode with BindingVersionPropertyMapping {
  override def name: String = "SolaceOperationBindingObject"

  override def nodeTypeMapping: String = SolaceOperationBindingModel.`type`.head.iri()

  override def properties: Seq[PropertyMapping] = Seq(bindingVersion)
}

object SolaceOperationDestination10Object extends BaseSolaceOperationDestinationObject {
  override def nodeTypeMapping: String = SolaceOperationDestination010Model.`type`.head.iri()

  override def properties: Seq[PropertyMapping] = super.properties ++ Seq(
    PropertyMapping()
      .withId(location + s"#/declarations/$name/queue")
      .withName("queue")
      .withNodePropertyMapping(SolaceOperationDestination010Model.Queue.value.iri())
      .withObjectRange(Seq(SolaceOperationQueue10Object.id))
  )
}
object SolaceOperationDestination20Object extends BaseSolaceOperationDestinationObject {
  override def nodeTypeMapping: String = SolaceOperationDestination020Model.`type`.head.iri()

  override def properties: Seq[PropertyMapping] = super.properties ++ Seq(
    PropertyMapping()
      .withId(location + s"#/declarations/$name/queue")
      .withName("queue")
      .withNodePropertyMapping(SolaceOperationDestination020Model.Queue.value.iri())
      .withObjectRange(Seq(SolaceOperationQueue10Object.id)),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/topic")
      .withName("topic")
      .withNodePropertyMapping(SolaceOperationDestination020Model.Topic.value.iri())
      .withObjectRange(Seq(SolaceOperationTopicObject.id))
  )
}
object SolaceOperationDestination30Object extends BaseSolaceOperationDestinationObject {
  override def nodeTypeMapping: String = SolaceOperationDestination030Model.`type`.head.iri()
  override def properties: Seq[PropertyMapping] = super.properties ++ Seq(
    PropertyMapping()
      .withId(location + s"#/declarations/$name/queue")
      .withName("queue")
      .withNodePropertyMapping(SolaceOperationDestination030Model.Queue.value.iri())
      .withObjectRange(Seq(SolaceOperationQueue30Object.id)),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/topic")
      .withName("topic")
      .withNodePropertyMapping(SolaceOperationDestination030Model.Topic.value.iri())
      .withObjectRange(Seq(SolaceOperationTopicObject.id))
  )
}
object SolaceOperationDestination40Object
    extends BaseSolaceOperationDestinationObject
    with BindingVersionPropertyMapping {
  override def nodeTypeMapping: String = SolaceOperationDestination040Model.`type`.head.iri()
  override def properties: Seq[PropertyMapping] = super.properties ++ Seq(
    PropertyMapping()
      .withId(location + s"#/declarations/$name/queue")
      .withName("queue")
      .withNodePropertyMapping(SolaceOperationDestination040Model.Queue.value.iri())
      .withObjectRange(Seq(SolaceOperationQueue30Object.id)),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/topic")
      .withName("topic")
      .withNodePropertyMapping(SolaceOperationDestination040Model.Topic.value.iri())
      .withObjectRange(Seq(SolaceOperationTopicObject.id))
  ) :+ bindingVersion
}
trait BaseSolaceOperationDestinationObject extends DialectNode {
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
      .withLiteralRange(xsdString.iri())
  )
}

object SolaceOperationQueue10Object extends BaseSolaceOperationQueueObject {
  override def nodeTypeMapping: String = SolaceOperationQueue010Model.`type`.head.iri()
}
object SolaceOperationQueue30Object extends BaseSolaceOperationQueueObject {
  override def nodeTypeMapping: String = SolaceOperationQueue030Model.`type`.head.iri()
  override def properties: Seq[PropertyMapping] = super.properties ++ Seq(
    PropertyMapping()
      .withId(location + s"#/declarations/$name/maxMsgSpoolSize")
      .withName("maxMsgSpoolSize")
      .withNodePropertyMapping(SolaceOperationQueue030Model.MaxMsgSpoolSize.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/maxTtl")
      .withName("maxTtl")
      .withNodePropertyMapping(SolaceOperationQueue030Model.MaxTtl.value.iri())
      .withLiteralRange(xsdString.iri())
  )
}
trait BaseSolaceOperationQueueObject extends DialectNode {
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
