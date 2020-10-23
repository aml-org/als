package org.mulesoft.amfintegration.vocabularies.propertyterms.schemaorg

import org.mulesoft.amfintegration.vocabularies.PropertyTermObjectNode

object CorrelationIdPropertyTerm extends PropertyTermObjectNode {
  override val name: String        = "correlationId"
  override val description: String = "An identifier that can be used for message tracing and correlation"
}
