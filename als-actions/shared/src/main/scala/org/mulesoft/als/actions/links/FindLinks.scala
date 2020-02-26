package org.mulesoft.als.actions.links

import amf.core.annotations.{Aliases, ReferenceTargets}
import amf.core.model.document.BaseUnit
import amf.core.remote.Platform
import org.mulesoft.als.common.dtoTypes.PositionRange
import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.lsp.feature.common.{Location, Position}
import org.mulesoft.lsp.feature.link.DocumentLink

object FindLinks {
  def getLinks(bu: BaseUnit): Seq[DocumentLink] =
    bu.annotations
      .collect { case rt: ReferenceTargets => rt }
      .map { rt =>
        DocumentLink(LspRangeConverter.toLspRange(PositionRange(rt.originRange)), rt.targetLocation, None)
      }

  // what information do I have available?
  // should I map label/alias to URIs?
  def getAliases(bu: BaseUnit): Seq[(Location, String)] =
    bu.annotations
      .collect { case a: Aliases => a }
      .flatMap { a =>
        a.aliases.map(
          alias =>
            (Location(bu.location().getOrElse(bu.id),
                      org.mulesoft.lsp.feature.common.Range(Position(0, 0), Position(0, 0))),
             alias._2._1)) // TODO: FIX ME
      }
}
