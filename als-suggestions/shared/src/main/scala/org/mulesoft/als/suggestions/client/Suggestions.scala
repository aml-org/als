package org.mulesoft.als.suggestions.client

import amf.core.client.ParserConfig
import amf.core.model.document.BaseUnit
import amf.core.remote._
import amf.internal.environment.Environment
import org.mulesoft.als.common.{DirectoryResolver, EnvironmentPatcher}
import org.mulesoft.als.suggestions.implementation.{
  CompletionConfig,
  DummyASTProvider,
  DummyEditorStateProvider,
  EmptyASTProvider
}
import org.mulesoft.als.suggestions.interfaces.Syntax
import org.mulesoft.als.suggestions.interfaces.Syntax._
import org.mulesoft.als.suggestions.{CompletionProvider, Core}
import org.mulesoft.high.level.InitOptions
import org.mulesoft.high.level.amfmanager.ParserHelper
import org.mulesoft.high.level.interfaces.IProject

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Suggestions {
  def init(options: InitOptions = InitOptions.WebApiProfiles): Future[Unit] =
    Core.init(options)

  def suggest(language: String,
              url: String,
              position: Int,
              directoryResolver: DirectoryResolver,
              environment: Environment,
              platform: Platform): Future[Seq[Suggestion]] = {

    platform
      .resolve(url, environment)
      .map(content => {
        val originalContent = content.stream.toString
        val (_, patchedEnv) =
          patchContentInEnvironment(environment, url, originalContent, position)
        (originalContent, patchedEnv)
      })
      .flatMap {
        case (originalContent, patchedEnv) =>
          suggestWithPatchedEnvironment(language,
                                        url,
                                        originalContent,
                                        position,
                                        directoryResolver,
                                        patchedEnv,
                                        platform)
      }
  }

  def getMediaType(originalContent: String): Syntax = {
    val trimmed = originalContent.trim
    if (trimmed.startsWith("{") || trimmed.startsWith("[")) Syntax.JSON
    else Syntax.YAML
  }

  private def suggestWithPatchedEnvironment(language: String,
                                            url: String,
                                            originalContent: String,
                                            position: Int,
                                            directoryResolver: DirectoryResolver,
                                            environment: Environment,
                                            platform: Platform): Future[Seq[Suggestion]] = {

    val config = this.buildParserConfig(language, url)
    val completionProviderFuture: Future[CompletionProvider] = this
      .amfParse(config, environment, platform)
      .flatMap(this.buildHighLevel(_, platform))
      .map(this.buildCompletionProvider(_, url, position, originalContent, directoryResolver, platform))
      .recoverWith {
        case e: Throwable =>
          println(e)
          Future.successful(
            this.buildCompletionProviderNoAST(originalContent, url, position, directoryResolver, platform))
        case any =>
          println(any)
          Future.failed(new Error("Failed to construct CompletionProvider"))
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
            category = suggestion.category,
            range = suggestion.range
        )))
      .map(suggestions => suggestions)
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

  def patchContentInEnvironment(environment: Environment,
                                fileUrl: String,
                                fileContentsStr: String,
                                position: Int): (String, Environment) = {

    val patchedContent = Core.prepareText(fileContentsStr, position, YAML)
    val envWithOverride =
      EnvironmentPatcher.patch(environment, fileUrl, patchedContent)

    (patchedContent, envWithOverride)
  }

  def buildHighLevel(model: BaseUnit, platform: Platform): Future[IProject] = {
    org.mulesoft.high.level.Core.buildModel(model, platform)
  }

  def buildCompletionProvider(project: IProject,
                              url: String,
                              position: Int,
                              originalContent: String,
                              directoryResolver: DirectoryResolver,
                              platform: Platform): CompletionProvider = {

    val rootUnit = project.rootASTUnit

    val astProvider = new DummyASTProvider(project, position)

    val baseName = url.substring(url.lastIndexOf('/') + 1)

    val editorStateProvider =
      new DummyEditorStateProvider(rootUnit.text, url, baseName, position)
    val completionConfig = new CompletionConfig(directoryResolver, platform)
      .withAstProvider(astProvider)
      .withEditorStateProvider(editorStateProvider)
      .withOriginalContent(originalContent)

    CompletionProvider().withConfig(completionConfig)
  }

  def buildCompletionProviderNoAST(text: String,
                                   url: String,
                                   position: Int,
                                   directoryResolver: DirectoryResolver,
                                   platform: Platform): CompletionProvider = {

    val baseName = url.substring(url.lastIndexOf('/') + 1)

    val editorStateProvider =
      new DummyEditorStateProvider(text, url, baseName, position)

    val trimmed = text.trim
    val vendor  = if (trimmed.startsWith("#%RAML")) Raml10 else Oas20
    val syntax  = getMediaType(trimmed)

    val astProvider = new EmptyASTProvider(vendor, syntax)

    val completionConfig = new CompletionConfig(directoryResolver, platform)
      .withEditorStateProvider(editorStateProvider)
      .withAstProvider(astProvider)
      .withOriginalContent(text)

    CompletionProvider().withConfig(completionConfig)
  }
}
