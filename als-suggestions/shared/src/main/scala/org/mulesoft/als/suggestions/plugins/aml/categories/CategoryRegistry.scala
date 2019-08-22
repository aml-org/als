package org.mulesoft.als.suggestions.plugins.aml.categories

import amf.plugins.document.vocabularies.metamodel.domain.NodeMappingModel
import amf.plugins.document.webapi.vocabulary.VocabularyMappings
import amf.plugins.domain.shapes.metamodel.{CreativeWorkModel, ExampleModel, XMLSerializerModel}
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
import amf.plugins.domain.webapi.metamodel.security.{ApiKeySettingsModel, OAuth2SettingsModel, SecuritySchemeModel}

case class CategoryIndex(classTerm: String, property: String)

case class CategoryField(classTerm: String, property: String, category: String)

object CategoryRegistry {
  private val setCategoriesRoot: Set[CategoryField] = Set(
    (OperationModel.`type`.head.iri(), "protocols"),
    (WebApiModel.`type`.head.iri(), "swagger"),
    (WebApiModel.`type`.head.iri(), "securityDefinitions"),
    (WebApiModel.`type`.head.iri(), "paths"),
    (WebApiModel.`type`.head.iri(), "basePath"),
    (WebApiModel.`type`.head.iri(), "schemes"),
    (WebApiModel.`type`.head.iri(), "consumes"),
    (WebApiModel.`type`.head.iri(), "produces"),
    (WebApiModel.`type`.head.iri(), "host"),
    (WebApiModel.`type`.head.iri(), "version"),
    (WebApiModel.`type`.head.iri(), "baseUri"),
    (WebApiModel.`type`.head.iri(), "mediaType"),
    (WebApiModel.`type`.head.iri(), "protocols"),
    (WebApiModel.`type`.head.iri(), "title")
  ).map(t => CategoryField(t._1, t._2, "root"))

  private val setCategoriesDocs: Set[CategoryField] = Set(
    (WebApiModel.`type`.head.iri(), "info"),
    (TagModel.`type`.head.iri(), "description"),
    (TagModel.`type`.head.iri(), "name"),
    (WebApiModel.`type`.head.iri(), "externalDocs"),
    (OAuth2SettingsModel.`type`.head.iri(), "description"),
    (ResponseModel.`type`.head.iri(), "examples"),
    (CreativeWorkModel.`type`.head.iri(), "description"),
    (CreativeWorkModel.`type`.head.iri(), "url"),
    (PayloadModel.`type`.head.iri(), "description"),
    (OrganizationModel.`type`.head.iri(), "description"),
    (OrganizationModel.`type`.head.iri(), "email"),
    (OrganizationModel.`type`.head.iri(), "url"),
    (OrganizationModel.`type`.head.iri(), "name"),
    (LicenseModel.`type`.head.iri(), "name"),
    (LicenseModel.`type`.head.iri(), "url"),
    (ParameterModel.`type`.head.iri(), "name"),
    (ParameterModel.`type`.head.iri(), "description"),
    (SecuritySchemeModel.`type`.head.iri(), "description"),
    (ApiKeySettingsModel.`type`.head.iri(), "description"),
    (ResponseModel.`type`.head.iri(), "description"),
    (CreativeWorkModel.`type`.head.iri(), "title"),
    (CreativeWorkModel.`type`.head.iri(), "content"),
    (ExampleModel.`type`.head.iri(), "displayName"),
    (ExampleModel.`type`.head.iri(), "description"),
    (OperationModel.`type`.head.iri(), "displayName"),
    (OperationModel.`type`.head.iri(), "description"),
    (EndPointModel.`type`.head.iri(), "displayName"),
    (EndPointModel.`type`.head.iri(), "description"),
    (NodeMappingModel.`type`.head.iri(), "example"),
    (NodeMappingModel.`type`.head.iri(), "examples"),
    (NodeMappingModel.`type`.head.iri(), "displayName"),
    (NodeMappingModel.`type`.head.iri(), "description"),
    (WebApiModel.`type`.head.iri(), "documentation"),
    (WebApiModel.`type`.head.iri(), "description")
  ).map(t => CategoryField(t._1, t._2, "docs"))

  private val setCategoriesParameters: Set[CategoryField] = Set(
    (WebApiModel.`type`.head.iri(), "baseUriParameters"),
    (ResponseModel.`type`.head.iri(), "headers"),
    (NodeMappingModel.`type`.head.iri(), "headers"),
    (NodeMappingModel.`type`.head.iri(), "queryParameters"),
    (OperationModel.`type`.head.iri(), "queryParameters"),
    (OperationModel.`type`.head.iri(), "headers"),
    (EndPointModel.`type`.head.iri(), "uriParameters"),
    (NodeMappingModel.`type`.head.iri(), "enum"),
    (NodeMappingModel.`type`.head.iri(), "default"),
    (NodeMappingModel.`type`.head.iri(), "minLength"),
    (NodeMappingModel.`type`.head.iri(), "maxLength"),
    (WebApiModel.`type`.head.iri(), "parameters"),
    (ParameterModel.`type`.head.iri(), "required"),
    (ParameterModel.`type`.head.iri(), "in"),
    (PayloadModel.`type`.head.iri(), "required"),
    (PayloadModel.`type`.head.iri(), "name"),
    (PayloadModel.`type`.head.iri(), "in")
  ).map(t => CategoryField(t._1, t._2, "parameters"))

  private val setCategoriesTypesAndTraits: Set[CategoryField] = Set(
    (VocabularyMappings.library, "annotationTypes"),
    (VocabularyMappings.library, "traits"),
    (VocabularyMappings.library, "resourceTypes"),
    (VocabularyMappings.library, "types"),
    (OperationModel.`type`.head.iri(), "is"),
    (EndPointModel.`type`.head.iri(), "is"),
    (EndPointModel.`type`.head.iri(), "type"),
    (NodeMappingModel.`type`.head.iri(), "type"),
    (WebApiModel.`type`.head.iri(), "types"),
    (WebApiModel.`type`.head.iri(), "traits"),
    (WebApiModel.`type`.head.iri(), "resourceTypes"),
    (WebApiModel.`type`.head.iri(), "annotationTypes"),
    (NodeMappingModel.`type`.head.iri(), "type"),
    (ApiKeySettingsModel.`type`.head.iri(), "types")
  ).map(t => CategoryField(t._1, t._2, "types and traits"))

  private val setCategoriesMethods: Set[CategoryField] = Set(
    (EndPointModel.`type`.head.iri(), "types"),
    (EndPointModel.`type`.head.iri(), "get"),
    (EndPointModel.`type`.head.iri(), "put"),
    (EndPointModel.`type`.head.iri(), "post"),
    (EndPointModel.`type`.head.iri(), "delete"),
    (EndPointModel.`type`.head.iri(), "options"),
    (EndPointModel.`type`.head.iri(), "head"),
    (EndPointModel.`type`.head.iri(), "patch"),
    (EndPointModel.`type`.head.iri(), "connect")
  ).map(t => CategoryField(t._1, t._2, "methods"))

  private val setCategoriesResponses: Set[CategoryField] = Set(
    (NodeMappingModel.`type`.head.iri(), "responses"),
    (OperationModel.`type`.head.iri(), "responses"),
    (ResponseModel.`type`.head.iri(), "body"),
    (WebApiModel.`type`.head.iri(), "responses")
  ).map(t => CategoryField(t._1, t._2, "responses"))

  private val setCategoriesSchemas: Set[CategoryField] = Set(
    (WebApiModel.`type`.head.iri(), "definitions"),
    (ResponseModel.`type`.head.iri(), "schema"),
    (ParameterModel.`type`.head.iri(), "schema"),
    (PayloadModel.`type`.head.iri(), "schema"),
    (XMLSerializerModel.`type`.head.iri(), "namespace"),
    (XMLSerializerModel.`type`.head.iri(), "name"),
    (XMLSerializerModel.`type`.head.iri(), "prefix"),
    (XMLSerializerModel.`type`.head.iri(), "wrapped"),
    (XMLSerializerModel.`type`.head.iri(), "attribute")
  ).map(t => CategoryField(t._1, t._2, "schemas"))

  private val setCategoriesSecurity: Set[CategoryField] = Set(
    (WebApiModel.`type`.head.iri(), "security"),
    (OAuth2SettingsModel.`type`.head.iri(), "flow"),
    (OAuth2SettingsModel.`type`.head.iri(), "tokenUrl"),
    (OAuth2SettingsModel.`type`.head.iri(), "scopes"),
    (OAuth2SettingsModel.`type`.head.iri(), "authorizationUrl"),
    (SecuritySchemeModel.`type`.head.iri(), "type"),
    (ApiKeySettingsModel.`type`.head.iri(), "in"),
    (ApiKeySettingsModel.`type`.head.iri(), "name"),
    (NodeMappingModel.`type`.head.iri(), "describedBy"),
    (VocabularyMappings.library, "securitySchemes"),
    (OperationModel.`type`.head.iri(), "securedBy"),
    (EndPointModel.`type`.head.iri(), "securedBy"),
    (WebApiModel.`type`.head.iri(), "securitySchemes"),
    (WebApiModel.`type`.head.iri(), "securedBy")
  ).map(t => CategoryField(t._1, t._2, "security"))

  private val setCategoriesBody: Set[CategoryField] = Set(
    (OperationModel.`type`.head.iri(), "body")
  ).map(t => CategoryField(t._1, t._2, "body"))

  private val allCategories = setCategoriesBody ++ setCategoriesRoot ++ setCategoriesDocs ++ setCategoriesParameters ++
    setCategoriesSecurity ++ setCategoriesTypesAndTraits ++ setCategoriesMethods ++ setCategoriesResponses ++ setCategoriesSchemas

  def apply(classTerm: String, property: String): String = {
    allCategories
      .find(p => p.classTerm == classTerm && p.property == property)
      .map(_.category)
      .getOrElse("unknown")
  }
}
