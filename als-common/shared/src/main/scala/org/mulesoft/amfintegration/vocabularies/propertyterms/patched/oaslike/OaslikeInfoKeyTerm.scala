package org.mulesoft.amfintegration.vocabularies.propertyterms.patched.oaslike

import org.mulesoft.amfintegration.vocabularies.propertyterms.patched.{PatchedKeyTerm, WebApiPatchedTopLevelKeyTerm}

trait OaslikeInfoKeyTerm extends PatchedKeyTerm with WebApiPatchedTopLevelKeyTerm {
  override val key: String         = "info"
  override val description: String = "Contains general information about the defined API, such as title and version"
}
