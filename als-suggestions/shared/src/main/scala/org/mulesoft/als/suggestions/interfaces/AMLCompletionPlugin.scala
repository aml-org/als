package org.mulesoft.als.suggestions.interfaces

import org.mulesoft.als.suggestions.{AMLCompletionParams, RawSuggestion}

import scala.concurrent.Future

trait AMLCompletionPlugin extends CompletionPlugin[AMLCompletionParams] {
  protected def emptySuggestion: Future[Seq[RawSuggestion]] = Future.successful(Seq())
}