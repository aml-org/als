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
import org.mulesoft.common.client.lexical.{PositionRange => AmfPositionRange}
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
          val decodedName = n.value.name.urlComponentDecoded
          val range =
            PositionRange(a.find(classOf[LexicalInformation]).map(l => l.range).getOrElse(AmfPositionRange.ZERO))
          ctx.factory
            .builderFor(v)
            .map(_.build())
            .map { s => // in case a child contains the same name, merge it's childs
              s.filterNot(ds => ds.name == decodedName) ++ s.filter(ds => ds.name == decodedName).flatMap(_.children)
            }
            .map { r =>
              Seq(DocumentSymbol(decodedName, KindForResultMatcher.getKind(v), range, r.toList))
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
