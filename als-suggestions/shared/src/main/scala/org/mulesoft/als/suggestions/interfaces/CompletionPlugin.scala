package org.mulesoft.als.suggestions.interfaces

import amf.core.annotations.SourceAST
import amf.core.model.document.BaseUnit
import amf.core.model.domain.AmfObject
import amf.core.parser.FieldEntry
import amf.plugins.document.vocabularies.model.document.Dialect
import amf.plugins.document.vocabularies.model.domain.PropertyMapping
import org.mulesoft.als.common.{NodeBranchBuilder, YPartBranch}
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.suggestions.DeclarationProvider
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
  val propertyMappings: Seq[PropertyMapping]
  val position: Position
  val prefix: String
  val fieldEntry: Option[FieldEntry]
  val actualDialect: Dialect
  lazy val declarationProvider: DeclarationProvider = DeclarationProvider(currentBaseUnit, Some(actualDialect))
  lazy val yPartBranch: Option[YPartBranch] =
    currentBaseUnit.annotations.find(classOf[SourceAST]).map(a => NodeBranchBuilder.build(a.ast, position))
  val amfObject: AmfObject
}

trait RawSuggestion {
  def newText: String

  def displayText: String

  def description: String

  def textEdits: Seq[TextEdit]

  def whiteSpacesEnding: String
}

object RawSuggestion {
  def apply(value: String): RawSuggestion = {
    new RawSuggestion {
      override def newText: String = value

      override def displayText: String = value

      override def description: String = value

      override def textEdits: Seq[TextEdit] = Seq()

      override def whiteSpacesEnding: String = ""
    }
  }

  def apply(value: String, ws: String): RawSuggestion = {
    new RawSuggestion {
      override def newText: String = value

      override def displayText: String = value

      override def description: String = value

      override def textEdits: Seq[TextEdit] = Seq()

      override def whiteSpacesEnding: String = ws
    }
  }
}
