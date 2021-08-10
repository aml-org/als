package org.mulesoft.language.outline.structure.structureImpl

import amf.core.metamodel.Field
import amf.core.metamodel.Type.Scalar
import amf.core.metamodel.domain.extensions.{CustomDomainPropertyModel, PropertyShapeModel}
import amf.core.metamodel.domain.{DomainElementModel, ShapeModel}
import amf.core.model.document.BaseUnit
import amf.core.model.domain._
import amf.core.remote.{Oas, Oas30, Raml, _}
import amf.plugins.document.vocabularies.model.document.{Dialect, DialectFragment, DialectLibrary, Vocabulary}
import amf.plugins.domain.shapes.metamodel._
import amf.plugins.domain.webapi.metamodel._
import amf.plugins.domain.webapi.metamodel.api.WebApiModel
import amf.plugins.domain.webapi.metamodel.templates.{ResourceTypeModel, TraitModel}
import org.mulesoft.language.outline.structure.structureImpl.SymbolKinds.SymbolKind
import org.mulesoft.language.outline.structure.structureImpl.factory.amlfactory.{
  AmlBuilderFactory,
  AmlMetaDialectBuilderFactory,
  AmlVocabularyBuilderFactory
}
import org.mulesoft.language.outline.structure.structureImpl.factory.webapi.{
  Async20BuilderFactory,
  Oas20BuilderFactory,
  Oas30BuilderFactory,
  RamlBuilderFactory
}

class StructureBuilder(unit: BaseUnit, definedBy: Dialect) {

  // todo: general amf model dialect?
  private val factory: BuilderFactory = {
    unit.sourceVendor match {
      case Some(Raml08)     => RamlBuilderFactory
      case Some(_: Raml)    => RamlBuilderFactory
      case Some(Oas30)      => Oas30BuilderFactory
      case Some(_: Oas)     => Oas20BuilderFactory
      case Some(AsyncApi20) => Async20BuilderFactory
      case _
          if unit.isInstanceOf[Dialect] || unit.isInstanceOf[DialectLibrary] || unit.isInstanceOf[DialectFragment] =>
        AmlMetaDialectBuilderFactory
      case _ if unit.isInstanceOf[Vocabulary] => AmlVocabularyBuilderFactory
      case _                                  => AmlBuilderFactory
    }
  }

  private val context = new StructureContextBuilder(unit)
    .withDialect(definedBy)
    .withFactory(factory)
    .build() // todo: change for default amf model dialect

  def listSymbols(): List[DocumentSymbol] =
    context.factory.builderFor(unit)(context).map(_.build().toList).getOrElse(Nil)
}

object StructureBuilder {
  def listSymbols(unit: BaseUnit, definedBy: Dialect): List[DocumentSymbol] =
    new StructureBuilder(unit, definedBy).listSymbols()
}

object KindForResultMatcher {

  private val irisMap = Map(
    ParametersFieldModel.Headers.value.iri()              -> SymbolKinds.Module,
    ParametersFieldModel.QueryParameters.value.iri()      -> SymbolKinds.Module,
    ParametersFieldModel.QueryString.value.iri()          -> SymbolKinds.Module,
    ParametersFieldModel.UriParameters.value.iri()        -> SymbolKinds.Module,
    EndPointModel.Parameters.value.iri()                  -> SymbolKinds.Module,
    OperationModel.Request.value.iri()                    -> SymbolKinds.Interface,
    OperationModel.Responses.value.iri()                  -> SymbolKinds.Constructor,
    DomainElementModel.CustomDomainProperties.value.iri() -> SymbolKinds.Enum,
    new TagsModel {}.Tags.value.iri() -> SymbolKinds.Package,
    WebApiModel.Security.value.iri() -> SymbolKinds.String,
    ServerModel.Url.value.iri()      -> SymbolKinds.String,
    WebApiModel.Version.value.iri()  -> SymbolKinds.String
  )

  def getKind(element: AmfElement): SymbolKind = {
    element match {
      case _: ObjectNode => SymbolKinds.Property
      // not showing array/scalar (check git history to bring back if needed) - 14/07/2020
      case domainElement: DomainElement =>
        domainElement.meta match {
          case ParameterModel | PayloadModel   => SymbolKinds.Variable
          case TagModel                        => SymbolKinds.Package
          case CreativeWorkModel               => SymbolKinds.Module
          case RequestModel                    => SymbolKinds.Interface
          case ResponseModel                   => SymbolKinds.Constructor
          case CustomDomainPropertyModel       => SymbolKinds.Enum
          case _: ShapeModel                   => SymbolKinds.Class
          case EndPointModel                   => SymbolKinds.Function
          case OperationModel | OperationModel => SymbolKinds.Method
          case PropertyShapeModel              => SymbolKinds.Property
          case ResourceTypeModel | TraitModel  => SymbolKinds.Interface
          case _                               => SymbolKinds.Property
        }
      case _ => SymbolKinds.Property // default to class?
    }
  }

  def kindForField(f: Field): SymbolKind = irisMap.getOrElse(f.value.iri(), kindForFieldType(f))

  def kindForFieldType(f: Field): SymbolKind = f.`type` match {
    case _: Scalar => SymbolKinds.String
    case amf.core.metamodel.Type.Int | amf.core.metamodel.Type.Float | amf.core.metamodel.Type.Double =>
      SymbolKinds.Number
    case amf.core.metamodel.Type.Bool => SymbolKinds.Boolean
    case _                            => SymbolKinds.Property
  }

}
