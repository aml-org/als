package org.mulesoft.als.suggestions.plugins.aml.webapi.async

import amf.apicontract.client.scala.model.domain.api.AsyncApi
import amf.apicontract.client.scala.model.domain.{EndPoint, Server}
import amf.core.client.scala.model.document.{BaseUnit, EncodesModel}
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Async26ChannelServersPlugin extends AMLCompletionPlugin {
  override def id: String = "Async26ChannelServersPlugin"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = Future {
    request.amfObject match {
      case _: Server if request.branchStack.headOption.exists(_.isInstanceOf[EndPoint]) =>
        getRootServers(request.baseUnit)
          .map(server => RawSuggestion(server, isAKey = false, "Servers", mandatory = false))
      case _ => Seq.empty
    }
  }

  private def getRootServers(baseUnit: BaseUnit): Seq[String] = (for {
    encodes <- baseUnit match {
      case e: EncodesModel => Some(e)
      case _               => None
    }
    asyncApi <- encodes.encodes match {
      case async: AsyncApi => Some(async)
      case _               => None
    }
  } yield {
    asyncApi.servers.map(_.name).flatMap(_.option())
  }).getOrElse(Seq.empty)
}
