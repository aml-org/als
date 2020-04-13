package org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders

import amf.core.annotations.LexicalInformation
import amf.core.model.domain.DomainElement
import amf.core.parser.Value
import org.mulesoft.als.common.SemanticNamedElement._
import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.language.outline.structure.structureImpl.BuilderFactory
import org.mulesoft.als.common.AlsAmfElement._

case class SemanticNamedDomainElementSymbolBuilder(
    override val name: String,
    override val selectionRange: Option[PositionRange],
    element: DomainElement)(override implicit val factory: BuilderFactory)
    extends AmfObjSymbolBuilder[DomainElement] {}

object SemanticNamedDomainElementSymbolBuilder {
  def unapply(element: DomainElement)(
      implicit factory: BuilderFactory): Option[SemanticNamedDomainElementSymbolBuilder] =
    element
      .namedField()
      .flatMap(v => nameAndRange(v))
      .map(t => SemanticNamedDomainElementSymbolBuilder(t._1, t._2, element))

  private def nameAndRange(value: Value): Option[(String, Option[PositionRange])] = {
    val maybeRange = value.annotations
      .find(classOf[LexicalInformation])
      .orElse(value.value.annotations.find(classOf[LexicalInformation]))
      .map(a => PositionRange(a.range))
    value.value.toScalar.map(s => (s.toString(), maybeRange))
  }
}
