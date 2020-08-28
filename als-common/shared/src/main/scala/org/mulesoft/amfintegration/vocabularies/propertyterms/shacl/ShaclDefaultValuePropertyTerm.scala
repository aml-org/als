package org.mulesoft.amfintegration.vocabularies.propertyterms.shacl

import org.mulesoft.amfintegration.vocabularies.PropertyTermObjectNode

object ShaclDefaultValuePropertyTerm extends PropertyTermObjectNode {
  override val name: String        = "defaultValue"
  override val description: String = "Default value for this element"
}
