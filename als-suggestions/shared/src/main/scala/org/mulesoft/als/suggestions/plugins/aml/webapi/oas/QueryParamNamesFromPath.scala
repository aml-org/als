package org.mulesoft.als.suggestions.plugins.aml.webapi.oas

import amf.core.model.domain.{AmfObject, Shape}
import amf.plugins.domain.webapi.models.{EndPoint, Parameter}
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin

import scala.concurrent.{ExecutionContext, Future}

object QueryParamNamesFromPath extends AMLCompletionPlugin {
  override def id: String = "QueryParamNamesFromPath"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] = {
    Future {
      if (request.amfObject.isInstanceOf[Shape]) {
        request.branchStack.headOption match {
          case Some(p: Parameter)
              if p.binding
                .option()
                .contains("query") && request.yPartBranch.parentEntryIs("name") && request.yPartBranch.isValue =>
            getQueryParams(request.branchStack).map(RawSuggestion(_, isAKey = false))
          case _ => Nil
        }
      } else Nil
    }(ExecutionContext.Implicits.global)
  }

  private def getQueryParams(stack: Seq[AmfObject]): Seq[String] = {
    stack.collectFirst({ case e: EndPoint => e }).flatMap(_.path.option()) match {
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
