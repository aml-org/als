package org.mulesoft.als.suggestions.patcher

import org.mulesoft.als.suggestions.interfaces.Syntax
import org.mulesoft.als.suggestions.interfaces.Syntax.YAML


trait ContentPatcher {

  val textRaw: String
  val offsetRaw: Int
  def prepareContent(): PatchedContent
}

object ContentPatcher {

  def apply(text: String, offset: Int, syntax: Syntax): ContentPatcher =
    if (text.trim.startsWith("{"))
      new JsonContentPatcher(text, offset)
    else
      syntax match {
        case YAML => new YamlContentPatcher(text, offset)
        case _    => throw new Error(s"Syntax not supported: $syntax")
      }
}
