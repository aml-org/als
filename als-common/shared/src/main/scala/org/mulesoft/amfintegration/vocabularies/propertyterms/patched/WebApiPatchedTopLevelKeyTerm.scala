package org.mulesoft.amfintegration.vocabularies.propertyterms.patched

import amf.apicontract.internal.metamodel.domain.api.WebApiModel

trait WebApiPatchedTopLevelKeyTerm extends PatchedKeyTerm {
  override val amfObjectName: String = WebApiModel.`type`.head.name
}
