package org.mulesoft.amfintegration.vocabularies.propertyterms.shacl

import org.mulesoft.amfintegration.vocabularies.PropertyTermObjectNode

object ShaclInPropertyTerm extends PropertyTermObjectNode {
  override val name: String        = "in"
  override val description: String = "Each element should be part of the provided list to be valid"
}
