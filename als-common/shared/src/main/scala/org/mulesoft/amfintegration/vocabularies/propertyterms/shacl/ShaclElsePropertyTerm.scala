package org.mulesoft.amfintegration.vocabularies.propertyterms.shacl

import org.mulesoft.amfintegration.vocabularies.PropertyTermObjectNode

object ShaclElsePropertyTerm extends PropertyTermObjectNode {
  override val name: String = "else"
  override val description: String =
    "Used to implement conditional application of a subschema. Instances that fail to validate against the \"if\" keyword's subschema will be validated agains this key subschema"
}
