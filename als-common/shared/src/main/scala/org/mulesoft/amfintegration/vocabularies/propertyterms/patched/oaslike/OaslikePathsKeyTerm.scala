package org.mulesoft.amfintegration.vocabularies.propertyterms.patched.oaslike

import org.mulesoft.amfintegration.vocabularies.propertyterms.patched.{PatchedKeyTerm, WebApiPatchedTopLevelKeyTerm}

trait OaslikePathsKeyTerm extends PatchedKeyTerm with WebApiPatchedTopLevelKeyTerm {
  override val key: String         = "paths"
  override val description: String = "Path to each declared endpoint. Relative to basePath"
}
