package org.mulesoft.als.suggestions.client

import amf.core.client.ParserConfig
import amf.core.model.document.BaseUnit
import amf.core.remote._
import amf.internal.environment.Environment
import org.mulesoft.als.suggestions.implementation.{
  CompletionConfig,
  DummyASTProvider,
  DummyEditorStateProvider,
  EmptyASTProvider
}
import org.mulesoft.als.suggestions.interfaces.Syntax
import org.mulesoft.als.suggestions.interfaces.Syntax._
import org.mulesoft.als.suggestions.{CompletionProvider, Core, interfaces}
import org.mulesoft.high.level.InitOptions
import org.mulesoft.high.level.amfmanager.ParserHelper
import org.mulesoft.high.level.implementation.AlsPlatform
import org.mulesoft.high.level.interfaces.IProject

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Suggestions {
  def init(options: InitOptions = InitOptions.WebApiProfiles): Future[Unit] = Core.init(options)

  def suggest(language: String,
              url: String,
              position: Int,
              environment: Environment,
              platform: AlsPlatform): Future[Seq[Suggestion]] = {
    platform
      .resolve(url, environment)
      .map(content => {
        val originalContent      = content.stream.toString
        val (_, patchedPlatform) = patchContent(platform, url, originalContent, position)
        (originalContent, patchedPlatform)
      })
      .flatMap {
        case (originalContent, patchedPlatform) =>
          suggestWithPatchedPlatform(language, url, originalContent, position, patchedPlatform)
      }
  }

  private def suggestWithPatchedPlatform(language: String,
                                         url: String,
                                         originalContent: String,
                                         position: Int,
                                         patchedPlatform: AlsPlatform): Future[Seq[Suggestion]] = {
    val config = this.buildParserConfig(language, url)

    val completionProviderFuture: Future[CompletionProvider] = this
      .amfParse(config, patchedPlatform.defaultEnvironment, patchedPlatform)
      .flatMap(this.buildHighLevel(_, patchedPlatform))
      .map(this.buildCompletionProvider(_, url, position, originalContent, patchedPlatform))
      .recoverWith {
        case e: Throwable =>
          println(e)
          Future.successful(this.buildCompletionProviderNoAST(originalContent, url, position, patchedPlatform))
        case any =>
          println(any)
          Future.failed(new Error("Failed to construct CompletionProvider"))
      }

    def completeFinalSuggestion(suggestion: interfaces.Suggestion) = {
      new Suggestion(
        text = if (!suggestion.text.endsWith(":")) suggestion.text else s"${suggestion.text} ",
        description = suggestion.description,
        displayText = suggestion.displayText,
        prefix = suggestion.prefix,
        category = suggestion.category,
        range = suggestion.range
      )
    }

    completionProviderFuture
      .flatMap(_.suggest)
      .map(suggestions => suggestions.map(completeFinalSuggestion))
  }

  def buildParserConfig(language: String, url: String): ParserConfig =
    new ParserConfig(
      Some(ParserConfig.PARSE),
      Some(url),
      Some(language),
      Some("application/yaml"),
      None,
      Some("AMF Graph"),
      Some("application/ld+json")
    )

  def amfParse(config: ParserConfig, environment: Environment, platform: Platform): Future[BaseUnit] =
    ParserHelper(platform).parse(config, environment)

  def patchContent(platform: AlsPlatform,
                   fileUrl: String,
                   fileContentsStr: String,
                   position: Int): (String, AlsPlatform) = {
    val patchedContent       = Core.prepareText(fileContentsStr, position, YAML)
    val platformWithOverride = platform.withOverride(fileUrl, patchedContent)

    (patchedContent, platformWithOverride)
  }

  def buildHighLevel(model: BaseUnit, platform: AlsPlatform): Future[IProject] = {
    org.mulesoft.high.level.Core.buildModel(model, platform)
  }

  def buildCompletionProvider(project: IProject,
                              url: String,
                              position: Int,
                              originalContent: String,
                              platform: AlsPlatform): CompletionProvider = {

    val rootUnit = project.rootASTUnit

    val astProvider = new DummyASTProvider(project, position)

    val baseName = url.substring(url.lastIndexOf('/') + 1)

    val editorStateProvider = new DummyEditorStateProvider(rootUnit.text, url, baseName, position)
    val completionConfig = new CompletionConfig(platform)
      .withAstProvider(astProvider)
      .withEditorStateProvider(editorStateProvider)
      .withOriginalContent(originalContent)

    CompletionProvider().withConfig(completionConfig)
  }

  def buildCompletionProviderNoAST(text: String,
                                   url: String,
                                   position: Int,
                                   platform: AlsPlatform): CompletionProvider = {

    val baseName = url.substring(url.lastIndexOf('/') + 1)

    val editorStateProvider = new DummyEditorStateProvider(text, url, baseName, position)

    val trimmed = text.trim
    val vendor  = if (trimmed.startsWith("#%RAML")) Raml10 else Oas20
    val syntax  = if (trimmed.startsWith("{") || trimmed.startsWith("[")) Syntax.JSON else Syntax.YAML

    val astProvider = new EmptyASTProvider(vendor, syntax)

    val completionConfig = new CompletionConfig(platform)
      .withEditorStateProvider(editorStateProvider)
      .withAstProvider(astProvider)
      .withOriginalContent(text)

    CompletionProvider().withConfig(completionConfig)
  }
}
