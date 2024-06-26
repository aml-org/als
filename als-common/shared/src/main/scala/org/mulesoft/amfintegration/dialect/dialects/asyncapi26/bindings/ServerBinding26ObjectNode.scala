package org.mulesoft.amfintegration.dialect.dialects.asyncapi26.bindings

import amf.aml.client.scala.model.domain.PropertyMapping
import amf.apicontract.internal.metamodel.domain.bindings.{
  IBMMQServerBindingModel,
  PulsarServerBindingModel,
  ServerBindingModel,
  SolaceServerBindingModel
}
import amf.core.client.scala.vocabulary.Namespace.XsdTypes.{xsdBoolean, xsdInteger, xsdString}
import org.mulesoft.amfintegration.dialect.dialects.asyncapi20.bindings.BindingVersionPropertyMapping
import org.mulesoft.amfintegration.dialect.dialects.oas.nodes.DialectNode

object ServerBinding26ObjectNode extends BindingObjectNode26 {
  override def name: String = "ServerBindingObjectNode"

  override protected def keys: Seq[String] = super.keys ++ Seq(
    "ibmmq",
    "solace",
    "pulsar"
  )

  override def nodeTypeMapping: String = ServerBindingModel.`type`.head.iri()
}

object IBMMQServerBindingObject extends DialectNode with BindingVersionPropertyMapping {
  override def name: String = "IBMMQServerBindingObject"

  override def nodeTypeMapping: String = IBMMQServerBindingModel.`type`.head.iri()

  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(location + s"#/declarations/$name/groupId")
      .withName("groupId")
      .withNodePropertyMapping(IBMMQServerBindingModel.GroupId.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/ccdtQueueManagerName")
      .withName("ccdtQueueManagerName")
      .withNodePropertyMapping(IBMMQServerBindingModel.CcdtQueueManagerName.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/cipherSpec")
      .withName("cipherSpec")
      .withNodePropertyMapping(IBMMQServerBindingModel.CipherSpec.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/multiEndpointServer")
      .withName("multiEndpointServer")
      .withNodePropertyMapping(IBMMQServerBindingModel.MultiEndpointServer.value.iri())
      .withLiteralRange(xsdBoolean.iri()),
    PropertyMapping()
      .withId(location + s"#/declarations/$name/heartBeatInterval")
      .withName("heartBeatInterval")
      .withNodePropertyMapping(IBMMQServerBindingModel.HeartBeatInterval.value.iri())
      .withLiteralRange(xsdInteger.iri())
  ) :+ bindingVersion
}
object SolaceServerBindingObject extends DialectNode with BindingVersionPropertyMapping {
  override def name: String = "SolaceServerBindingObject"

  override def nodeTypeMapping: String = SolaceServerBindingModel.`type`.head.iri()

  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(location + s"#/declarations/$name/msgVpn")
      .withName("msgVpn")
      .withNodePropertyMapping(SolaceServerBindingModel.MsgVpn.value.iri())
      .withLiteralRange(xsdString.iri())
  ) :+ bindingVersion
}

object PulsarServerBindingObject extends DialectNode with BindingVersionPropertyMapping {
  override def name: String = "PulsarServerBindingObject"

  override def nodeTypeMapping: String = PulsarServerBindingModel.`type`.head.iri()

  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(location + s"#/declarations/$name/tenant")
      .withName("tenant")
      .withNodePropertyMapping(PulsarServerBindingModel.Tenant.value.iri())
      .withLiteralRange(xsdString.iri())
  ) :+ bindingVersion
}
