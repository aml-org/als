package org.mulesoft.amfintegration.vocabularies.propertyterms.shacl

import org.mulesoft.amfintegration.vocabularies.PropertyTermObjectNode

object ShaclPathPropertyTerm extends PropertyTermObjectNode {
  override val name: String        = "path"
  override val description: String = "Path to the property affected by the constraint"
}
