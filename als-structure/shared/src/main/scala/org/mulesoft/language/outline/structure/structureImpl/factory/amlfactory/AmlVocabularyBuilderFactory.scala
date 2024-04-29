package org.mulesoft.language.outline.structure.structureImpl.factory.amlfactory

import org.mulesoft.language.outline.structure.structureImpl.companion.FieldCompanionList
import org.mulesoft.language.outline.structure.structureImpl.factory.webapi.AmfBuilderFactory
import org.mulesoft.language.outline.structure.structureImpl.symbol.amlbuilders.AmlPropertyTermsFieldSymbolBuilder

object AmlVocabularyBuilderFactory extends AmfBuilderFactory {
  override protected def companion: FieldCompanionList =
    super.companion + AmlPropertyTermsFieldSymbolBuilder
}
