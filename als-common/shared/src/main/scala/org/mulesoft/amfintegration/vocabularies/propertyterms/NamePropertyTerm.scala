package org.mulesoft.amfintegration.vocabularies.propertyterms

import org.mulesoft.amfintegration.vocabularies.PropertyTermObjectNode

object NamePropertyTerm extends PropertyTermObjectNode {
  override val name: String        = "name"
  override val description: String = "Human readable name for the object"
}
