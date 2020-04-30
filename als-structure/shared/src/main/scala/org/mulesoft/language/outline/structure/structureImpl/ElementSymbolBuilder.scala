package org.mulesoft.language.outline.structure.structureImpl

import amf.core.annotations.LexicalInformation
import amf.core.metamodel.domain.{DomainElementModel, LinkableElementModel}
import amf.core.model.domain.{AmfArray, AmfElement, AmfObject, AmfScalar}
import amf.core.parser.FieldEntry
import amf.plugins.domain.webapi.metamodel.WebApiModel
import org.mulesoft.als.common.dtoTypes.PositionRange

/**
  * Common Symbol builder
  * */
trait SymbolBuilder[T] {
  val element: T
  implicit val factory: BuilderFactory
  def build(): Seq[DocumentSymbol]
}

trait SymbolBuilderCompanion[T] {
  def getType: Class[_]
  final def construct(element: Any)(implicit factory: BuilderFactory): Option[SymbolBuilder[T]] = {
    if (getType.isInstance(element)) construct(element.asInstanceOf[T])
    else None
  }

  protected def construct(element: T)(implicit factory: BuilderFactory): Option[SymbolBuilder[T]]
}

trait IriSymbolBuilderCompanion {
  val supportedIri: String
}

//

trait FieldSymbolBuilder extends SymbolBuilder[FieldEntry] {}

trait FieldSymbolBuilderCompanion extends SymbolBuilderCompanion[FieldEntry] {
  override def getType: Class[_] = classOf[FieldEntry]
}

//

trait IriFieldSymbolBuilderCompanion extends FieldSymbolBuilderCompanion with IriSymbolBuilderCompanion {}
//

trait FieldTypeSymbolBuilder[ElementType <: AmfElement] extends FieldSymbolBuilder {
  val value: ElementType

  protected def range: PositionRange =
    PositionRange(
      value.annotations
        .find(classOf[LexicalInformation])
        .map(l => l.range)
        .getOrElse(amf.core.parser.Range.NONE))
}

trait SingleFieldTypeSymbolBuilder[ElementType <: AmfElement] extends FieldTypeSymbolBuilder[ElementType] {
  protected val name: Option[String]
  def build(): Seq[DocumentSymbol] =
    name.map(n => DocumentSymbol(n, SymbolKind.String, deprecated = false, range, range, Nil)).toSeq
}

trait FieldTypeSymbolBuilderCompanion[ElementType <: AmfElement] extends FieldSymbolBuilderCompanion {

  def getElementType: Class[_ <: AmfElement]
  def construct(element: FieldEntry, value: ElementType)(
      implicit factory: BuilderFactory): Option[FieldTypeSymbolBuilder[ElementType]]

  final override def construct(element: FieldEntry)(
      implicit factory: BuilderFactory): Option[SymbolBuilder[FieldEntry]] = {
    if (getElementType.isInstance(element.value.value)) {
      construct(element, element.value.value.asInstanceOf[ElementType])
    } else None
  }
}
//

trait ArrayFieldTypeSymbolBuilder extends FieldTypeSymbolBuilder[AmfArray] {}
trait ArrayFieldTypeSymbolBuilderCompanion extends FieldTypeSymbolBuilderCompanion[AmfArray] {
  override def getElementType: Class[_ <: AmfElement] = classOf[AmfArray]
}

// separate between optional and mandatory name for aml?
trait ScalarFieldTypeSymbolBuilder extends SingleFieldTypeSymbolBuilder[AmfScalar] {}

trait ScalarFieldTypeSymbolBuilderCompanion extends FieldTypeSymbolBuilderCompanion[AmfScalar] {
  override def getElementType: Class[_ <: AmfElement] = classOf[AmfScalar]
}

trait ObjectFieldTypeSymbolBuilder extends SingleFieldTypeSymbolBuilder[AmfObject] {}
trait ObjectFieldTypeSymbolBuilderCompanion extends FieldTypeSymbolBuilderCompanion[AmfObject] {
  override def getElementType: Class[_ <: AmfElement] = classOf[AmfObject]
}

//

trait AmfObjectSimpleBuilderCompanion[DM <: AmfObject]
    extends SymbolBuilderCompanion[DM]
    with IriSymbolBuilderCompanion {}

trait AmfObjectSymbolBuilder[DM <: AmfObject] extends SymbolBuilder[DM] {
  def ignoreFields =
    List(WebApiModel.Name, DomainElementModel.Extends, LinkableElementModel.Target, WebApiModel.Version)
  // WebApiModel.Version should be on it's own builder, or not?

//  def customBuilders: Seq[CustomBuilder] =
//    Seq(
//      ExamplesCustomArrayBuilder()(factory),
//      CustomDomainPropertiesCustomArrayBuilder()(factory),
//      PayloadCustomArrayBuilder()(factory), // order is important here? if paylad is not first, it will match web api custom array builder.
//      WebApiCustomArrayBuilder()(factory)
//    )

  protected def children: List[DocumentSymbol] =
    element.fields
      .fields()
      .filterNot(fe => ignoreFields.contains(fe.field))
      .toList
      .flatMap(factory
        .builderFor(_, element.location()))
      .flatMap(_.build())
}
