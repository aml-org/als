package org.mulesoft.amfintegration.vocabularies.propertyterms.shacl

import org.mulesoft.amfintegration.vocabularies.PropertyTermObjectNode

object ShaclNotPropertyTerm extends PropertyTermObjectNode {
  override val name: String = "not"
  override val description: String =
    "Objects will be valid if they fail to validate successfully against the schema defined by this keyword."
}
