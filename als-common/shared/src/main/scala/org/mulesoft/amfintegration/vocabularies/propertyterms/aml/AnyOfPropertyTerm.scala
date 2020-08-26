package org.mulesoft.amfintegration.vocabularies.propertyterms.aml

import org.mulesoft.amfintegration.vocabularies.PropertyTermObjectNode

object AnyOfPropertyTerm extends PropertyTermObjectNode {
  override val name: String        = "or"
  override val description: String = "The schema could be any of the schemas included as values of this key"
}
