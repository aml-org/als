package org.mulesoft.als.suggestions.plugins.aml.webapi.async.structure

import amf.core.model.domain.Linkable
import amf.plugins.document.webapi.parser.spec.WebApiDeclarations.ErrorOperationTrait
import amf.plugins.domain.webapi.models.{Message, Operation, Response}
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.ResolveIfApplies
import org.mulesoft.als.suggestions.plugins.aml.ResolveDefault
import org.mulesoft.als.suggestions.plugins.aml.webapi.async.MessageKnowledge

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object ResolveTraits extends ResolveIfApplies {
  override def resolve(request: AmlCompletionRequest): Option[Future[Seq[RawSuggestion]]] =
    request.amfObject match {
      case o: Operation if o.isAbstract.option().contains(true) =>
        if (!request.yPartBranch.isInArray)
          ResolveDefault
            .resolve(request)
            .map(_.map(_.filterNot(rs => rs.newText == "traits" || rs.newText == "message")))
        else applies(Future.successful(Seq()))
      case m: Message if m.isAbstract.option().contains(true) =>
        if (!request.yPartBranch.isInArray)
          ResolveDefault
            .resolve(request)
            .map(_.map(_.filterNot(rs => rs.newText == "traits" || rs.newText == "payload")))
        else applies(Future.successful(Seq()))
      case l: Linkable if l.linkTarget.exists(_.isInstanceOf[ErrorOperationTrait]) => applies(Future(Seq()))
      case _: ErrorOperationTrait =>
        applies(Future(Seq()))
      case _ => notApply
    }
}
