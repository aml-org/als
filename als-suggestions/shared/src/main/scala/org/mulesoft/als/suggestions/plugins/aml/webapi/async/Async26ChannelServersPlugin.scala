package org.mulesoft.als.suggestions.plugins.aml.webapi.async

import amf.apicontract.client.scala.model.domain.{EndPoint, Server}
import amf.core.client.scala.model.document.{BaseUnit, DeclaresModel}
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object Async26ChannelServersPlugin extends AMLCompletionPlugin {
  override def id: String = "Async26ChannelServersPlugin"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = Future {
    request.amfObject match {
      case _: Server if request.branchStack.headOption.exists(_.isInstanceOf[EndPoint]) =>
        getDeclaredServers(request.baseUnit)
          .map(server => RawSuggestion(server, isAKey = false, "Servers", mandatory = false))
      case _ => Seq.empty
    }
  }

  private def getDeclaredServers(baseUnit: BaseUnit): Seq[String] =
    baseUnit match {
      case d: DeclaresModel =>
        d.declares.collect { case s: Server =>
          s.name.value()
        }
      case _ => Seq.empty
    }
}
