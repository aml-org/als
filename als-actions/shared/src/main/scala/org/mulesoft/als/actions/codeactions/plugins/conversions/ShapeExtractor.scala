package org.mulesoft.als.actions.codeactions.plugins.conversions

import amf.core.client.scala.model.domain.{AmfObject, Shape}
import amf.shapes.client.scala.model.domain.AnyShape
import amf.shapes.internal.annotations.{ParsedJSONSchema, SchemaIsJsonSchema}
import org.mulesoft.als.actions.codeactions.plugins.declarations.common.BaseElementDeclarableExtractors
import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.amfintegration.AmfImplicits.AmfAnnotationsImp

trait ShapeExtractor extends BaseElementDeclarableExtractors {

  def isJsonSchemaShape(obj: AmfObject): Boolean = {
    obj match {
      case s: AnyShape if isInlinedJsonSchema(s) => true
      case _                                     => false
    }
  }

  def containsPosition(obj: AmfObject, position: Option[Position]): Boolean =
    obj.annotations
      .lexicalInformation()
      .map(l => PositionRange(l.range))
      .exists(r => position.exists(r.contains))

  def isInlinedJsonSchema(shape: Shape): Boolean =
    shape.annotations.find(ann => ann.isInstanceOf[ParsedJSONSchema] || ann.isInstanceOf[SchemaIsJsonSchema]).isDefined

}
