package org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders

import amf.core.annotations.{LexicalInformation, SourceAST}
import amf.core.metamodel.domain.ObjectNodeModel
import amf.core.model.domain._
import amf.core.parser.{Value, Range => AmfRange}
import amf.core.utils._
import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.language.outline.structure.structureImpl._
import org.mulesoft.lexer.InputRange
import org.yaml.model.YMapEntry

class ObjectNodeSymbolBuilder(override val element: ObjectNode)(override implicit val factory: BuilderFactory)
    extends AmfObjectSymbolBuilder[ObjectNode] {

  override def build(): Seq[DocumentSymbol] = {
    element
      .propertyFields()
      .flatMap(f => {
        val value = element.fields.getValueAsOption(f)
        value.map(v => f -> v)
      })
      .flatMap {
        case (_, Value(_: ScalarNode, _)) => Nil
        case (n, Value(v: DataNode, a)) =>
          val range =
            PositionRange(a.find(classOf[LexicalInformation]).map(l => l.range).getOrElse(AmfRange(InputRange.Zero)))
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
                                   KindForResultMatcher.getKind(v),
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

object ObjectNodeSymbolBuilder extends AmfObjectSimpleBuilderCompanion[ObjectNode] {

  override def getType: Class[_ <: AmfElement] = classOf[ObjectNode]

  override val supportedIri: String = ObjectNodeModel.`type`.head.iri()

  override def construct(element: ObjectNode)(implicit factory: BuilderFactory): Option[SymbolBuilder[ObjectNode]] =
    Some(new ObjectNodeSymbolBuilder(element))
}
