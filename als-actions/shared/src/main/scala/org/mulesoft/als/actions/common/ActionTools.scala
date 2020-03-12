package org.mulesoft.als.actions.common

import amf.core.parser.{Position => AmfPosition}
import amf.core.remote.Platform
import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.als.convert.LspRangeConverter
import org.mulesoft.lexer.SourceLocation
import org.mulesoft.lsp.feature.common.{Location, LocationLink, Range}

object ActionTools {
  private def sourceLocationToRange(targetLocation: SourceLocation): Range =
    LspRangeConverter.toLspRange(
      PositionRange(
        Position(AmfPosition(targetLocation.lineFrom, targetLocation.columnFrom)),
        Position(AmfPosition(targetLocation.lineTo, targetLocation.columnTo))
      ))

  def sourceLocationToLocation(targetLocation: SourceLocation): Location =
    Location(targetLocation.sourceName, sourceLocationToRange(targetLocation))

  def locationToLsp(sourceLocation: SourceLocation, targetLocation: SourceLocation, platform: Platform): LocationLink =
    LocationLink(
      targetLocation.sourceName,
      sourceLocationToRange(targetLocation),
      sourceLocationToRange(targetLocation),
      Some(sourceLocationToRange(sourceLocation))
    )
}
