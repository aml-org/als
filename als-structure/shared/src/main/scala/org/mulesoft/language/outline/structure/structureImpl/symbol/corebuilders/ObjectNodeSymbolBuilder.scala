package org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders

import amf.core.client.scala.model.domain.{AmfElement, DataNode, ObjectNode, ScalarNode}
import amf.core.internal.annotations.LexicalInformation
import amf.core.internal.parser.domain.Value
import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.language.outline.structure.structureImpl._
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.{
  AmfObjectSimpleBuilderCompanion,
  AmfObjectSymbolBuilder,
  SymbolBuilder
}
import org.mulesoft.lexer.InputRange
import amf.core.client.common.position.{Range => AmfRange}
import amf.core.internal.metamodel.domain.ObjectNodeModel
import amf.core.internal.utils._

class ObjectNodeSymbolBuilder(override val element: ObjectNode)(override implicit val ctx: StructureContext)
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
          ctx.factory
            .builderFor(v)
            .map(_.build())
            .map { r =>
              Seq(DocumentSymbol(n.value.name.urlComponentDecoded, KindForResultMatcher.getKind(v), range, r.toList))
            }
            .getOrElse(Nil)
        case _ => Nil

      }
      .toSeq
  }

  override protected val optionName: Option[String] = None

  override protected val kind: SymbolKinds.SymbolKind = SymbolKinds.Property
}

object ObjectNodeSymbolBuilder extends AmfObjectSimpleBuilderCompanion[ObjectNode] {

  override def getType: Class[_ <: AmfElement] = classOf[ObjectNode]

  override val supportedIri: String = ObjectNodeModel.`type`.head.iri()

  override def construct(element: ObjectNode)(implicit ctx: StructureContext): Option[SymbolBuilder[ObjectNode]] =
    Some(new ObjectNodeSymbolBuilder(element))
}
