package org.mulesoft.amfintegration.vocabularies.propertyterms.shacl

import org.mulesoft.amfintegration.vocabularies.PropertyTermObjectNode

object ShaclIfPropertyTerm extends PropertyTermObjectNode {
  override val name: String = "if"
  override val description: String =
    "Used to implement conditional application of a subschema. Instances will be validated againts this keyword's subschema"
}
