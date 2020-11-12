package org.mulesoft.amfintegration.vocabularies.propertyterms.patched

import amf.plugins.domain.webapi.metamodel.api.WebApiModel

trait PatchedTopLevelKeyTerm extends PatchedKeyTerm {
  override val amfObjectName: String = WebApiModel.`type`.head.name
}
