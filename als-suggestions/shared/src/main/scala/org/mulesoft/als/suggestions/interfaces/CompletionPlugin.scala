package org.mulesoft.als.suggestions.interfaces

import org.mulesoft.als.suggestions.{CompletionParams, RawSuggestion}

import scala.concurrent.Future

trait CompletionPlugin {
  def id: String
  def resolve(params: CompletionParams): Future[Seq[RawSuggestion]]

  override def equals(obj: Any): Boolean = obj match {
    case other: CompletionPlugin => other.id == id
    case _                       => false
  }

  override def hashCode(): Int = id.hashCode()
}
