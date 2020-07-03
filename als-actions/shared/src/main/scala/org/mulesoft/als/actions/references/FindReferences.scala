package org.mulesoft.als.actions.references

import org.mulesoft.als.actions.common.RelationshipLink
import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.lsp.feature.common.Location

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object FindReferences {
  def getReferences(uri: String,
                    position: Position,
                    references: Future[Seq[RelationshipLink]]): Future[Seq[RelationshipLink]] =
    references.map { refs =>
      refs
        .filter { t =>
          containsPosition(uri, position, t.destination.uri, t.nameRange.getOrElse(PositionRange(t.targetEntry.range))) &&
          t.source.uri.nonEmpty
        }
    }

  private def containsPosition(uri: String,
                               position: Position,
                               destinationUri: String,
                               destinationRange: PositionRange) =
    destinationUri == uri && destinationRange
      .contains(position)
}
