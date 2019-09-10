package org.mulesoft.als.suggestions.plugins.headers

import amf.plugins.document.vocabularies.AMLPlugin
import org.mulesoft.als.configuration.Configuration
import org.mulesoft.als.suggestions.interfaces.HeaderCompletionPlugin
import org.mulesoft.als.suggestions.{HeaderCompletionParams, RawSuggestion}

import scala.concurrent.Future

object KeyPropertyHeaderCompletionPlugin extends HeaderCompletionPlugin {
  override def id: String = "KeyPropertyHeaderCompletionPlugin"

  private def swaggerHeader(isJson: Boolean, hasBracket: Boolean) = {
    val text = (if (isJson) jsonFlavour("swagger", "2.0", hasBracket)
                else yamlFlavour("swagger", "2.0"))._1
    RawSuggestion(text, text, s"Define a OAS 2.0 file", Seq(), "")
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
//    if (Configuration.snippetsEnabled) s"${"\"" + key + "\""}: ${"\"" + value + "\""},\n"
//    else
    s"${"\"" + key + "\""}: ${"\"" + value + "\""}"

  private def inBrackets(text: String) =
//    if (Configuration.snippetsEnabled)
//      s"{\n${text.linesIterator.map(l => s"  $l").mkString("\n")}\n  $$0\n}"
//    else
    s"{\n${text.linesIterator.map(l => s"  $l").mkString("\n")}\n}"

  private def getSuggestions(isJson: Boolean, hasBracket: Boolean = false): Seq[RawSuggestion] = {
    AMLPlugin.registry
      .allDialects()
      .filter(_.documents().keyProperty().value())
      .map(d => {
        val (text, isASnippet) =
          if (isJson)
            jsonFlavour(d.name().value(), d.version().value(), hasBracket)
          else yamlFlavour(d.name().value(), d.version().value())

        new RawSuggestion(text, text, s"Define a ${d.nameAndVersion()} file", Seq(), "")
      })
      .toSeq :+ swaggerHeader(isJson, hasBracket) // TODO: remove when OAS is added as a Dialect
  }

  override def resolve(params: HeaderCompletionParams): Future[Seq[RawSuggestion]] =
    Future.successful(
      getSuggestions(params.uri.endsWith(".json"), params.content.trim.startsWith("{"))
    )
}
