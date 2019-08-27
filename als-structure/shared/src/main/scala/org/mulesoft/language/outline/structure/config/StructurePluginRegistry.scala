package org.mulesoft.language.outline.structure.config

object StructurePluginRegistry {

  val plugins: Seq[IStructurePlugin] = Seq(
    new RAMLPlugin
  )
}
