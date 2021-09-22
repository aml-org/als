package org.mulesoft.als.actions.codeactions.plugins.conversions

import amf.core.client.common.validation.ProfileNames
import amf.core.client.scala.errorhandling.AMFErrorHandler
import amf.core.client.scala.model.domain.AmfObject
import amf.shapes.client.scala.model.domain.AnyShape
import amf.shapes.internal.domain.resolution.elements.CompleteShapeTransformationPipeline
import org.mulesoft.amfintegration.LocalIgnoreErrorHandler
import org.mulesoft.amfintegration.amfconfiguration.AmfConfigurationWrapper

import scala.collection.mutable

trait AmfObjectResolver extends ShapeExtractor {

  private val eh: AMFErrorHandler = LocalIgnoreErrorHandler
  protected val amfConfiguration: AmfConfigurationWrapper

  protected def resolveShape(anyShape: AnyShape): Option[AnyShape] =
    new CompleteShapeTransformationPipeline(anyShape, eh, ProfileNames.RAML10)
      .transform(amfConfiguration.getConfiguration) match {
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
