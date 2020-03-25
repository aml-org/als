package org.mulesoft.als.suggestions.plugins.aml.webapi.async

import amf.core.model.domain.{AmfObject, Shape}
import amf.plugins.document.webapi.parser.spec.WebApiDeclarations.ErrorOperationTrait
import amf.plugins.domain.shapes.models.AnyShape
import amf.plugins.domain.webapi.metamodel.MessageModel
import amf.plugins.domain.webapi.models.{Message, Operation, Payload, Response}
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.AMLStructureCompletionPlugin

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object Async20StructureCompletionPlugin extends AMLCompletionPlugin {
  override def id: String = "AMLStructureCompletionPlugin"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] =
    request.amfObject match {
      case r: Response if request.fieldEntry.map(_.field).contains(MessageModel.Headers) =>
        Future.successful(
          Async20TypeFacetsCompletionPlugin
            .resolveShape(AnyShape(r.fields, r.annotations), Nil))
      case o: Operation if o.isAbstract.option().contains(true) =>
        AMLStructureCompletionPlugin
          .resolve(request)
          .map(_.filterNot(rs => rs.newText == "traits" || rs.newText == "message"))
      case m: Message if m.isAbstract.option().contains(true) =>
        AMLStructureCompletionPlugin
          .resolve(request)
          .map(_.filterNot(rs => rs.newText == "traits" || rs.newText == "payload"))
      case _: Response if !MessageKnowledge.isRootMessageBlock(request) =>
        emptySuggestion
      case _: ErrorOperationTrait =>
        emptySuggestion
      case _: Shape =>
        emptySuggestion
      case _ =>
        AMLStructureCompletionPlugin.resolve(request)
    }

}
