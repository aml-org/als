package org.mulesoft.als.actions.common

import java.net.{URI, URISyntaxException}

import amf.core.remote.Platform
import org.mulesoft.als.common.FileUtils
import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.lexer.SourceLocation
import org.mulesoft.lsp.common.Range
import org.mulesoft.lsp.convert.LspRangeConverter
import amf.core.parser.{Position => AmfPosition}

object ActionTools {
  def sourceLocationToRange(targetLocation: SourceLocation): Range = {
    LspRangeConverter.toLspRange(
      PositionRange(
        Position(AmfPosition(targetLocation.lineFrom, targetLocation.columnFrom)),
        Position(AmfPosition(targetLocation.lineTo, targetLocation.columnTo))
      ))
  }

  def valueToUri(root: String, relative: String, platform: Platform): String =
    if (relative.startsWith("#"))
      root
    else if (!extractProtocol(relative).isEmpty)
      relative
    else
      FileUtils.getEncodedUri(FileUtils.getPath(root.substring(0, root.lastIndexOf('/')), platform) + "/" +
                                FileUtils.getPath(relative, platform).stripPrefix("/"),
                              platform)

  /**
    *
    * @param relative
    * @return scheme of URI, except file:// (relative paths) which return empty
    */
  def extractProtocol(relative: String): String =
    try {
      val uri = new URI(relative)
      Option(uri.getScheme)
        .map {
          case "file" if !uri.getPath.startsWith("/") => ""
          case a                                      => a
        }
        .getOrElse("")
    } catch {
      case _: URISyntaxException =>
        ""
    }
}
