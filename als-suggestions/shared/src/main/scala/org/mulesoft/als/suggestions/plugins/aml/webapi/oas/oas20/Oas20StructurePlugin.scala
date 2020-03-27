package org.mulesoft.als.suggestions.plugins.aml.webapi.oas.oas20

import amf.core.model.domain.Shape
import amf.dialects.oas.nodes.AMLInfoObject
import amf.plugins.document.vocabularies.model.domain.NodeMapping
import amf.plugins.domain.webapi.metamodel.OperationModel
import amf.plugins.domain.webapi.models.{EndPoint, Parameter, Request, WebApi}
import org.mulesoft.als.common.YPartBranch
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.webapi.oas.OasStructurePlugin
import org.mulesoft.als.suggestions.plugins.aml.{AMLStructureCompletionPlugin, _}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Oas20StructurePlugin extends AMLCompletionPlugin {
  override def id: String = "AMLStructureCompletionPlugin"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    request.amfObject match {
      case _: Parameter | _: Shape                           => emptySuggestion
      case _: EndPoint if isInParameter(request.yPartBranch) => emptySuggestion
      case _: Request if isInParameter(request.yPartBranch)  => emptySuggestion
      case _: Request if request.fieldEntry.isEmpty && !definingParam(request.yPartBranch) =>
        Future {
          request.actualDialect.declares
            .collect({ case n: NodeMapping => n })
            .find(_.nodetypeMapping.option().contains(OperationModel.`type`.head.iri()))
            .map(_.propertiesRaw())
            .getOrElse(Nil)
        }
      case _ => OasStructurePlugin.resolve(request)
    }
  }

  def isInParameter(yPartBranch: YPartBranch): Boolean =
    yPartBranch.isKeyDescendantOf("parameters") || (yPartBranch.isJson && yPartBranch.isInArray && yPartBranch
      .parentEntryIs("parameters"))

  def definingParam(yPart: YPartBranch): Boolean = yPart.isKeyDescendantOf("parameters")
}
