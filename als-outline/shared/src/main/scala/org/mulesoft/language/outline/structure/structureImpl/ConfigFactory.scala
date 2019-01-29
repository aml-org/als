package org.mulesoft.language.outline.structure.structureImpl

import org.mulesoft.language.outline.common.commonInterfaces.IASTProvider
import org.mulesoft.language.outline.structure.config.StructurePluginRegistry
import org.mulesoft.language.outline.structure.structureDefault.DefaultKeyProvider
import org.mulesoft.language.outline.structure.structureInterfaces.StructureConfiguration

object ConfigFactory {

  def getConfig(astProvider: IASTProvider): Option[StructureConfiguration] = {

    val plugin = StructurePluginRegistry.plugins.find(plugin => plugin.accepts(astProvider))

    if (plugin.isDefined) {

      val languageConfig = plugin.get.buildConfig(astProvider)

      Some(StructureConfiguration(
        astProvider,
        languageConfig.labelProvider,
        languageConfig.contentProvider,
        languageConfig.categories,
        languageConfig.decorators,
        new DefaultKeyProvider(),
        languageConfig.visibilityFilter
      ))
    } else {

      None
    }
  }
}
