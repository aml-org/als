package org.mulesoft.amfintegration.vocabularies.propertyterms.patched.oaslike.oas2

import org.mulesoft.amfintegration.vocabularies.propertyterms.patched.WebApiPatchedTopLevelKeyTerm

object Oas2BasePathKeyTerm extends Oas2PatchedKeyTerm with WebApiPatchedTopLevelKeyTerm {
  override val key: String         = "basePath"
  override val description: String = "URL prefix to all API endpoints"
}
