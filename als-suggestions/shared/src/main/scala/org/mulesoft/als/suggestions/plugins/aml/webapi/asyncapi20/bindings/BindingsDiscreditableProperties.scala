package org.mulesoft.als.suggestions.plugins.aml.webapi.asyncapi20.bindings

import amf.core.annotations.SynthesizedField
import amf.plugins.domain.webapi.metamodel.bindings.Amqp091ChannelBindingModel
import amf.plugins.domain.webapi.models.bindings.amqp.Amqp091ChannelBinding
import org.mulesoft.als.common.YPartBranch
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object BindingsDiscreditableProperties extends AMLCompletionPlugin {
  override def id: String = "BindingsDiscreditableProperties"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    Future {
      (request.amfObject match {
        case amqp: Amqp091ChannelBinding if notSynthesizedIs(amqp) => amqp.is.option().flatMap(amqpIsMap.get)
        case _                                                     => None
      }).toSeq
    }
  }

  private def notSynthesizedIs(amqp: Amqp091ChannelBinding) =
    !amqp.fields
      .getValueAsOption(Amqp091ChannelBindingModel.Is)
      .exists(_.annotations.contains(classOf[SynthesizedField]))
  private val amqpIsMap = Map(
    "queue"      -> RawSuggestion.forObject("queue", "AMQP", mandatory = true),
    "routingKey" -> RawSuggestion.forObject("exchange", "AMQP", mandatory = true)
  )
}
