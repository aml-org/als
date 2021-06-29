package org.mulesoft.amfintegration.vocabularies.integration

import amf.aml.client.scala.model.document.Vocabulary
import amf.aml.internal.parse.plugin.AMLVocabularyParsingPlugin
import amf.core.client.common.{HighPriority, PluginPriority}
import amf.core.client.scala.model.document.BaseUnit
import amf.core.client.scala.parse.document.ParserContext
import amf.core.internal.parser.Root
import org.mulesoft.amfintegration.AMLRegistry

case class AlsVocabularyParsingPlugin(vocabularyRegistry: AMLRegistry[Vocabulary]) extends AMLVocabularyParsingPlugin {

  override val id: String = "als-vocabulary-parsing-plugin"

  override def parse(document: Root, parentContext: ParserContext): BaseUnit = {
    super.parse(document, parentContext) match {
      case v: Vocabulary =>
        vocabularyRegistry.index(v)
        v
      case other => other
    }
  }

  override def priority: PluginPriority = HighPriority
}
