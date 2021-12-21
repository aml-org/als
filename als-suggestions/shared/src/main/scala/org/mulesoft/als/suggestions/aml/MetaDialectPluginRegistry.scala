package org.mulesoft.als.suggestions.aml

import amf.aml.client.scala.model.document.Dialect
import org.mulesoft.als.suggestions.AMLBaseCompletionPlugins
import org.mulesoft.als.suggestions.aml.webapi.WebApiCompletionPluginRegistry
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.plugins.aml.metadialect._
import org.mulesoft.als.suggestions.plugins.aml.{ResolveDefault, StructureCompletionPlugin}
import org.mulesoft.amfintegration.dialect.dialects.metadialect.MetaDialect

object MetaDialectPluginRegistry extends WebApiCompletionPluginRegistry {

  private val all: Seq[AMLCompletionPlugin] =
    AMLBaseCompletionPlugins.all :+
      StructureCompletionPlugin(
        List(
          MetaDialectDocumentsCompletionPlugin,
          NodeUnionDeclarationCompletionPlugin,
          ResolveDefault
        )) :+
      NamespaceForTermCompletionPlugin :+
      AnyUriValueCompletionPlugin :+
      MapLabelInPropertyMappingCompletionPlugin :+
      VocabularyTermsValueCompletionPlugin :+
      UnionRangeCompletionPlugin :+
      MetaDialectExtensionsCompletionPlugin

  override def plugins: Seq[AMLCompletionPlugin] = all

  override def dialect: Dialect = MetaDialect()
}
