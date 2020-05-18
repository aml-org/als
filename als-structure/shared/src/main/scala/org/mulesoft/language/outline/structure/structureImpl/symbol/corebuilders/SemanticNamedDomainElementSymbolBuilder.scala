package org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders

import amf.core.model.domain.DomainElement
import amf.core.parser.{Range, Value}
import org.mulesoft.als.common.AlsAmfElement._
import org.mulesoft.als.common.SemanticNamedElement._
import org.mulesoft.amfmanager.AmfImplicits.AmfAnnotationsImp
import org.mulesoft.language.outline.structure.structureImpl.StructureContext
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.StructuredSymbolBuilder
case class SemanticNamedDomainElementSymbolBuilder(name: String,
                                                   override protected val selectionRange: Option[Range],
                                                   element: DomainElement)(override implicit val ctx: StructureContext)
    extends StructuredSymbolBuilder[DomainElement] {

  override protected val optionName: Option[String] = Some(name)
}

object SemanticNamedDomainElementSymbolBuilder {
  def unapply(element: DomainElement)(
      implicit ctx: StructureContext): Option[SemanticNamedDomainElementSymbolBuilder] =
    element
      .namedField()
      .flatMap(v => nameAndRange(v))
      .map(t => SemanticNamedDomainElementSymbolBuilder(t._1, t._2, element))

  private def nameAndRange(value: Value): Option[(String, Option[Range])] = {
    val maybeRange: Option[Range] = value.annotations
      .range()
      .orElse(value.value.annotations.range())
    value.value.toScalar.map(s => (s.toString(), maybeRange))
  }
}
