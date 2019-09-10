package org.mulesoft.als.suggestions.client

import amf.core.model.document.BaseUnit
import amf.core.remote._
import amf.dialects.WebApiDialectsRegistry
import amf.internal.environment.Environment
import amf.plugins.document.vocabularies.model.document.{Dialect, DialectInstanceUnit}
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.common.{DirectoryResolver, EnvironmentPatcher}
import org.mulesoft.als.suggestions._
import org.mulesoft.als.suggestions.aml.{AmlCompletionRequestBuilder, CompletionEnvironment}
import org.mulesoft.als.suggestions.interfaces.Syntax._
import org.mulesoft.als.suggestions.interfaces.{CompletionProvider, Syntax}
import org.mulesoft.als.suggestions.plugins.aml.webapi.oas.Oas20DialectWrapper
import org.mulesoft.als.suggestions.plugins.aml.webapi.raml.raml08.Raml08TypesDialect
import org.mulesoft.als.suggestions.plugins.aml.webapi.raml.raml10.Raml10TypesDialect
import org.mulesoft.amfmanager.{InitOptions, ParserHelper}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Suggestions extends SuggestionsHelper {
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

  def buildProvider(bu: BaseUnit,
                    position: Int,
                    directoryResolver: DirectoryResolver,
                    platform: Platform,
                    env: Environment,
                    url: String,
                    originalContent: String): Future[CompletionProvider] = {
    dialectFor(bu) match {
      case Some(d) =>
        Future(
          buildCompletionProviderAST(bu,
                                     d,
                                     bu.id,
                                     Position(position, originalContent),
                                     originalContent,
                                     directoryResolver,
                                     env,
                                     platform))
      case _ if isHeader(position, url, originalContent) =>
        if (!url.toLowerCase().endsWith(".raml"))
          Future(
            HeaderCompletionProviderBuilder
              .build(url, originalContent, Position(position, originalContent)))
        else
          Future(
            RamlHeaderCompletionProvider
              .build(url, originalContent, Position(position, originalContent)))
      case _ =>
        Future.failed(new Exception("Cannot find dialect for unit: " + bu.id))
    }
  }

  def buildProviderAsync(unitFuture: Future[BaseUnit],
                         position: Int,
                         directoryResolver: DirectoryResolver,
                         platform: Platform,
                         env: Environment,
                         url: String,
                         originalContent: String): Future[CompletionProvider] = {
    unitFuture
      .flatMap(buildProvider(_, position, directoryResolver, platform, env, url, originalContent))
  }

  private def isHeader(position: Int, url: String, originalContent: String): Boolean =
    !originalContent
      .substring(0, position)
      .replaceAll("^\\{?\\s+", "")
      .contains('\n')

  private def dialectFor(bu: BaseUnit): Option[Dialect] = bu match {
    case d: DialectInstanceUnit => WebApiDialectsRegistry.dialectFor(bu)
    case d if d.sourceVendor.contains(Oas20) =>
      Some(Oas20DialectWrapper.dialect)
    case d if d.sourceVendor.contains(Raml10) =>
      Some(Raml10TypesDialect.dialect)
    case d if d.sourceVendor.contains(Raml08) =>
      Some(Raml08TypesDialect.dialect)
    case _ => None
  }

  private def suggestWithPatchedEnvironment(language: String,
                                            url: String,
                                            originalContent: String,
                                            position: Int,
                                            directoryResolver: DirectoryResolver,
                                            environment: Environment,
                                            platform: Platform): Future[Seq[Suggestion]] = {

    buildProviderAsync(this.amfParse(url, environment, platform),
                       position,
                       directoryResolver,
                       platform,
                       environment,
                       url,
                       originalContent)
      .flatMap(_.suggest())
      .map(suggestions => suggestions map toClientSuggestion)
  }

  private def toClientSuggestion(suggestion: interfaces.Suggestion) =
    new Suggestion(
      text = suggestion.text,
      description = suggestion.description,
      displayText = suggestion.displayText,
      prefix = suggestion.prefix,
      category = suggestion.category,
      range = suggestion.range
    )

  private def buildCompletionProviderAST(bu: BaseUnit,
                                         dialect: Dialect,
                                         url: String,
                                         pos: Position,
                                         originalContent: String,
                                         directoryResolver: DirectoryResolver,
                                         env: Environment,
                                         platform: Platform): CompletionProviderAST = {

    val amfPosition = pos.moveLine(1)
    CompletionProviderAST(
      AmlCompletionRequestBuilder
        .build(bu, amfPosition, dialect, CompletionEnvironment(directoryResolver, platform, env), originalContent))
  }
}

trait SuggestionsHelper {

  def amfParse(url: String, environment: Environment, platform: Platform): Future[BaseUnit] =
    ParserHelper(platform).parse(url, environment)

  def getMediaType(originalContent: String): Syntax = {

    val trimmed = originalContent.trim
    if (trimmed.startsWith("{") || trimmed.startsWith("[")) Syntax.JSON
    else Syntax.YAML
  }

  def patchContentInEnvironment(environment: Environment,
                                fileUrl: String,
                                fileContentsStr: String,
                                position: Int): (String, Environment) = {

    val patchedContent = Core.prepareText(fileContentsStr, position, YAML)
    val envWithOverride =
      EnvironmentPatcher.patch(environment, fileUrl, patchedContent)

    (patchedContent, envWithOverride)
  }
}
