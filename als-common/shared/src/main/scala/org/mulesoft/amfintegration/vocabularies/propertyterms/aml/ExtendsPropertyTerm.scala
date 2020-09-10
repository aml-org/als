package org.mulesoft.amfintegration.vocabularies.propertyterms.aml

import org.mulesoft.amfintegration.vocabularies.PropertyTermObjectNode

object ExtendsPropertyTerm extends PropertyTermObjectNode {
  override val name: String = "extends"
  override val description: String =
    "Element that is going to be extended overlaying or adding additional information. The type of the relationship determines how the properties of the element will be combined together to build the resulting element."
}
