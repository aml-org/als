package org.mulesoft.amfintegration.vocabularies.propertyterms.patched.oaslike.asyncapi2

import org.mulesoft.amfintegration.vocabularies.propertyterms.patched.{AsyncApiPatchedTopLevelKeyTerm, PatchedKeyTerm}

trait AsynclikeInfoKeyTerm extends PatchedKeyTerm with AsyncApiPatchedTopLevelKeyTerm {
  override val key: String         = "info"
  override val description: String = "Contains general information about the defined API, such as title and version"
}
