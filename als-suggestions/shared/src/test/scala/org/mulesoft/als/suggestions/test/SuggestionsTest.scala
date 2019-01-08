package org.mulesoft.als.suggestions.test

import amf.client.remote.Content
import amf.core.client.ParserConfig
import amf.core.model.document.BaseUnit
import amf.core.remote.{Oas20, Raml10}
import amf.core.unsafe.PlatformSecrets
import amf.internal.environment.Environment
import amf.internal.resource.ResourceLoader
import org.mulesoft.als.suggestions.implementation.{
  CompletionConfig,
  DummyASTProvider,
  DummyEditorStateProvider,
  EmptyASTProvider
}
import org.mulesoft.als.suggestions.interfaces.Syntax
import org.mulesoft.als.suggestions.interfaces.Syntax.YAML
import org.mulesoft.als.suggestions.{CompletionProvider, Core, PlatformBasedExtendedFSProvider}
import org.mulesoft.high.level.interfaces.IProject
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

trait SuggestionsTest extends AsyncFunSuite with PlatformSecrets {

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

  def runTest(path: String, originalSuggestions: Set[String]): Future[Assertion] =
    this
      .suggest(path)
      .map(r => assert(path, r.toSet, originalSuggestions))

  def format: String
  def rootPath: String

  def buildLoaders(path: String, content: String): Seq[ResourceLoader] = {
    var loaders: Seq[ResourceLoader] = List(new ResourceLoader {
      override def accepts(resource: String): Boolean = resource == path

      override def fetch(resource: String): Future[Content] = Future.successful(new Content(content, path))
    })
    loaders ++= platform.loaders()
    loaders
  }

  def suggest(u: String): Future[Seq[String]] = {

    var position                        = 0
    val url                             = filePath(u)
    var contentOpt: Option[String]      = None
    var originalContent: Option[String] = None
    // todo: replace first logic for build model, and then delete all suggest method and fix runtest
    (for {
      _       <- init()
      content <- this.platform.resolve(url)
      env <- Future {
        val fileContentsStr = content.stream.toString
        originalContent = Some(fileContentsStr.replace("*", ""))
        val markerInfo = this.findMarker(fileContentsStr)

        position = markerInfo.position
        contentOpt = Some(markerInfo.originalContent)
        var env = this.buildEnvironment(url, markerInfo.content, content.mime)
        env
      }
      u <- this.parseAMF(url, env)
      r <- handleUnit(u, contentOpt, url, position, originalContent)
    } yield r) recoverWith {
      case e: Throwable =>
        println(e)
        contentOpt match {
          case Some(c) =>
            var cProvider = this.buildCompletionProviderNoAST(c, url, position)
            cProvider.suggest.map(suggestions =>
              suggestions.map(suggestion => {
                suggestion.text
              }))
          case None => Future.successful(Seq())
        }
      case _ => Future.successful(Seq())
    }
  }

  private def handleUnit(u: BaseUnit,
                         contentOpt: Option[String],
                         url: String,
                         position: Int,
                         originalContent: Option[String]): Future[Seq[String]] = u match {
    case amfUnit: BaseUnit =>
      this
        .buildHighLevel(amfUnit)
        .map(project => {

          this.buildCompletionProvider(project, url, position, originalContent)

        })
        .flatMap(_.suggest)
        .map(suggestions =>
          suggestions.map(suggestion => {

            suggestion.text
          }))
    case _ =>
      contentOpt match {
        case Some(c) =>
          var cProvider = this.buildCompletionProviderNoAST(c, url, position)
          cProvider.suggest.map(suggestions =>
            suggestions.map(suggestion => {
              suggestion.text
            }))
        case None => Future.successful(Seq())
      }
  }

  case class ModelResult(u: BaseUnit, url: String, position: Int, originalContent: Option[String])

  def init(): Future[Unit] = Core.init()

  // todo: hnajles refactor and unify with suggest method
  protected def buildModel(path: String): Future[ModelResult] = {
    val url = filePath(path)
    init().flatMap { _ =>
      this.platform
        .resolve(url)
        .flatMap(content => {
          val fileContentsStr = content.stream.toString
          val originalContent = Some(fileContentsStr.replace("*", ""))
          val markerInfo      = this.findMarker(fileContentsStr)

          val position = markerInfo.position
          val env      = this.buildEnvironment(url, markerInfo.content, content.mime)

          this.parseAMF(url, env).map(u => ModelResult(u, url, position, originalContent))
        })
    }
  }

  protected def buildCompletionProviderFromUnit(r: ModelResult): Future[CompletionProvider] = {
    this
      .buildHighLevel(r.u)
      .map(project => {
        this.buildCompletionProvider(project, r.url, r.position, r.originalContent)
      })
  }

  def parseAMF(path: String, env: Environment = Environment()): Future[BaseUnit] = {

    var cfg = new ParserConfig(
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

  def buildEnvironment(fileUrl: String, content: String, mime: Option[String]): Environment = {

    var loaders: Seq[ResourceLoader] = List(new ResourceLoader {
      override def accepts(resource: String): Boolean = resource == fileUrl

      override def fetch(resource: String): Future[Content] = Future.successful(new Content(content, fileUrl))
    })
    loaders ++= platform.loaders()
    var env: Environment = Environment(loaders)
    env
  }
  //  def cacheUnit(fileUrl: String, content: String, position: Int, mime: Option[String]): Unit = {
  //
  //    File.unapply(fileUrl).foreach(x=>this.platform.cacheResourceText(
  //      x, content, mime))
  //  }

  def buildHighLevel(model: BaseUnit): Future[IProject] = {

    org.mulesoft.high.level.Core.buildModel(model, platform)
  }

  def buildCompletionProvider(project: IProject,
                              url: String,
                              position: Int,
                              originalContent: Option[String]): CompletionProvider = {

    val rootUnit = project.rootASTUnit

    val astProvider = new DummyASTProvider(project, position)

    val baseName = url.substring(url.lastIndexOf('/') + 1)

    val editorStateProvider = new DummyEditorStateProvider(rootUnit.text, url, baseName, position)

    val platformFSProvider = new PlatformBasedExtendedFSProvider(this.platform)

    val completionConfig = new CompletionConfig()
      .withAstProvider(astProvider)
      .withEditorStateProvider(editorStateProvider)
      .withFsProvider(platformFSProvider)
      .withOriginalContent(originalContent.orNull)

    CompletionProvider().withConfig(completionConfig)
  }

  def buildCompletionProviderNoAST(text: String, url: String, position: Int): CompletionProvider = {

    val baseName = url.substring(url.lastIndexOf('/') + 1)

    val editorStateProvider = new DummyEditorStateProvider(text, url, baseName, position)

    val trimmed = text.trim
    val vendor  = if (trimmed.startsWith("#%RAML")) Raml10 else Oas20
    val syntax  = if (trimmed.startsWith("{") || trimmed.startsWith("[")) Syntax.JSON else Syntax.YAML

    val astProvider = new EmptyASTProvider(vendor, syntax)

    val platformFSProvider = new PlatformBasedExtendedFSProvider(this.platform)

    val completionConfig = new CompletionConfig()
      .withEditorStateProvider(editorStateProvider)
      .withFsProvider(platformFSProvider)
      .withAstProvider(astProvider)
      .withOriginalContent(text)

    CompletionProvider().withConfig(completionConfig)
  }

  def filePath(path: String): String = {
    var result = s"file://als-suggestions/shared/src/test/resources/test/$rootPath/$path".replace('\\', '/')
    result = result.replace("/null", "")
    result
  }

  def findMarker(str: String, label: String = "*", cut: Boolean = true): MarkerInfo = {

    var position = str.indexOf(label);

    if (position < 0) {
      new MarkerInfo(str, str.length, str)
    } else {
      var rawContent = str.substring(0, position) + str.substring(position + 1)
      var preparedContent =
        org.mulesoft.als.suggestions.Core.prepareText(rawContent, position, YAML)
      new MarkerInfo(preparedContent, position, rawContent)
    }

  }
}

class MarkerInfo(val content: String, val position: Int, val originalContent: String) {}
