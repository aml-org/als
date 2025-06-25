package org.mulesoft.als.server.workspace.command

import amf.apicontract.client.scala.APIConfiguration
import amf.core.internal.parser.YMapOps
import org.mulesoft.als.server.protocol.textsync.IndexDialectParams
import org.mulesoft.als.server.workspace.WorkspaceManager
import org.mulesoft.amfintegration.dialect.integration.BaseAlsDefinitionsProvider
import org.mulesoft.amfintegration.platform.AlsPlatformSecrets
import org.yaml.model.YMap

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/** This will index at workspace level
  * @param Logger
  * @param amfConfiguration
  */
class IndexDialectCommandExecutor(workspaceManager: WorkspaceManager)
    extends CommandExecutor[IndexDialectParams, Unit]
    with AlsPlatformSecrets {
  override protected def buildParamFromMap(ast: YMap): Option[IndexDialectParams] = {
    val content: Option[String] = ast.key("content").map(e => e.value.asScalar.map(_.text).getOrElse(e.value.toString))
    ast.key("uri").map(e => e.value.asScalar.map(_.text).getOrElse(e.value.toString)) match {
      case Some(uri) => Some(IndexDialectParams(uri, content))
      case _         => None
    }
  }

  def loadFromEnv(uri: String): Future[String] =
    platform
      .fetchContent(
        uri,
        APIConfiguration
          .API()
          .withResourceLoaders(
            workspaceManager.environmentProvider.getResourceLoader +: workspaceManager.editorConfiguration.resourceLoaders.toList
          )
      )
      .map(_.toString())

  override protected def runCommand(param: IndexDialectParams): Future[Unit] =
    param.content
      .map(Future(_))
      .getOrElse(loadFromEnv(param.uri))
      .map(content => {
        BaseAlsDefinitionsProvider.indexDialect(param.uri, content)
        workspaceManager.editorConfiguration.withDialect(param.uri)
      })

}
