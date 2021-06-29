package org.mulesoft.language.outline.structure.structureImpl.symbol.amlbuilders

import amf.aml.client.scala.model.document.Dialect
import amf.aml.internal.metamodel.document.DialectModel
import amf.core.client.scala.model.domain.AmfScalar
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.{
  AmfObjectSimpleBuilderCompanion,
  StructuredSymbolBuilder,
  SymbolBuilder
}
import org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders.DefaultNamedScalarTypeSymbolBuilder
import org.mulesoft.language.outline.structure.structureImpl.{DocumentSymbol, StructureContext}

case class AmlMetaDialectSymbolBuilder(override val element: Dialect)(override implicit val ctx: StructureContext)
    extends StructuredSymbolBuilder[Dialect] {

  private val dialectNameBuilder: Option[DefaultNamedScalarTypeSymbolBuilder] = element.fields
    .fields()
    .find(_.field == DialectModel.Name)
    .map(fe => new DefaultNamedScalarTypeSymbolBuilder(fe.value.value.asInstanceOf[AmfScalar], fe, "dialect"))

  override protected def children: List[DocumentSymbol] =
    dialectNameBuilder.map(_.build()).getOrElse(Nil).toList ++ super.children

  override protected def optionName: Option[String] = None
}

object AmlMetaDialectSymbolBuilder extends AmfObjectSimpleBuilderCompanion[Dialect] {
  override def construct(element: Dialect)(implicit ctx: StructureContext): Option[SymbolBuilder[Dialect]] = {
    element match {
      case d: Dialect => Some(AmlMetaDialectSymbolBuilder(d))
      case _          => None
    }
  }

  override def getType: Class[_] = classOf[Dialect]

  override val supportedIri: String = DialectModel.`type`.head.iri()
}
