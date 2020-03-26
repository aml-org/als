package org.mulesoft.als.suggestions.plugins.aml.webapi.oas.oas20

import amf.plugins.document.vocabularies.model.domain.NodeMapping
import amf.plugins.domain.webapi.metamodel.OperationModel
import amf.plugins.domain.webapi.models.{EndPoint, Request}
import org.mulesoft.als.common.YPartBranch
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.plugins.aml._
import org.mulesoft.als.suggestions.plugins.aml.webapi.oas.{
  ResolveDeclaredResponse,
  ResolveInfo,
  ResolveParameterShapes,
  ResolveTag
}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Oas20StructurePlugin extends StructureCompletionPlugin {
  override protected val resolvers: List[ResolveIfApplies] = List(
    ResolveParameterShapes,
    ResolveParameterEndpoint,
    ResolveRequest,
    ResolveDeclaredResponse,
    ResolveTag,
    ResolveInfo,
    ResolveDefault
  )

  object ResolveParameterEndpoint extends ResolveIfApplies {
    override def resolve(request: AmlCompletionRequest): Option[Future[Seq[RawSuggestion]]] =
      request.amfObject match {
        case _: EndPoint if isInParameter(request.yPartBranch) =>
          applies(emptySuggestion)
        case _ => notApply
      }
  }

  object ResolveRequest extends ResolveIfApplies {
    override def resolve(request: AmlCompletionRequest): Option[Future[Seq[RawSuggestion]]] =
      request.amfObject match {
        case _: Request =>
          if (isInParameter(request.yPartBranch))
            applies(emptySuggestion)
          else if (request.fieldEntry.isEmpty && !definingParam(request.yPartBranch))
            applies(Future {
              request.actualDialect.declares
                .collect({ case n: NodeMapping => n })
                .find(_.nodetypeMapping.option().contains(OperationModel.`type`.head.iri()))
                .map(_.propertiesRaw())
                .getOrElse(Nil)
            })
          else notApply
        case _ => notApply
      }

    private def definingParam(yPart: YPartBranch): Boolean = yPart.isKeyDescendantOf("parameters")
  }

  private def isInParameter(yPartBranch: YPartBranch): Boolean =
    yPartBranch.isKeyDescendantOf("parameters") || (yPartBranch.isJson && yPartBranch.isInArray && yPartBranch
      .parentEntryIs("parameters"))
}
