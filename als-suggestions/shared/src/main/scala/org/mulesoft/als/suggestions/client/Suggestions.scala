package org.mulesoft.als.suggestions.client

import amf.core.model.document.BaseUnit
import amf.core.parser.{Position => AmfPosition}
import amf.core.remote._
import amf.internal.environment.Environment
import amf.plugins.document.vocabularies.model.document.Dialect
import org.mulesoft.als.common.dtoTypes.{Position => DtoPosition}
import org.mulesoft.als.common.{DirectoryResolver, EnvironmentPatcher}
import org.mulesoft.als.suggestions._
import org.mulesoft.als.suggestions.aml.{AmlCompletionRequestBuilder, CompletionEnvironment}
import org.mulesoft.als.suggestions.interfaces.Syntax._
import org.mulesoft.als.suggestions.interfaces.{CompletionProvider, Syntax}
import org.mulesoft.als.suggestions.patcher.{ContentPatcher, PatchedContent}
import org.mulesoft.amfmanager.dialect.DialectKnowledge
import org.mulesoft.amfmanager.{InitOptions, ParserHelper}
import org.mulesoft.lsp.feature.completion.CompletionItem

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
        val (patched, patchedEnv) =
          patchContentInEnvironment(environment, url, originalContent, position)
        (patched, patchedEnv)
      })
      .flatMap {
        case (patchedContent, patchedEnv) =>
          suggestWithPatchedEnvironment(language,
                                        url,
                                        patchedContent,
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
                    patchedContent: PatchedContent,
                    snippetSupport: Boolean): CompletionProvider = {
    DialectKnowledge.dialectFor(bu) match {
      case Some(d) =>
        Future(
          buildCompletionProviderAST(bu,
                                     d,
                                     bu.id,
                                     DtoPosition(position, patchedContent.original),
                                     patchedContent,
                                     directoryResolver,
                                     env,
                                     platform,
                                     snippetSupport))
      case _ if isHeader(position, url, patchedContent.original) =>
        if (!url.toLowerCase().endsWith(".raml"))
          HeaderCompletionProviderBuilder
            .build(url, patchedContent.original, DtoPosition(position, patchedContent.original))
        else
          Future(
            RamlHeaderCompletionProvider
              .build(url, patchedContent.original, DtoPosition(position, patchedContent.original)))
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
                         patchedContent: PatchedContent,
                         snippetSupport: Boolean): Future[CompletionProvider] = {
    unitFuture
      .flatMap(buildProvider(_, position, directoryResolver, platform, env, url, patchedContent, snippetSupport))
  }

  private def isHeader(position: Int, url: String, originalContent: String): Boolean =
    !originalContent
      .substring(0, position)
      .replaceAll("^\\{?\\s+", "")
      .contains('\n')

  private def suggestWithPatchedEnvironment(language: String,
                                            url: String,
                                            patchedContent: PatchedContent,
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
                       patchedContent,
                       snippetsSupport)
      .flatMap(_.suggest())
  }

  private def buildCompletionProviderAST(bu: BaseUnit,
                                         dialect: Dialect,
                                         url: String,
                                         pos: DtoPosition,
                                         patchedContent: PatchedContent,
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
               patchedContent,
               snippetSupport))
  }
}

trait SuggestionsHelper {

  def amfParse(url: String, environment: Environment, platform: Platform): Future[BaseUnit] =
    ParserHelper(platform).parse(url, environment).map(_.baseUnit)

  def getMediaType(originalContent: String): Syntax = {

    val trimmed = originalContent.trim
    if (trimmed.startsWith("{") || trimmed.startsWith("[")) Syntax.JSON
    else Syntax.YAML
  }

  def patchContentInEnvironment(environment: Environment,
                                fileUrl: String,
                                fileContentsStr: String,
                                position: Int): (PatchedContent, Environment) = {

    val patchedContent = ContentPatcher(fileContentsStr, position, YAML).prepareContent()
    val envWithOverride =
      EnvironmentPatcher.patch(environment, fileUrl, patchedContent.content)

    (patchedContent, envWithOverride)
  }
}
