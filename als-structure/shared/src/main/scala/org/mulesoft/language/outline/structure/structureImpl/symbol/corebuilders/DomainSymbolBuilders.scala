package org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders

import amf.core.client.scala.model.domain.{AmfElement, DomainElement, NamedDomainElement, ScalarNode}
import amf.core.internal.annotations.SourceAST
import amf.core.internal.metamodel.domain.DomainElementModel
import org.mulesoft.amfintegration.AmfImplicits.AmfAnnotationsImp
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

  override protected val optionName: Option[String] =
    entryAst.key.asScalar.map(_.text).orElse(entryAst.key.asScalar.map(_.text))
}

object DomainElementSymbolBuilder extends AmfObjectSimpleBuilderCompanion[DomainElement] {
  override val supportedIri: String = DomainElementModel.`type`.head.iri()

  override def getType: Class[_ <: AmfElement] = classOf[DomainElement]

  override def construct(element: DomainElement)(
      implicit ctx: StructureContext): Option[SymbolBuilder[DomainElement]] =
    element match {
      case _: ScalarNode => None
      case n: NamedDomainElement if n.name.option().isDefined =>
        NamedElementSymbolBuilder.construct(n).map(_.asInstanceOf[SymbolBuilder[DomainElement]])
      case SemanticNamedDomainElementSymbolBuilder(builder) => Some(builder)
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

  override protected val optionName: Option[String] =
    if (element.name.annotations().isSynthesized) None else element.name.option()
}

class NamedElementSymbolBuilder(override val element: NamedDomainElement)(override implicit val ctx: StructureContext)
    extends NamedElementSymbolBuilderTrait[NamedDomainElement]
