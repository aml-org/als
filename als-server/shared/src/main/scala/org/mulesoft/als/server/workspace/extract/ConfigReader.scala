package org.mulesoft.als.server.workspace.extract

import amf.core.remote.{Platform, UnsupportedUrlScheme}
import amf.internal.environment.Environment
import org.mulesoft.als.common.FileUtils

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait ConfigReader {
  val configFileName: String

  def readRoot(rootPath: String,
               platform: Platform,
               environment: Environment = Environment()): Future[Option[WorkspaceConf]] =
    readFile(FileUtils.getEncodedUri(s"$rootPath/$configFileName", platform), platform, environment).flatMap {
      case Some(content) =>
        buildConfig(content, FileUtils.getPath(rootPath, platform), platform) match {
          case Some(f) => f.map(Some(_))
          case _       => Future.successful(None)
        }
      case _ => Future.successful(None)
    }

  protected def readFile(uri: String, platform: Platform, environment: Environment): Future[Option[String]] = {
    try {
      platform.resolve(uri, environment).map { content =>
        Some(content.stream.toString)
      }
    } catch {
      case _: UnsupportedUrlScheme => Future.successful(None)
      case e: Exception            => Future.failed(e)
    }
  }

  protected def buildConfig(content: String, path: String, platform: Platform): Option[Future[WorkspaceConf]]
}
