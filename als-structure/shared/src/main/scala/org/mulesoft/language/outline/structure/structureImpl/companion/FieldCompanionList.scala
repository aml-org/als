package org.mulesoft.language.outline.structure.structureImpl.companion

import amf.core.model.domain.{AmfElement, AmfObject}
import amf.core.parser.{FieldEntry, Value}
import org.mulesoft.language.outline.structure.structureImpl.{
  AmfObjectSimpleBuilderCompanion,
  ArrayFieldTypeSymbolBuilderCompanion,
  BuilderFactory,
  DocumentSymbol,
  FieldSymbolBuilderCompanion,
  FieldTypeSymbolBuilderCompanion,
  IriFieldSymbolBuilderCompanion,
  ObjectFieldTypeSymbolBuilder,
  ObjectFieldTypeSymbolBuilderCompanion,
  ScalarFieldTypeSymbolBuilderCompanion,
  SymbolBuilder
}

class FieldCompanionList(list: List[FieldSymbolBuilderCompanion], objectCompanionList: AmfObjectCompanionList)(
    implicit factory: BuilderFactory)
    extends CompanionList[FieldEntry, FieldSymbolBuilderCompanion](list) {

  override protected def newInstance(list: List[FieldSymbolBuilderCompanion]): FieldCompanionList =
    new FieldCompanionList(list, objectCompanionList)

  private val iriList: Map[String, FieldSymbolBuilderCompanion with IriFieldSymbolBuilderCompanion] =
    list.collect({ case iri: IriFieldSymbolBuilderCompanion => iri }).map(i => i.supportedIri -> i).toMap

  // dynamic by type/class?
  private val arrayFieldList: Option[ArrayFieldTypeSymbolBuilderCompanion] = list.collectFirst({
    case arrayField: ArrayFieldTypeSymbolBuilderCompanion => arrayField
  })
  private val scalarFieldList: Option[ScalarFieldTypeSymbolBuilderCompanion] = list.collectFirst({
    case scalarField: ScalarFieldTypeSymbolBuilderCompanion => scalarField
  })
  private val objectFieldList: Option[ObjectFieldTypeSymbolBuilderCompanion] = list.collectFirst({
    case objectField: ObjectFieldTypeSymbolBuilderCompanion => objectField
  })

  private val iriObject: FieldTypeSymbolBuilderCompanion[AmfObject] = new FieldTypeSymbolBuilderCompanion[AmfObject] {
    override def getElementType: Class[_ <: AmfElement] = classOf[AmfObject]

    override def construct(element: FieldEntry, value: AmfObject): Option[ObjectFieldTypeSymbolBuilder] = {
      find(value).map { b =>
        new ObjectFieldTypeSymbolBuilder {
          override val value: AmfObject                 = value
          override val element: FieldEntry              = element
          override implicit val factory: BuilderFactory = factory

          override def build(): Seq[DocumentSymbol] = b.build()
        }
      }
    }
  }

  override def +(builder: FieldSymbolBuilderCompanion): CompanionList[FieldEntry, FieldSymbolBuilderCompanion] = {
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

  def +(builder: AmfObjectSimpleBuilderCompanion[_ <: AmfObject])
    : CompanionList[FieldEntry, FieldSymbolBuilderCompanion] = new FieldCompanionList(list, objectCompanionList)

  def find(obj: AmfObject): Option[SymbolBuilder[_ <: AmfObject]] = objectCompanionList.find(obj)

  override def find(element: FieldEntry): Option[SymbolBuilder[FieldEntry]] = {
    iriList
      .get(element.field.value.iri())
      .flatMap(_.construct(element))
      .orElse(arrayFieldList.flatMap(_.construct(element)))
      .orElse(scalarFieldList.flatMap(_.construct(element)))
      .orElse(iriObject.construct(element))
      .orElse(objectFieldList.flatMap(_.construct(element)))
  }

}

object FieldCompanionList {
  def apply(list: List[FieldSymbolBuilderCompanion], objList: List[AmfObjectSimpleBuilderCompanion[_ <: AmfObject]])(
      implicit factory: BuilderFactory): FieldCompanionList =
    new FieldCompanionList(list, new AmfObjectCompanionList(objList))
}
