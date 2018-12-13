package org.mulesoft.language.outline.structure.config

object StructurePluginRegistry {

  val plugins = Seq(
    new RAMLPlugin,
    new OASPlugin,
    new AMLPlugin
  )
}
