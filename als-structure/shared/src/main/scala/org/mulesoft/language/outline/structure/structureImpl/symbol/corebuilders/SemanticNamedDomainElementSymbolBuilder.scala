package org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders

import amf.aml.client.scala.model.domain.{NodeMapping, PropertyMapping}
import amf.core.client.scala.model.domain.DomainElement
import org.mulesoft.als.common.AlsAmfElement._
import org.mulesoft.als.common.SemanticNamedElement._
import org.mulesoft.language.outline.structure.structureImpl.StructureContext
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.StructuredSymbolBuilder

case class SemanticNamedDomainElementSymbolBuilder(name: String, element: DomainElement)(
    override implicit val ctx: StructureContext
) extends StructuredSymbolBuilder[DomainElement] {

  override protected val optionName: Option[String] = Some(name)
}

object SemanticNamedDomainElementSymbolBuilder {
  def unapply(element: DomainElement)(implicit ctx: StructureContext): Option[SemanticNamedDomainElementSymbolBuilder] =
    element match {
      case _: NodeMapping | _: PropertyMapping =>
        element
          .namedField()
          .flatMap(v => v.value.toScalar.map(_.toString()))
          .map(t => SemanticNamedDomainElementSymbolBuilder(t, element))
      case _ => None
    }
}
