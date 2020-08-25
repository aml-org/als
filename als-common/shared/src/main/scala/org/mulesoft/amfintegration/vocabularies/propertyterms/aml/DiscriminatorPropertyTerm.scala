package org.mulesoft.amfintegration.vocabularies.propertyterms.aml

import org.mulesoft.amfintegration.vocabularies.PropertyTermObjectNode

object DiscriminatorPropertyTerm extends PropertyTermObjectNode {
  override val name: String        = "discriminator"
  override val description: String = "An object name that is used to differentiate between ambiguous types/schemas"
}
