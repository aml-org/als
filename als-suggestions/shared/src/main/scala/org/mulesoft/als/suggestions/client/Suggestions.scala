package org.mulesoft.als.suggestions.client

import amf.core.model.document.BaseUnit
import amf.core.remote._
import amf.internal.environment.Environment
import amf.plugins.document.vocabularies.model.document.Dialect
import org.mulesoft.als.common.dtoTypes.{Position => DtoPosition}
import org.mulesoft.als.common.{DirectoryResolver, EnvironmentPatcher}
import org.mulesoft.als.suggestions._
import org.mulesoft.als.suggestions.aml.{AmlCompletionRequestBuilder, CompletionEnvironment}
import org.mulesoft.als.suggestions.interfaces.Syntax._
import org.mulesoft.als.suggestions.interfaces.{CompletionProvider, Syntax}
import org.mulesoft.amfmanager.dialect.DialectKnowledge
import org.mulesoft.amfmanager.{InitOptions, ParserHelper}
import org.mulesoft.lsp.feature.completion.CompletionItem
import amf.core.parser.{Position => AmfPosition}

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
              platform: Platform,
              snippetsSupport: Boolean): Future[Seq[CompletionItem]] = {

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
                                        platform,
                                        snippetsSupport)
      }
  }

  def buildProvider(bu: BaseUnit,
                    position: Int,
                    directoryResolver: DirectoryResolver,
                    platform: Platform,
                    env: Environment,
                    url: String,
                    originalContent: String,
                    snippetSupport: Boolean): Future[CompletionProvider] = {
    DialectKnowledge.dialectFor(bu) match {
      case Some(d) =>
        Future(
          buildCompletionProviderAST(bu,
                                     d,
                                     bu.id,
                                     DtoPosition(position, originalContent),
                                     originalContent,
                                     directoryResolver,
                                     env,
                                     platform,
                                     snippetSupport))
      case _ if isHeader(position, url, originalContent) =>
        if (!url.toLowerCase().endsWith(".raml"))
          Future(
            HeaderCompletionProviderBuilder
              .build(url, originalContent, DtoPosition(position, originalContent)))
        else
          Future(
            RamlHeaderCompletionProvider
              .build(url, originalContent, DtoPosition(position, originalContent)))
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
                         originalContent: String,
                         snippetSupport: Boolean): Future[CompletionProvider] = {
    unitFuture
      .flatMap(buildProvider(_, position, directoryResolver, platform, env, url, originalContent, snippetSupport))
  }

  private def isHeader(position: Int, url: String, originalContent: String): Boolean =
    !originalContent
      .substring(0, position)
      .replaceAll("^\\{?\\s+", "")
      .contains('\n')

  private def suggestWithPatchedEnvironment(language: String,
                                            url: String,
                                            originalContent: String,
                                            position: Int,
                                            directoryResolver: DirectoryResolver,
                                            environment: Environment,
                                            platform: Platform,
                                            snippetsSupport: Boolean): Future[Seq[CompletionItem]] = {

    buildProviderAsync(this.amfParse(url, environment, platform),
                       position,
                       directoryResolver,
                       platform,
                       environment,
                       url,
                       originalContent,
                       snippetsSupport)
      .flatMap(_.suggest())
  }

  private def buildCompletionProviderAST(bu: BaseUnit,
                                         dialect: Dialect,
                                         url: String,
                                         pos: DtoPosition,
                                         originalContent: String,
                                         directoryResolver: DirectoryResolver,
                                         env: Environment,
                                         platform: Platform,
                                         snippetSupport: Boolean): CompletionProviderAST = {

    val amfPosition: AmfPosition = pos.toAmfPosition
    CompletionProviderAST(
      AmlCompletionRequestBuilder
        .build(bu,
               amfPosition,
               dialect,
               CompletionEnvironment(directoryResolver, platform, env),
               originalContent,
               snippetSupport))
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
