package org.mulesoft.als.suggestions.aml

import amf.aml.client.scala.model.document.Dialect
import org.mulesoft.als.suggestions.AMLBaseCompletionPlugins
import org.mulesoft.als.suggestions.aml.webapi.WebApiCompletionPluginRegistry
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.vocabulary.{ClassTermUriCompletionPlugin, PropertyTermUriCompletionPlugin}
import org.mulesoft.als.suggestions.plugins.aml.webapi.raml.AMLLibraryPathCompletion
import org.mulesoft.amfintegration.dialect.dialects.metadialect.VocabularyDialect

object VocabularyDialectPluginRegistry extends WebApiCompletionPluginRegistry {

  private val all: Seq[AMLCompletionPlugin] =
    AMLBaseCompletionPlugins.all :+
      PropertyTermUriCompletionPlugin :+
      ClassTermUriCompletionPlugin :+
      AMLLibraryPathCompletion

  override def plugins: Seq[AMLCompletionPlugin] = all

  override def dialect: Dialect = VocabularyDialect.dialect
}
