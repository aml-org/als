package org.mulesoft.als.server.workspace.command

import amf.core.internal.parser.YMapOps
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.protocol.textsync.IndexDialectParams
import org.mulesoft.amfintegration.amfconfiguration.AmfConfigurationWrapper
import org.yaml.model.YMap

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class IndexDialectCommandExecutor(val logger: Logger, amfConfiguration: AmfConfigurationWrapper)
    extends CommandExecutor[IndexDialectParams, Unit] {
  override protected def buildParamFromMap(ast: YMap): Option[IndexDialectParams] = {
    val content: Option[String] = ast.key("content").map(e => e.value.asScalar.map(_.text).getOrElse(e.value.toString))
    ast.key("uri").map(e => e.value.asScalar.map(_.text).getOrElse(e.value.toString)) match {
      case Some(uri) => Some(IndexDialectParams(uri, content))
      case _         => None
    }
  }

  override protected def runCommand(param: IndexDialectParams): Future[Unit] = {
    param.content
      .foreach(
        content =>
          amfConfiguration
            .withResourceLoader(AmfConfigurationWrapper.resourceLoaderForFile(param.uri, content)))
    amfConfiguration.parse(param.uri).map(_ => {})
  }
}
