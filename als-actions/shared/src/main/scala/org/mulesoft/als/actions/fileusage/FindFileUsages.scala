package org.mulesoft.als.actions.fileusage

import amf.core.model.document.BaseUnit
import amf.core.remote.Platform
import org.mulesoft.als.actions.common.RelationshipLink
import org.mulesoft.lsp.feature.common.Location

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object FindFileUsages {
  def getUsages(uri: String, allRelationships: Future[Seq[RelationshipLink]]): Future[Seq[Location]] =
    for {
      relationships <- allRelationships
    } yield {
      relationships
        .filter(r => r.destination.uri == uri)
        .map(r => Location(r.source.uri, r.source.range))
    }
}
