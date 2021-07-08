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

  def readRoot(rootUri: String,
               platform: Platform,
               environment: Environment,
               logger: Logger): Future[Option[WorkspaceConf]] = {
    logger.debug(s"reading $rootUri", "ConfigReader", "readRoot")
    val fileUri      = appendFileToUri(rootUri)
    val fileAsAmfUri = fileUri.toAmfUri(platform)
    logger.debug(s"full URI $fileUri", "ConfigReader", "readRoot")
    logger.debug(s"full AMF URI $fileAsAmfUri", "ConfigReader", "readRoot")
    readFile(fileAsAmfUri, platform, environment, logger).flatMap {
      case Some(content) =>
        val asPath = rootUri.toPath(platform)
        logger.debug(s"asPath $asPath", "ConfigReader", "readRoot")
        buildConfig(content, asPath, platform, environment, logger) match {
          case Some(f) =>
            f.map(Some(_))
          case _ =>
            Future.successful(None)
        }
      case _ =>
        Future.successful(None)
    }
  }

  private def appendFileToUri(rootPath: String): String =
    if (rootPath.endsWith("/"))
      s"$rootPath$configFileName"
    else s"$rootPath/$configFileName"

  protected def readFile(uri: String,
                         platform: Platform,
                         environment: Environment,
                         logger: Logger): Future[Option[String]] = {
    try {
      logger.debug(s"reading $uri", "ConfigReader", "readFile")
      environment.loaders
        .foreach(l => logger.debug(s"loader accepts $uri: ${l.accepts(uri)}", "ConfigReader", "readFile"))
      platform
        .fetchContent(uri, AMFPluginsRegistry.obtainStaticConfig().withResourceLoaders(environment.loaders.toList))
        .map { content =>
          Some(content.stream.toString)
        }
    } catch {
      case e: UnsupportedUrlScheme =>
        logger.error(s"${e.getMessage}", "ConfigReader", "readFile")
        logger.debug(s"$uri will fallback to `None` config", "ConfigReader", "readFile")
        Future.successful(None)
      case e: Exception =>
        logger.error(s"${e.getMessage}", "ConfigReader", "readFile")
        logger.debug(s"$uri", "ConfigReader", "readFile")
        Future.failed(e)
    }
  }

  protected def buildConfig(content: String,
                            path: String,
                            platform: Platform,
                            environment: Environment,
                            logger: Logger): Option[Future[WorkspaceConf]]
}
