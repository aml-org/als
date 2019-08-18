package org.mulesoft.als.suggestions.interfaces

import amf.core.model.document.BaseUnit
import amf.core.model.domain.AmfObject
import amf.plugins.document.vocabularies.model.document.Dialect
import amf.plugins.document.vocabularies.model.domain.NodeMapping
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest

import scala.concurrent.Future

trait AMLCompletionPlugin extends CompletionPlugin[AmlCompletionRequest] {
  protected def emptySuggestion: Future[Seq[RawSuggestion]] = Future.successful(Seq())

  // TODO: remove or separate indentation from ALS
  //  If not removed, clean up and use AST
  def getIndentation(bu: BaseUnit, position: Position): String =
    bu.raw
      .flatMap(text => {
        val pos  = position.moveLine(-1)
        val left = text.substring(0, pos.offset(text))
        val line = if (left.contains("\n")) left.substring(left.lastIndexOf("\n")).stripPrefix("\n") else left
        val first = line.headOption match {
          case Some(c) if c == ' ' || c == '\t' => Some(c)
          case _                                => None
        }
        first.map(f => {
          val spaces = line.substring(0, line.takeWhile(_ == f).length)
          if (f == '\t') s"$spaces\t"
          else s"$spaces  "
        })
      })
      .getOrElse("  ")

  protected def isEncodes(amfObject: AmfObject, dialect: Dialect) = {
    val iri = amfObject.meta.`type`.head.iri()

    dialect.declares
      .find(nm => dialect.documents().root().encoded().option().contains(nm.id))
      .collectFirst({ case d: NodeMapping if d.nodetypeMapping.option().contains(iri) => d })
      .isDefined
  }
}
