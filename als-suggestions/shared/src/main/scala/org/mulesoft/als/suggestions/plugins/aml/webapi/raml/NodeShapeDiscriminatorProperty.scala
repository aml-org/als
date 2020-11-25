package org.mulesoft.als.suggestions.plugins.aml.webapi.raml

import amf.ProfileNames
import amf.core.model.domain.Shape
import amf.plugins.domain.shapes.metamodel.NodeShapeModel
import amf.plugins.domain.shapes.models.{NodeShape, ScalarShape}
import amf.plugins.domain.shapes.resolution.stages.elements.CompleteShapeTransformationPipeline
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.amfintegration.LocalIgnoreErrorHandler

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object NodeShapeDiscriminatorProperty extends AMLCompletionPlugin {
  override def id: String = "NodeShapeDiscriminatorProperty"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    Future {
      request.amfObject match {
        case node: NodeShape if request.fieldEntry.exists(t => t.field == NodeShapeModel.Discriminator) =>
          nodeProperties(node).map(RawSuggestion.apply(_, isAKey = false))
        case _ => Nil
      }
    }
  }

  private def nodeProperties(n: NodeShape): Seq[String] = {
    resolve(n) match {
      case node: NodeShape => node.properties.filter(_.range.isInstanceOf[ScalarShape]).flatMap(_.name.option())
      case _               => Nil
    }
  }
  private def resolve(s: Shape) =
    new CompleteShapeTransformationPipeline(s, LocalIgnoreErrorHandler, ProfileNames.RAML).resolve()
}
