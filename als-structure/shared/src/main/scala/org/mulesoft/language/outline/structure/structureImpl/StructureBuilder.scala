package org.mulesoft.language.outline.structure.structureImpl

import amf.client.model.DataTypes
import amf.core.metamodel.Field
import amf.core.metamodel.Type.Scalar
import amf.core.metamodel.domain.extensions.{CustomDomainPropertyModel, PropertyShapeModel}
import amf.core.metamodel.domain.{DomainElementModel, ShapeModel}
import amf.core.model.document.BaseUnit
import amf.core.model.domain._
import amf.core.remote._
import amf.core.vocabulary.Namespace.XsdTypes
import amf.plugins.document.vocabularies.AMLPlugin
import amf.plugins.document.vocabularies.model.document.{DialectInstance, DialectInstanceUnit}
import amf.plugins.domain.shapes.metamodel._
import amf.plugins.domain.shapes.metamodel.common.DocumentationField
import amf.plugins.domain.shapes.models.ScalarShape
import amf.plugins.domain.webapi.metamodel._
import amf.plugins.domain.webapi.metamodel.templates.{ResourceTypeModel, TraitModel}
import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.amfintegration.dialect.dialects.asyncapi20.AsyncApi20Dialect
import org.mulesoft.amfmanager.dialect.webapi.oas.{Oas20DialectWrapper, Oas30DialectWrapper}
import org.mulesoft.amfmanager.dialect.webapi.raml.raml08.Raml08TypesDialect
import org.mulesoft.amfmanager.dialect.webapi.raml.raml10.Raml10TypesDialect
import org.mulesoft.language.outline.structure.structureImpl.SymbolKind.SymbolKind
import org.mulesoft.language.outline.structure.structureImpl.factory.amlfactory.AmlBuilderFactory
import org.mulesoft.language.outline.structure.structureImpl.factory.webapi.{
  Async20BuilderFactory,
  Oas20BuilderFactory,
  Oas30BuilderFactory,
  RamlBuilderFactory
}

class StructureBuilder(unit: BaseUnit) {

  // todo: general amf model dialect?
  private val contextBuilder = new StructureContextBuilder(unit)

  private def populateBuilder(): Unit = unit.sourceVendor match {
    case Some(Raml08)     => contextBuilder.withDialect(Raml08TypesDialect()).withFactory(RamlBuilderFactory)
    case Some(_: Raml)    => contextBuilder.withDialect(Raml10TypesDialect()).withFactory(RamlBuilderFactory)
    case Some(Oas30)      => contextBuilder.withDialect(Oas30DialectWrapper()).withFactory(Oas30BuilderFactory)
    case Some(_: Oas)     => contextBuilder.withDialect(Oas20DialectWrapper.dialect).withFactory(Oas20BuilderFactory)
    case Some(AsyncApi20) => contextBuilder.withDialect(AsyncApi20Dialect()).withFactory(Async20BuilderFactory)
    case _                => amlBuilder
  }

  private def amlBuilder = {
    val maybeFactory = unit match {
      //case _:Dialect => AmlBuilderFactory(MetaDialect//) // todo: meta dialect merge
      case instance: DialectInstanceUnit => // todo: I cannot  assume that declared elements in a dialect instance will be the same that for library. Declared uris dependens on context/
        AMLPlugin.registry.dialectFor(instance)
      case _ => None
    }
    val d = maybeFactory.getOrElse(Oas30DialectWrapper.dialect) // amf model  === oas 3?
    contextBuilder.withDialect(d).withFactory(AmlBuilderFactory)
  }

  def listSymbols(): List[DocumentSymbol] = {
    populateBuilder()
    val context = contextBuilder.build()
    context.factory.builderFor(unit)(context).map(_.build().toList).getOrElse(Nil)
  }

  // unused?
  def fullRange(ranges: Seq[PositionRange]): PositionRange = {
    val sortedStart = ranges.sortWith((a, b) => a.start < b.start)
    val sortedEnd   = ranges.sortWith((a, b) => a.end < b.end)
    PositionRange(sortedStart.head.start, sortedEnd.last.end)
  }
}

object StructureBuilder {
  def apply(unit: BaseUnit): StructureBuilder = new StructureBuilder(unit)

  def listSymbols(ast: BaseUnit): List[DocumentSymbol] =
    new StructureBuilder(ast).listSymbols()
}

object KindForResultMatcher {

  private val irisMap = Map(
    ParametersFieldModel.Headers.value.iri()              -> SymbolKind.Module,
    ParametersFieldModel.QueryParameters.value.iri()      -> SymbolKind.Module,
    ParametersFieldModel.QueryString.value.iri()          -> SymbolKind.Module,
    ParametersFieldModel.UriParameters.value.iri()        -> SymbolKind.Module,
    EndPointModel.Parameters.value.iri()                  -> SymbolKind.Module,
    OperationModel.Request.value.iri()                    -> SymbolKind.Interface,
    OperationModel.Responses.value.iri()                  -> SymbolKind.Constructor,
    DomainElementModel.CustomDomainProperties.value.iri() -> SymbolKind.Enum,
    (new TagsModel {}).Tags.value.iri() -> SymbolKind.Package,
    WebApiModel.Security.value.iri() -> SymbolKind.String,
    ServerModel.Url.value.iri()      -> SymbolKind.String,
    WebApiModel.Version.value.iri()  -> SymbolKind.String
  )

  private val documentationField: String =
    (new DocumentationField {}).Documentation.value.iri()

  def getKind(element: AmfElement): SymbolKind = {
    element match {
      case _: ObjectNode => SymbolKind.Property
      case _: ArrayNode  => SymbolKind.Array
      case s: ScalarNode =>
        s.dataType.option() match {
          case Some(t) if t == XsdTypes.xsdBoolean.iri() => SymbolKind.Boolean
          case Some(t) if t == XsdTypes.amlNumber.iri()  => SymbolKind.Number
          case Some(t) if t == XsdTypes.xsdInteger.iri() => SymbolKind.Number
          case Some(t) if t == XsdTypes.xsdDouble.iri()  => SymbolKind.Number
          case Some(t) if t == XsdTypes.xsdFloat.iri()   => SymbolKind.Number
          case _                                         => SymbolKind.String
        }

      case domainElement: DomainElement =>
        domainElement.meta match {
          case ParameterModel | PayloadModel   => SymbolKind.Variable
          case TagModel                        => SymbolKind.Package
          case CreativeWorkModel               => SymbolKind.Module
          case RequestModel                    => SymbolKind.Interface
          case ResponseModel                   => SymbolKind.Constructor
          case CustomDomainPropertyModel       => SymbolKind.Enum
          case _: ShapeModel                   => SymbolKind.Class
          case EndPointModel                   => SymbolKind.Function
          case OperationModel | OperationModel => SymbolKind.Method
          case PropertyShapeModel              => SymbolKind.Property
          case ResourceTypeModel | TraitModel  => SymbolKind.Interface
          case _                               => SymbolKind.Property
        }
      case _ => SymbolKind.Property // default to class?
    }
  }

  def kindForScalar(scalarShape: ScalarShape): SymbolKind = {
    scalarShape.dataType.option() match {
      case Some(DataTypes.Boolean) => SymbolKind.Boolean
      case Some(
          DataTypes.Number | DataTypes.Decimal | DataTypes.Double | DataTypes.Float | DataTypes.Long |
          DataTypes.Integer) =>
        SymbolKind.Number
      case Some(DataTypes.File) => SymbolKind.File
      case _                    => SymbolKind.String

    }
  }

  def kindForField(f: Field): SymbolKind = irisMap.getOrElse(f.value.iri(), kindForFieldType(f))

  def kindForFieldType(f: Field): SymbolKind = f.`type` match {
    case s: Scalar => SymbolKind.String
    case amf.core.metamodel.Type.Int | amf.core.metamodel.Type.Float | amf.core.metamodel.Type.Double =>
      SymbolKind.Number
    case amf.core.metamodel.Type.Bool => SymbolKind.Boolean
    case _                            => SymbolKind.Property
  }

}
