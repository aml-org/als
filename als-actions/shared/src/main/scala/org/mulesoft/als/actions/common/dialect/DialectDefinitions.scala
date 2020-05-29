package org.mulesoft.als.actions.common.dialect

import amf.core.model.document.BaseUnit
import amf.core.remote.Platform
import amf.plugins.document.vocabularies.model.document.Dialect
import org.mulesoft.als.actions.common.{ActionTools, AliasInfo, RelationshipLink}
import org.mulesoft.als.common.{NodeBranchBuilder, YamlUtils}
import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.lsp.feature.common.{Location, LocationLink}
import org.yaml.model.{YNodePlain, YPart}

import scala.collection.mutable
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object DialectDefinitions {
  def getDefinition(uri: String,
                    position: Position,
                    fbu: Future[BaseUnit],
                    platform: Platform): Future[Seq[LocationLink]] =
    for {
      bu <- fbu
    } yield {
      findByPosition(uri, bu, position)
        .map(toLocationLink)
        .sortWith(sortInner)
    }

  private def getLinkForPosition(bu: BaseUnit, position: Position): Option[String] = {
    None
  }

  private def getPositionForLink(bu: Dialect, link: String): Option[Location] = {
    None
  }

  private def findByPosition(uri: String, bu: Dialect, position: Position): Seq[(Location, Location)] = {
    bu match {
      case d: Dialect =>
        val target: Option[Location] = getLinkForPosition(d, position)
          .flatMap(getPositionForLink(d, _))
        target
          .map { t =>
            val origin = getYBranch(position, d).node.location
            Seq((ActionTools.sourceLocationToLocation(origin), t))
          }
          .getOrElse(Nil)
      case _ => Nil
    }
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

  private def getYBranch(position: Position, bu: BaseUnit) =
    NodeBranchBuilder.build(bu, position.toAmfPosition, YamlUtils.isJson(bu))
}
