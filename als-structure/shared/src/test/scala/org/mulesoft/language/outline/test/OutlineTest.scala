package org.mulesoft.language.outline.test

import amf.client.remote.Content
import amf.core.errorhandling.ErrorCollector
import amf.core.model.document.BaseUnit
import amf.core.remote.Platform
import amf.core.unsafe.PlatformSecrets
import amf.internal.environment.Environment
import amf.internal.resource.ResourceLoader
import amf.plugins.document.vocabularies.model.document.Dialect
import org.mulesoft.als.CompilerEnvironment
import org.mulesoft.als.common.MarkerFinderTest
import org.mulesoft.als.common.diff.FileAssertionTest
import org.mulesoft.amfintegration.AmfInstance
import org.scalatest.{Assertion, AsyncFunSuite}

import scala.concurrent.{ExecutionContext, Future}

object File {
  val FILE_PROTOCOL = "file://"

  def unapply(url: String): Option[String] = {
    url match {
      case s if s.startsWith(FILE_PROTOCOL) =>
        val path = s.stripPrefix(FILE_PROTOCOL)
        Some(path)
      case _ => None
    }
  }
}

trait OutlineTest[T] extends AsyncFunSuite with FileAssertionTest with PlatformSecrets with MarkerFinderTest {

  implicit override def executionContext: ExecutionContext =
    scala.concurrent.ExecutionContext.Implicits.global

  def readDataFromAST(unit: BaseUnit, position: Int, definedBy: Dialect): T

  def writeDataToString(data: T): String

  def emptyData(): T

  def runTest(path: String, jsonPath: String, amfInstance: Option[AmfInstance] = None): Future[Assertion] = {

    val fullFilePath = filePath(platform.encodeURI(path))
    val fullJsonPath = filePath(jsonPath)
    val amfConfig    = amfInstance.getOrElse(AmfInstance.default)
    for {
      _             <- amfConfig.init()
      actualOutline <- this.getActualOutline(fullFilePath, platform, amfConfig)
      tmp           <- writeTemporaryFile(jsonPath)(writeDataToString(actualOutline))
      r             <- assertDifferences(tmp, fullJsonPath)

    } yield r
  }

  def rootPath: String

  def bulbLoaders(path: String, content: String): Seq[ResourceLoader] = {
    var loaders: Seq[ResourceLoader] = List(new ResourceLoader {
      override def accepts(resource: String): Boolean = resource == path

      override def fetch(resource: String): Future[Content] = Future.successful(new Content(content, path))
    })
    Environment().loaders
  }

  def getExpectedOutline(url: String): Future[String] =
    this.platform.fetchContent(url, Environment(this.platform.loaders)).map(_.stream.toString)

  def getActualOutline(
      url: String,
      platform: Platform,
      compilerEnvironment: CompilerEnvironment[BaseUnit, ErrorCollector, Dialect, Environment]): Future[T] = {

    var position = 0

    var contentOpt: Option[String] = None
    platform
      .resolve(url)
      .map(content => {

        val fileContentsStr = content.stream.toString
        val markerInfo      = this.findMarker(fileContentsStr)

        position = markerInfo.offset
        contentOpt = Some(markerInfo.content)
        val env = this.buildEnvironment(url, markerInfo.content, position, content.mime)
        env
      })
      .flatMap(env => {
        compilerEnvironment.modelBuilder().parse(url, env).map(cu => (cu.baseUnit, cu.definedBy))
      })
      .map {
        case (amfUnit, d) =>
          readDataFromAST(amfUnit, position, d)
        case _ =>
          emptyData()
      } recoverWith {
      case e: Throwable =>
        println(e)
        Future.successful(emptyData())
      case _ => Future.successful(emptyData())
    }
  }

  def buildEnvironment(fileUrl: String, content: String, position: Int, mime: Option[String]): Environment = {

    var loaders: Seq[ResourceLoader] = List(new ResourceLoader {
      override def accepts(resource: String): Boolean = resource == fileUrl

      override def fetch(resource: String): Future[Content] = Future.successful(new Content(content, fileUrl))
    })
    var env: Environment = Environment()
    env
  }

  def filePath(path: String): String = {
    s"file://als-structure/shared/src/test/resources/$rootPath/$path".replace('\\', '/').replace("null/", "")
  }

}
