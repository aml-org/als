package org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders

import amf.core.metamodel.Field
import amf.core.metamodel.domain.ShapeModel
import amf.core.metamodel.domain.extensions.PropertyShapeModel
import amf.core.model.domain.extensions.PropertyShape
import amf.core.model.domain.{AmfArray, AmfElement}
import amf.plugins.domain.shapes.models.{AnyShape, ArrayShape, NodeShape, UnionShape}
import org.mulesoft.language.outline.structure.structureImpl._

class ShapeInheritsSymbolBuilder(element: AmfArray)(override implicit val factory: BuilderFactory)
    extends ElementSymbolBuilder[AmfArray] {

  override def build(): Seq[DocumentSymbol] = Seq.empty // cuts nesting
}

object ShapeInheritsSymbolBuilder extends ElementSymbolBuilderCompanion {
  override type T = AmfArray

  override def getType: Class[_ <: AmfElement] = classOf[AmfArray]

  override val supportedIri: String = ShapeModel.Inherits.value.iri()

  override def construct(element: AmfArray)(implicit factory: BuilderFactory): Option[ElementSymbolBuilder[AmfArray]] =
    Some(new ShapeInheritsSymbolBuilder(element))
}

class PropertyShapeSymbolBuilder(override val element: PropertyShape)(override implicit val factory: BuilderFactory)
    extends NamedElementSymbolBuilderTrait[PropertyShape] {

  private val buildRange: Boolean = element.range match {
    case n: AnyShape if n.linkTarget.isDefined => false
    case n: NodeShape                          => n.properties.nonEmpty
    case _: ArrayShape                         => true
    case _: UnionShape                         => true
    case _                                     => false
  }
  override val ignoreFields: List[Field] =
    if (buildRange) super.ignoreFields else PropertyShapeModel.Range +: super.ignoreFields
}

object PropertyShapeSymbolBuilder extends ElementSymbolBuilderCompanion {
  override type T = PropertyShape

  override def getType: Class[_ <: AmfElement] = classOf[PropertyShape]

  override val supportedIri: String = PropertyShapeModel.`type`.head.iri()

  override def construct(element: PropertyShape)(
      implicit factory: BuilderFactory): Option[AmfObjSymbolBuilder[PropertyShape]] =
    Some(new PropertyShapeSymbolBuilder(element))
}
