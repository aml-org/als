package org.mulesoft.language.outline.structure.structureImpl.companion
import org.mulesoft.amfmanager.AmfImplicits._
import amf.core.model.domain.AmfObject
import org.mulesoft.language.outline.structure.structureImpl.{
  AmfObjectSimpleBuilderCompanion,
  BuilderFactory,
  SymbolBuilder
}

class AmfObjectCompanionList(list: List[AmfObjectSimpleBuilderCompanion[_ <: AmfObject]])(
    implicit factory: BuilderFactory)
    extends CompanionList[AmfObject, AmfObjectSimpleBuilderCompanion[_ <: AmfObject]](list) {

  private val map: Map[String, AmfObjectSimpleBuilderCompanion[_ <: AmfObject]] =
    list.map(c => c.supportedIri -> c).toMap

  override def find(element: AmfObject): Option[SymbolBuilder[_ <: AmfObject]] = {
    find(element.metaURIs, element)
  }

  private def find(definitions: Seq[String], element: AmfObject)(
      implicit factory: BuilderFactory): Option[SymbolBuilder[_ <: AmfObject]] = {
    definitions match {
      case Nil => None
      case head :: tail =>
        val maybeOption: Option[SymbolBuilder[_ <: AmfObject]] = map.get(head).flatMap(_.construct(element))
        maybeOption.orElse(find(tail, element))
    }
  }

  override protected def newInstance(list: List[AmfObjectSimpleBuilderCompanion[_ <: AmfObject]])
    : CompanionList[AmfObject, AmfObjectSimpleBuilderCompanion[_ <: AmfObject]] = new AmfObjectCompanionList(list)
}
