package org.mulesoft.amfintegration.vocabularies.propertyterms.patched.oas2

import amf.plugins.domain.webapi.metamodel.WebApiModel
import org.mulesoft.amfintegration.vocabularies.propertyterms.patched.PatchedKeyTerm

object Oas2InfoKeyTerm extends Oas2PatchedKeyTerm {
  override val key: String           = "info"
  override val amfObjectName: String = WebApiModel.`type`.head.name
  override val description: String   = "Contains general information about the defined API, such as title and version "
}
