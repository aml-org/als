package org.mulesoft.amfintegration.vocabularies.propertyterms.patched.oaslike

import org.mulesoft.amfintegration.vocabularies.propertyterms.patched.WebApiPatchedTopLevelKeyTerm

trait OaslikeComponentsKeyTerm extends WebApiPatchedTopLevelKeyTerm {
  override val key: String         = "components"
  override val description: String = "Contains reusable definitions"
}
