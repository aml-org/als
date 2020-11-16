package org.mulesoft.amfintegration.vocabularies.propertyterms.patched

import amf.plugins.domain.webapi.metamodel.api.WebApiModel

trait WebApiPatchedTopLevelKeyTerm extends PatchedKeyTerm {
  override val amfObjectName: String = WebApiModel.`type`.head.name
}
