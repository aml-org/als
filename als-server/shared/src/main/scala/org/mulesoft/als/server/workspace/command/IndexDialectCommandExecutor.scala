package org.mulesoft.als.server.workspace.command

import amf.core.parser._
import amf.core.remote.Platform
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.amfmanager.ParserHelper
import org.mulesoft.lsp.textsync.IndexDialectParams
import org.yaml.model.YMap

class IndexDialectCommandExecutor(val logger: Logger, platform: Platform) extends CommandExecutor[IndexDialectParams] {
  override protected def buildParamFromMap(ast: YMap): Option[IndexDialectParams] = {
    val content: Option[String] = ast.key("content").map(e => e.value.asScalar.map(_.text).getOrElse(e.value.toString))
    ast.key("uri").map(e => e.value.asScalar.map(_.text).getOrElse(e.value.toString)) match {
      case Some(uri) => Some(IndexDialectParams(uri, content))
      case _         => None
    }
  }

  override protected def runCommand(param: IndexDialectParams): Unit = {
    ParserHelper(platform).indexDialect(param.uri, param.content)
  }
}
