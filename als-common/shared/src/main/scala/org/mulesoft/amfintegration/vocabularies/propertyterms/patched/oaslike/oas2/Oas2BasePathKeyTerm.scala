package org.mulesoft.amfintegration.vocabularies.propertyterms.patched.oaslike.oas2

import amf.plugins.domain.webapi.metamodel.WebApiModel
import org.mulesoft.amfintegration.vocabularies.propertyterms.patched.PatchedTopLevelKeyTerm

object Oas2BasePathKeyTerm extends Oas2PatchedKeyTerm with PatchedTopLevelKeyTerm {
  override val key: String         = "basePath"
  override val description: String = "URL prefix to all API endpoints"
}
