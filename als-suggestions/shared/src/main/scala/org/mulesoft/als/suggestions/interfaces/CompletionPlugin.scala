package org.mulesoft.als.suggestions.interfaces

import amf.core.model.document.BaseUnit
import amf.core.model.domain.AmfObject
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.lsp.edit.TextEdit

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

trait CompletionParams {
  val currentBaseUnit: BaseUnit
  val node: AmfObject
  val position: Position
  val prefix: String
}

trait RawSuggestion {
  def newText: String

  def displayText: String

  def description: String

  def textEdits: Seq[TextEdit]

  def whiteSpacesEnding: String
}