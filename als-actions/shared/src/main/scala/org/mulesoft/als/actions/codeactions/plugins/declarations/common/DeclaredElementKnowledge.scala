package org.mulesoft.als.actions.codeactions.plugins.declarations.common

import amf.core.model.document.{BaseUnit, DeclaresModel, Document}
import amf.core.model.domain.{AmfObject, DomainElement, NamedDomainElement}
import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.amfintegration.AmfImplicits.AmfAnnotationsImp

object DeclaredElementKnowledge {

  /**
    * @param de
    * @param range
    * @return true if it somehow intersects with the range
    */
  def domainElementWithinRange(de: DomainElement, range: PositionRange): Boolean =
    de.annotations.ast().map(_.range).map(PositionRange(_)).flatMap(range.intersection).isDefined

  def declaredInRange(range: PositionRange, bu: BaseUnit): Seq[DomainElement] = bu match {
    case d: Document =>
      d.declares.filter(DeclaredElementKnowledge.domainElementWithinRange(_, range)).filter {
        case nde: NamedDomainElement =>
          nde.name.option().isDefined // if this tries to emit, it will explode
        case _ => true
      }
    case _ => Seq.empty
  }
}
