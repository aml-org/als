package org.mulesoft.als.suggestions.js

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
//import amf.core.client.ExitCodes
import amf.core.client.ParserConfig
import amf.core.model.document.{BaseUnit, Document}
import amf.core.remote._
//import amf.core.unsafe.PlatformSecrets
import org.mulesoft.als.suggestions.{CompletionProvider, Core}
import org.mulesoft.als.suggestions.interfaces.Syntax._
import org.mulesoft.als.suggestions.implementation.{CompletionConfig, DummyASTProvider, DummyEditorStateProvider}
import org.mulesoft.high.level.interfaces.IProject
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.js.JSConverters._

@JSExportTopLevel("Suggestions")
object Suggestions {

  private var platform: Platform = null;

  @JSExport
  def init(fsProvider: IFSProvider): Unit = {

    this.platform = new FSProviderBasedPlatform(fsProvider)
  }

  @JSExport
  def suggest(language: String, url: String, position: Int): js.Promise[js.Array[ISuggestion]] = {

    val config = this.buildParserConfig(language, url)

    val result = this.platform.resolve(url,None).map(content => {

      this.cacheUnit(url, content, position)

    }).flatMap(_=>{

      this.amfParse(config)

    }).flatMap(amfUnit=>{

      this.buildHighLevel(amfUnit)

    }).map(project=>{

      this.buildCompletionProvider(project, url, position)

    }).flatMap(_.suggest)
      .map(suggestions=>suggestions.map(suggestion=>{

        new ISuggestion(

          text = suggestion.text,

          description = suggestion.description,

          displayText = suggestion.displayText,

          prefix = suggestion.prefix,

          category = suggestion.category
        )

    })).map(suggestions=>suggestions.toJSArray)

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

    val helper = ParserHelper(this.platform)
    helper.parse(config)
  }

  def cacheUnit(fileUrl: String, content: Content, position: Int): Unit = {

    val fileContentsStr = content.stream.toString

    val patchedContent = Core.prepareText(fileContentsStr, position, YAML)

    File.unapply(fileUrl).foreach(x=>this.platform.cacheResourceText(
      x, patchedContent, content.mime))
  }

  def buildHighLevel(model:BaseUnit):Future[IProject] = {

      Core.init().flatMap(_=>org.mulesoft.high.level.Core.buildModel(model,platform))
  }

  def buildCompletionProvider(project: IProject, url: String, position: Int): CompletionProvider = {

    val rootUnit = project.rootASTUnit

    val astProvider = new DummyASTProvider(project,position)

    val baseName = url.substring(url.lastIndexOf('/') + 1)

    val editorStateProvider = new DummyEditorStateProvider(rootUnit.text,url,baseName,position)

    val completionConfig = new CompletionConfig()
      .withAstProvider(astProvider)
      .withEditorStateProvider(editorStateProvider)

    CompletionProvider().withConfig(completionConfig)
  }
}
