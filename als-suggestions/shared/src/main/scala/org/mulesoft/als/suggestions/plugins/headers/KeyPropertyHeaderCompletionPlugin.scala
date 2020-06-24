package org.mulesoft.als.suggestions.plugins.headers

import amf.plugins.document.vocabularies.AMLPlugin
import org.mulesoft.als.configuration.Configuration
import org.mulesoft.als.suggestions.interfaces.HeaderCompletionPlugin
import org.mulesoft.als.suggestions.{HeaderCompletionParams, RawSuggestion}
import org.mulesoft.amfintegration.ALSAMLPlugin

import scala.concurrent.Future

object KeyPropertyHeaderCompletionPlugin extends HeaderCompletionPlugin {
  override def id: String = "KeyPropertyHeaderCompletionPlugin"

  // todo: remove this three when internal dialect initialization is done
  private def swaggerHeader(isJson: Boolean, hasBracket: Boolean) = {
    val text = (if (isJson) jsonFlavour("swagger", "2.0", hasBracket)
                else yamlFlavour("swagger", "2.0"))._1
    RawSuggestion(text, text, s"Define an OAS 2.0 file", Seq(), children = Nil)
  }

  private def openApi(isJson: Boolean, hasBracket: Boolean) = {
    val text = (if (isJson) jsonFlavour("openapi", "3.0.0", hasBracket)
                else yamlFlavour("openapi", "3.0.0"))._1
    RawSuggestion(text, text, s"Define an OpenApi 3.0.0 file", Seq(), children = Nil)
  }

  private def asyncApi(isJson: Boolean, hasBracket: Boolean) = {
    val text = (if (isJson) jsonFlavour("asyncapi", "2.0.0", hasBracket)
                else yamlFlavour("asyncapi", "2.0.0"))._1
    RawSuggestion(text, text, s"Define an AsyncApi 2.0.0 file", Seq(), children = Nil)
  }

  private def yamlFlavour(key: String, value: String) =
    (s"$key: ${"\"" + value + "\""}", false)

  private def jsonFlavour(key: String, value: String, hasBracket: Boolean) = {
    if (hasBracket)
      (simpleContent(key, value), false)
    else
      (inBrackets(simpleContent(key, value)), Configuration.snippetsEnabled)
  }

  private def simpleContent(key: String, value: String) =
    s"${"\"" + key + "\""}: ${"\"" + value + "\""}"

  private def inBrackets(text: String) =
    s"{\n${text.linesIterator.map(l => s"  $l").mkString("\n")}\n}"

  private def getSuggestions(isJson: Boolean,
                             hasBracket: Boolean = false,
                             aslAmlPlugin: ALSAMLPlugin): Seq[RawSuggestion] = {
    aslAmlPlugin.registry.amlAdnWebApiDialects
      .filter(d => Option(d.documents()).exists(_.keyProperty().value()))
      .map(d => {
        val (text, isASnippet) =
          if (isJson)
            jsonFlavour(d.name().value(), d.version().value(), hasBracket)
          else yamlFlavour(d.name().value(), d.version().value())

        new RawSuggestion(text, text, s"Define a ${d.nameAndVersion()} file", Seq(), children = Nil)
      })
      .toSeq // TODO: remove when OAS is added as a Dialect
  }

  override def resolve(params: HeaderCompletionParams): Future[Seq[RawSuggestion]] =
    Future.successful(
      getSuggestions(params.uri.endsWith(".json"),
                     params.content.trim.startsWith("{"),
                     params.amfInstance.alsAmlPlugin)
    )
}
