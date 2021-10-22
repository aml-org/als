package org.mulesoft.als.suggestions.plugins.aml.webapi.raml

import amf.core.client.common.validation.ProfileNames
import amf.core.client.scala.model.domain.Shape
import amf.shapes.client.scala.model.domain.{NodeShape, ScalarShape}
import amf.shapes.internal.domain.metamodel.NodeShapeModel
import amf.shapes.internal.domain.resolution.elements.CompleteShapeTransformationPipeline
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.amfintegration.LocalIgnoreErrorHandler
import org.mulesoft.amfintegration.amfconfiguration.AmfConfigurationWrapper

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object NodeShapeDiscriminatorProperty extends AMLCompletionPlugin {
  override def id: String = "NodeShapeDiscriminatorProperty"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    Future {
      request.amfObject match {
        case node: NodeShape if request.fieldEntry.exists(t => t.field == NodeShapeModel.Discriminator) =>
          nodeProperties(node, request.amfConfiguration).map(RawSuggestion.apply(_, isAKey = false))
        case _ => Nil
      }
    }
  }

  private def nodeProperties(n: NodeShape, amfConfiguration: AmfConfigurationWrapper): Seq[String] = {
    resolve(n, amfConfiguration) match {
      case node: NodeShape => node.properties.filter(_.range.isInstanceOf[ScalarShape]).flatMap(_.name.option())
      case _               => Nil
    }
  }
  private def resolve(s: Shape, amfConfiguration: AmfConfigurationWrapper) =
    new CompleteShapeTransformationPipeline(s, LocalIgnoreErrorHandler, ProfileNames.RAML10)
      .transform(amfConfiguration.getConfiguration)
}
