package org.mulesoft.amfintegration.dialect.dialects.asyncapi20

import amf.aml.client.scala.model.domain.PropertyMapping
import amf.apicontract.internal.metamodel.domain.{MessageModel, PayloadModel}
import amf.core.client.scala.vocabulary.Namespace.XsdTypes.xsdString
import org.mulesoft.amfintegration.dialect.dialects.asyncapi20.bindings.MessageBindingsObjectNode
import org.mulesoft.amfintegration.dialect.dialects.asyncapi20.schema.NodeShapeAsync2Node
import org.mulesoft.amfintegration.dialect.dialects.oas.nodes.{
  AMLExternalDocumentationObject,
  AMLTagObject,
  DialectNode
}

trait MessageAbstractObjectNode extends DialectNode {

  val exampleProperty: PropertyMapping
  val specVersion: String

  protected def mediaTypes: Seq[String] = Seq(
    s"application/vnd.aai.asyncapi;version=$specVersion",
    s"application/vnd.aai.asyncapi+json;version=$specVersion",
    s"application/vnd.aai.asyncapi+yaml;version=$specVersion",
    "application/vnd.oai.openapi;version=3.0.0",
    "application/vnd.oai.openapi+json;version=3.0.0",
    "application/vnd.oai.openapi+yaml;version=3.0.0",
    "application/schema+json;version=draft-07",
    "application/schema+yaml;version=draft-07",
    "application/raml+yaml;version=1.0"
  )
  lazy val schemaFormatProp: PropertyMapping = PropertyMapping()
    .withId(location + "#/declarations/Message/schemaFormat")
    .withName("schemaFormat")
    .withNodePropertyMapping(PayloadModel.SchemaMediaType.value.iri())
    .withLiteralRange(xsdString.iri())
    .withEnum(
      mediaTypes
    )
  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(location + "#/declarations/Message/headers")
      .withName("headers")
      .withNodePropertyMapping(MessageModel.HeaderSchema.value.iri())
      .withObjectRange(Seq(NodeShapeAsync2Node.id)), // todo: schema async2 id
    PropertyMapping()
      .withId(location + "#/declarations/Message/correlationId")
      .withName("correlationId")
      .withNodePropertyMapping(MessageModel.CorrelationId.value.iri())
      .withObjectRange(Seq(CorrelationIdObjectNode.id)),
    schemaFormatProp,
    PropertyMapping()
      .withId(location + "#/declarations/Message/contentType")
      .withName("contentType")
      .withNodePropertyMapping(PayloadModel.MediaType.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + "#/declarations/Message/name")
      .withName("name")
      .withNodePropertyMapping(MessageModel.Name.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + "#/declarations/Message/title")
      .withName("title")
      .withNodePropertyMapping(MessageModel.Title.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + "#/declarations/Message/summary")
      .withName("summary")
      .withNodePropertyMapping(MessageModel.Summary.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + "#/declarations/Message/description")
      .withName("description")
      .withNodePropertyMapping(MessageModel.Description.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + "#/declarations/Message/tags")
      .withName("tags")
      .withNodePropertyMapping(MessageModel.Tags.value.iri())
      .withObjectRange(Seq(AMLTagObject.id))
      .withAllowMultiple(true),
    PropertyMapping()
      .withId(location + "#/declarations/Message/externalDocs")
      .withName("externalDocs")
      .withNodePropertyMapping(MessageModel.Documentation.value.iri())
      .withObjectRange(Seq(AMLExternalDocumentationObject.id)),
    PropertyMapping()
      .withId(location + "#/declarations/Message/bindings")
      .withName("bindings")
      .withNodePropertyMapping(MessageModel.Bindings.value.iri())
      .withObjectRange(Seq(MessageBindingsObjectNode.id)),
    exampleProperty
  )
}

object MessageTraitsObjectNode extends MessageAbstractObjectNode {
  override val specVersion: String = "2.0.0"
  override def name: String        = "MessageTraitsObjectNode"

  override def isAbstract: Boolean = true

  override def nodeTypeMapping: String = MessageModel.`type`.head.iri()

  override val exampleProperty: PropertyMapping = PropertyMapping()
    .withId(location + "#/declarations/Message/examples")
    .withName("examples")
    .withNodePropertyMapping(MessageModel.Examples.value.iri())
    .withObjectRange(Seq(Async20MessageExampleNode.id))
}
