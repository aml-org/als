package org.mulesoft.language.outline.structure.structureImpl.symbol.corebuilders

import amf.core.client.scala.model.domain.AmfElement
import amf.core.client.scala.model.domain.extensions.PropertyShape
import amf.core.internal.metamodel.Field
import amf.core.internal.metamodel.domain.extensions.PropertyShapeModel
import amf.shapes.client.scala.model.domain.{AnyShape, ArrayShape, NodeShape, UnionShape}
import org.mulesoft.language.outline.structure.structureImpl._
import org.mulesoft.language.outline.structure.structureImpl.symbol.builders.{
  AmfObjectSimpleBuilderCompanion,
  StructuredSymbolBuilder
}

class PropertyShapeSymbolBuilder(override val element: PropertyShape)(override implicit val ctx: StructureContext)
    extends NamedElementSymbolBuilderTrait[PropertyShape] {

  private def buildRange: Boolean = element.range match {
    case n: AnyShape if n.linkTarget.isDefined => false
    case n: NodeShape                          => n.properties.nonEmpty
    case _: ArrayShape                         => true
    case _: UnionShape                         => false
    case _                                     => false
  }
  override def ignoreFields: List[Field] =
    if (buildRange) super.ignoreFields else PropertyShapeModel.Range +: super.ignoreFields
}

object PropertyShapeSymbolBuilder extends AmfObjectSimpleBuilderCompanion[PropertyShape] {

  override def getType: Class[_ <: AmfElement] = classOf[PropertyShape]

  override val supportedIri: String = PropertyShapeModel.`type`.head.iri()

  override def construct(element: PropertyShape)(
      implicit ctx: StructureContext): Option[StructuredSymbolBuilder[PropertyShape]] =
    Some(new PropertyShapeSymbolBuilder(element))
}
