package org.mulesoft.als.suggestions.plugins.aml.webapi.oas.oas20.structure

import amf.plugins.document.vocabularies.model.domain.NodeMapping
import amf.plugins.domain.webapi.metamodel.OperationModel
import amf.plugins.domain.webapi.models.{EndPoint, Request}
import org.mulesoft.als.common.YPartBranch
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.ResolveIfApplies
import org.mulesoft.als.suggestions.plugins.aml._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object ResolveRequest extends ResolveIfApplies with ParameterKnowledge {
  override def resolve(request: AmlCompletionRequest): Option[Future[Seq[RawSuggestion]]] =
    request.amfObject match {
      case _: Request =>
        if (isInParameter(request.yPartBranch))
          applies(Future.successful(Seq()))
        else if (request.fieldEntry.isEmpty && !definingParam(request.yPartBranch))
          applies(Future {
            request.actualDialect.declares
              .collect({ case n: NodeMapping => n })
              .find(_.nodetypeMapping.option().contains(OperationModel.`type`.head.iri()))
              .map(_.propertiesRaw(d = request.actualDialect))
              .getOrElse(Nil)
          })
        else notApply
      case _ => notApply
    }

  private def definingParam(yPart: YPartBranch): Boolean = yPart.isKeyDescendantOf("parameters")
}
