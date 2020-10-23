package org.mulesoft.amfintegration.relationships

import org.mulesoft.als.common.cache.YPartBranchCached
import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.amfintegration.VirtualYPart
import org.mulesoft.lsp.feature.common.Location
import org.yaml.model.{YMapEntry, YNodePlain, YPart, YScalar}
import org.mulesoft.als.common.YamlWrapper._
object AliasRelationships {

  /**
    * source, destination, source YPart
    */
  type FullLink = (Location, Location, Option[YPart])

  def isAliasDeclaration(alias: Seq[AliasInfo], position: Position, yPartBranchCached: YPartBranchCached): Boolean =
    alias.exists(a => a.keyRange(yPartBranchCached).exists(_.contains(position)))

  def getLinks(alias: Seq[AliasInfo],
               relationships: Seq[RelationshipLink],
               yPartBranchCached: YPartBranchCached): Seq[FullLink] = {
    relationships.flatMap { r =>
      val tuples: Seq[FullLink] = alias.flatMap { a =>
        r.sourceEntry match {
          case s: YScalar if s.text.indexOf(s"${a.tag}.") >= 0 =>
            splitLocation(a, r, s, s.text, yPartBranchCached)
          case s: VirtualYPart if s.text.indexOf(s"${a.tag}.") >= 0 =>
            splitLocation(a, r, s, s.text, yPartBranchCached)
          case n: YNodePlain if n.asScalar.exists(_.text.indexOf(s"${a.tag}.") >= 0) =>
            n.asScalar.toSeq.flatMap(s => splitLocation(a, r, s, s.text, yPartBranchCached))
          case _ => Seq.empty
        }
      }
      if (tuples.isEmpty)
        Seq((r.source, r.nameYPart.yPartToLocation, Some(value(r.sourceEntry))))
      else
        tuples
    }
  }

  private def value(yPart: YPart): YPart =
    yPart match {
      case yMapEntry: YMapEntry => yMapEntry.value
      case _                    => yPart
    }

  private def splitLocation(a: AliasInfo,
                            r: RelationshipLink,
                            s: YPart,
                            text: String,
                            yPartBranchCached: YPartBranchCached): Seq[FullLink] = {
    val positionRange = PositionRange(s.range)
    val start         = positionRange.start.moveColumn(text.indexOf(s"${a.tag}."))
    val aliasRange =
      PositionRange(start, start.moveColumn(a.tag.length))
    val startPart = start.moveColumn(a.tag.length + 1)
    val notAliasRange = PositionRange(
      startPart,
      r.nameYPart match {
        case s: YScalar => startPart.moveColumn(s.text.length)
        case _          => positionRange.end
      }
    )
    Seq(
      (Location(s.location.sourceName, LspRangeConverter.toLspRange(notAliasRange)),
       r.nameYPart.yPartToLocation,
       None),
      (Location(s.location.sourceName, LspRangeConverter.toLspRange(aliasRange)),
       Location(s.location.sourceName,
                a.keyRange(yPartBranchCached).map(LspRangeConverter.toLspRange).getOrElse(a.declaration.range)),
       None)
    )
  }
}
