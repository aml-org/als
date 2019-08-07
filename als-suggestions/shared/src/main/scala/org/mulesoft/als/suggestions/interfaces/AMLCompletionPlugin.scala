package org.mulesoft.als.suggestions.interfaces

import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest

import scala.concurrent.Future

trait AMLCompletionPlugin extends CompletionPlugin[AmlCompletionRequest]{
  protected def emptySuggestion: Future[Seq[RawSuggestion]] = Future.successful(Seq())

}