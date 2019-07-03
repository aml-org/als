package org.mulesoft.als.suggestions.interfaces

import scala.concurrent.Future

trait CompletionProvider {
  def suggest(): Future[Seq[Suggestion]]
}
