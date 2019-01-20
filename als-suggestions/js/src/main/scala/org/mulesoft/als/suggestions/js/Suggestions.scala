package org.mulesoft.als.suggestions.js

import amf.client.remote.Content
import amf.client.resource.ClientResourceLoader
import amf.core.client.ParserConfig
import amf.core.model.document.BaseUnit
import amf.core.remote._
import amf.internal.environment.Environment
import org.mulesoft.als.suggestions.implementation.{CompletionConfig, DummyASTProvider, DummyEditorStateProvider, EmptyASTProvider}
import org.mulesoft.als.suggestions.interfaces.Syntax
import org.mulesoft.als.suggestions.interfaces.Syntax._
import org.mulesoft.als.suggestions.{CompletionProvider, Core, PlatformBasedExtendedFSProvider}
import org.mulesoft.high.level.InitOptions
import org.mulesoft.high.level.amfmanager.ParserHelper
import org.mulesoft.high.level.interfaces.IProject

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

@JSExportTopLevel("Suggestions")
object Suggestions {
  @JSExport
  def init(options: InitOptions = InitOptions.WebApiProfiles): js.Promise[Unit] = {
    Core.init(options).toJSPromise
  }

  def internalResourceLoader(loader: ClientResourceLoader): amf.internal.resource.ResourceLoader = new amf.internal.resource.ResourceLoader {
    override def fetch(resource: String): Future[Content] = loader.fetch(resource).toFuture

    override def accepts(resource: String): Boolean = loader.accepts(resource)
  }

  @JSExport
  def suggest(language: String,
              url: String,
              position: Int,
              loaders: js.Array[ClientResourceLoader] = js.Array()): js.Promise[js.Array[Suggestion]] = {
    val environment = new Environment(loaders.map(internalResourceLoader).toSeq)
    val platform = new AlsBrowserPlatform(environment)

    val config = this.buildParserConfig(language, url)

    var contentOpt: Option[String] = None
    var originalContent: Option[String] = None
    val completionProviderFuture: Future[CompletionProvider] = platform
      .resolve(url, environment)
      .map(content => {
        val fileContentsStr = content.stream.toString
        originalContent = Option(fileContentsStr)
        contentOpt = Option(this.cacheUnit(url, fileContentsStr, position))

      })
      .flatMap(_ => this.amfParse(config, environment, platform))
      .flatMap(this.buildHighLevel(_, platform))
      .map(this.buildCompletionProvider(_, url, position, originalContent, platform))
      .recoverWith {
        case e: Throwable =>
          println(e)
          contentOpt match {
            case Some(c) =>
              Future.successful(this.buildCompletionProviderNoAST(c, url, position, platform))
            case None => Future.failed(new Error("Failed to construct Completionprovider"))
          }
        case any =>
          println(any)
          Future.failed(new Error("Failed to construct Completionprovider"))
      }

    completionProviderFuture
      .flatMap(_.suggest)
      .map(suggestions =>
        suggestions.map(suggestion =>
          new Suggestion(
            text = suggestion.text,
            description = suggestion.description,
            displayText = suggestion.displayText,
            prefix = suggestion.prefix,
            category = suggestion.category
          ))
      )
      .map(suggestions => suggestions.toJSArray).toJSPromise
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

  def amfParse(config: ParserConfig, environment: Environment, platform: Platform): Future[BaseUnit] =
    ParserHelper(platform).parse(config, environment)

  def cacheUnit(fileUrl: String, fileContentsStr: String, position: Int): String = {

    val patchedContent = Core.prepareText(fileContentsStr, position, YAML)

    // ToDo: Check if is usefull to cache file content
    // File.unapply(fileUrl).foreach(x => AlsBrowserPlatform.withOverride(x, patchedContent))
    patchedContent
  }

  def buildHighLevel(model: BaseUnit, platform: Platform): Future[IProject] = {
    org.mulesoft.high.level.Core.buildModel(model, platform)
  }

  def buildCompletionProvider(project: IProject,
                              url: String,
                              position: Int,
                              originalContent: Option[String],
                              platform: Platform): CompletionProvider = {

    val rootUnit = project.rootASTUnit

    val astProvider = new DummyASTProvider(project, position)

    val baseName = url.substring(url.lastIndexOf('/') + 1)

    val editorStateProvider = new DummyEditorStateProvider(rootUnit.text, url, baseName, position)

    val platformFSProvider = new PlatformBasedExtendedFSProvider(platform)

    val completionConfig = new CompletionConfig()
      .withAstProvider(astProvider)
      .withEditorStateProvider(editorStateProvider)
      .withFsProvider(platformFSProvider)
      .withOriginalContent(originalContent.orNull)

    CompletionProvider().withConfig(completionConfig)
  }

  def buildCompletionProviderNoAST(text: String, url: String, position: Int, platform: Platform): CompletionProvider = {

    val baseName = url.substring(url.lastIndexOf('/') + 1)

    val editorStateProvider = new DummyEditorStateProvider(text, url, baseName, position)

    val trimmed = text.trim
    val vendor = if (trimmed.startsWith("#%RAML")) Raml10 else Oas20
    val syntax = if (trimmed.startsWith("{") || trimmed.startsWith("[")) Syntax.JSON else Syntax.YAML

    val astProvider = new EmptyASTProvider(vendor, syntax)

    val platformFSProvider = new PlatformBasedExtendedFSProvider(platform)

    val completionConfig = new CompletionConfig()
      .withEditorStateProvider(editorStateProvider)
      .withFsProvider(platformFSProvider)
      .withAstProvider(astProvider)
      .withOriginalContent(text)

    CompletionProvider().withConfig(completionConfig)
  }
}
