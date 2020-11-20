package org.mulesoft.amfintegration.vocabularies.propertyterms.patched

import amf.plugins.domain.webapi.metamodel.api.AsyncApiModel

trait AsyncApiPatchedTopLevelKeyTerm extends PatchedKeyTerm {
  override val amfObjectName: String = AsyncApiModel.`type`.head.name
}
