package org.mulesoft.amfintegration.dialect.dialects.asyncapi20

import amf.core.vocabulary.Namespace.XsdTypes.xsdString
import amf.plugins.document.vocabularies.model.domain.PropertyMapping
import amf.plugins.domain.webapi.metamodel.api.WebApiModel
import amf.plugins.domain.webapi.metamodel.{EndPointModel, ServerModel}
import org.mulesoft.amfintegration.dialect.dialects.oas.OAS20Dialect.OwlSameAs
import org.mulesoft.amfintegration.dialect.dialects.oas.nodes.{
  AMLExternalDocumentationObject,
  AMLInfoObject,
  AMLTagObject,
  DialectNode
}

object AsyncApi20WebApiNode extends DialectNode {
  override val location: String = AsyncApi20Dialect.DialectLocation
  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(location + "#/declarations/WebAPIObject/info")
      .withName("info")
      .withMinCount(1)
      .withNodePropertyMapping(OwlSameAs)
      .withObjectRange(Seq(
        AMLInfoObject.id
      )),
    PropertyMapping()
      .withId(location + "#/declarations/WebAPIObject/id")
      .withName("id")
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + "#/declarations/WebAPIObject/servers")
      .withName("servers")
      .withObjectRange(
        Seq(
          AsyncApiServerObject.id
        ))
      .withMapTermKeyProperty(ServerModel.Name.value.iri()),
    PropertyMapping()
      .withId(location + "#/declarations/WebAPIObject/externalDocs")
      .withName("externalDocs")
      .withObjectRange(
        Seq(
          AMLExternalDocumentationObject.id
        )),
    PropertyMapping()
      .withId(location + "#/declarations/WebAPIObject/tags")
      .withName("tags")
      .withNodePropertyMapping(WebApiModel.Tags.value.iri())
      .withAllowMultiple(true)
      .withObjectRange(Seq(AMLTagObject.id)),
    PropertyMapping()
      .withId(location + "#/declarations/WebAPIObject/channels")
      .withName("channels")
      .withNodePropertyMapping(WebApiModel.EndPoints.value.iri())
      .withMapTermKeyProperty(EndPointModel.Path.value.iri())
      .withObjectRange(Seq(ChannelObject.id))
  )

  override def name: String = "AsyncApiWebApiNode"

  override def nodeTypeMapping: String = WebApiModel.`type`.head.iri()
}
