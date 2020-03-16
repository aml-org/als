package org.mulesoft.als.actions.references

import amf.core.model.document.BaseUnit
import org.mulesoft.als.common.dtoTypes.{PositionRange, Position}
import org.mulesoft.lsp.feature.common.Location

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object FindReferences {
  def getReferences(bu: BaseUnit,
                    uri: String,
                    position: Position,
                    references: Future[Seq[(Location, Location)]]): Future[Seq[Location]] =
    references.map { refs =>
      refs
        .filter(
          t =>
            t._2.uri == uri && PositionRange(t._2.range)
              .contains(position) &&
              t._1.uri.nonEmpty)
        .map(_._1)
    }
}
