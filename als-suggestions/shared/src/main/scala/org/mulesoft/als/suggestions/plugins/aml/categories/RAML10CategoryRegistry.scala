package org.mulesoft.als.suggestions.plugins.aml.categories

import amf.plugins.document.vocabularies.metamodel.domain.NodeMappingModel
import amf.plugins.document.webapi.vocabulary.VocabularyMappings
import amf.plugins.domain.shapes.metamodel.{CreativeWorkModel, ExampleModel}
import amf.plugins.domain.webapi.metamodel.{EndPointModel, OperationModel, ResponseModel, WebApiModel}

trait RAML10CategoryRegistry {
  private val setCategoriesBody: Set[CategoryField] = Set(
    (OperationModel.`type`.head.iri(), "body")
  ).map(t => CategoryField(t._1, t._2, "body"))

  private val setCategoriesRoot: Set[CategoryField] = Set(
    (OperationModel.`type`.head.iri(), "protocols"),
    (WebApiModel.`type`.head.iri(), "version"),
    (WebApiModel.`type`.head.iri(), "baseUri"),
    (WebApiModel.`type`.head.iri(), "mediaType"),
    (WebApiModel.`type`.head.iri(), "protocols"),
    (WebApiModel.`type`.head.iri(), "title")
  ).map(t => CategoryField(t._1, t._2, "root"))

  private val setCategoriesDocs: Set[CategoryField] = Set(
    (ResponseModel.`type`.head.iri(), "description"),
    (CreativeWorkModel.`type`.head.iri(), "title"),
    (CreativeWorkModel.`type`.head.iri(), "content"),
    (ExampleModel.`type`.head.iri(), "displayName"), // was not in universe
    (ExampleModel.`type`.head.iri(), "description"), // was not in universe
    (OperationModel.`type`.head.iri(), "displayName"),
    (OperationModel.`type`.head.iri(), "description"),
    (EndPointModel.`type`.head.iri(), "displayName"),
    (EndPointModel.`type`.head.iri(), "description"),
    (NodeMappingModel.`type`.head.iri(), "example"),
    (NodeMappingModel.`type`.head.iri(), "examples"), // was not in universe
    (NodeMappingModel.`type`.head.iri(), "displayName"), // was not in universe
    (NodeMappingModel.`type`.head.iri(), "description"), // was not in universe
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
    (NodeMappingModel.`type`.head.iri(), "maxLength")
  ).map(t => CategoryField(t._1, t._2, "parameters"))

  private val setCategoriesSecurity: Set[CategoryField] = Set(
    (NodeMappingModel.`type`.head.iri(), "describedBy"),
    (VocabularyMappings.library, "securitySchemes"),
    (OperationModel.`type`.head.iri(), "securedBy"),
    (EndPointModel.`type`.head.iri(), "securedBy"),
    (WebApiModel.`type`.head.iri(), "securitySchemes"),
    (WebApiModel.`type`.head.iri(), "securedBy")
  ).map(t => CategoryField(t._1, t._2, "security"))

  private val setCategoriesTypesAndTraits: Set[CategoryField] = Set(
    (VocabularyMappings.library, "annotationTypes"),
    (VocabularyMappings.library, "traits"),
    (VocabularyMappings.library, "resourceTypes"),
    (VocabularyMappings.library, "types"), // was not in universe
    (OperationModel.`type`.head.iri(), "is"), // was not in universe
    (EndPointModel.`type`.head.iri(), "is"), // was not in universe
    (EndPointModel.`type`.head.iri(), "type"),
    (NodeMappingModel.`type`.head.iri(), "type"),
    (WebApiModel.`type`.head.iri(), "types"), // was not in universe
    (WebApiModel.`type`.head.iri(), "traits"),
    (WebApiModel.`type`.head.iri(), "resourceTypes"),
    (WebApiModel.`type`.head.iri(), "annotationTypes"), // was not in universe
    (NodeMappingModel.`type`.head.iri(), "type") // was not in universe
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
    (ResponseModel.`type`.head.iri(), "body")
  ).map(t => CategoryField(t._1, t._2, "responses"))

  protected val allRamlCategories =
    setCategoriesBody ++ setCategoriesRoot ++ setCategoriesDocs ++ setCategoriesParameters ++
      setCategoriesSecurity ++ setCategoriesTypesAndTraits ++ setCategoriesMethods ++ setCategoriesResponses
}
