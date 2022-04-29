package org.mulesoft.als.suggestions.plugins.aml.metadialect

import amf.aml.client.scala.model.document.Dialect
import amf.aml.client.scala.model.domain.External
import amf.core.client.scala.model.document.BaseUnit
import amf.core.internal.annotations.Aliases
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object NamespaceForTermCompletionPlugin extends AMLCompletionPlugin {
  override def id: String = "NamespaceForTermCompletionPlugin"

  override def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] =
    if (applies(request))
      Future {
        (externals(request.baseUnit) ++ references(request.baseUnit)).map(RawSuggestion(_, isAKey = false))
      }
    else emptySuggestion

  private def applies(request: AmlCompletionRequest) =
    isTerm(request.amfObject, request.yPartBranch)

  private def externals(bu: BaseUnit): Seq[String] =
    bu match {
      case d: Dialect => d.externals.collect({ case e: External => e.alias.option() }).flatten
      case _          => Nil
    }

  private def references(bu: BaseUnit): Seq[String] =
    bu.annotations.find(classOf[Aliases]) match {
      case Some(aliases) => aliases.aliases.map(_._1).toSeq
      case _             => Nil
    }

}
