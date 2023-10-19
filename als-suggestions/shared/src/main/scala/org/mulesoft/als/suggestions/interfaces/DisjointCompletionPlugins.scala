package org.mulesoft.als.suggestions.interfaces

import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest

import scala.concurrent.Future
trait DisjointCompletionPlugins extends AMLCompletionPlugin {
  protected val resolvers: List[ResolveIfApplies]

  override final def resolve(request: AmlCompletionRequest): Future[Seq[RawSuggestion]] =
    lookForResolver(resolvers, request)

  @scala.annotation.tailrec
  private def lookForResolver(res: List[ResolveIfApplies], request: AmlCompletionRequest): Future[Seq[RawSuggestion]] =
    res match {
      case head :: tail =>
        head.resolve(request) match {
          case Some(rs) =>
            rs
          case _ =>
            lookForResolver(tail, request)
        }
      case Nil =>
        emptySuggestion
    }
}

trait ResolveIfApplies {
  def resolve(request: AmlCompletionRequest): Option[Future[Seq[RawSuggestion]]]

  protected val notApply: Option[Future[Seq[RawSuggestion]]] = None

  protected def applies(response: Future[Seq[RawSuggestion]]): Option[Future[Seq[RawSuggestion]]] =
    Some(response)
}
