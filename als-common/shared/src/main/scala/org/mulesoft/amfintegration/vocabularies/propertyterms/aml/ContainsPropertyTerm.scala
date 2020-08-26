package org.mulesoft.amfintegration.vocabularies.propertyterms.aml

import org.mulesoft.amfintegration.vocabularies.PropertyTermObjectNode

object ContainsPropertyTerm extends PropertyTermObjectNode {
  override val name: String        = "contains"
  override val description: String = "One of the elements should match the value of this key"
}
