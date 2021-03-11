package org.mulesoft.als.actions.definition

import org.mulesoft.als.common.cache.YPartBranchCached
import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.amfintegration.relationships.{AliasInfo, AliasRelationships, RelationshipLink}
import org.mulesoft.lsp.feature.common.{Location, LocationLink}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object FindDefinition {

  def getDefinition(uri: String,
                    position: Position,
                    allRelationships: Future[Seq[RelationshipLink]],
                    allAliases: Future[Seq[AliasInfo]],
                    yPartBranchCached: YPartBranchCached): Future[Seq[LocationLink]] =
    for {
      relationships <- allRelationships
      aliases       <- allAliases
    } yield
      findByPosition(
        uri,
        AliasRelationships.getLinks(aliases, relationships, yPartBranchCached).map(fl => (fl.source, fl.destination)),
        position)
        .map(toLocationLink)
        .sortWith(sortInner)

  private def findByPosition(uri: String,
                             allRelationships: Seq[(Location, Location)],
                             position: Position): Seq[(Location, Location)] =
    allRelationships.filter { s =>
      val range =
        PositionRange(LspRangeConverter.toPosition(s._1.range.start), LspRangeConverter.toPosition(s._1.range.end))
      s._1.uri == uri && range.contains(position)
    }

  private def sortInner(l1: LocationLink, l2: LocationLink): Boolean =
    l1.originSelectionRange
      .flatMap { l1pr =>
        l2.originSelectionRange.map { l2pr =>
          val pr1 = PositionRange(LspRangeConverter.toPosition(l1pr.start), LspRangeConverter.toPosition(l1pr.end))
          val pr2 = PositionRange(LspRangeConverter.toPosition(l2pr.start), LspRangeConverter.toPosition(l2pr.end))
          pr1.intersection(pr2).contains(pr1)
        }
      }
      .getOrElse(false)

  private def toLocationLink(s: (Location, Location)) =
    LocationLink(s._2.uri, s._2.range, s._2.range, Some(s._1.range))
}
