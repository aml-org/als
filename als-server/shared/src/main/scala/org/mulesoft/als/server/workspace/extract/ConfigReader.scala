package org.mulesoft.als.server.workspace.extract

import amf.core.registries.AMFPluginsRegistry
import amf.core.remote.{Platform, UnsupportedUrlScheme}
import amf.internal.environment.Environment
import org.mulesoft.als.common.URIImplicits._
import org.mulesoft.als.server.logger.Logger

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait ConfigReader {
  val configFileName: String

  def readRoot(rootPath: String,
               platform: Platform,
               environment: Environment,
               logger: Logger): Future[Option[WorkspaceConf]] =
    readFile(appendFileToUri(rootPath).toAmfUri(platform), platform, environment).flatMap {
      case Some(content) =>
        buildConfig(content, rootPath.toPath(platform), platform, environment, logger) match {
          case Some(f) =>
            f.map(Some(_))
          case _ =>
            Future.successful(None)
        }
      case _ =>
        Future.successful(None)
    }

  private def appendFileToUri(rootPath: String): String =
    if (rootPath.endsWith("/"))
      s"$rootPath$configFileName"
    else s"$rootPath/$configFileName"

  protected def readFile(uri: String, platform: Platform, environment: Environment): Future[Option[String]] = {
    try {
      platform
        .fetchContent(uri, AMFPluginsRegistry.obtainStaticConfig().withResourceLoaders(environment.loaders.toList))
        .map { content =>
          Some(content.stream.toString)
        }
    } catch {
      case _: UnsupportedUrlScheme => Future.successful(None)
      case e: Exception            => Future.failed(e)
    }
  }

  protected def buildConfig(content: String,
                            path: String,
                            platform: Platform,
                            environment: Environment,
                            logger: Logger): Option[Future[WorkspaceConf]]
}
