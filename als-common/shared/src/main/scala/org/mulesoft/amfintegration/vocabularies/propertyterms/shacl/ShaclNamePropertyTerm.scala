package org.mulesoft.amfintegration.vocabularies.propertyterms.shacl

import org.mulesoft.amfintegration.vocabularies.PropertyTermObjectNode

object ShaclNamePropertyTerm extends PropertyTermObjectNode {
  override val name: String        = "name"
  override val description: String = "Human readable name for the object"
}
