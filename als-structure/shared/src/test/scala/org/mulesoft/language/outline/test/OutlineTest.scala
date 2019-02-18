package org.mulesoft.language.outline.test

import amf.client.remote.Content
import amf.core.client.ParserConfig
import amf.core.model.document.BaseUnit
import amf.internal.environment.Environment
import amf.internal.resource.ResourceLoader
import org.mulesoft.high.level.Core
import org.mulesoft.high.level.amfmanager.ParserHelper
import org.mulesoft.high.level.implementation.AlsPlatform
import org.mulesoft.high.level.interfaces.{IParseResult, IProject}
import org.mulesoft.language.outline.structure.structureImpl.{ConfigFactory, DocumentSymbol, StructureBuilder}
import org.mulesoft.language.outline.test.amfmigrated.common.FileAssertionTest
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

trait OutlineTest[T] extends AsyncFunSuite with FileAssertionTest {

  implicit override def executionContext: ExecutionContext =
    scala.concurrent.ExecutionContext.Implicits.global

  override val platform: AlsPlatform = AlsPlatform.default

  def readDataFromAST(project: IProject, position: Int): T

  def writeDataToString(data: T): String

  def emptyData(): T

  def runTest(path: String, jsonPath: String): Future[Assertion] = {

    val fullFilePath = filePath(path)
    val fullJsonPath = filePath(jsonPath)

    for {
      _             <- org.mulesoft.high.level.Core.init()
      actualOutline <- this.getActualOutline(fullFilePath)
      tmp           <- writeTemporaryFile(jsonPath)(writeDataToString(actualOutline))
      r             <- assertDifferences(tmp, fullJsonPath)

    } yield r
  }

  def format: String
  def rootPath: String

  def bulbLoaders(path: String, content: String): Seq[ResourceLoader] = {
    var loaders: Seq[ResourceLoader] = List(new ResourceLoader {
      override def accepts(resource: String): Boolean = resource == path

      override def fetch(resource: String): Future[Content] = Future.successful(new Content(content, path))
    })
    Environment().loaders
  }

  def getExpectedOutline(url: String): Future[String] =
    this.platform.resolve(url, Environment(this.platform.loaders)).map(_.stream.toString)

  def getActualOutline(url: String): Future[T] = {

    val config = this.buildParserConfig(format, url)

    var position = 0;

    var contentOpt: Option[String] = None
    this.platform
      .resolve(url, Environment(this.platform.loaders))
      .map(content => {

        val fileContentsStr = content.stream.toString
        val markerInfo      = this.findMarker(fileContentsStr)

        position = markerInfo.position
        contentOpt = Some(markerInfo.content)
        var env = this.buildEnvironment(url, markerInfo.content, position, content.mime)
        env
      })
      .flatMap(env => {

        this.amfParse(config, env)

      })
      .flatMap {
        case amfUnit: BaseUnit =>
          this
            .buildHighLevel(amfUnit)
            .map(project => readDataFromAST(project, position))
        case _ =>
          Future.successful(emptyData())
      } recoverWith {
      case e: Throwable =>
        println(e)
        Future.successful(emptyData())
      case _ => Future.successful(emptyData())
    }
  }

  def buildParserConfig(language: String, url: String): ParserConfig = {

    new ParserConfig(
      Some(ParserConfig.PARSE),
      Some(url),
      Some(language),
      Some("application/yaml"),
      None,
      Some("AMF Graph"),
      Some("application/ld+json")
    )
  }

  def amfParse(config: ParserConfig, env: Environment = Environment()): Future[BaseUnit] = {

    val helper = ParserHelper(this.platform)
    helper.parse(config, env)
  }

  def buildEnvironment(fileUrl: String, content: String, position: Int, mime: Option[String]): Environment = {

    var loaders: Seq[ResourceLoader] = List(new ResourceLoader {
      override def accepts(resource: String): Boolean = resource == fileUrl

      override def fetch(resource: String): Future[Content] = Future.successful(new Content(content, fileUrl))
    })
    var env: Environment = Environment()
    env
  }
  //  def cacheUnit(fileUrl: String, content: String, position: Int, mime: Option[String]): Unit = {
  //
  //    File.unapply(fileUrl).foreach(x=>this.platform.cacheResourceText(
  //      x, content, mime))
  //  }

  def buildHighLevel(model: BaseUnit): Future[IProject] = {

    Core.init().flatMap(_ => org.mulesoft.high.level.Core.buildModel(model, platform))
  }

  def filePath(path: String): String = {
    s"file://als-structure/shared/src/test/resources/$rootPath/$path".replace('\\', '/').replace("null/", "")
  }

  def findMarker(str: String, label: String = "*", cut: Boolean = true): MarkerInfo = {

    var position = str.indexOf(label)

    if (position < 0) {
      new MarkerInfo(str, str.length)
    } else {
      var rawContent = str.substring(0, position) + str.substring(position + 1)
      new MarkerInfo(rawContent, position)
    }

  }

  def getDocumentSymbolFromAST(ast: IParseResult, language: String, position: Int): List[DocumentSymbol] =
    ConfigFactory
      .getConfig(new ASTProvider(ast, position, language))
      .map(c => StructureBuilder.listSymbols(ast, c))
      .getOrElse(Nil)

}

class MarkerInfo(val content: String, val position: Int) {}
