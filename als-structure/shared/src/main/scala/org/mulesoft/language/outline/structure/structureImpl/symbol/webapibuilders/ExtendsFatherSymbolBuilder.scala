package org.mulesoft.language.outline.structure.structureImpl.symbol.webapibuilders

import amf.apicontract.client.scala.model.domain.Operation
import amf.apicontract.internal.annotations.{EndPointResourceTypeEntry, EndPointTraitEntry, OperationTraitEntry}
import amf.apicontract.internal.metamodel.domain.OperationModel
import amf.core.client.scala.model.domain.{AmfElement, NamedDomainElement}
import amf.core.internal.metamodel.domain.DomainElementModel
import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.language.outline.structure.structureImpl._
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.{
  AmfObjectSimpleBuilderCompanion,
  SymbolBuilder
}
import org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders.NamedElementSymbolBuilderTrait
// if the annotations would be at the field, we could handle this in nwe interface
trait ExtendsFatherSymbolBuilder[T <: NamedDomainElement] extends NamedElementSymbolBuilderTrait[T] {
  override protected def children: List[DocumentSymbol] = super.children ++ getExtendsChildren

  protected def getExtendsChildren: Seq[DocumentSymbol] = {
    val traitSons = element.annotations
      .find(classOf[OperationTraitEntry])
      .map(_.range)
      .orElse(element.annotations.find(classOf[EndPointTraitEntry]).map(_.range))
      .map(r => {
        DocumentSymbol("is", KindForResultMatcher.kindForField(DomainElementModel.Extends), PositionRange(r), Nil)
      })

    val typeSons = element.annotations
      .find(classOf[EndPointResourceTypeEntry])
      .map(_.range)
      .map(r => {
        DocumentSymbol("type", KindForResultMatcher.kindForField(DomainElementModel.Extends), PositionRange(r), Nil)
      })

    (traitSons ++ typeSons).toSeq
  }
}

class OperationSymbolBuilder(override val element: Operation)(override implicit val ctx: StructureContext)
    extends ExtendsFatherSymbolBuilder[Operation] {

  override protected val optionName: Option[String] = Some(element.name.option().getOrElse(element.method.value()))
}

object OperationSymbolBuilderCompanion extends AmfObjectSimpleBuilderCompanion[Operation] {
  override def getType: Class[_ <: AmfElement] = classOf[Operation]

  override val supportedIri: String = OperationModel.`type`.head.iri()

  override def construct(element: Operation)(implicit ctx: StructureContext): Option[SymbolBuilder[Operation]] =
    Some(new OperationSymbolBuilder(element))
}
