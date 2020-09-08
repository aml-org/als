package org.mulesoft.amfintegration.vocabularies.propertyterms.patched.oaslike

import amf.plugins.domain.webapi.metamodel.WebApiModel
import org.mulesoft.amfintegration.vocabularies.propertyterms.patched.{PatchedKeyTerm, PatchedTopLevelKeyTerm}

trait OaslikeComponentsKeyTerm extends PatchedTopLevelKeyTerm {
  override val key: String         = "components"
  override val description: String = "Contains reusable definitions"
}
