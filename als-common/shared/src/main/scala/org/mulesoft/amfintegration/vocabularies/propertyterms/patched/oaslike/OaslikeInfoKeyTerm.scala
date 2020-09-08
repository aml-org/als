package org.mulesoft.amfintegration.vocabularies.propertyterms.patched.oaslike

import org.mulesoft.amfintegration.vocabularies.propertyterms.patched.{PatchedKeyTerm, PatchedTopLevelKeyTerm}

trait OaslikeInfoKeyTerm extends PatchedKeyTerm with PatchedTopLevelKeyTerm {
  override val key: String         = "info"
  override val description: String = "Contains general information about the defined API, such as title and version"
}
