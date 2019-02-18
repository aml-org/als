package org.mulesoft.language.outline.structure.structureImpl

import amf.client.model.DataTypes
import amf.core.annotations.{LexicalInformation, SourceAST}
import amf.core.metamodel.domain.extensions.PropertyShapeModel
import amf.core.model.domain.{DomainElement, NamedDomainElement}
import amf.plugins.domain.shapes.metamodel.{ArrayShapeModel, FileShapeModel, NodeShapeModel, ScalarShapeModel}
import amf.plugins.domain.shapes.models.ScalarShape
import amf.plugins.domain.webapi.metamodel.templates.ResourceTypeModel
import amf.plugins.domain.webapi.metamodel.{EndPointModel, OperationModel}
import common.dtoTypes.{EmptyPositionRange, PositionRange}
import org.mulesoft.high.level.interfaces.IParseResult
import org.mulesoft.language.outline.common.commonInterfaces.{CategoryFilter, LabelProvider, VisibilityFilter}
import org.mulesoft.language.outline.structure.structureImpl.SymbolKind.SymbolKind
import org.mulesoft.language.outline.structure.structureInterfaces.StructureConfiguration
import org.mulesoft.lexer.InputRange
import org.yaml.model.YMapEntry
class StructureBuilder(root: IParseResult, labelProvider: LabelProvider, visibilityFilter: VisibilityFilter) {

  def listSymbols(categoryFilter: List[CategoryFilter]): List[DocumentSymbol] =
    root.children
      .filter(c => !c.isAttr && visibilityFilter(c) && categoryFilter.exists(_.apply(c)))
      .map(documentSymbol)
      .toList

  private def documentSymbol(hlNode: IParseResult): DocumentSymbol = {
    val (range, keyRange) = positionRange(hlNode)

    DocumentSymbol(
      labelProvider.getLabelText(hlNode),
      KindForResultMatcher.getKind(hlNode),
      deprecated = false,
      range,
      keyRange,
      hlNode.children
        .filter(c => (!c.isAttr) && visibilityFilter(c))
        .map(documentSymbol)
        .toList
    )
  }

  private def positionRange(node: IParseResult): (PositionRange, PositionRange) = {
    node.sourceInfo.yamlSources.headOption.map(_.range) match {
      case Some(syamlRange) =>
        val keyRange = (node.amfNode match {
          case n: NamedDomainElement => n.name.annotations().find(classOf[LexicalInformation]).map(_.range)
          case _                     => None
        }).orElse {
          node.amfNode.annotations
            .find(classOf[SourceAST])
            .map(_.ast)
            .flatMap {
              case entry: YMapEntry =>
                Some(amfRangeFromSyamlRange(entry.key.range))
              case _ => None
            }
        }
        val finalNodeRange = amfRangeFromSyamlRange(syamlRange)
        (PositionRange(finalNodeRange), PositionRange(finalNodeRange))
      case _ => (EmptyPositionRange, EmptyPositionRange)
    }
  }

  private def amfRangeFromSyamlRange(range: InputRange) = {
    amf.core.parser.Range((range.lineFrom - 1, range.columnFrom), (range.lineTo - 1, range.columnTo))
  }

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

object StructureBuilder {
  def apply(ast: IParseResult, config: StructureConfiguration): StructureBuilder =
    new StructureBuilder(ast, config.labelProvider, config.visibilityFilter)

  def listSymbols(ast: IParseResult, config: StructureConfiguration): List[DocumentSymbol] =
    new StructureBuilder(ast, config.labelProvider, config.visibilityFilter)
      .listSymbols(config.categories.keys.map(config.categories(_)).toList)
}
