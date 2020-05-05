package org.mulesoft.language.outline.structure.structureImpl.companion

import amf.core.model.domain.{AmfElement, AmfObject}
import amf.core.parser.FieldEntry
import org.mulesoft.language.outline.structure.structureImpl._
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.fieldbuilders.{
  ArrayFieldTypeSymbolBuilderCompanion,
  DefaultArrayTypeSymbolBuilder,
  DefaultObjectTypeSymbolBuilder,
  DefaultScalarTypeSymbolBuilder,
  ObjectFieldTypeSymbolBuilder,
  ObjectFieldTypeSymbolBuilderCompanion,
  ScalarFieldTypeSymbolBuilderCompanion
}
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.{
  AmfObjectSimpleBuilderCompanion,
  FieldSymbolBuilderCompanion,
  FieldTypeSymbolBuilder,
  FieldTypeSymbolBuilderCompanion,
  IriFieldSymbolBuilderCompanion,
  SymbolBuilder
}

class FieldCompanionList(
    list: List[FieldSymbolBuilderCompanion],
    objectCompanionList: CompanionList[AmfObject, AmfObjectSimpleBuilderCompanion[_ <: AmfObject]])
    extends CompanionList[FieldEntry, FieldSymbolBuilderCompanion](list) {

  override protected def newInstance(list: List[FieldSymbolBuilderCompanion]): FieldCompanionList =
    new FieldCompanionList(list, objectCompanionList)

  private val iriList: Map[String, FieldSymbolBuilderCompanion with IriFieldSymbolBuilderCompanion] =
    list.collect({ case iri: IriFieldSymbolBuilderCompanion => iri }).map(i => i.supportedIri -> i).toMap

  // dynamic by type/class?
  private val arrayFieldList: Option[ArrayFieldTypeSymbolBuilderCompanion] = list.collectFirst({
    case arrayField: DefaultArrayTypeSymbolBuilder => arrayField
  })
  private val scalarFieldList: Option[ScalarFieldTypeSymbolBuilderCompanion] = list.collectFirst({
    case scalarField: DefaultScalarTypeSymbolBuilder => scalarField
  })
  private val objectFieldList: Option[ObjectFieldTypeSymbolBuilderCompanion] = list.collectFirst({
    case objectField: DefaultObjectTypeSymbolBuilder => objectField
  })

  private val iriObject: FieldTypeSymbolBuilderCompanion[AmfObject] = new FieldTypeSymbolBuilderCompanion[AmfObject] {
    override def getElementType: Class[_ <: AmfElement] = classOf[AmfObject]

    override def construct(element: FieldEntry, value: AmfObject)(
        implicit ctx: StructureContext): Option[FieldTypeSymbolBuilder[AmfObject]] = {
      find(value).map { b =>
        val builder: ObjectFieldTypeSymbolBuilder = new ObjectFieldTypeSymbolBuilder {
          override val value: AmfObject               = value
          override val element: FieldEntry            = element
          override implicit val ctx: StructureContext = ctx

          override def build(): Seq[DocumentSymbol] = b.build()
        }
        builder
      }

    }
  }

  override def +(builder: FieldSymbolBuilderCompanion): FieldCompanionList = {
    builder match {
      case i: IriFieldSymbolBuilderCompanion =>
        newInstance(
          iriList.updated(i.supportedIri, i).values.toList ++ arrayFieldList ++ scalarFieldList ++ objectFieldList)
      case a: ArrayFieldTypeSymbolBuilderCompanion =>
        newInstance(iriList.values.toList ++ scalarFieldList ++ objectFieldList :+ a)
      case s: ScalarFieldTypeSymbolBuilderCompanion =>
        newInstance(iriList.values.toList ++ arrayFieldList ++ objectFieldList :+ s)
      case o: ObjectFieldTypeSymbolBuilderCompanion =>
        newInstance(iriList.values.toList ++ arrayFieldList ++ scalarFieldList :+ o)
      case _ => this
    }
  }

  def +(builder: AmfObjectSimpleBuilderCompanion[_ <: AmfObject]): FieldCompanionList = {
    val newObjectCompanionList = objectCompanionList + builder
    new FieldCompanionList(list, newObjectCompanionList)
  }

  def find(obj: AmfObject)(implicit ctx: StructureContext): Option[SymbolBuilder[_ <: AmfObject]] =
    objectCompanionList.find(obj)

  override def find(element: FieldEntry)(implicit ctx: StructureContext): Option[SymbolBuilder[FieldEntry]] = {
    iriList
      .get(element.field.value.iri())
      .flatMap(_.constructAny(element))
      .orElse(arrayFieldList.flatMap(_.construct(element)))
      .orElse(scalarFieldList.flatMap(_.construct(element)))
      .orElse(iriObject.construct(element))
      .orElse(objectFieldList.flatMap(_.construct(element)))
  }

}

object FieldCompanionList {
  def apply(list: List[FieldSymbolBuilderCompanion],
            objList: List[AmfObjectSimpleBuilderCompanion[_ <: AmfObject]]): FieldCompanionList =
    new FieldCompanionList(list, new AmfObjectCompanionList(objList))
}
