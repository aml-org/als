package org.mulesoft.als.server.workspace.command

import amf.core.parser._
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.protocol.textsync.IndexDialectParams
import org.mulesoft.amfintegration.AmfInstance
import org.yaml.model.YMap

import scala.concurrent.Future

class IndexDialectCommandExecutor(val logger: Logger, amfConfiguration: AmfInstance)
    extends CommandExecutor[IndexDialectParams, Unit] {
  override protected def buildParamFromMap(ast: YMap): Option[IndexDialectParams] = {
    val content: Option[String] = ast.key("content").map(e => e.value.asScalar.map(_.text).getOrElse(e.value.toString))
    ast.key("uri").map(e => e.value.asScalar.map(_.text).getOrElse(e.value.toString)) match {
      case Some(uri) => Some(IndexDialectParams(uri, content))
      case _         => None
    }
  }

  override protected def runCommand(param: IndexDialectParams): Future[Unit] = {
    amfConfiguration.parserHelper.indexDialect(param.uri, param.content)
  }
}
