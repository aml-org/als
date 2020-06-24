package org.mulesoft.als.server

import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.server.modules.reference.MarkerInfo
import org.mulesoft.als.server.protocol.LanguageServer
import org.mulesoft.als.suggestions.interfaces.Syntax.YAML
import org.mulesoft.als.suggestions.patcher.{ContentPatcher, PatchedContent}

import scala.concurrent.Future

abstract class ServerWithMarkerTest[Out] extends LanguageServerBaseTest {

  def runTest(server: LanguageServer, path: String, dialect: Option[String] = None): Future[Out] =
    withServer[Out](server) { server =>
      val resolved = filePath(platform.encodeURI(path))

      for {
        _       <- dialect.map(openDialect(_, server)).getOrElse(Future.unit)
        content <- this.platform.resolve(resolved)
        definitions <- {
          val fileContentsStr = content.stream.toString
          val markerInfo      = this.findMarker(fileContentsStr)
          openFile(server)(resolved, markerInfo.patchedContent.original)
          getAction(resolved, server, markerInfo)
        }
      } yield definitions
    }

  private def openDialect(path: String, server: LanguageServer): Future[Unit] = {
    val resolved = filePath(platform.encodeURI(path))
    this.platform.resolve(resolved).map(c => openFile(server)(resolved, c.stream.toString))
  }
  def getAction(path: String, server: LanguageServer, markerInfo: MarkerInfo): Future[Out]

  def findMarker(str: String, label: String = "[*]", cut: Boolean = true): MarkerInfo = {
    val offset = str.indexOf(label)

    if (offset < 0)
      new MarkerInfo(PatchedContent(str, str, Nil), Position(str.length, str))
    else {
      val rawContent = str.substring(0, offset) + str.substring(offset + label.length)
      val preparedContent =
        ContentPatcher(rawContent, offset, YAML).prepareContent()
      new MarkerInfo(preparedContent, Position(offset, str))
    }
  }
}
