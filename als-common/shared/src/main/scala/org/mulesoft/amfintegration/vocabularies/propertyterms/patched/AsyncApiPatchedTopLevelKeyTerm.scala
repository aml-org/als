package org.mulesoft.amfintegration.vocabularies.propertyterms.patched

import amf.apicontract.internal.metamodel.domain.api.AsyncApiModel

trait AsyncApiPatchedTopLevelKeyTerm extends PatchedKeyTerm {
  override val amfObjectName: String = AsyncApiModel.`type`.head.name
}
