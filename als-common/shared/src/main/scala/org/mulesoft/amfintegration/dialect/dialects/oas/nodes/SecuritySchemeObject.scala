package org.mulesoft.amfintegration.dialect.dialects.oas.nodes

import amf.core.vocabulary.Namespace.XsdTypes.{xsdString, xsdUri}
import amf.plugins.document.vocabularies.model.domain.PropertyMapping
import amf.plugins.domain.webapi.metamodel.security._
import org.mulesoft.amfintegration.dialect.dialects.oas.{OAS20Dialect, OAS30Dialect}

object Oas20SecuritySettingsNode extends DialectNode {
  override def name: String            = "SecuritySettingsNode"
  override def nodeTypeMapping: String = SettingsModel.`type`.head.iri()
  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(OAS20Dialect.DialectLocation + "#/declarations/securityScheme/type")
      .withName("type")
      .withMinCount(1)
      .withNodePropertyMapping(SecuritySchemeModel.Type.value.iri())
      .withEnum(
        Seq(
          "basic",
          "apiKey",
          "oauth2"
        ))
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(OAS20Dialect.DialectLocation + "#/declarations/securityScheme/description")
      .withName("description")
      .withNodePropertyMapping(SecuritySchemeModel.Description.value.iri())
      .withLiteralRange(xsdString.iri())
  )
}

object Oas20SecuritySchemeObject extends DialectNode {
  override def name: String            = "SecuritySchemeNode"
  override def nodeTypeMapping: String = SecuritySchemeModel.`type`.head.iri()
  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(OAS20Dialect.DialectLocation + "#/declarations/securityScheme/type")
      .withName("type")
      .withMinCount(1)
      .withEnum(
        Seq(
          "basic",
          "apiKey",
          "oauth2"
        ))
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(OAS20Dialect.DialectLocation + "#/declarations/securityScheme/description")
      .withName("description")
      .withNodePropertyMapping(SecuritySchemeModel.Description.value.iri())
      .withLiteralRange(xsdString.iri())
  )
}

trait ApiKeySecuritySchemeObject extends DialectNode {
  override def name: String            = "ApiKeySecurityScheme"
  override def nodeTypeMapping: String = ApiKeySettingsModel.`type`.head.iri()
  def specProperties: Seq[PropertyMapping]
  override def properties: Seq[PropertyMapping] = Oas20SecuritySchemeObject.properties ++ specProperties ++ Seq(
    PropertyMapping()
      .withId(OAS20Dialect.DialectLocation + "#/declarations/ApiKeySecurityScheme/Settings/Name")
      .withName("name")
      .withMinCount(1)
      .withNodePropertyMapping(ApiKeySettingsModel.Name.value.iri())
      .withLiteralRange(xsdString.iri())
  )
}

object Oas20ApiKeySecuritySchemeObject extends ApiKeySecuritySchemeObject {
  override def specProperties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(OAS20Dialect.DialectLocation + "#/declarations/ApiKeySecurityScheme/Settings/In")
      .withName("in")
      .withMinCount(1)
      .withEnum(
        Seq(
          "query",
          "header"
        ))
      .withNodePropertyMapping(ApiKeySettingsModel.In.value.iri())
      .withLiteralRange(xsdString.iri())
  )
}

object Oas30ApiKeySecuritySchemeObject extends ApiKeySecuritySchemeObject {
  override def specProperties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(OAS30Dialect.DialectLocation + "#/declarations/ApiKeySecurityScheme/Settings/In")
      .withName("in")
      .withMinCount(1)
      .withEnum(
        Seq(
          "query",
          "header",
          "cookie"
        ))
      .withNodePropertyMapping(ApiKeySettingsModel.In.value.iri())
      .withLiteralRange(xsdString.iri())
  )
}

trait Oauth2Properties {
  def flowProperty: PropertyMapping
  def oauth2Properties: Seq[PropertyMapping] = {
    Oas20SecuritySchemeObject.properties ++ Seq(
      flowProperty,
      PropertyMapping()
        .withId(OAS20Dialect.DialectLocation + "#/declarations/Oauth2SecurityScheme/authorizationUrl")
        .withName("authorizationUrl")
        .withNodePropertyMapping(OAuth2FlowModel.AuthorizationUri.value.iri())
        .withLiteralRange(xsdUri.iri()),
      PropertyMapping()
        .withId(OAS20Dialect.DialectLocation + "#/declarations/Oauth2SecurityScheme/tokenUrl")
        .withName("tokenUrl")
        .withNodePropertyMapping(OAuth2FlowModel.AccessTokenUri.value.iri())
        .withLiteralRange(xsdUri.iri()),
      PropertyMapping()
        .withId(OAS20Dialect.DialectLocation + "#/declarations/Oauth2SecurityScheme/scopes")
        .withName("scopes")
        .withNodePropertyMapping(OAuth2FlowModel.Scopes.value.iri())
        .withMapTermKeyProperty(ScopeModel.Name.value.iri())
        .withMapTermValueProperty(ScopeModel.Description.value.iri())
        .withObjectRange(Seq(Oas20ScopeObject.id))
    )
  }
}

object Oauth2SecuritySchemeObject extends DialectNode with Oauth2Properties {
  override def name: String                     = "Oauth2SecurityScheme"
  override def nodeTypeMapping: String          = OAuth2SettingsModel.`type`.head.iri()
  override def properties: Seq[PropertyMapping] = oauth2Properties

  override def flowProperty: PropertyMapping =
    PropertyMapping()
      .withId(OAS20Dialect.DialectLocation + "#/declarations/Oauth2SecurityScheme/flow")
      .withName("flow")
      .withMinCount(1)
      .withNodePropertyMapping(OAuth2FlowModel.Flow.value.iri())
      .withEnum(
        Seq(
          "implicit",
          "password",
          "application",
          "accessCode"
        ))
      .withLiteralRange(xsdString.iri())
}

object Oas2Oauth2FlowSchemeObject extends DialectNode with Oauth2Properties {
  override def name: String                     = "Oas2Oauth2FlowSchemeObject"
  override def nodeTypeMapping: String          = OAuth2FlowModel.`type`.head.iri()
  override def properties: Seq[PropertyMapping] = oauth2Properties

  override def flowProperty: PropertyMapping =
    PropertyMapping()
      .withId(OAS20Dialect.DialectLocation + "#/declarations/Oas2Oauth2FlowSchemeObject/flow")
      .withName("flow")
      .withMinCount(1)
      .withNodePropertyMapping(OAuth2FlowModel.Flow.value.iri())
      .withEnum(
        Seq(
          "implicit",
          "password",
          "application",
          "accessCode"
        ))
      .withLiteralRange(xsdString.iri())
}

object Oauth2FlowObject extends DialectNode with Oauth2Properties {
  override def name: String                     = "Oauth2FlowScheme"
  override def nodeTypeMapping: String          = OAuth2FlowModel.`type`.head.iri()
  override def properties: Seq[PropertyMapping] = oauth2Properties

  override def flowProperty: PropertyMapping =
    PropertyMapping()
      .withId(OAS20Dialect.DialectLocation + "#/declarations/Oauth2SecurityScheme/flow")
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

object Oas30SecuritySchemeObject extends DialectNode {
  override def name: String            = "SecuritySchemeNode"
  override def nodeTypeMapping: String = SecuritySchemeModel.`type`.head.iri()
  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(OAS30Dialect.DialectLocation + "#/declarations/securityScheme/type")
      .withName("type")
      .withMinCount(1)
      .withNodePropertyMapping(SecuritySchemeModel.Type.value.iri())
      .withEnum(
        Seq(
          "apiKey",
          "http",
          "oauth2",
          "openIdConnect"
        ))
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(OAS30Dialect.DialectLocation + "#/declarations/securityScheme/description")
      .withName("description")
      .withNodePropertyMapping(SecuritySchemeModel.Description.value.iri())
      .withLiteralRange(xsdString.iri())
  )
}

object Oas30SecuritySettingsObject extends DialectNode {
  override def name: String            = "SecuritySettingsNode"
  override def nodeTypeMapping: String = SettingsModel.`type`.head.iri()
  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(OAS30Dialect.DialectLocation + "#/declarations/securitySettings/type")
      .withName("type")
      .withMinCount(1)
      .withNodePropertyMapping(SecuritySchemeModel.Type.value.iri())
      .withEnum(
        Seq(
          "apiKey",
          "http",
          "oauth2",
          "openIdConnect"
        ))
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(OAS30Dialect.DialectLocation + "#/declarations/securitySettings/description")
      .withName("description")
      .withNodePropertyMapping(SecuritySchemeModel.Description.value.iri())
      .withLiteralRange(xsdString.iri())
  )
}

object Oas30ApiKeySecurityObject extends DialectNode {
  override def name: String            = "ApiKeySecurityObject"
  override def nodeTypeMapping: String = ApiKeySettingsModel.`type`.head.iri()
  override def properties: Seq[PropertyMapping] = Oas30SecuritySchemeObject.properties ++ Seq(
    PropertyMapping()
      .withId(OAS30Dialect.DialectLocation + "#/declarations/securityScheme/name")
      .withName("name")
      .withMinCount(1)
      .withNodePropertyMapping(ApiKeySettingsModel.Name.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(OAS30Dialect.DialectLocation + "#/declarations/securityScheme/in")
      .withName("in")
      .withMinCount(1)
      .withNodePropertyMapping(ApiKeySettingsModel.In.value.iri())
      .withLiteralRange(xsdString.iri())
      .withEnum(
        Seq(
          "query",
          "header",
          "cookie"
        ))
  )
}

object Oas30HttpSecurityObject extends DialectNode {
  override def name: String            = "HttpSecurityObject"
  override def nodeTypeMapping: String = HttpSettingsModel.`type`.head.iri()
  override def properties: Seq[PropertyMapping] = Oas30SecuritySchemeObject.properties ++ Seq(
    PropertyMapping()
      .withId(OAS30Dialect.DialectLocation + "#/declarations/securityScheme/scheme")
      .withName("scheme")
      .withMinCount(1)
      .withNodePropertyMapping(HttpSettingsModel.Scheme.value.iri())
      .withLiteralRange(xsdString.iri()),
    PropertyMapping()
      .withId(OAS30Dialect.DialectLocation + "#/declarations/securityScheme/bearerFormat")
      .withName("bearerFormat")
      .withNodePropertyMapping(HttpSettingsModel.BearerFormat.value.iri())
      .withLiteralRange(xsdString.iri())
  )
}

object Oas30OAuth20SecurityObject extends DialectNode {
  override def name: String            = "OAuth20SecurityObject"
  override def nodeTypeMapping: String = OAuth2SettingsModel.`type`.head.iri()
  override def properties: Seq[PropertyMapping] = Oas30SecuritySchemeObject.properties ++ Seq(
    PropertyMapping()
      .withId(OAS30Dialect.DialectLocation + "#/declarations/securityScheme/flows")
      .withName("flows")
      .withMinCount(1)
      .withNodePropertyMapping(OAuth2SettingsModel.Flows.value.iri())
      .withMapTermKeyProperty(OAuth2FlowModel.Flow.value.iri())
      .withObjectRange(Seq(Oas30FlowObject.id))
  )
}

object Oas30OpenIdConnectUrl extends DialectNode {
  override def name: String            = "OpenIdConnectUrlObject"
  override def nodeTypeMapping: String = OpenIdConnectSettingsModel.`type`.head.iri()
  override def properties: Seq[PropertyMapping] = Oas30SecuritySchemeObject.properties ++ Seq(
    PropertyMapping()
      .withId(OAS30Dialect.DialectLocation + "#/declarations/securityScheme/openIdConnectUrl")
      .withName("openIdConnectUrl")
      .withMinCount(1)
      .withNodePropertyMapping(OpenIdConnectSettingsModel.Url.value.iri())
      .withLiteralRange(xsdString.iri())
  )
}

object Oas30FlowObject extends DialectNode {
  override def name: String            = "Oas30FlowObject"
  override def nodeTypeMapping: String = OAuth2FlowModel.`type`.head.iri()
  override def properties: Seq[PropertyMapping] = Seq(
    PropertyMapping()
      .withId(OAS30Dialect.DialectLocation + "#/declarations/Oas20FlowObject/authorizationUrl")
      .withName("authorizationUrl")
      .withNodePropertyMapping(OAuth2FlowModel.AuthorizationUri.value.iri())
      .withLiteralRange(xsdUri.iri()),
    PropertyMapping()
      .withId(OAS30Dialect.DialectLocation + "#/declarations/Oas20FlowObject/tokenUrl")
      .withName("tokenUrl")
      .withNodePropertyMapping(OAuth2FlowModel.AccessTokenUri.value.iri())
      .withLiteralRange(xsdUri.iri()),
    PropertyMapping()
      .withId(OAS30Dialect.DialectLocation + "#/declarations/Oas20FlowObject/refreshUrl")
      .withName("refreshUrl")
      .withNodePropertyMapping(OAuth2FlowModel.RefreshUri.value.iri())
      .withLiteralRange(xsdUri.iri()),
    PropertyMapping()
      .withId(OAS30Dialect.DialectLocation + "#/declarations/Oas20FlowObject/scopes")
      .withName("scopes")
      .withNodePropertyMapping(OAuth2FlowModel.Scopes.value.iri())
      .withMapTermKeyProperty(ScopeModel.Name.value.iri())
      .withMapTermValueProperty(ScopeModel.Description.value.iri())
      .withObjectRange(Seq(Oas20ScopeObject.id)),
    PropertyMapping()
      .withId(OAS30Dialect.DialectLocation + "#/declarations/Oas20FlowObject/flow")
      .withName("flow")
      .withNodePropertyMapping(OAuth2FlowModel.Flow.value.iri())
      .withLiteralRange(xsdString.iri())
      .withEnum(Seq())
  )
}
