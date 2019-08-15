package org.mulesoft.language.outline.structure.structureImpl

import amf.client.model.DataTypes
import amf.core.metamodel.domain.extensions.PropertyShapeModel
import amf.core.model.document.BaseUnit
import amf.core.model.domain._
import amf.core.remote.{Oas, Raml}
import amf.plugins.domain.shapes.metamodel.{ArrayShapeModel, FileShapeModel, NodeShapeModel, ScalarShapeModel}
import amf.plugins.domain.shapes.models.ScalarShape
import amf.plugins.domain.webapi.metamodel.templates.ResourceTypeModel
import amf.plugins.domain.webapi.metamodel.{EndPointModel, OperationModel}
import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.high.level.interfaces.IParseResult
import org.mulesoft.language.outline.structure.structureImpl.SymbolKind.SymbolKind
import org.mulesoft.language.outline.structure.structureImpl.factory.amlfactory.AmlBuilderFactory
import org.mulesoft.language.outline.structure.structureImpl.factory.webapi.{OasBuilderFactory, RamlBuilderFactory}

class StructureBuilder(unit: BaseUnit) {

  private val builderFactory: BuilderFactory = unit.sourceVendor match {
    case Some(_: Raml) => RamlBuilderFactory
    case Some(_: Oas)  => OasBuilderFactory
    case _             => AmlBuilderFactory
  }

  def listSymbols(): List[DocumentSymbol] = {
    builderFactory.builderFor(unit).map(_.build().toList).getOrElse(Nil)
  }

  def fullRange(ranges: Seq[PositionRange]): PositionRange = {
    val sortedStart = ranges.sortWith((a, b) => a.start < b.start)
    val sortedEnd   = ranges.sortWith((a, b) => a.end < b.end)
    PositionRange(sortedStart.head.start, sortedEnd.last.end)
  }
}

object StructureBuilder {
  def apply(unit: BaseUnit): StructureBuilder = new StructureBuilder(unit)

  def listSymbols(ast: BaseUnit): List[DocumentSymbol] = new StructureBuilder(ast).listSymbols()
}

object KindForResultMatcher {

  def getKind(hlNode: IParseResult): SymbolKind = {
    hlNode.amfNode match {
      case domainElement: DomainElement =>
        domainElement.meta match {
          case ScalarShapeModel =>
            kindForScalar(hlNode.amfNode.asInstanceOf[ScalarShape])
          case NodeShapeModel                    => SymbolKind.Class
          case ArrayShapeModel                   => SymbolKind.Array
          case FileShapeModel                    => SymbolKind.File
          case EndPointModel | ResourceTypeModel => SymbolKind.Function
          case OperationModel | OperationModel   => SymbolKind.Method
          case PropertyShapeModel                => SymbolKind.Property
          case _                                 => SymbolKind.Property
        }
      case _ => SymbolKind.Property
    }
  }

  def getKind(element: AmfElement): SymbolKind = {
    element match {
      case domainElement: DomainElement =>
        domainElement.meta match {
          case ScalarShapeModel =>
            kindForScalar(element.asInstanceOf[ScalarShape])
          case NodeShapeModel                    => SymbolKind.Class
          case ArrayShapeModel                   => SymbolKind.Array
          case FileShapeModel                    => SymbolKind.File
          case EndPointModel | ResourceTypeModel => SymbolKind.Function
          case OperationModel | OperationModel   => SymbolKind.Method
          case PropertyShapeModel                => SymbolKind.Property
          case _                                 => SymbolKind.Property
        }
      case _ => SymbolKind.Property
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
}
