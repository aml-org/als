package org.mulesoft.amfintegration.vocabularies.propertyterms.patched.raml.raml10

import org.mulesoft.amfintegration.vocabularies.propertyterms.patched.PatchedTopLevelKeyTerm

object Raml10UsesKeyTerm extends Raml10PatchedKeyTerm with PatchedTopLevelKeyTerm {
  override val key: String         = "uses"
  override val description: String = "Imported external libraries for use within the API"
}
