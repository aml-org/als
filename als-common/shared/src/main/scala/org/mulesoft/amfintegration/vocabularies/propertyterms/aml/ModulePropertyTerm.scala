package org.mulesoft.amfintegration.vocabularies.propertyterms.aml

import org.mulesoft.amfintegration.vocabularies.PropertyTermObjectNode

object ModulePropertyTerm extends PropertyTermObjectNode {
  override val name: String = "Module"
  override val description: String =
    "Modules/Libraries are used to combine any collection of elements declarations into modular, externalized, reusable groups."
}
