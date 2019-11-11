package org.mulesoft.als.suggestions.plugins.aml.categories

import amf.plugins.domain.shapes.metamodel.{
  AnyShapeModel,
  CreativeWorkModel,
  ExampleModel,
  XMLSerializerModel
}
import amf.plugins.domain.webapi.metamodel.{
  EndPointModel,
  LicenseModel,
  OperationModel,
  OrganizationModel,
  ParameterModel,
  PayloadModel,
  ResponseModel,
  TagModel,
  WebApiModel
}
import amf.plugins.domain.webapi.metamodel.security.{
  ApiKeySettingsModel,
  OAuth2SettingsModel,
  SecuritySchemeModel
}
import org.mulesoft.amfmanager.dialect.webapi.raml.raml10.Raml10TypesDialect

case class CategoryIndex(classTerm: String, property: String)

case class CategoryField(classTerm: Option[String],
                         property: String,
                         category: String)

object CategoryRegistry {
  private val setCategoriesRoot: Set[CategoryField] = Set(
    (Some(OperationModel.`type`.head.iri()), "protocols"),
    (Some(WebApiModel.`type`.head.iri()), "swagger"),
    (Some(WebApiModel.`type`.head.iri()), "securityDefinitions"),
    (Some(WebApiModel.`type`.head.iri()), "paths"),
    (Some(WebApiModel.`type`.head.iri()), "basePath"),
    (Some(WebApiModel.`type`.head.iri()), "schemes"),
    (Some(WebApiModel.`type`.head.iri()), "consumes"),
    (Some(WebApiModel.`type`.head.iri()), "produces"),
    (Some(WebApiModel.`type`.head.iri()), "host"),
    (Some(WebApiModel.`type`.head.iri()), "version"),
    (Some(WebApiModel.`type`.head.iri()), "baseUri"),
    (Some(WebApiModel.`type`.head.iri()), "mediaType"),
    (Some(WebApiModel.`type`.head.iri()), "protocols"),
    (Some(WebApiModel.`type`.head.iri()), "title")
  ).map(t => CategoryField(t._1, t._2, "root"))

  private val setCategoriesDocs: Set[CategoryField] = Set(
    (None, "externalDocs"),
    (None, "title"),
    (None, "description"),
    (None, "example"),
    (None, "examples"),
    (None, "displayName"),
    (None, "name"),
    (None, "url"),
    (Some(WebApiModel.`type`.head.iri()), "externalDocs"),
    (Some(WebApiModel.`type`.head.iri()), "info"),
    (Some(OrganizationModel.`type`.head.iri()), "email"),
    (Some(CreativeWorkModel.`type`.head.iri()), "title"),
    (Some(CreativeWorkModel.`type`.head.iri()), "content"),
    (Some(WebApiModel.`type`.head.iri()), "documentation")
  ).map(t => {
    CategoryField(t._1, t._2, "docs")
  })

  private val setCategoriesParameters: Set[CategoryField] = Set(
    (None, "headers"),
    (None, "queryParameters"),
    (None, "pattern"),
    (None, "format"),
    (None, "enum"),
    (None, "default"),
    (None, "minLength"),
    (None, "maxLength"),
    (None, "required"),
    (Some(WebApiModel.`type`.head.iri()), "baseUriParameters"),
    (Some(EndPointModel.`type`.head.iri()), "uriParameters"),
    (Some(WebApiModel.`type`.head.iri()), "parameters"),
    (Some(PayloadModel.`type`.head.iri()), "name"),
    (Some(PayloadModel.`type`.head.iri()), "in"),
    (Some(ParameterModel.`type`.head.iri()), "in")
  ).map(t => CategoryField(t._1, t._2, "parameters"))

  private val setCategoriesTypesAndTraits: Set[CategoryField] = Set(
    (None, "annotationTypes"),
    (None, "traits"),
    (None, "resourceTypes"),
    (None, "types"),
    (None, "type"),
    (Some(OperationModel.`type`.head.iri()), "is"),
    (Some(EndPointModel.`type`.head.iri()), "is"),
  ).map(t => CategoryField(t._1, t._2, "types and traits"))

  private val setCategoriesMethods: Set[CategoryField] = Set(
    (Some(EndPointModel.`type`.head.iri()), "types"),
    (Some(EndPointModel.`type`.head.iri()), "get"),
    (Some(EndPointModel.`type`.head.iri()), "put"),
    (Some(EndPointModel.`type`.head.iri()), "post"),
    (Some(EndPointModel.`type`.head.iri()), "delete"),
    (Some(EndPointModel.`type`.head.iri()), "options"),
    (Some(EndPointModel.`type`.head.iri()), "head"),
    (Some(EndPointModel.`type`.head.iri()), "patch"),
    (Some(EndPointModel.`type`.head.iri()), "connect")
  ).map(t => CategoryField(t._1, t._2, "methods"))

  private val setCategoriesResponses: Set[CategoryField] = Set(
    (None, "responses"),
    (Some(ResponseModel.`type`.head.iri()), "body")
  ).map(t => CategoryField(t._1, t._2, "responses"))

  private val setCategoriesSchemas: Set[CategoryField] = Set(
    (None, "allOf"),
    (None, "items"),
    (None, "properties"),
    (None, "readOnly"),
    (None, "xml"),
    (Some(WebApiModel.`type`.head.iri()), "definitions"),
    (Some(WebApiModel.`type`.head.iri()), "schemas"),
    (Some(ResponseModel.`type`.head.iri()), "schema"),
    (Some(ParameterModel.`type`.head.iri()), "schema"),
    (Some(PayloadModel.`type`.head.iri()), "schema"),
    (Some(XMLSerializerModel.`type`.head.iri()), "namespace"),
    (Some(XMLSerializerModel.`type`.head.iri()), "name"),
    (Some(XMLSerializerModel.`type`.head.iri()), "prefix"),
    (Some(XMLSerializerModel.`type`.head.iri()), "wrapped"),
    (Some(XMLSerializerModel.`type`.head.iri()), "attribute")
  ).map(t => CategoryField(t._1, t._2, "schemas"))

  private val setCategoriesSecurity: Set[CategoryField] = Set(
    (None, "securitySchemes"),
    (None, "describedBy"),
    (Some(WebApiModel.`type`.head.iri()), "security"),
    (Some(OAuth2SettingsModel.`type`.head.iri()), "flow"),
    (Some(OAuth2SettingsModel.`type`.head.iri()), "tokenUrl"),
    (Some(OAuth2SettingsModel.`type`.head.iri()), "scopes"),
    (Some(OAuth2SettingsModel.`type`.head.iri()), "authorizationUrl"),
    (Some(SecuritySchemeModel.`type`.head.iri()), "type"),
    (Some(ApiKeySettingsModel.`type`.head.iri()), "in"),
    (Some(ApiKeySettingsModel.`type`.head.iri()), "name"),
    (Some(OperationModel.`type`.head.iri()), "securedBy"),
    (Some(EndPointModel.`type`.head.iri()), "securedBy"),
    (Some(WebApiModel.`type`.head.iri()), "securedBy")
  ).map(t => CategoryField(t._1, t._2, "security"))

  private val setCategoriesBody: Set[CategoryField] = Set(
    (Some(OperationModel.`type`.head.iri()), "body")
  ).map(t => CategoryField(t._1, t._2, "body"))

  private val allCategories = setCategoriesBody ++ setCategoriesRoot ++ setCategoriesDocs ++ setCategoriesParameters ++
    setCategoriesSecurity ++ setCategoriesTypesAndTraits ++ setCategoriesMethods ++ setCategoriesResponses ++ setCategoriesSchemas

  def apply(classTerm: String, property: String): String = {
    val (specific, general) = allCategories
      .filter(p => p.property == property && (p.classTerm.contains(classTerm) || p.classTerm.isEmpty)).partition(_.classTerm.isDefined)

    specific.headOption.orElse(general.headOption)
      .map(_.category)
      .getOrElse("unknown")
  }
}
