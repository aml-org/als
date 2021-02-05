package org.mulesoft.als.actions.codeactions.plugins.conversions

import amf.ProfileNames
import amf.core.errorhandling.ErrorHandler
import amf.core.metamodel.document.FragmentModel
import amf.core.model.domain.{AmfObject, Shape}
import amf.plugins.document.webapi.annotations.{ParsedJSONSchema, SchemaIsJsonSchema}
import amf.plugins.domain.shapes.models.AnyShape
import amf.plugins.domain.shapes.resolution.stages.elements.CompleteShapeTransformationPipeline
import org.mulesoft.als.actions.codeactions.plugins.declarations.common.{
  BaseElementDeclarableExtractors,
  FileExtractor
}
import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.amfintegration.AmfImplicits.AmfAnnotationsImp
import org.mulesoft.amfintegration.LocalIgnoreErrorHandler

import scala.collection.mutable

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
