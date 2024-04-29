package org.mulesoft.als.suggestions.plugins.aml.webapi.oas.oas20.structure

import amf.aml.client.scala.model.domain.NodeMapping
import amf.apicontract.client.scala.model.domain.Request
import amf.apicontract.internal.metamodel.domain.OperationModel
import org.mulesoft.als.common.{ASTPartBranch, YPartBranch}
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.ResolveIfApplies
import org.mulesoft.als.suggestions.plugins.aml.NodeMappingWrapper

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object ResolveRequest extends ResolveIfApplies with ParameterKnowledge {
  override def resolve(request: AmlCompletionRequest): Option[Future[Seq[RawSuggestion]]] =
    request.amfObject match {
      case _: Request =>
        if (isInParameter(request.astPartBranch))
          applies(Future.successful(Seq()))
        else if (request.fieldEntry.isEmpty && !definingParam(request.astPartBranch))
          applies(Future {
            request.actualDialect.declares
              .collect({ case n: NodeMapping => n })
              .find(_.nodetypeMapping.option().contains(OperationModel.`type`.head.iri()))
              .map(_.propertiesRaw(fromDialect = request.actualDialect))
              .getOrElse(Nil)
          })
        else notApply
      case _ => notApply
    }

  private def definingParam(astPart: ASTPartBranch): Boolean = astPart.isKeyDescendantOf("parameters")
}
