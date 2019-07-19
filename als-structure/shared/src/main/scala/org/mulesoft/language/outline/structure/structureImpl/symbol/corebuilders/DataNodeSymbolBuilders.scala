package org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders

import amf.core.annotations.{LexicalInformation, SourceAST}
import amf.core.metamodel.domain.{ArrayNodeModel, ObjectNodeModel}
import amf.core.model.domain._
import org.mulesoft.language.outline.structure.structureImpl._
import org.yaml.model.YMapEntry
import amf.core.utils._
import amf.core.parser.{Value, Range => AmfRange}
import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.lexer.InputRange

class ObjectNodeSymbolBuilder(obj: ObjectNode)(override implicit val factory: BuilderFactory)
    extends ElementSymbolBuilder[ObjectNode] {

  override def build(): Seq[DocumentSymbol] = {
    obj
      .propertyFields()
      .flatMap(f => {
        val value = obj.fields.getValueAsOption(f)
        value.map(v => f -> v)
      })
      .flatMap {
        case (n, Value(v: ScalarNode, _)) => Nil
        case (n, Value(v: DataNode, a)) =>
          val range = PositionRange(a.find(classOf[LexicalInformation]).map(l => l.range).getOrElse(AmfRange(InputRange.Zero)))
          val selectionRange = a
            .find(classOf[SourceAST])
            .map(_.ast)
            .collect({ case e: YMapEntry => PositionRange(e.key.range) })
            .getOrElse(range)
          factory
            .builderFor(v)
            .map(_.build())
            .map { r =>
              Seq(
                new DocumentSymbol(n.value.name.urlComponentDecoded,
                                   SymbolKind.Property,
                                   false,
                                   range,
                                   selectionRange,
                                   r.toList))
            }
            .getOrElse(Nil)
        case _ => Nil

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
