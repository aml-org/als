package org.mulesoft.als.suggestions.plugins.aml

import amf.core.model.document.BaseUnit
import org.mulesoft.als.common.dtoTypes.Position

trait AMLSuggestionsHelper {

  // TODO: remove or separate indentation from ALS
  //  If not removed, clean up and use AST
  def getIndentation(bu: BaseUnit, position: Position): String =
    bu.raw
      .flatMap(text => {
        val left = text.substring(0, position.offset(text))
        val line = left.substring(left.lastIndexOf("\n")).stripPrefix("\n")
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
}
