package org.mulesoft.als.suggestions.plugins.aml.webapi.async

import amf.core.model.domain.{AmfObject, Shape}
import amf.plugins.document.webapi.parser.spec.WebApiDeclarations.ErrorOperationTrait
import amf.plugins.domain.shapes.models.AnyShape
import amf.plugins.domain.webapi.metamodel.MessageModel
import amf.plugins.domain.webapi.models.{Message, Operation, Payload, Response}
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.plugins.aml.{
  AMLStructureCompletionPlugin,
  ResolveIfApplies,
  StructureCompletionPlugin
}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Async20StructureCompletionPlugin extends StructureCompletionPlugin {

  override protected val resolvers: List[ResolveIfApplies] = List(
    ResolveShapeInPayload,
    ResolveResponses,
    ResolveTraits,
    ResolveDefault
  )

  object ResolveShapeInPayload extends ResolveIfApplies {
    private def isInPayload(branch: Seq[AmfObject]): Boolean = {
      branch.headOption match {
        case Some(_: Payload) => true
        case _                => false
      }
    }

    override def resolve(request: AmlCompletionRequest): Option[Future[Seq[RawSuggestion]]] =
      request.amfObject match {
        case _: Shape if isInPayload(request.branchStack) =>
          applies(emptySuggestion)
        case _ =>
          notApply
      }
  }

  object ResolveResponses extends ResolveIfApplies {
    override def resolve(request: AmlCompletionRequest): Option[Future[Seq[RawSuggestion]]] =
      request.amfObject match {
        case r: Response if request.fieldEntry.map(_.field).contains(MessageModel.Headers) =>
          applies(
            Future.successful(Async20TypeFacetsCompletionPlugin
              .resolveShape(AnyShape(r.fields, r.annotations), Nil)))
        case _: Response if !MessageKnowledge.isRootMessageBlock(request) =>
          applies(emptySuggestion)
        case _ => notApply
      }
  }

  object ResolveTraits extends ResolveIfApplies {
    override def resolve(request: AmlCompletionRequest): Option[Future[Seq[RawSuggestion]]] =
      request.amfObject match {
        case o: Operation if o.isAbstract.option().contains(true) =>
          applies(
            AMLStructureCompletionPlugin
              .resolve(request)
              .map(_.filterNot(rs => rs.newText == "traits" || rs.newText == "message")))
        case m: Message if m.isAbstract.option().contains(true) =>
          applies(
            defaultStructure(request)
              .map(_.filterNot(rs => rs.newText == "traits" || rs.newText == "payload")))
        case _: ErrorOperationTrait =>
          applies(emptySuggestion)
        case _ => notApply
      }
  }
}
