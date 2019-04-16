package org.mulesoft.language.outline.structure.structureImpl

import amf.client.model.DataTypes
import amf.core.annotations._
import amf.core.metamodel.domain.extensions.PropertyShapeModel
import amf.core.model.domain.{DomainElement, NamedDomainElement}
import amf.plugins.domain.shapes.metamodel.{ArrayShapeModel, FileShapeModel, NodeShapeModel, ScalarShapeModel}
import amf.plugins.domain.shapes.models.ScalarShape
import amf.plugins.domain.webapi.metamodel.templates.ResourceTypeModel
import amf.plugins.domain.webapi.metamodel.{EndPointModel, OperationModel}
import common.dtoTypes.{EmptyPositionRange, Position, PositionRange}
import org.mulesoft.high.level.interfaces.IParseResult
import org.mulesoft.language.outline.common.commonInterfaces.{CategoryFilter, LabelProvider, VisibilityFilter}
import org.mulesoft.language.outline.structure.structureImpl.SymbolKind.SymbolKind
import org.mulesoft.language.outline.structure.structureInterfaces.StructureConfiguration
import org.yaml.model.YMapEntry

import scala.collection.{GenTraversableOnce, mutable}
class StructureBuilder(root: IParseResult, labelProvider: LabelProvider, visibilityFilter: VisibilityFilter) {

  def listSymbols(categoryFilter: List[CategoryFilter]): List[DocumentSymbol] =
    root.children
      .filter(c => !c.isAttr && visibilityFilter(c) && categoryFilter.exists(_.apply(c)))
      .map(documentSymbol)
      .toList ++ childDocumentSymbol(root)

  def fullRange(ranges: Seq[PositionRange]): PositionRange = {
    val sortedStart = ranges.sortWith((a, b) => a.start < b.start)
    val sortedEnd   = ranges.sortWith((a, b) => a.end < b.end)
    PositionRange(sortedStart.head.start, sortedEnd.last.end)
  }

  private def documentSymbol(hlNode: IParseResult): DocumentSymbol = {
    val (range, keyRange) = positionRange(hlNode)
    DocumentSymbol(
      labelProvider.getLabelText(hlNode),
      KindForResultMatcher.getKind(hlNode),
      deprecated = false,
      range,
      keyRange,
      hlNode.children
        .filter(c => !c.isAttr && visibilityFilter(c))
        .map(documentSymbol)
        .toList ++ childDocumentSymbol(hlNode)
    )
  }

  // TODO: ALS-759 after dialect refactor, fix this
  def synthesizedChildren(filtered: Seq[IParseResult]): List[DocumentSymbol] = {
    val result = mutable.ListBuffer[DocumentSymbol]()
    filtered.foreach(f => {
      if (f.property.get.nameId.get == "host") {
        val rangeHostLexicalInformation: Option[PositionRange] = f.amfNode.fields
          .fields()
          .head
          .value
          .annotations
          .find(classOf[HostLexicalInformation])
          .map(li =>
            PositionRange(Position(li.range.start.line - 1, li.range.start.column),
                          Position(li.range.end.line - 1, li.range.end.column)))
        rangeHostLexicalInformation match {
          case Some(li) =>
            result.append(
              DocumentSymbol("host", KindForResultMatcher.getKind(f), deprecated = false, li, li, Nil)
            )
          case _ => ???
        }
      }
      if (f.property.get.nameId.get == "basePath") {
        val rangeBasePathLexicalInformation: Option[PositionRange] = f.amfNode.fields
          .fields()
          .head
          .value
          .annotations
          .find(classOf[BasePathLexicalInformation])
          .map(li =>
            PositionRange(Position(li.range.start.line - 1, li.range.start.column),
                          Position(li.range.end.line - 1, li.range.end.column)))
        rangeBasePathLexicalInformation match {
          case Some(li) =>
            result.append(
              DocumentSymbol("basePath", KindForResultMatcher.getKind(f), deprecated = false, li, li, Nil)
            )
          case _ => ???
        }
      }
    })
    result.toList
  }

  private def childDocumentSymbol(hlNode: IParseResult) = {
    val attrChildren = hlNode.children
      .filter(c => c.isAttr && visibilityFilter(c))

    val labels = attrChildren.map(labelProvider.getLabelText).distinct
    val result = mutable.ListBuffer[DocumentSymbol]()
    labels
      .foreach(label => {
        val corresponding: Seq[IParseResult] = attrChildren.filter(
          c =>
            labelProvider.getLabelText(c) == label &&
              !c.amfNode.annotations
                .contains(classOf[SynthesizedField])) // TODO: ALS-759 after dialect refactor, fix this
        if (corresponding.size > 0)
          result.append(
            DocumentSymbol(
              label,
              KindForResultMatcher.getKind(corresponding.head),
              deprecated = false,
              fullRange(corresponding.map(positionRange(_)._1)),
              corresponding.map(positionRange(_)._2).sortWith((a, b) => a.start < b.start).head,
              Nil
            ))
      })
    result.toList ++
      // TODO: ALS-759 after dialect refactor, fix this
      synthesizedChildren(
        attrChildren.filter(c =>
          labels.contains(labelProvider.getLabelText(c)) &&
            c.amfNode.annotations.contains(classOf[SynthesizedField])))

  }

  private def positionRange(node: IParseResult): (PositionRange, PositionRange) = {
    val valRange = node.amfNode.annotations
      .find(classOf[SourceAST]) match {
      case Some(sAST: SourceAST) => Option(PositionRange(sAST.ast.range))
      case _                     => None
    }
    val keyRange = (node.amfNode match {
      case n: NamedDomainElement =>
        n.name
          .annotations()
          .find(classOf[LexicalInformation])
          .map(_.range)
          .map(PositionRange(_))
      case _ => None
    }).orElse {
        node.amfNode.annotations
          .find(classOf[SourceAST])
          .map(_.ast)
          .flatMap {
            case entry: YMapEntry =>
              Some(PositionRange(entry.key.range))
            case _ => None
          }
      }
      .orElse(valRange)
    (fullRange(Seq(valRange.getOrElse(EmptyPositionRange), keyRange.getOrElse(EmptyPositionRange))),
     keyRange.getOrElse(EmptyPositionRange))
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
