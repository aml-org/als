package org.mulesoft.amfintegration.vocabularies.propertyterms.patched.oaslike.asyncapi2

import org.mulesoft.amfintegration.vocabularies.propertyterms.patched.AsyncApiPatchedTopLevelKeyTerm

trait AsynclikeComponentsKeyTerm extends AsyncApiPatchedTopLevelKeyTerm {
  override val key: String         = "components"
  override val description: String = "Contains reusable definitions"
}
