package org.mulesoft.als.suggestions.plugins.headers

import amf.aml.client.scala.model.document.Dialect
import amf.core.internal.remote.{FileMediaType, Spec}
import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.configuration.{AlsConfigurationReader, Configuration, TemplateTypes}
import org.mulesoft.als.suggestions.interfaces.HeaderCompletionPlugin
import org.mulesoft.als.suggestions.{HeaderCompletionParams, RawSuggestion}
import org.mulesoft.amfintegration.amfconfiguration.ALSConfigurationState

import scala.concurrent.Future

object KeyPropertyHeaderCompletionPlugin extends HeaderCompletionPlugin {
  override def id: String = "KeyPropertyHeaderCompletionPlugin"

  override def resolve(params: HeaderCompletionParams): Future[Seq[RawSuggestion]] =
    Future.successful(
      KeyPropertyHeaderCompletionPlugin(
        params.uri.endsWith(".json"),
        params.content.trim.startsWith("{"),
        params.parseContext.state,
        params.position,
        params.configuration
      ).getSuggestions
    )

  def apply(
      isJson: Boolean,
      hasBracket: Boolean = false,
      alsConfiguration: ALSConfigurationState,
      position: Position,
      configuration: AlsConfigurationReader
  ) =
    new KeyPropertyHeaderCompletionPlugin(isJson, hasBracket, alsConfiguration, position, configuration)
}

class KeyPropertyHeaderCompletionPlugin(
    isJson: Boolean,
    hasBracket: Boolean = false,
    configurationState: ALSConfigurationState,
    position: Position,
    configuration: AlsConfigurationReader
) {

  private lazy val mimeType = FileMediaType
    .mimeFromExtension(if (isJson) "json" else "yaml")
    .getOrElse("default")
  private lazy val formattingOptions =
    configuration.getFormatOptionForMime(mimeType)

  private def yamlFlavour(key: String, value: String): Flavour = {
    val yamlContent = s"$key: ${"\"" + value + "\""}"
    Flavour(yamlContent, yamlContent, snippets = false)
  }

  private def jsonFlavour(key: String, value: String, hasBracket: Boolean, position: Position): Flavour = {
    val sc = simpleContent(key, value, position)
    if (hasBracket)
      Flavour(sc, sc, snippets = false)
    else
      Flavour(inBrackets(sc), sc, configuration.getTemplateType != TemplateTypes.NONE)
  }

  private def simpleContent(key: String, value: String, position: Position) =
    (if (position.column == 0) jsonPrefix else "") + s"${"\"" + key + "\""}: ${"\"" + value + "\""}"

  private def jsonPrefix =
    if (formattingOptions.insertSpaces) " " * formattingOptions.tabSize else "\t"

  private def inBrackets(text: String) =
    s"{\n${text.linesIterator.map(l => s"  $l").mkString("\n")}\n}"

  private def getSuggestions: Seq[RawSuggestion] = {
    configurationState.allDialects
      .filter(d => Option(d.documents()).exists(_.keyProperty().value()))
      .filter(compliesFormat)
      .map(d => {
        val Flavour(text, label, isASnippet) =
          if (isJson)
            jsonFlavour(purgeName(d), d.version().value(), hasBracket, position)
          else yamlFlavour(purgeName(d), d.version().value())

        new RawSuggestion(text, label.trim, s"Define a ${purgedNameAndVersion(d)} file", Seq(), children = Nil)
      })
      .toSeq // TODO: remove when OAS is added as a Dialect
  }

  /** only suggest $schema for json documents
    * @param dialect
    * @return
    */
  private def compliesFormat(dialect: Dialect): Boolean =
    if (!isJson)
      !isJsonSchema(dialect)
    else true

  private def isJsonSchema(dialect: Dialect) =
    dialect.name().option().contains(Spec.JSONSCHEMA.toString)

  private def purgeName(d: Dialect) =
    if (isJsonSchema(d)) "$schema"
    else d.name().value()

  private def purgedNameAndVersion(d: Dialect) = {
    val nameAndVersion = d.nameAndVersion()
    if (nameAndVersion == "swagger 2.0") "openapi 2.0"
    else nameAndVersion
  }

  case class Flavour(content: String, label: String, snippets: Boolean)
}
