package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders

import amf.core.annotations.LexicalInformation
import amf.core.model.domain.{AmfElement, NamedDomainElement}
import amf.plugins.domain.webapi.metamodel.OperationModel
import amf.plugins.domain.webapi.models.{EndPoint, Operation}
import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders.NamedElementSymbolBuilderTrait
import org.mulesoft.language.outline.structure.structureImpl._
import amf.plugins.document.webapi.annotations.{OperationTraitEntry, EndPointTraitEntry, EndPointResourceTypeEntry}

trait ExtendsFatherSymbolBuilder[T <: NamedDomainElement] extends NamedElementSymbolBuilderTrait[T] {
  override def children: List[DocumentSymbol] = super.children ++ getExtendsChildren

  private def getExtendsChildren: Seq[DocumentSymbol] = {
    val traitSons = element.annotations
      .find(classOf[OperationTraitEntry])
      .map(_.range)
      .orElse(element.annotations.find(classOf[EndPointTraitEntry]).map(_.range))
      .map(r => {
        DocumentSymbol("is", SymbolKind.Interface, deprecated = false, PositionRange(r), PositionRange(r), Nil)
      })

    val typeSons = element.annotations
      .find(classOf[EndPointResourceTypeEntry])
      .map(_.range)
      .map(r => {
        DocumentSymbol("type", SymbolKind.Interface, deprecated = false, PositionRange(r), PositionRange(r), Nil)
      })

    (traitSons ++ typeSons).toSeq
  }
}

class EndPointSymbolBuilder(override val element: EndPoint)(override implicit val factory: BuilderFactory)
    extends ExtendsFatherSymbolBuilder[EndPoint] {

  override protected val name: String =
    element.path.value().stripPrefix(element.parent.flatMap(_.path.option()).getOrElse(""))
  override protected val selectionRange: Option[PositionRange] =
    element.path.annotations().find(classOf[LexicalInformation]).map(l => PositionRange(l.range)).orElse(range)

}

class OperationSymbolBuilder(override val element: Operation)(override implicit val factory: BuilderFactory)
    extends ExtendsFatherSymbolBuilder[Operation] {

  override protected val name: String = element.name.option().getOrElse(element.method.value())
  override protected val selectionRange: Option[PositionRange] =
    element.method.annotations().find(classOf[LexicalInformation]).map(l => PositionRange(l.range)).orElse(range)
}

object OperationSymbolBuilderCompanion extends ElementSymbolBuilderCompanion {
  override type T = Operation

  override def getType: Class[_ <: AmfElement] = classOf[Operation]

  override val supportedIri: String = OperationModel.`type`.head.iri()

  override def construct(element: Operation)(
      implicit factory: BuilderFactory): Option[ElementSymbolBuilder[Operation]] =
    Some(new OperationSymbolBuilder(element))
}
