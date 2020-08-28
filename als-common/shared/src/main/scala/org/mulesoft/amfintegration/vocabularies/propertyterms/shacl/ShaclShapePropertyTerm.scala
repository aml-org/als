package org.mulesoft.amfintegration.vocabularies.propertyterms.shacl

import org.mulesoft.amfintegration.vocabularies.PropertyTermObjectNode

object ShaclShapePropertyTerm extends PropertyTermObjectNode {
  override val name: String                = "Shape"
  override val displayName: Option[String] = Some("Schema")
  override val description: String         = "Defines a data type"
}
