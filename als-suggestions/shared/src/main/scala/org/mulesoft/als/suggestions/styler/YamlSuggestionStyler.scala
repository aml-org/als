package org.mulesoft.als.suggestions.styler

import org.mulesoft.als.suggestions._
import org.mulesoft.als.suggestions.styler.astbuilder.{AstRawBuilder, YamlAstRawBuilder}
import org.yaml.model._
import org.yaml.render.YamlRender

case class YamlSuggestionStyler(override val params: StylerParams) extends SuggestionRender {

  private def fixEmptyMap(rendered: String): String =
    if (rendered.endsWith("{}"))
      rendered.stripSuffix(" {}") + "\n  " + stringIden
    else rendered

  private def fixPrefix(prefix: String, text: String) = {
    if (prefix.isEmpty && text.startsWith(stringIden))
      text.stripPrefix(stringIden)
    else prefix + text
  }

  override protected def render(options: SuggestionStructure, builder: AstRawBuilder): String = {
    val prefix =
      if (!options.isKey && ((options.isArray && !params.yPartBranch.isInArray) || options.isObject)) // never will suggest object in value as is not key. Suggestions should be empty
        "\n"
      else ""
    val ast = builder.ast
    val indentation = ast match {
      case n: YNode if n.value.isInstanceOf[YSequence] => params.indentation + 2
      case _                                           => params.indentation
    }
    fixPrefix(prefix, fixEmptyMap(YamlRender.render(ast, indentation)))
  }

  override def astBuilder: RawSuggestion => AstRawBuilder =
    (raw: RawSuggestion) => new YamlAstRawBuilder(raw, false, params.yPartBranch)
}
