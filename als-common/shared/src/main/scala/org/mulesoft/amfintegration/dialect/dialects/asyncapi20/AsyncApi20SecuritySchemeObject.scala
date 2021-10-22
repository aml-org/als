package org.mulesoft.amfintegration.dialect.dialects.asyncapi20

import amf.aml.client.scala.model.domain.PropertyMapping
import amf.apicontract.internal.metamodel.domain.security.{ApiKeySettingsModel, HttpApiKeySettingsModel, HttpSettingsModel, OAuth2FlowModel, OAuth2SettingsModel, OpenIdConnectSettingsModel, ScopeModel, SecuritySchemeModel}
import amf.core.client.scala.vocabulary.Namespace.XsdTypes.{xsdString, xsdUri}
import org.mulesoft.amfintegration.dialect.dialects.oas.nodes.{DialectNode, Oauth2Properties}

object AsyncApi20SecuritySchemeObject extends DialectNode {
  override def name: String = "SecuritySchemeNode"

  override def nodeTypeMapping: String = SecuritySchemeModel.`type`.head.iri()

  val `type`: PropertyMapping = PropertyMapping()
    .withId(AsyncApi20Dialect.DialectLocation + "#/declarations/securityScheme/type")
    .withName("type")
    .withMinCount(1)
    .withNodePropertyMapping(SecuritySchemeModel.Type.value.iri())
    .withEnum(Seq(
      "userPassword",
      "apiKey",
      "X509",
      "symmetricEncryption",
      "asymmetricEncryption",
      "httpApiKey",
      "http",
      "oauth2",
      "openIdConnect"
    ))
    .withLiteralRange(xsdString.iri())
  override def properties: Seq[PropertyMapping] = Seq(
    `type`,
    PropertyMapping()
      .withId(AsyncApi20Dialect.DialectLocation + "#/declarations/securityScheme/description")
      .withName("description")
      .withNodePropertyMapping(SecuritySchemeModel.Description.value.iri())
      .withLiteralRange(xsdString.iri())
  )
}

object AsyncApi20SecuritySettingsObject extends DialectNode {
  override def name: String = "SecuritySettingsNode"

  override def nodeTypeMapping: String = SecuritySchemeModel.`type`.head.iri()

  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(AsyncApi20Dialect.DialectLocation + "#/declarations/securitySettings/type")
      .withName("type")
      .withMinCount(1)
      .withNodePropertyMapping(SecuritySchemeModel.Type.value.iri())
      .withEnum(Seq(
        "userPassword",
        "apiKey",
        "X509",
        "symmetricEncryption",
        "asymmetricEncryption",
        "httpApiKey",
        "http",
        "oauth2",
        "openIdConnect"
      ))
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(AsyncApi20Dialect.DialectLocation + "#/declarations/securityScheme/description")
      .withName("description")
      .withNodePropertyMapping(SecuritySchemeModel.Description.value.iri())
      .withLiteralRange(xsdString.iri())
  )
}


object AsyncAPI20ApiKeySecurityObject extends DialectNode {
  override def name: String            = "ApiKeySecurityObject"
  override def nodeTypeMapping: String = ApiKeySettingsModel.`type`.head.iri()
  override def properties: Seq[PropertyMapping] = AsyncApi20SecuritySchemeObject.properties ++ Seq(
    PropertyMapping()
      .withId(AsyncApi20Dialect.DialectLocation + "#/declarations/securityScheme/in")
      .withName("in")
      .withMinCount(1)
      .withNodePropertyMapping(ApiKeySettingsModel.In.value.iri())
      .withLiteralRange(xsdString.iri())
      .withEnum(
        Seq(
          "user",
          "password"
        ))
  )
}

object AsyncAPI20HttpApiKeySecurityObject extends DialectNode {
  override def name: String = "HttpApiKeySecurityObject"

  override def nodeTypeMapping: String = HttpApiKeySettingsModel.`type`.head.iri()

  override def properties: Seq[PropertyMapping] = AsyncApi20SecuritySchemeObject.properties ++
    Seq(
      PropertyMapping()
      .withId(AsyncApi20Dialect.DialectLocation + "#/declarations/securityScheme/in")
      .withName("in")
      .withMinCount(1)
      .withNodePropertyMapping(ApiKeySettingsModel.In.value.iri())
      .withLiteralRange(xsdString.iri())
      .withEnum(
        Seq(
          "query",
          "header",
          "cookie"
        )),
      PropertyMapping()
        .withId(AsyncApi20Dialect.DialectLocation + "#/declarations/securityScheme/name")
        .withName("name")
        .withMinCount(1)
        .withNodePropertyMapping(ApiKeySettingsModel.Name.value.iri())
        .withLiteralRange(xsdString.iri()),
    )
}

object AsyncAPI20HttpSecurityObject extends DialectNode {
  override def name: String            = "HttpSecurityObject"
  override def nodeTypeMapping: String = HttpSettingsModel.`type`.head.iri()
  override def properties: Seq[PropertyMapping] = AsyncApi20SecuritySchemeObject.properties ++ Seq(
    PropertyMapping()
      .withId(AsyncApi20Dialect.DialectLocation + "#/declarations/securityScheme/scheme")
      .withName("scheme")
      .withMinCount(1)
      .withNodePropertyMapping(HttpSettingsModel.Scheme.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(AsyncApi20Dialect.DialectLocation + "#/declarations/securityScheme/bearerFormat")
      .withName("bearerFormat")
      .withNodePropertyMapping(HttpSettingsModel.BearerFormat.value.iri())
      .withLiteralRange(xsdString.iri())
  )
}

object AsyncAPI20Auth20SecurityObject extends DialectNode {
  override def name: String            = "OAuth20SecurityObject"
  override def nodeTypeMapping: String = OAuth2SettingsModel.`type`.head.iri()
  override def properties: Seq[PropertyMapping] = AsyncApi20SecuritySchemeObject.properties ++ Seq(
    PropertyMapping()
      .withId(AsyncApi20Dialect.DialectLocation + "#/declarations/securityScheme/flows")
      .withName("flows")
      .withMinCount(1)
      .withNodePropertyMapping(OAuth2SettingsModel.Flows.value.iri())
      .withMapTermKeyProperty(OAuth2FlowModel.Flow.value.iri())
      .withObjectRange(Seq(AsyncAPI20FlowObject.id))
  )
}

object AsyncAPI20penIdConnectUrl extends DialectNode {
  override def name: String            = "OpenIdConnectUrlObject"
  override def nodeTypeMapping: String = OpenIdConnectSettingsModel.`type`.head.iri()
  override def properties: Seq[PropertyMapping] = AsyncApi20SecuritySchemeObject.properties ++ Seq(
    PropertyMapping()
      .withId(AsyncApi20Dialect.DialectLocation + "#/declarations/securityScheme/openIdConnectUrl")
      .withName("openIdConnectUrl")
      .withMinCount(1)
      .withNodePropertyMapping(OpenIdConnectSettingsModel.Url.value.iri())
      .withLiteralRange(xsdString.iri())
  )
}

object Oauth2FlowObject extends DialectNode with Oauth2Properties {
  override def name: String                     = "Oauth2FlowScheme"
  override def nodeTypeMapping: String          = OAuth2FlowModel.`type`.head.iri()
  override def properties: Seq[PropertyMapping] = oauth2Properties

  override def flowProperty: PropertyMapping =
    PropertyMapping()
      .withId(AsyncApi20Dialect.DialectLocation + "#/declarations/Oauth2SecurityScheme/flow")
      .withName("flow")
      .withMinCount(1)
      .withNodePropertyMapping(OAuth2FlowModel.Flow.value.iri())
      .withEnum(
        Seq(
          "implicit",
          "password",
          "clientCredentials",
          "authorizationCode"
        ))
      .withLiteralRange(xsdString.iri())
}

object AsyncAPI20FlowObject extends DialectNode {
  override def name: String            = "AsyncAPI2FlowObject"
  override def nodeTypeMapping: String = OAuth2FlowModel.`type`.head.iri()
  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(AsyncApi20Dialect.DialectLocation + "#/declarations/AsyncAPI2FlowObject/authorizationUrl")
      .withName("authorizationUrl")
      .withNodePropertyMapping(OAuth2FlowModel.AuthorizationUri.value.iri())
      .withMinCount(1)
      .withLiteralRange(xsdUri.iri()),
    PropertyMapping()
      .withId(AsyncApi20Dialect.DialectLocation + "#/declarations/AsyncAPI2FlowObject/tokenUrl")
      .withName("tokenUrl")
      .withNodePropertyMapping(OAuth2FlowModel.AccessTokenUri.value.iri())
      .withMinCount(1)
      .withLiteralRange(xsdUri.iri()),
    PropertyMapping()
      .withId(AsyncApi20Dialect.DialectLocation + "#/declarations/AsyncAPI2FlowObject/refreshUrl")
      .withName("refreshUrl")
      .withNodePropertyMapping(OAuth2FlowModel.RefreshUri.value.iri())
      .withLiteralRange(xsdUri.iri()),
    PropertyMapping()
      .withId(AsyncApi20Dialect.DialectLocation + "#/declarations/AsyncAPI2FlowObject/scopes")
      .withName("scopes")
      .withNodePropertyMapping(OAuth2FlowModel.Scopes.value.iri())
      .withMapTermKeyProperty(ScopeModel.Name.value.iri())
      .withMapTermValueProperty(ScopeModel.Description.value.iri())
      .withAllowMultiple(true)
      .withObjectRange(Seq(AsyncAPI20ScopeObject.id)),
  )
}
