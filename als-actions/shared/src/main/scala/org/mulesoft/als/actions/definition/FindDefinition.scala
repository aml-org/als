package org.mulesoft.als.actions.definition

import amf.core.remote.Platform
import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.lsp.feature.common.{Location, LocationLink}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Cases in which to return a Location:
  * * root key "uses" indicates there is a map with libraries (whose values are relative paths)
  * * SYAML MutRefs will be returned as location
  * *  [X] if clicked on a URI it will also return this as location
  */
object FindDefinition {

  private def findByPosition(uri: String,
                             allRelationships: Seq[(Location, Location)],
                             position: Position): Seq[(Location, Location)] =
    allRelationships.filter { s =>
      val range =
        PositionRange(LspRangeConverter.toPosition(s._1.range.start), LspRangeConverter.toPosition(s._1.range.end))
      s._1.uri == uri && range.contains(position)
    }

  def getDefinition(uri: String,
                    position: Position,
                    allRelationships: Future[Seq[(Location, Location)]],
                    platform: Platform): Future[Seq[LocationLink]] =
    allRelationships.map { relationships =>
      findByPosition(uri, relationships, position).map { s =>
        LocationLink(s._2.uri, s._2.range, s._2.range, Some(s._1.range))
      }
    }
}
