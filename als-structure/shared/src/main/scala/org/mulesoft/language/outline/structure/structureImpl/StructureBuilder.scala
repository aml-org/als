package org.mulesoft.language.outline.structure.structureImpl

import amf.aml.client.scala.model.document.{Dialect, DialectFragment, DialectLibrary, Vocabulary}
import amf.apicontract.internal.metamodel.domain.api.WebApiModel
import amf.apicontract.internal.metamodel.domain.templates.{ResourceTypeModel, TraitModel}
import amf.apicontract.internal.metamodel.domain.{EndPointModel, OperationModel, ParameterModel, ParametersFieldModel, PayloadModel, RequestModel, ResponseModel, ServerModel, TagModel, TagsModel}
import amf.core.client.scala.model.document.BaseUnit
import amf.core.client.scala.model.domain.{AmfElement, DomainElement, ObjectNode}
import amf.core.internal.metamodel.Field
import amf.core.internal.metamodel.Type.Scalar
import amf.core.internal.metamodel.domain.{DomainElementModel, ShapeModel}
import amf.core.internal.metamodel.domain.extensions.{CustomDomainPropertyModel, PropertyShapeModel}
import amf.core.internal.remote._
import amf.shapes.internal.domain.metamodel.CreativeWorkModel
import org.mulesoft.language.outline.structure.structureImpl.SymbolKinds.SymbolKind
import org.mulesoft.language.outline.structure.structureImpl.factory.amlfactory.{AmlBuilderFactory, AmlMetaDialectBuilderFactory, AmlVocabularyBuilderFactory}
import org.mulesoft.language.outline.structure.structureImpl.factory.webapi.{Async20BuilderFactory, Oas20BuilderFactory, Oas30BuilderFactory, RamlBuilderFactory}
import amf.core.internal.metamodel.Type
import org.mulesoft.amfintegration.amfconfiguration.DocumentDefinition

class StructureBuilder(unit: BaseUnit, documentDefinition: DocumentDefinition) {

  // todo: general amf model dialect?
  private val factory: BuilderFactory = {
    unit.sourceSpec match {
      case Some(Spec.RAML08)  => RamlBuilderFactory
      case Some(Spec.RAML10)  => RamlBuilderFactory
      case Some(Spec.OAS30)   => Oas30BuilderFactory
      case Some(Spec.OAS20)   => Oas20BuilderFactory
      case Some(Spec.ASYNC20) => Async20BuilderFactory
      case _ if unit.isInstanceOf[Dialect] || unit.isInstanceOf[DialectLibrary] || unit.isInstanceOf[DialectFragment] =>
        AmlMetaDialectBuilderFactory
      case _ if unit.isInstanceOf[Vocabulary] => AmlVocabularyBuilderFactory
      case _                                  => AmlBuilderFactory
    }
  }

  private val context = new StructureContextBuilder(unit)
    .withDefinition(documentDefinition)
    .withFactory(factory)
    .build() // todo: change for default amf model dialect

  def listSymbols(): List[DocumentSymbol] =
    context.factory.builderFor(unit)(context).map(_.build().toList).getOrElse(Nil)
}

object StructureBuilder {
  def listSymbols(unit: BaseUnit, documentDefinition: DocumentDefinition): List[DocumentSymbol] =
    new StructureBuilder(unit, documentDefinition).listSymbols()
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
    new TagsModel {}.Tags.value.iri()                     -> SymbolKinds.Package,
    WebApiModel.Security.value.iri()                      -> SymbolKinds.String,
    ServerModel.Url.value.iri()                           -> SymbolKinds.String,
    WebApiModel.Version.value.iri()                       -> SymbolKinds.String
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
    case Type.Int | Type.Float | Type.Double =>
      SymbolKinds.Number
    case Type.Bool => SymbolKinds.Boolean
    case _         => SymbolKinds.Property
  }

}
