package org.mulesoft.als.suggestions.js

import org.mulesoft.als.suggestions.PlatformBasedExtendedFSProvider
import org.mulesoft.als.suggestions.implementation.EmptyASTProvider
import org.mulesoft.als.suggestions.interfaces.Syntax
import org.mulesoft.high.level.amfmanager.ParserHelper

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

import amf.core.client.ParserConfig
import amf.core.model.document.BaseUnit
import amf.core.remote._
import amf.internal.environment.Environment
import org.mulesoft.als.suggestions.implementation.{CompletionConfig, DummyASTProvider, DummyEditorStateProvider}
import org.mulesoft.als.suggestions.interfaces.Syntax._
import org.mulesoft.als.suggestions.{CompletionProvider, Core}
import org.mulesoft.high.level.interfaces.IProject

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.js.JSConverters._

@JSExportTopLevel("Suggestions")
object Suggestions {

  @JSExport
  def init(): js.Promise[Unit] = {
    val result = org.mulesoft.high.level.Core
      .init()
      .flatMap(_ => Core.init())

    result.toJSPromise
  }

  @JSExport
  def suggest(language: String, url: String, position: Int, env: Environment = Environment()): js.Promise[js.Array[Suggestion]] = {
    val config = this.buildParserConfig(language, url)

    var contentOpt: Option[String] = None
    var originalContent: Option[String] = None
    val completionProviderFuture: Future[CompletionProvider] = AlsBrowserPlatform
      .resolve(url, env)
      .map(content => {
        val fileContentsStr = content.stream.toString
        originalContent = Option(fileContentsStr)
        contentOpt = Option(this.cacheUnit(url, fileContentsStr, position))

      })
      .flatMap(_ => {

        this.amfParse(config)

      })
      .flatMap(amfUnit => {

        this.buildHighLevel(amfUnit)

      })
      .map(project => {

        this.buildCompletionProvider(project, url, position, originalContent)

      }) recoverWith {
      case e: Throwable =>
        //println(e)
        contentOpt match {
          case Some(c) =>
            Future.successful(this.buildCompletionProviderNoAST(c, url, position))
          case None => Future.failed(new Error("Failed to construct Completionprovider"))
        }
      case _ => Future.failed(new Error("Failed to construct Completionprovider"))
    }

    val result = completionProviderFuture
      .flatMap(_.suggest)
      .map(suggestions =>
        suggestions.map(suggestion => {

          new Suggestion(
            text = suggestion.text,
            description = suggestion.description,
            displayText = suggestion.displayText,
            prefix = suggestion.prefix,
            category = suggestion.category
          )

        }))
      .map(suggestions => suggestions.toJSArray)

    result.toJSPromise
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

  def amfParse(config: ParserConfig): Future[BaseUnit] = {

    val helper = ParserHelper(AlsBrowserPlatform)
    helper.parse(config, new Environment(AlsBrowserPlatform.loaders))
  }

  def cacheUnit(fileUrl: String, fileContentsStr: String, position: Int): String = {

    val patchedContent = Core.prepareText(fileContentsStr, position, YAML)

    // ToDo: Check if is usefull to cache file content
    // File.unapply(fileUrl).foreach(x => AlsBrowserPlatform.withOverride(x, patchedContent))
    patchedContent
  }

  def buildHighLevel(model: BaseUnit): Future[IProject] = {

    org.mulesoft.high.level.Core.buildModel(model, AlsBrowserPlatform)
  }

  def buildCompletionProvider(project: IProject,
                              url: String,
                              position: Int,
                              originalContent: Option[String]): CompletionProvider = {

    val rootUnit = project.rootASTUnit

    val astProvider = new DummyASTProvider(project, position)

    val baseName = url.substring(url.lastIndexOf('/') + 1)

    val editorStateProvider = new DummyEditorStateProvider(rootUnit.text, url, baseName, position)

    val platformFSProvider = new PlatformBasedExtendedFSProvider(AlsBrowserPlatform)

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
    val vendor = if (trimmed.startsWith("#%RAML")) Raml10 else Oas20
    val syntax = if (trimmed.startsWith("{") || trimmed.startsWith("[")) Syntax.JSON else Syntax.YAML

    val astProvider = new EmptyASTProvider(vendor, syntax)

    val platformFSProvider = new PlatformBasedExtendedFSProvider(AlsBrowserPlatform)

    val completionConfig = new CompletionConfig()
      .withEditorStateProvider(editorStateProvider)
      .withFsProvider(platformFSProvider)
      .withAstProvider(astProvider)
      .withOriginalContent(text)

    CompletionProvider().withConfig(completionConfig)
  }
}
