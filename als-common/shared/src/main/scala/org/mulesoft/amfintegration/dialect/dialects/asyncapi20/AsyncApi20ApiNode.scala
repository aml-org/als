package org.mulesoft.amfintegration.dialect.dialects.asyncapi20

import amf.aml.client.scala.model.domain.PropertyMapping
import amf.apicontract.internal.metamodel.domain.{EndPointModel, ServerModel}
import amf.apicontract.internal.metamodel.domain.api.AsyncApiModel
import amf.core.client.scala.vocabulary.Namespace.XsdTypes.xsdString
import org.mulesoft.amfintegration.dialect.dialects.oas.OAS20Dialect.OwlSameAs
import org.mulesoft.amfintegration.dialect.dialects.oas.nodes.{
  AMLExternalDocumentationObject,
  AMLTagObject,
  DialectNode
}

object AsyncApi20ApiNode extends DialectNode {
  override val location: String = AsyncApi20Dialect.DialectLocation
  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(location + "#/declarations/AsyncAPIObject/info")
      .withName("info")
      .withMinCount(1)
      .withNodePropertyMapping(OwlSameAs)
      .withObjectRange(Seq(
        AMLInfoObject.id
      )),
    PropertyMapping()
      .withId(location + "#/declarations/AsyncAPIObject/id")
      .withName("id")
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + "#/declarations/AsyncAPIObject/servers")
      .withName("servers")
      .withObjectRange(
        Seq(
          AsyncApiServerObject.id
        ))
      .withMapTermKeyProperty(ServerModel.Name.value.iri()),
    PropertyMapping()
      .withId(location + "#/declarations/AsyncAPIObject/externalDocs")
      .withName("externalDocs")
      .withObjectRange(
        Seq(
          AMLExternalDocumentationObject.id
        )),
    PropertyMapping()
      .withId(location + "#/declarations/AsyncAPIObject/tags")
      .withName("tags")
      .withNodePropertyMapping(AsyncApiModel.Tags.value.iri())
      .withAllowMultiple(true)
      .withObjectRange(Seq(AMLTagObject.id)),
    PropertyMapping()
      .withId(location + "#/declarations/AsyncAPIObject/channels")
      .withName("channels")
      .withNodePropertyMapping(AsyncApiModel.EndPoints.value.iri())
      .withMapTermKeyProperty(EndPointModel.Path.value.iri())
      .withObjectRange(Seq(ChannelObject.id))
  )

  override def name: String = "AsyncApi2Node"

  override def nodeTypeMapping: String = AsyncApiModel.`type`.head.iri()
}
