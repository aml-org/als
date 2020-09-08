package org.mulesoft.amfintegration.vocabularies.propertyterms.patched.oaslike

import org.mulesoft.amfintegration.vocabularies.propertyterms.patched.{PatchedKeyTerm, PatchedTopLevelKeyTerm}

trait OaslikePathsKeyTerm extends PatchedKeyTerm with PatchedTopLevelKeyTerm {
  override val key: String         = "paths"
  override val description: String = "Path to each declared endpoint. Relative to basePath"
}
