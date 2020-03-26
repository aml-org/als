package org.mulesoft.als.suggestions.plugins.aml.webapi.oas

import amf.core.annotations.DeclaredElement
import amf.core.model.domain.Shape
import amf.dialects.oas.nodes.{AMLInfoObject, Oas20ResponseObject}
import amf.plugins.domain.webapi.models._
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.plugins.aml._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object OasStructurePlugin extends StructureCompletionPlugin {
  override protected val resolvers: List[ResolveIfApplies] = List(
    ResolveParameterShapes,
    ResolveDeclaredResponse,
    ResolveTag,
    ResolveInfo,
    ResolveDefault
  )
}

object ResolveParameterShapes extends ResolveIfApplies {
  override def resolve(request: AmlCompletionRequest): Option[Future[Seq[RawSuggestion]]] =
    request.amfObject match {
      case _: Parameter | _: Shape => applies(Future.successful(Seq()))
      case _                       => notApply
    }
}

object ResolveDeclaredResponse extends ResolveIfApplies {
  override def resolve(request: AmlCompletionRequest): Option[Future[Seq[RawSuggestion]]] =
    request.amfObject match {
      case r: Response if r.annotations.contains(classOf[DeclaredElement]) && request.fieldEntry.isEmpty =>
        applies(declaredResponse(request))
      case _ => notApply
    }

  private def declaredResponse(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = Future {
    new AMLStructureCompletionsPlugin(
      request.propertyMapping.filter(_.id != Oas20ResponseObject.statusCodeProperty.id))
      .resolve(request.amfObject.meta.`type`.head.iri())
  }
}

object ResolveTag extends ResolveIfApplies {
  override def resolve(request: AmlCompletionRequest): Option[Future[Seq[RawSuggestion]]] =
    request.amfObject match {
      case _: Tag if request.branchStack.headOption.exists(_.isInstanceOf[Operation]) =>
        applies(Future.successful(Seq()))
      case _ => notApply
    }
}

object ResolveInfo extends ResolveIfApplies {
  override def resolve(request: AmlCompletionRequest): Option[Future[Seq[RawSuggestion]]] =
    request.amfObject match {
      case _: WebApi if request.yPartBranch.isKeyDescendantOf("info") =>
        applies(infoSuggestions())
      case _ => notApply
    }

  private def infoSuggestions() =
    Future(AMLInfoObject.Obj.propertiesRaw(Some("docs")))
}
