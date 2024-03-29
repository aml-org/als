package org.mulesoft.als.suggestions.interfaces

import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest

import scala.concurrent.Future

trait CompletionPlugin[T] {
  def id: String
  def resolve(request: T): Future[Seq[RawSuggestion]]

  override def equals(obj: Any): Boolean = obj match {
    case other: CompletionPlugin[T] => other.id == id
    case _                          => false
  }

  override def hashCode(): Int = id.hashCode()
}
