package org.mulesoft.amfintegration.vocabularies.propertyterms.aml

import org.mulesoft.amfintegration.vocabularies.PropertyTermObjectNode

object InheritsPropertyTerm extends PropertyTermObjectNode {
  override val name: String        = "inherits"
  override val description: String = "Name of another element from which its properties/value will be inherited"
}
