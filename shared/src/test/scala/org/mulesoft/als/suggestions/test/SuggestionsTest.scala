package org.mulesoft.als.suggestions.test

import amf.client.remote.Content
import amf.core.client.ParserConfig
import amf.core.model.document.BaseUnit
import amf.core.remote.JvmPlatform
import amf.core.unsafe.PlatformSecrets
import amf.internal.environment.Environment
import amf.internal.resource.ResourceLoader
import org.mulesoft.als.suggestions.{CompletionProvider, PlatformBasedExtendedFSProvider}
import org.mulesoft.als.suggestions.implementation.{CompletionConfig, DummyASTProvider, DummyEditorStateProvider}
import org.mulesoft.als.suggestions.interfaces.Syntax.YAML
import org.mulesoft.high.level.Core
import org.mulesoft.high.level.implementation.PlatformFsProvider
import org.mulesoft.high.level.interfaces.IProject
import org.scalatest.{Assertion, AsyncFunSuite}
import org.scalatest.{Assertion, Succeeded}

import scala.collection.mutable.ListBuffer
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

  def runTest(path:String,originalSuggestions: Set[String]):Future[Assertion] = {

    val fullFilePath = filePath(path)

    org.mulesoft.als.suggestions.Core.init()

    this.suggest(fullFilePath).map(suggestions=>{

        val resultSet = suggestions.toSet
        val diff1 = resultSet.diff(originalSuggestions)
        val diff2 = originalSuggestions.diff(resultSet)

        if (diff1.isEmpty && diff2.isEmpty) succeed
        else fail(s"Difference for $path: got [${suggestions.mkString(", ")}] while expecting [${originalSuggestions.mkString(", ")}]")
    })


  }

  def format:String
  def rootPath:String

  def bulbLoaders(path: String, content:String): Seq[ResourceLoader] = {
    var loaders: Seq[ResourceLoader] = List(new ResourceLoader {
      override def accepts(resource: String): Boolean = resource == path

      override def fetch(resource: String): Future[Content] = Future.successful(new Content(content, path))
    })
    loaders ++= platform.loaders()
    loaders
  }

  def suggest(url: String): Future[Seq[String]] = {

    val config = this.buildParserConfig(format, url)

    var position = 0;

    var contentOpt:Option[String] = None
    var originalContent:Option[String] = None
    this.platform.resolve(url).map(content => {

      val fileContentsStr = content.stream.toString
      originalContent = Some(fileContentsStr.replace("*",""))
      val markerInfo = this.findMarker(fileContentsStr)

      position = markerInfo.position
      contentOpt = Some(markerInfo.content)
      var env = this.buildEnvironment(url, markerInfo.content, position, content.mime)
      env
    }).flatMap(env=>{

      this.amfParse(config,env)

    }).flatMap {
        case amfUnit: BaseUnit => this.buildHighLevel(amfUnit).map(project => {

            this.buildCompletionProvider(project, url, position, originalContent)

            }).flatMap(_.suggest)
                .map(suggestions => suggestions.map(suggestion => {

                suggestion.text
            }))
        case _ =>
            contentOpt match {
                case Some(c) =>
                    var cProvider = this.buildCompletionProviderNoAST(c, url, position)
                    cProvider.suggest.map(suggestions => suggestions.map(suggestion => {
                        suggestion.text
                    }))
                case None => Future.successful(Seq())
            }
    } recoverWith {
        case e:Throwable =>
            println(e)
            contentOpt match {
                case Some(c) =>
                    var cProvider = this.buildCompletionProviderNoAST(c, url, position)
                    cProvider.suggest.map(suggestions => suggestions.map(suggestion => {
                        suggestion.text
                    }))
                case None => Future.successful(Seq())
            }
        case _ => Future.successful(Seq())
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

  def amfParse(config: ParserConfig,env:Environment=Environment()): Future[BaseUnit] = {

    val helper = ParserHelper(this.platform)
    helper.parse(config,env)
  }

  def buildEnvironment(fileUrl: String, content: String, position: Int, mime: Option[String]): Environment = {

      var loaders:Seq[ResourceLoader] = List(new ResourceLoader {override def accepts(resource: String): Boolean = resource == fileUrl

          override def fetch(resource: String): Future[Content] = Future.successful(new Content(content,fileUrl))
      })
      loaders ++= platform.loaders()
      var env:Environment = Environment(loaders)
      env
  }
//  def cacheUnit(fileUrl: String, content: String, position: Int, mime: Option[String]): Unit = {
//
//    File.unapply(fileUrl).foreach(x=>this.platform.cacheResourceText(
//      x, content, mime))
//  }

  def buildHighLevel(model:BaseUnit):Future[IProject] = {

    Core.init().flatMap(_=>org.mulesoft.high.level.Core.buildModel(model,platform))
  }

  def buildCompletionProvider(project: IProject, url: String, position: Int, originalContent:Option[String]): CompletionProvider = {

    val rootUnit = project.rootASTUnit

    val astProvider = new DummyASTProvider(project,position)

    val baseName = url.substring(url.lastIndexOf('/') + 1)

    val editorStateProvider = new DummyEditorStateProvider(rootUnit.text,url,baseName,position)

    val platformFSProvider = new PlatformBasedExtendedFSProvider(this.platform)

    val completionConfig = new CompletionConfig()
      .withAstProvider(astProvider)
      .withEditorStateProvider(editorStateProvider)
      .withFsProvider(platformFSProvider)
      .withOriginalContent(originalContent.orNull)

    CompletionProvider().withConfig(completionConfig)
  }

  def buildCompletionProviderNoAST(text:String, url: String, position: Int): CompletionProvider = {

        val baseName = url.substring(url.lastIndexOf('/') + 1)

        val editorStateProvider = new DummyEditorStateProvider(text, url, baseName, position)

        val platformFSProvider = new PlatformBasedExtendedFSProvider(this.platform)

        val completionConfig = new CompletionConfig()
            .withEditorStateProvider(editorStateProvider)
            .withFsProvider(platformFSProvider)

        CompletionProvider().withConfig(completionConfig)
    }

  def filePath(path:String):String = {
    var rootDir = System.getProperty("user.dir")
    s"file://$rootDir/shared/src/test/resources/test/$rootPath/$path".replace('\\','/')
  }

  def findMarker(str:String,label:String="*", cut: Boolean = true): MarkerInfo = {

    var position = str.indexOf(label);

    if(position<0){
        new MarkerInfo(str,str.length)
    }
    else {
        var rawContent = str.substring(0, position) + str.substring(position + 1)
        var preparedContent =
          org.mulesoft.als.suggestions.Core.prepareText(rawContent, position, YAML)
        new MarkerInfo(preparedContent, position)
    }

  }
}

class MarkerInfo(val content:String, val position:Int) {}
