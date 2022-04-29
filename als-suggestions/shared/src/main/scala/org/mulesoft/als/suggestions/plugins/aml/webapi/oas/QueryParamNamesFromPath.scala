package org.mulesoft.als.suggestions.plugins.aml.webapi.oas

import amf.apicontract.client.scala.model.domain.{EndPoint, Parameter}
import amf.core.client.scala.model.domain.AmfObject
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object QueryParamNamesFromPath extends AMLCompletionPlugin {
  override def id: String = "QueryParamNamesFromPath"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    Future {
      (request.amfObject match {
        case parameter: Parameter => Some(parameter)
        case _                    => None
      }).map(parameter =>
        if (
          parameter.binding
            .option()
            .contains("query") && request.yPartBranch
            .parentEntryIs("name") && request.yPartBranch.isValue
        )
          getQueryParams(request.branchStack).map(RawSuggestion(_, isAKey = false))
        else Nil
      ).getOrElse(Nil)
    }
  }

  private def getQueryParams(stack: Seq[AmfObject]): Seq[String] = {
    stack
      .collectFirst({ case e: EndPoint => e })
      .flatMap(_.path.option()) match {
      case Some(path) =>
        path
          .split('?')
          .lastOption
          .map { params =>
            params
              .split('&')
              .flatMap(pv => {
                pv.split('=').headOption
              })
              .toSeq
          }
          .getOrElse(Nil)
      case _ => Nil
    }
  }
}
