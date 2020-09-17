package org.mulesoft.amfintegration.vocabularies.propertyterms.patched.oaslike

import org.mulesoft.amfintegration.vocabularies.propertyterms.patched.PatchedTopLevelKeyTerm

trait OaslikeComponentsKeyTerm extends PatchedTopLevelKeyTerm {
  override val key: String         = "components"
  override val description: String = "Contains reusable definitions"
}
