package org.mulesoft.als.server.workspace.extract

import amf.core.internal.remote.UnsupportedUrlScheme
import org.mulesoft.als.common.URIImplicits._
import org.mulesoft.als.logger.Logger
import org.mulesoft.amfintegration.amfconfiguration.AmfConfigurationWrapper

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait ConfigReader {
  val configFileName: String

  def readRoot(rootUri: String,
               amfConfiguration: AmfConfigurationWrapper,
               logger: Logger): Future[Option[WorkspaceConfig]] = {
    logger.debug(s"reading $rootUri", "ConfigReader", "readRoot")
    val fileUri      = appendFileToUri(rootUri)
    val fileAsAmfUri = fileUri.toAmfUri(amfConfiguration.platform)
    logger.debug(s"full URI $fileUri", "ConfigReader", "readRoot")
    logger.debug(s"full AMF URI $fileAsAmfUri", "ConfigReader", "readRoot")
    readFile(fileAsAmfUri, amfConfiguration, logger).flatMap {
      case Some(content) =>
        val asPath = rootUri.toPath(amfConfiguration.platform)
        logger.debug(s"asPath $asPath", "ConfigReader", "readRoot")
        buildConfig(content, asPath, amfConfiguration, logger) match {
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
                         amfConfiguration: AmfConfigurationWrapper,
                         logger: Logger): Future[Option[String]] = {
    try {
      logger.debug(s"reading $uri", "ConfigReader", "readFile")
      amfConfiguration
        .fetchContent(uri)
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
                            amfConfiguration: AmfConfigurationWrapper,
                            logger: Logger): Option[Future[WorkspaceConfig]]
}
