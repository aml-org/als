package org.mulesoft.als.suggestions.plugins.aml.webapi.async.bindings

import amf.apicontract.client.scala.model.domain.bindings.amqp.Amqp091ChannelBinding
import amf.apicontract.internal.metamodel.domain.bindings.Amqp091ChannelBindingModel
import amf.core.internal.annotations.SynthesizedField
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
