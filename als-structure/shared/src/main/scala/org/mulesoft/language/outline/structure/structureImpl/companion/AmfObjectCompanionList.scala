package org.mulesoft.language.outline.structure.structureImpl.companion
import amf.core.client.scala.model.domain.AmfObject
import org.mulesoft.amfintegration.AmfImplicits.AmfObjectImp
import org.mulesoft.language.outline.structure.structureImpl.StructureContext
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.{
  AmfObjectSimpleBuilderCompanion,
  SymbolBuilder
}

class AmfObjectCompanionList(list: List[AmfObjectSimpleBuilderCompanion[_ <: AmfObject]])
    extends CompanionList[AmfObject, AmfObjectSimpleBuilderCompanion[_ <: AmfObject]](list) {

  private val map: Map[String, AmfObjectSimpleBuilderCompanion[_ <: AmfObject]] =
    list.map(c => c.supportedIri -> c).toMap

  override def find(element: AmfObject)(implicit ctx: StructureContext): Option[SymbolBuilder[_ <: AmfObject]] =
    find(element.metaURIs, element)

  private def find(definitions: Seq[String], element: AmfObject)(implicit
      ctx: StructureContext
  ): Option[SymbolBuilder[_ <: AmfObject]] =
    definitions match {
      case Nil => None
      case head :: tail =>
        val maybeOption: Option[SymbolBuilder[_ <: AmfObject]] =
          map.get(head).flatMap(b => b.constructAny(element).map(_.asInstanceOf[SymbolBuilder[AmfObject]]))
        maybeOption.orElse(find(tail, element))
    }

  override protected def newInstance(
      list: List[AmfObjectSimpleBuilderCompanion[_ <: AmfObject]]
  ): CompanionList[AmfObject, AmfObjectSimpleBuilderCompanion[_ <: AmfObject]] = new AmfObjectCompanionList(list)
}
