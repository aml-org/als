package org.mulesoft.amfintegration.vocabularies.propertyterms.patched.oaslike.asyncapi2

import org.mulesoft.amfintegration.vocabularies.propertyterms.patched.AsyncApiPatchedTopLevelKeyTerm

object AsyncApi20IdKeyTerm extends AsyncApi20PatchedKeyTerm with AsyncApiPatchedTopLevelKeyTerm {
  override val key: String = "id"
  override val description: String =
    "Represents the universal identifier of the application the specification is defining"
}
