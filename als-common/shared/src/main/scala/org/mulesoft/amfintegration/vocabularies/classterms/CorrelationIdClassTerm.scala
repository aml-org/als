package org.mulesoft.amfintegration.vocabularies.classterms

import org.mulesoft.amfintegration.vocabularies.ClassTermObjectNode

object CorrelationIdClassTerm extends ClassTermObjectNode {
  override val name: String        = "CorrelationId"
  override val description: String = "Model defining an identifier that can used for message tracing and correlation"
}
