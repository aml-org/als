package org.mulesoft.amfintegration.dialect.dialects.oas.nodes

import amf.aml.client.scala.model.domain.PropertyMapping
import amf.apicontract.internal.metamodel.domain.{CallbackModel, OperationModel, RequestModel, ResponseModel}
import amf.core.client.scala.vocabulary.Namespace.XsdTypes.{xsdBoolean, xsdString}
import org.mulesoft.amfintegration.dialect.dialects.oas.OAS20Dialect.DialectLocation
import org.mulesoft.amfintegration.dialect.dialects.oas.{OAS20Dialect, OAS30Dialect}

trait AMLOperationObject extends DialectNode {

  override def name: String = "OperationObject"

  override def nodeTypeMapping: String = OperationModel.`type`.head.iri()

  def versionProperties: Seq[PropertyMapping]

  override def properties: Seq[PropertyMapping] = versionProperties ++ Seq(
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/OperationObject/tags")
      .withName("tags")
      .withAllowMultiple(true)
      .withNodePropertyMapping(OperationModel.Tags.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/OperationObject/summary")
      .withName("summary")
      .withNodePropertyMapping(OperationModel.Summary.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/OperationObject/description")
      .withName("description")
      .withNodePropertyMapping(OperationModel.Description.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/OperationObject/externalDocs")
      .withName("externalDocs")
      .withNodePropertyMapping(OperationModel.Documentation.value.iri())
      .withObjectRange(Seq(
        AMLExternalDocumentationObject.id
      )),
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/OperationObject/operationId")
      .withName("operationId")
      .withNodePropertyMapping(OperationModel.Name.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/OperationObject/deprecated")
      .withName("deprecated")
      .withNodePropertyMapping(OperationModel.Deprecated.value.iri())
      .withLiteralRange(xsdBoolean.iri()),
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/OperationObject/security")
      .withName("security")
      .withNodePropertyMapping(OperationModel.Security.value.iri())
      .withAllowMultiple(true)
      .withObjectRange(
        Seq(
          Oas20SecuritySchemeObject.id,
          Oas20ApiKeySecuritySchemeObject.id,
          Oauth2SecuritySchemeObject.id
        ))
  )
}

object Oas20AMLOperationObject extends AMLOperationObject {

  override val location: String = OAS20Dialect.DialectLocation
  override def versionProperties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/OperationObject/consumes")
      .withName("consumes")
      .withNodePropertyMapping(OperationModel.Accepts.value.iri())
      .withAllowMultiple(true)
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/OperationObject/produces")
      .withName("produces")
      .withNodePropertyMapping(OperationModel.ContentType.value.iri())
      .withAllowMultiple(true)
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/OperationObject/schemes")
      .withName("schemes")
      .withNodePropertyMapping(OperationModel.Schemes.value.iri())
      .withEnum(Seq("ws", "wss", "http", "https"))
      .withAllowMultiple(true)
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/OperationObject/Request/parameters")
      .withName("parameters")
      .withNodePropertyMapping(RequestModel.UriParameters.value.iri())
      .withAllowMultiple(true)
      .withObjectRange(Seq(
        Oas20ParamObject.id
      )),
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/OperationObject/responses")
      .withName("responses")
      .withNodePropertyMapping(OperationModel.Responses.value.iri())
      .withMinCount(1)
      .withMapTermKeyProperty(ResponseModel.StatusCode.value.iri())
      .withObjectRange(Seq(
        Oas20ResponseObject.id
      ))
  )
}

object Oas30OperationObject extends AMLOperationObject {

  override val location: String = OAS30Dialect.DialectLocation

  override def versionProperties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/OperationObject/requestBody")
      .withName("requestBody")
      .withNodePropertyMapping(OperationModel.Request.value.iri())
      .withObjectRange(Seq(AMLRequestBodyObject.id)),
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/OperationObject/responses")
      .withName("responses")
      .withNodePropertyMapping(OperationModel.Responses.value.iri())
      .withMinCount(1)
      .withMapTermKeyProperty(ResponseModel.StatusCode.value.iri())
      .withObjectRange(Seq(
        Oas30ResponseObject.id
      )),
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/OperationObject/Request/parameters")
      .withName("parameters")
      .withNodePropertyMapping(RequestModel.UriParameters.value.iri())
      .withAllowMultiple(true)
      .withObjectRange(Seq(
        Oas30ParamObject.id
      )),
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/OperationObject/Request/callbacks")
      .withName("callbacks")
      .withNodePropertyMapping(OperationModel.Callbacks.value.iri())
      .withMapTermKeyProperty(CallbackModel.Name.value.iri())
      .withObjectRange(Seq(
        AMLCallbackObject.id
      )),
    PropertyMapping()
      .withId(DialectLocation + "#/declarations/OperationObject/servers")
      .withName("servers")
      .withNodePropertyMapping(OperationModel.Servers.value.iri())
      .withAllowMultiple(true)
      .withObjectRange(Seq(
        Oas30ServerObject.id
      ))
  )
}
