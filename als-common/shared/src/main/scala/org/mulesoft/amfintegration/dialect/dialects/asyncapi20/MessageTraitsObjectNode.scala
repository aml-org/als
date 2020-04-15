package org.mulesoft.amfintegration.dialect.dialects.asyncapi20

import amf.core.vocabulary.Namespace.XsdTypes.xsdString
import amf.dialects.oas.nodes.{AMLExternalDocumentationObject, AMLTagObject, DialectNode}
import amf.plugins.document.vocabularies.model.domain.PropertyMapping
import amf.plugins.domain.webapi.metamodel.{MessageModel, PayloadModel}
import org.mulesoft.amfintegration.dialect.dialects.asyncapi20.bindings.MessageBindingsObjectNode

trait MessageAbstractObjectNode extends DialectNode {
  override def nodeTypeMapping: String = MessageModel.`type`.head.iri()

  val schemaFormatProp = PropertyMapping()
    .withId(location + "#/declarations/Message/schemaFormat")
    .withName("schemaFormat")
    .withNodePropertyMapping(PayloadModel.SchemaMediaType.value.iri())
    .withLiteralRange(xsdString.iri())
    .withEnum(Seq(
      "application/vnd.aai.asyncapi;version=2.0.0",
      "application/vnd.aai.asyncapi+json;version=2.0.0",
      "application/vnd.aai.asyncapi+yaml;version=2.0.0",
      "application/vnd.oai.openapi;version=3.0.0",
      "application/vnd.oai.openapi+json;version=3.0.0",
      "application/vnd.oai.openapi+yaml;version=3.0.0",
      "application/schema+json;version=draft-07",
      "application/schema+yaml;version=draft-07"
    ))
  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(location + "#/declarations/Message/headers")
      .withName("headers")
      .withNodePropertyMapping(MessageModel.Headers.value.iri())
      .withObjectRange(Seq()), // todo: schema async2 id
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
    PropertyMapping()
      .withId(location + "#/declarations/Message/examples")
      .withName("examples")
      .withNodePropertyMapping(MessageModel.Examples.value.iri())
      .withObjectRange(Seq(""))
  )
}

object MessageTraitsObjectNode extends MessageAbstractObjectNode {
  override def name: String = "MessageTraitsObjectNode"

  override def isAbstract: Boolean = true
}
