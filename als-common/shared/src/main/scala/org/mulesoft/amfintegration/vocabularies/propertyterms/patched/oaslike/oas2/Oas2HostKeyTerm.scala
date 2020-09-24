package org.mulesoft.amfintegration.vocabularies.propertyterms.patched.oaslike.oas2

import org.mulesoft.amfintegration.vocabularies.propertyterms.patched.PatchedTopLevelKeyTerm

object Oas2HostKeyTerm extends Oas2PatchedKeyTerm with PatchedTopLevelKeyTerm {
  override val key: String         = "host"
  override val description: String = "The domain name or IPv4 of the server that hosts the API"
}
