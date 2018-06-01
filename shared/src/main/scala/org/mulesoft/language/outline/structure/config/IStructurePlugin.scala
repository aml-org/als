package org.mulesoft.language.outline.structure.config

import org.mulesoft.language.outline.common.commonInterfaces.IASTProvider


trait IStructurePlugin {

  def accepts(provider: IASTProvider): Boolean

  def buildConfig(provider: IASTProvider): LanguageDependendStructureConfig
}
