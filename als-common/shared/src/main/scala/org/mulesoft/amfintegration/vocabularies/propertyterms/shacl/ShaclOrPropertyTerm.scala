package org.mulesoft.amfintegration.vocabularies.propertyterms.shacl

import org.mulesoft.amfintegration.vocabularies.PropertyTermObjectNode

object ShaclOrPropertyTerm extends PropertyTermObjectNode {
  override val name: String        = "or"
  override val description: String = "Each element should match against at least one of the elements provided"
}
