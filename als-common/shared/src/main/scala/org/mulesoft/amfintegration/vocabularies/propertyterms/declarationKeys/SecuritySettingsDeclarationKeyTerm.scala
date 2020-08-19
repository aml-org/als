package org.mulesoft.amfintegration.vocabularies.propertyterms.declarationKeys

import org.mulesoft.amfintegration.vocabularies.PropertyTermObjectNode

object SecuritySettingsDeclarationKeyTerm extends PropertyTermObjectNode {
  // This could end up colliding with another term, it now represents SettingsModel for OAS
  override val name: String        = "SettingsDeclarationKey"
  override val description: String = "Contains declarations of reusable SecuritySchemes"
}
