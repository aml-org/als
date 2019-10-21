package org.mulesoft.als.actions.definition.files

import java.net.{URI, URISyntaxException}

import amf.core.remote.Platform
import org.mulesoft.als.common.FileUtils
import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.lexer.SourceLocation
import org.mulesoft.lsp.common
import org.mulesoft.lsp.convert.LspRangeConverter

trait ActionTools {
  protected def sourceLocationToRange(targetLocation: SourceLocation): common.Range = {
    LspRangeConverter.toLspRange(
      PositionRange(
        Position(targetLocation.lineFrom, targetLocation.columnFrom, zeroBased = targetLocation.isZero).asZeroBased,
        Position(targetLocation.lineTo, targetLocation.columnTo, zeroBased = targetLocation.isZero).asZeroBased
      ))
  }

  protected def valueToUri(root: String, relative: String, platform: Platform): String = {
    if (relative.startsWith("#"))
      root // TODO: where to seek position
    else if (!extractProtocol(relative).isEmpty)
      relative
    else
      FileUtils.getEncodedUri(FileUtils.getPath(root.substring(0, root.lastIndexOf('/')), platform) + "/" +
                                FileUtils.getPath(relative, platform).stripPrefix("/"),
                              platform)
  }

  /**
    *
    * @param relative
    * @return scheme of URI, except file:// (relative paths) which return empty
    */
  protected def extractProtocol(relative: String): String =
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
