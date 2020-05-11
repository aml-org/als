package org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders

import amf.core.annotations.{LexicalInformation, SourceAST}
import amf.core.metamodel.domain.DomainElementModel
import amf.core.model.domain._
import amf.core.parser.{Range => AmfRange}
import amf.plugins.document.webapi.annotations.InlineDefinition
import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.language.outline.structure.structureImpl._
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.{
  AmfObjectSimpleBuilderCompanion,
  StructuredSymbolBuilder,
  SymbolBuilder
}
import org.yaml.model.YMapEntry

class DomainElementSymbolBuilder(override val element: DomainElement, entryAst: YMapEntry)(
    override implicit val ctx: StructureContext)
    extends StructuredSymbolBuilder[DomainElement] {

  val (name, selectionRange) =
    (entryAst.key.asScalar.map(_.text).getOrElse(entryAst.key.value.toString),
     Some(PositionRange(AmfRange(entryAst.key.range))))
}

object DomainElementSymbolBuilder extends AmfObjectSimpleBuilderCompanion[DomainElement] {
  override val supportedIri: String = DomainElementModel.`type`.head.iri()

  override def getType: Class[_ <: AmfElement] = classOf[DomainElement]

  override def construct(element: DomainElement)(
      implicit ctx: StructureContext): Option[SymbolBuilder[DomainElement]] =
    element match {
      case n: NamedDomainElement if n.name.option().isDefined =>
        NamedElementSymbolBuilder.construct(n).map(_.asInstanceOf[SymbolBuilder[DomainElement]])
      case _ =>
        element.annotations.find(classOf[SourceAST]).map(_.ast) match {
          case Some(entry: YMapEntry) =>
            Some(new DomainElementSymbolBuilder(element, entry))
          case _ => None
        }
    }
}

object NamedElementSymbolBuilder extends AmfObjectSimpleBuilderCompanion[NamedDomainElement] {

  override def getType: Class[_ <: AmfElement] = classOf[NamedDomainElement]

  override val supportedIri: String = DomainElementModel.`type`.head.iri()

  override def construct(element: NamedDomainElement)(
      implicit ctx: StructureContext): Option[NamedElementSymbolBuilder] =
    Some(new NamedElementSymbolBuilder(element))
}

trait NamedElementSymbolBuilderTrait[T <: NamedDomainElement] extends StructuredSymbolBuilder[T] {

  override protected val name: String = element.name.option().getOrElse("")
  override protected val selectionRange: Option[PositionRange] = element.name
    .annotations()
    .find(classOf[LexicalInformation])
    .map(l => PositionRange(l.range))
    .orElse(range)
}

class NamedElementSymbolBuilder(override val element: NamedDomainElement)(override implicit val ctx: StructureContext)
    extends NamedElementSymbolBuilderTrait[NamedDomainElement]
