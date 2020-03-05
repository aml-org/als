package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders

import amf.core.annotations.LexicalInformation
import amf.core.metamodel.domain.DomainElementModel
import amf.core.model.domain.{AmfElement, NamedDomainElement}
import amf.plugins.document.webapi.annotations.{EndPointResourceTypeEntry, EndPointTraitEntry, OperationTraitEntry}
import amf.plugins.domain.webapi.metamodel.OperationModel
import amf.plugins.domain.webapi.models.Operation
import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.language.outline.structure.structureImpl._
import org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders.NamedElementSymbolBuilderTrait

trait ExtendsFatherSymbolBuilder[T <: NamedDomainElement] extends NamedElementSymbolBuilderTrait[T] {
  override def children: List[DocumentSymbol] = super.children ++ getExtendsChildren

  protected def getExtendsChildren: Seq[DocumentSymbol] = {
    val traitSons = element.annotations
      .find(classOf[OperationTraitEntry])
      .map(_.range)
      .orElse(element.annotations.find(classOf[EndPointTraitEntry]).map(_.range))
      .map(r => {
        DocumentSymbol("is",
                       KindForResultMatcher.kindForField(DomainElementModel.Extends),
                       deprecated = false,
                       PositionRange(r),
                       PositionRange(r),
                       Nil)
      })

    val typeSons = element.annotations
      .find(classOf[EndPointResourceTypeEntry])
      .map(_.range)
      .map(r => {
        DocumentSymbol("type",
                       KindForResultMatcher.kindForField(DomainElementModel.Extends),
                       deprecated = false,
                       PositionRange(r),
                       PositionRange(r),
                       Nil)
      })

    (traitSons ++ typeSons).toSeq
  }
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
