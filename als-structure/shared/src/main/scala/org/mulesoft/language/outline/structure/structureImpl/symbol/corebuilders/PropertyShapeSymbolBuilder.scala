package org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders

import amf.core.metamodel.Field
import amf.core.metamodel.domain.extensions.PropertyShapeModel
import amf.core.model.domain.AmfElement
import amf.core.model.domain.extensions.PropertyShape
import amf.plugins.domain.shapes.models.{AnyShape, ArrayShape, NodeShape, UnionShape}
import org.mulesoft.language.outline.structure.structureImpl._

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

object PropertyShapeSymbolBuilder extends AmfObjectSimpleBuilderCompanion[PropertyShape] {

  override def getType: Class[_ <: AmfElement] = classOf[PropertyShape]

  override val supportedIri: String = PropertyShapeModel.`type`.head.iri()

  override def construct(element: PropertyShape)(
      implicit factory: BuilderFactory): Option[StructuredSymbolBuilder[PropertyShape]] =
    Some(new PropertyShapeSymbolBuilder(element))
}
