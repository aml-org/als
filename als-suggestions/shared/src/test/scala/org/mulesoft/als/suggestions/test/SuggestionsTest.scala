package org.mulesoft.als.suggestions.test

import amf.client.remote.Content
import amf.core.client.ParserConfig
import amf.core.model.document.BaseUnit
import amf.internal.environment.Environment
import amf.internal.resource.ResourceLoader
import org.mulesoft.als.suggestions.CompletionProvider
import org.mulesoft.als.suggestions.client.Suggestions
import org.mulesoft.als.suggestions.interfaces.Syntax.YAML
import org.mulesoft.high.level.InitOptions
import org.mulesoft.high.level.amfmanager.ParserHelper
import org.mulesoft.high.level.implementation.{AlsPlatform, AlsPlatformWrapper}
import org.mulesoft.high.level.interfaces.IProject
import org.scalatest.{Assertion, AsyncFunSuite}

import scala.concurrent.{ExecutionContext, Future}

trait SuggestionsTest extends AsyncFunSuite {
  val platform: AlsPlatform = AlsPlatform.default

  implicit override def executionContext: ExecutionContext =
    scala.concurrent.ExecutionContext.Implicits.global

  def assert(path: String, actualSet: Set[String], golden: Set[String]): Assertion = {
    val diff1 = actualSet.diff(golden)
    val diff2 = golden.diff(actualSet)

    diff1.foreach(println)
    diff2.foreach(println)

    if (diff1.isEmpty && diff2.isEmpty) succeed
    else
      fail(s"Difference for $path: got [${actualSet.mkString(", ")}] while expecting [${golden.mkString(", ")}]")
  }

  /**
    * @param path                URI for the API resource
    * @param originalSuggestions Expected result set
    * @param label               Pointer placeholder
    * @param cut                 if true, cuts text after label
    * @param labels              set of every label in the file (needed for cleaning API)
    */
  def runTest(path: String,
              originalSuggestions: Set[String],
              label: String = "*",
              cut: Boolean = false,
              labels: Array[String] = Array("*")): Future[Assertion] =
    this
      .suggest(path, label, cut, labels)
      .map(r => assert(path, r.toSet, originalSuggestions))

  def format: String

  def rootPath: String

  def suggest(path: String,
              label: String = "*",
              cutTail: Boolean = false,
              labels: Array[String] = Array("*")): Future[Seq[String]] = {

    var position = 0
    val url      = filePath(path)

    for {
      _       <- Suggestions.init(InitOptions.AllProfiles)
      content <- platform.resolve(url)
      env <- Future.successful {
        val fileContentsStr = content.stream.toString
        val markerInfo      = this.findMarker(fileContentsStr)

        position = markerInfo.position

        this.buildEnvironment(url, markerInfo.originalContent, content.mime)
      }

      suggestions <- Suggestions.suggest(format, url, position, env, this.platform)
    } yield suggestions.map(suggestion => suggestion.text)
  }

  case class ModelResult(u: BaseUnit, url: String, position: Int, originalContent: Option[String])

  def init(): Future[Unit] = org.mulesoft.als.suggestions.Core.init()

  def parseAMF(path: String, env: Environment = Environment()): Future[BaseUnit] = {
    val cfg = new ParserConfig(
      Some(ParserConfig.PARSE),
      Some(path),
      Some(format),
      Some("application/yaml"),
      None,
      Some("AMF Graph"),
      Some("application/ld+json")
    )

    val helper = ParserHelper(platform)
    helper.parse(cfg, env)
  }

  def buildParserConfig(language: String, url: String): ParserConfig = Suggestions.buildParserConfig(language, url)

  def buildEnvironment(fileUrl: String, content: String, mime: Option[String]): Environment = {
    var loaders: Seq[ResourceLoader] = List(new ResourceLoader {
      override def accepts(resource: String): Boolean = resource == fileUrl

      override def fetch(resource: String): Future[Content] = Future.successful(new Content(content, fileUrl))
    })

    loaders ++= platform.loaders()

    Environment(loaders)
  }

  def buildHighLevel(model: BaseUnit): Future[IProject] = Suggestions.buildHighLevel(model, platform)

  def buildCompletionProvider(project: IProject,
                              url: String,
                              position: Int,
                              originalContent: String): CompletionProvider =
    Suggestions.buildCompletionProvider(project, url, position, originalContent, platform)

  def buildCompletionProviderNoAST(text: String, url: String, position: Int): CompletionProvider =
    Suggestions.buildCompletionProviderNoAST(text, url, position, platform)

  def filePath(path: String): String = {
    var result = s"file://als-suggestions/shared/src/test/resources/test/$rootPath/$path".replace('\\', '/')
    result = result.replace("/null", "")
    result
  }

  def findMarker(str: String,
                 label: String = "*",
                 cut: Boolean = false,
                 labels: Array[String] = Array("*")): MarkerInfo = {
    val position = str.indexOf(label)

    val str1 = {
      if (cut && position >= 0) {
        str.substring(0, position)
      } else {
        str
      }
    }

    if (position < 0) {
      new MarkerInfo(str1, str1.length, str1)
    } else {
      val rawContent = str1.replace(label, "")

      val preparedContent =
        org.mulesoft.als.suggestions.Core.prepareText(rawContent, position, YAML)
      new MarkerInfo(preparedContent, position, rawContent)
    }

  }
}

class MarkerInfo(val content: String, val position: Int, val originalContent: String) {}
