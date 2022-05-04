package org.mulesoft.als.actions.references

import org.mulesoft.als.common.cache.{ASTPartBranchCached, YPartBranchCached}
import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.amfintegration.relationships.{AliasInfo, AliasRelationships, FullLink, RelationshipLink}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object FindReferences {
  def getReferences(
      uri: String,
      position: Position,
      allAliases: Future[Seq[AliasInfo]],
      allRelationships: Future[Seq[RelationshipLink]],
      astPartBranchCached: ASTPartBranchCached
  ): Future[Seq[FullLink]] =
    for {
      refs  <- allRelationships
      alias <- allAliases
    } yield AliasRelationships
      .getLinks(alias, refs, astPartBranchCached)
      .filter(r => containsPosition(uri, position, r.destination.uri, PositionRange(r.destination.range)))

  private def containsPosition(
      uri: String,
      position: Position,
      destinationUri: String,
      destinationRange: PositionRange
  ) =
    destinationUri == uri && destinationRange
      .contains(position)
}
