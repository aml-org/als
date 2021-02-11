package org.mulesoft.als.actions.codeactions.plugins.conversions

import amf.ProfileNames
import amf.core.errorhandling.ErrorHandler
import amf.core.model.domain.AmfObject
import amf.plugins.domain.shapes.models.AnyShape
import amf.plugins.domain.shapes.resolution.stages.elements.CompleteShapeTransformationPipeline
import org.mulesoft.als.actions.codeactions.plugins.declarations.common.BaseElementDeclarableExtractors
import org.mulesoft.amfintegration.LocalIgnoreErrorHandler

import scala.collection.mutable

trait AmfObjectResolver extends ShapeExtractor {

  private val eh: ErrorHandler = LocalIgnoreErrorHandler

  protected def resolveShape(anyShape: AnyShape): Option[AnyShape] =
    new CompleteShapeTransformationPipeline(anyShape, eh, ProfileNames.RAML).resolve() match {
      case a: AnyShape => Some(a)
      case _           => None
    }

  // We wouldn't want to override amfObject as a whole as it's used for range comparisons and such
  protected lazy val resolvedAmfObject: Option[AmfObject] = amfObject match {
    case Some(shape: AnyShape) =>
      resolveShape(shape.cloneElement(mutable.Map.empty).asInstanceOf[AnyShape])
    case e => e
  }
  lazy val maybeAnyShape: Option[AnyShape] = extractShapeFromAmfObject(resolvedAmfObject)

  def extractShapeFromAmfObject(obj: Option[AmfObject]): Option[AnyShape] = {
    obj.flatMap {
      case s: AnyShape => Some(s)
      case _           => None
    }
  }
}
