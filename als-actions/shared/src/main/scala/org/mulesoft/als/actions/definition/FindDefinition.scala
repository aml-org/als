package org.mulesoft.als.actions.definition

import amf.core.model.document.BaseUnit
import amf.core.remote.Platform
import org.mulesoft.als.actions.common.{ActionTools, AliasInfo}
import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.als.common.{NodeBranchBuilder, YamlUtils}
import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.lsp.feature.common.{Location, LocationLink}
import org.yaml.model.{YNodePlain, YPart, YScalar}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object FindDefinition {

  def getDefinition(uri: String,
                    position: Position,
                    allRelationships: Future[Seq[(Location, Location)]],
                    allAliases: Future[Seq[AliasInfo]],
                    fbu: Future[BaseUnit],
                    platform: Platform): Future[Seq[LocationLink]] =
    for {
      relationships <- allRelationships
      aliases       <- allAliases
      bu            <- fbu
    } yield {
      (findByPosition(uri, relationships, aliases, position) ++
        findAliases(getYBranch(position, bu).node, aliases, position))
        .map(toLocationLink)
        .sortWith(sortInner)
    }

  private def findByPosition(uri: String,
                             allRelationships: Seq[(Location, Location)],
                             aliases: Seq[AliasInfo],
                             position: Position): Seq[(Location, Location)] =
    allRelationships.filter { s =>
      val range =
        PositionRange(LspRangeConverter.toPosition(s._1.range.start), LspRangeConverter.toPosition(s._1.range.end))
      s._1.uri == uri && range.contains(position)
    }

  private def findAliases(node: YPart, aliases: Seq[AliasInfo], position: Position): Seq[(Location, Location)] =
    aliases
      .find { alias =>
        node match {
          case n: YNodePlain if n.value.isInstanceOf[YScalar] =>
            (n.location.sourceName == alias.declaration.uri) &&
              n.value.asInstanceOf[YScalar].text.startsWith(s"${alias.tag}.") &&
              (position.toAmfPosition.column - n.range.columnFrom) <= alias.tag.length
          case _ => false
        }

      }
      .map { alias =>
        (ActionTools.sourceLocationToLocation(node.location), alias.declaration)
      }
      .toSeq

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
