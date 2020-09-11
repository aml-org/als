package org.mulesoft.als.suggestions.plugins.headers

import amf.core.remote.FileMediaType
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.configuration.{AlsConfigurationReader, Configuration}
import org.mulesoft.als.suggestions.interfaces.HeaderCompletionPlugin
import org.mulesoft.als.suggestions.{HeaderCompletionParams, RawSuggestion}
import org.mulesoft.amfintegration.ALSAMLPlugin

import scala.concurrent.Future

object KeyPropertyHeaderCompletionPlugin extends HeaderCompletionPlugin {
  override def id: String = "KeyPropertyHeaderCompletionPlugin"

  override def resolve(params: HeaderCompletionParams): Future[Seq[RawSuggestion]] =
    Future.successful(
      KeyPropertyHeaderCompletionPlugin(params.uri.endsWith(".json"),
                                        params.content.trim.startsWith("{"),
                                        params.amfInstance.alsAmlPlugin,
                                        params.position,
                                        params.configuration).getSuggestions
    )

  def apply(isJson: Boolean,
            hasBracket: Boolean = false,
            alsAmlPlugin: ALSAMLPlugin,
            position: Position,
            configuration: AlsConfigurationReader) =
    new KeyPropertyHeaderCompletionPlugin(isJson, hasBracket, alsAmlPlugin, position, configuration)
}

class KeyPropertyHeaderCompletionPlugin(isJson: Boolean,
                                        hasBracket: Boolean = false,
                                        alsAmlPlugin: ALSAMLPlugin,
                                        position: Position,
                                        configuration: AlsConfigurationReader) {

  private lazy val mimeType = FileMediaType
    .mimeFromExtension(if (isJson) "json" else "yaml")
    .getOrElse("default")
  private lazy val formattingOptions =
    configuration.getFormatOptionForMime(mimeType)

  private def yamlFlavour(key: String, value: String) =
    (s"$key: ${"\"" + value + "\""}", false)

  private def jsonFlavour(key: String, value: String, hasBracket: Boolean, position: Position) = {
    if (hasBracket)
      (simpleContent(key, value, position), false)
    else
      (inBrackets(simpleContent(key, value, position)), Configuration.snippetsEnabled)
  }

  private def simpleContent(key: String, value: String, position: Position) =
    (if (position.column == 0) jsonPrefix else "") + s"${"\"" + key + "\""}: ${"\"" + value + "\""}"

  private def jsonPrefix =
    if (formattingOptions.insertSpaces) " " * formattingOptions.indentationSize else "\t"

  private def inBrackets(text: String) =
    s"{\n${text.linesIterator.map(l => s"  $l").mkString("\n")}\n}"

  private def getSuggestions: Seq[RawSuggestion] = {
    alsAmlPlugin.registry.amlAdnWebApiDialects
      .filter(d => Option(d.documents()).exists(_.keyProperty().value()))
      .map(d => {
        val (text, isASnippet) =
          if (isJson)
            jsonFlavour(d.name().value(), d.version().value(), hasBracket, position)
          else yamlFlavour(d.name().value(), d.version().value())

        new RawSuggestion(text, text.trim, s"Define a ${d.nameAndVersion()} file", Seq(), children = Nil)
      })
      .toSeq // TODO: remove when OAS is added as a Dialect
  }
}
