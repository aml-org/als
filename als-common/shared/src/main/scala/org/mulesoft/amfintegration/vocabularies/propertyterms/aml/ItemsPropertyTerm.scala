package org.mulesoft.amfintegration.vocabularies.propertyterms.aml

import org.mulesoft.amfintegration.vocabularies.PropertyTermObjectNode

object ItemsPropertyTerm extends PropertyTermObjectNode {
  override val name: String        = "items"
  override val description: String = "Determines how items of arrays are validated"
}
