package org.mulesoft.amfintegration.vocabularies.propertyterms.patched.oaslike.asyncapi2

import amf.plugins.domain.webapi.metamodel.WebApiModel
import org.mulesoft.amfintegration.vocabularies.propertyterms.patched.PatchedTopLevelKeyTerm

object AsyncApi20IdKeyTerm extends AsyncApi20PatchedKeyTerm with PatchedTopLevelKeyTerm {
  override val key: String = "id"
  override val description: String =
    "Represents the universal identifier of the application the specification is defining"
}
