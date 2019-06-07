package org.mulesoft.language.outline.structure.structureImpl

import amf.client.model.DataTypes
import amf.core.metamodel.domain.extensions.PropertyShapeModel
import amf.core.model.domain._
import amf.core.remote.{Oas, Raml}
import amf.plugins.domain.shapes.metamodel.{ArrayShapeModel, FileShapeModel, NodeShapeModel, ScalarShapeModel}
import amf.plugins.domain.shapes.models.ScalarShape
import amf.plugins.domain.webapi.metamodel.templates.ResourceTypeModel
import amf.plugins.domain.webapi.metamodel.{EndPointModel, OperationModel}
import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.high.level.interfaces.IParseResult
import org.mulesoft.language.outline.common.commonInterfaces.{CategoryFilter, LabelProvider, VisibilityFilter}
import org.mulesoft.language.outline.structure.structureDefault.NonEmptyNameVisibilityFilter
import org.mulesoft.language.outline.structure.structureImpl.SymbolKind.SymbolKind
import org.mulesoft.language.outline.structure.structureImpl.factory.amlfactory.AmlBuilderFactory
import org.mulesoft.language.outline.structure.structureImpl.factory.webapi.{OasBuilderFactory, RamlBuilderFactory}
import org.mulesoft.language.outline.structure.structureInterfaces.StructureConfiguration

class StructureBuilder(root: IParseResult, labelProvider: LabelProvider, filters: Seq[VisibilityFilter]) {

  private val builderFactory: BuilderFactory = root.amfBaseUnit.sourceVendor match {
    case Some(_: Raml) => RamlBuilderFactory
    case Some(_: Oas)  => OasBuilderFactory
    case _             => AmlBuilderFactory
  }

  def listSymbols(categoryFilter: List[CategoryFilter]): List[DocumentSymbol] = {
    builderFactory.builderFor(root.amfBaseUnit).map(_.build().toList).getOrElse(Nil)
  }

  def fullRange(ranges: Seq[PositionRange]): PositionRange = {
    val sortedStart = ranges.sortWith((a, b) => a.start < b.start)
    val sortedEnd   = ranges.sortWith((a, b) => a.end < b.end)
    PositionRange(sortedStart.head.start, sortedEnd.last.end)
  }
}

object StructureBuilder {
  def apply(ast: IParseResult, config: StructureConfiguration): StructureBuilder =
    new StructureBuilder(ast,
                         config.labelProvider,
                         Seq(config.visibilityFilter, NonEmptyNameVisibilityFilter(config.labelProvider)))

  def listSymbols(ast: IParseResult, config: StructureConfiguration): List[DocumentSymbol] =
    new StructureBuilder(ast,
                         config.labelProvider,
                         Seq(config.visibilityFilter, NonEmptyNameVisibilityFilter(config.labelProvider)))
      .listSymbols(config.categories.keys.map(config.categories(_)).toList)
}

object KindForResultMatcher {

  def getKind(hlNode: IParseResult): SymbolKind = {
    hlNode.amfNode match {
      case domainElement: DomainElement =>
        domainElement.meta match {
          case ScalarShapeModel =>
            kindForScalar(hlNode.amfNode.asInstanceOf[ScalarShape])
          case NodeShapeModel                    => SymbolKind.Object
          case ArrayShapeModel                   => SymbolKind.Array
          case FileShapeModel                    => SymbolKind.File
          case EndPointModel | ResourceTypeModel => SymbolKind.Function
          case OperationModel | OperationModel   => SymbolKind.Method
          case PropertyShapeModel                => SymbolKind.Property
          case _                                 => SymbolKind.Field
        }
      case _ => SymbolKind.Field
    }
  }

  def getKind(element: AmfElement): SymbolKind = {
    element match {
      case domainElement: DomainElement =>
        domainElement.meta match {
          case ScalarShapeModel =>
            kindForScalar(element.asInstanceOf[ScalarShape])
          case NodeShapeModel                    => SymbolKind.Object
          case ArrayShapeModel                   => SymbolKind.Array
          case FileShapeModel                    => SymbolKind.File
          case EndPointModel | ResourceTypeModel => SymbolKind.Function
          case OperationModel | OperationModel   => SymbolKind.Method
          case PropertyShapeModel                => SymbolKind.Property
          case _                                 => SymbolKind.Field
        }
      case _ => SymbolKind.Field
    }
  }

  def kindForScalar(scalarShape: ScalarShape): SymbolKind = {
    scalarShape.dataType.option() match {
      case Some(DataTypes.Boolean) => SymbolKind.Boolean
      case Some(
          DataTypes.Number | DataTypes.Decimal | DataTypes.Double | DataTypes.Float | DataTypes.Long |
          DataTypes.Integer) =>
        SymbolKind.Boolean
      case Some(DataTypes.File) => SymbolKind.File
      case _                    => SymbolKind.String

    }
  }
}
