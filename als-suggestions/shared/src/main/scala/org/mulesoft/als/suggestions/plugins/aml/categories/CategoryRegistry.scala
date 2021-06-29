package org.mulesoft.als.suggestions.plugins.aml.categories

import amf.apicontract.internal.metamodel.domain.{EndPointModel, OperationModel, OrganizationModel, ParameterModel, PayloadModel, ResponseModel}
import amf.apicontract.internal.metamodel.domain.api.{AsyncApiModel, WebApiModel}
import amf.apicontract.internal.metamodel.domain.security.{ApiKeySettingsModel, SecuritySchemeModel, SettingsModel}
import amf.shapes.internal.domain.metamodel.{CreativeWorkModel, XMLSerializerModel}
import org.mulesoft.amfintegration.dialect.dialects.raml.raml08.Raml08TypesDialect
import org.mulesoft.amfintegration.dialect.dialects.raml.raml10.Raml10TypesDialect

case class CategoryIndex(classTerm: String, property: String)

case class CategoryField(classTerm: Option[String],
                         property: String,
                         category: String)

trait BaseCategoryRegistry {

  def rootKey: String = "root"

  def documentationKey: String = "documentation"

  def parametersKey: String = "parameters"

  def typesAndTraitsKey: String = "schemas"

  def methodsKey: String = "methods"

  def responsesKey: String = "responses"

  def schemasKey: String = "schemas"

  def securityKey: String = "security"

  def bodyKey: String = "body"

  private def setCategoriesRoot: Set[CategoryField] = Set(
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
  ).map(t => CategoryField(t._1, t._2, rootKey))

  private val setCategoriesDocs: Set[CategoryField] = Set(
    (None, "externalDocs"),
    (None, "title"),
    (None, "description"),
    (None, "example"),
    (None, "examples"),
    (None, "displayName"),
    (None, "name"),
    (None, "url"),
    (None, "usage"),
    (Some(WebApiModel.`type`.head.iri()), "externalDocs"),
    (Some(WebApiModel.`type`.head.iri()), "info"),
    (Some(AsyncApiModel.`type`.head.iri()), "info"),
    (Some(OrganizationModel.`type`.head.iri()), "email"),
    (Some(CreativeWorkModel.`type`.head.iri()), "title"),
    (Some(CreativeWorkModel.`type`.head.iri()), "content"),
    (Some(WebApiModel.`type`.head.iri()), documentationKey)
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
  ).map(t => CategoryField(t._1, t._2, parametersKey))

  private val setCategoriesTypesAndTraits: Set[CategoryField] = Set(
    (None, "annotationTypes"),
    (None, "traits"),
    (None, "resourceTypes"),
    (None, "types"),
    (None, "type"),
    (Some(OperationModel.`type`.head.iri()), "is"),
    (Some(EndPointModel.`type`.head.iri()), "is"),
  ).map(t => CategoryField(t._1, t._2, typesAndTraitsKey))

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
  ).map(t => CategoryField(t._1, t._2, methodsKey))

  private val setCategoriesResponses: Set[CategoryField] = Set(
    (None, "responses"),
    (Some(ResponseModel.`type`.head.iri()), "body")
  ).map(t => CategoryField(t._1, t._2, responsesKey))

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
  ).map(t => CategoryField(t._1, t._2, schemasKey))

  private val setCategoriesSecurity: Set[CategoryField] = Set(
    (None, "securitySchemes"),
    (None, "describedBy"),
    (Some(WebApiModel.`type`.head.iri()), "security"),
    (None, "flow"),
    (None, "tokenUrl"),
    (None, "scopes"),
    (None, "authorizationUrl"),
    (Some(SecuritySchemeModel.`type`.head.iri()), "type"),
    (Some(SettingsModel.`type`.head.iri()), "type"),
    (Some(ApiKeySettingsModel.`type`.head.iri()), "in"),
    (Some(ApiKeySettingsModel.`type`.head.iri()), "name"),
    (Some(OperationModel.`type`.head.iri()), "securedBy"),
    (Some(EndPointModel.`type`.head.iri()), "securedBy"),
    (Some(WebApiModel.`type`.head.iri()), "securedBy")
  ).map(t => CategoryField(t._1, t._2, securityKey))

  private val setCategoriesBody: Set[CategoryField] = Set(
    (Some(OperationModel.`type`.head.iri()), "body")
  ).map(t => CategoryField(t._1, t._2, bodyKey))

  def allCategories: Set[CategoryField] = setCategoriesBody ++ setCategoriesRoot ++ setCategoriesDocs ++ setCategoriesParameters ++
    setCategoriesSecurity ++ setCategoriesTypesAndTraits ++ setCategoriesMethods ++ setCategoriesResponses ++ setCategoriesSchemas

}

object DefaultBaseCategoryRegistry extends BaseCategoryRegistry

object RamlCategoryRegistry extends BaseCategoryRegistry{
  override def typesAndTraitsKey: String = "types and traits"
}

object CategoryRegistry{
  private val index :Map[String,BaseCategoryRegistry] = Map(Raml10TypesDialect().id -> RamlCategoryRegistry,Raml08TypesDialect().id -> RamlCategoryRegistry)
  def apply(classTerm: String, property: String, dialect:String): String = {

    val (specific, general) = index.getOrElse(dialect, DefaultBaseCategoryRegistry).allCategories
      .filter(p => p.property == property && (p.classTerm.contains(classTerm) || p.classTerm.isEmpty)).partition(_.classTerm.isDefined)

    specific.headOption.orElse(general.headOption)
      .map(_.category)
      .getOrElse("unknown")
  }
}
