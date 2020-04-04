package org.mulesoft.als.actions.references

import amf.core.model.document.BaseUnit
import org.mulesoft.als.actions.common.RelationshipLink
import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.lsp.feature.common.Location

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object FindReferences {
  def getReferences(bu: BaseUnit,
                    uri: String,
                    position: Position,
                    references: Future[Seq[RelationshipLink]]): Future[Seq[Location]] =
    references.map { refs =>
      refs
        .filter { t =>
          containsPosition(uri, position, t.parentEntry.getOrElse(t.destination)) &&
          t.source.uri.nonEmpty
        }
        .map(_.source)
    }

  private def containsPosition(uri: String, position: Position, destination: Location) = {
    destination.uri == uri && PositionRange(destination.range)
      .contains(position)
  }
}
