package org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders

import amf.core.annotations.{LexicalInformation, SourceAST}
import amf.core.metamodel.domain.{ArrayNodeModel, ObjectNodeModel}
import amf.core.model.domain.{AmfElement, ArrayNode, ObjectNode, ScalarNode}
import org.mulesoft.language.outline.structure.structureImpl._
import org.yaml.model.YMapEntry
import amf.core.utils._
import amf.core.parser.{Range => AmfRange}
import org.mulesoft.als.common.dtoTypes.PositionRange

class ObjectNodeSymbolBuilder(obj: ObjectNode)(override implicit val factory: BuilderFactory)
    extends ElementSymbolBuilder[ObjectNode] {

  override def build(): Seq[DocumentSymbol] = {
    obj.properties
      .filter(p => !p._2.isInstanceOf[ScalarNode])
      .flatMap {
        case (n, d) =>
          val range = PositionRange(
            obj.propertyAnnotations(n).find(classOf[LexicalInformation]).map(l => l.range).getOrElse(AmfRange.NONE))
          val selectionRange = obj.propertyAnnotations
            .get(n)
            .flatMap(_.find(classOf[SourceAST]))
            .map(_.ast)
            .collect({ case e: YMapEntry => PositionRange(e.key.range) })
            .getOrElse(range)
          factory
            .builderFor(d)
            .map(_.build())
            .map { r =>
              Seq(new DocumentSymbol(n.urlComponentDecoded, SymbolKind.Field, false, range, selectionRange, r.toList))
            }
            .getOrElse(Nil)

      }
      .toSeq
  }
}

object ObjectNodeSymbolBuilder extends ElementSymbolBuilderCompanion {
  override type T = ObjectNode

  override def getType: Class[_ <: AmfElement] = classOf[ObjectNode]

  override val supportedIri: String = ObjectNodeModel.`type`.head.iri()

  override def construct(element: ObjectNode)(
      implicit factory: BuilderFactory): Option[ElementSymbolBuilder[ObjectNode]] =
    Some(new ObjectNodeSymbolBuilder(element))
}

class ArrayNodeSymbolBuilder(arr: ArrayNode)(override implicit val factory: BuilderFactory)
    extends ElementSymbolBuilder[ArrayNode] {

  override def build(): Seq[DocumentSymbol] = {
    arr.members.zipWithIndex.map {
      case (node, index) =>
        val range =
          PositionRange(node.annotations.find(classOf[LexicalInformation]).map(_.range).getOrElse(AmfRange.NONE))
        new DocumentSymbol(index.toString,
                           SymbolKind.Array,
                           false,
                           range,
                           range,
                           factory.builderFor(node).map(_.build()).getOrElse(Nil).toList)
    }
  }
}

object ArrayNodeSymbolBuilder extends ElementSymbolBuilderCompanion {
  override type T = ArrayNode

  override def getType: Class[_ <: AmfElement] = classOf[ArrayNode]

  override val supportedIri: String = ArrayNodeModel.`type`.head.iri()

  override def construct(element: ArrayNode)(
      implicit factory: BuilderFactory): Option[ElementSymbolBuilder[ArrayNode]] =
    Some(new ArrayNodeSymbolBuilder(element))
}
