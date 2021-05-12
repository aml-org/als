package org.mulesoft.amfintegration.dialect.dialects.oas.nodes

import amf.core.metamodel.domain.common.NameFieldSchema
import amf.core.vocabulary.Namespace.XsdTypes.xsdString
import amf.plugins.document.vocabularies.model.domain.PropertyMapping
import amf.plugins.domain.webapi.metamodel.api.WebApiModel
import amf.plugins.domain.webapi.metamodel.{EndPointModel, ServerModel}
import org.mulesoft.amfintegration.dialect.dialects.oas.OAS20Dialect.OwlSameAs
import org.mulesoft.amfintegration.dialect.dialects.oas.OAS30Dialect.DialectLocation
import org.mulesoft.amfintegration.dialect.dialects.oas.{OAS20Dialect, OAS30Dialect}

trait WebApiNode extends DialectNode {

  override def name: String            = "WebAPIObject"
  override def nodeTypeMapping: String = WebApiModel.`type`.head.iri()
  def pathItemObjectId: String
  def versionProperties: Seq[PropertyMapping]
  def properties: Seq[PropertyMapping] = versionProperties ++ Seq(
    PropertyMapping()
      .withId(location + "#/declarations/WebAPIObject/info")
      .withName("info")
      .withMinCount(1)
      .withNodePropertyMapping(OwlSameAs)
      .withObjectRange(Seq(
        AMLInfoObject.id
      )),
    PropertyMapping()
      .withId(location + "#/declarations/WebAPIObject/externalDocs")
      .withName("externalDocs")
      .withObjectRange(
        Seq(
          AMLExternalDocumentationObject.id
        )),
    PropertyMapping()
      .withId(location + "#/declarations/security")
      .withName("security")
      .withNodePropertyMapping(WebApiModel.Security.value.iri())
      .withAllowMultiple(true)
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(location + "#/declarations/WebAPIObject/paths")
      .withName("paths")
      .withMinCount(1)
      .withNodePropertyMapping(WebApiModel.EndPoints.value.iri())
      .withMapTermKeyProperty(EndPointModel.Path.value.iri())
      .withAllowMultiple(true)
      .withObjectRange(Seq(pathItemObjectId)),
    PropertyMapping()
      .withId(location + "#/declarations/WebAPIObject/tags")
      .withName("tags")
      .withNodePropertyMapping(WebApiModel.Tags.value.iri())
      .withAllowMultiple(true)
      .withObjectRange(Seq(AMLTagObject.id))
  )

}

object Oas20WebApiNode extends WebApiNode {

  override val location: String = OAS20Dialect.DialectLocation
  override def versionProperties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/WebAPIObject/Servers/url_host")
      .withName("host")
      .withNodePropertyMapping(ServerModel.Url.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/WebAPIObject/Servers/url_basePath")
      .withName("basePath")
      .withNodePropertyMapping(ServerModel.Url.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/WebAPIObject/schemes")
      .withName("schemes")
      .withNodePropertyMapping(WebApiModel.Schemes.value.iri())
      .withEnum(Seq("ws", "wss", "http", "https"))
      .withAllowMultiple(true)
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/WebAPIObject/consumes")
      .withName("consumes")
      .withNodePropertyMapping(WebApiModel.Accepts.value.iri())
      .withAllowMultiple(true)
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/WebAPIObject/produces")
      .withName("produces")
      .withNodePropertyMapping(WebApiModel.ContentType.value.iri())
      .withAllowMultiple(true)
      .withLiteralRange(xsdString.iri())
  )
  override def pathItemObjectId: String = Oas20PathItemObject.id
}

object Oas30WebApiNode extends WebApiNode {

  override val location: String         = OAS30Dialect.DialectLocation
  override def pathItemObjectId: String = Oas30PathItemObject.id

  override def versionProperties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/WebAPIObject/servers")
      .withName("servers")
      .withObjectRange(
        Seq(
          Oas30ServerObject.id
        ))
      .withAllowMultiple(true)
  )

}
