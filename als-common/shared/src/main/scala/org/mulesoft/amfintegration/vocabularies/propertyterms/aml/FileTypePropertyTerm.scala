package org.mulesoft.amfintegration.vocabularies.propertyterms.aml

import org.mulesoft.amfintegration.vocabularies.PropertyTermObjectNode

object FileTypePropertyTerm extends PropertyTermObjectNode {
  override val name: String        = "fileType"
  override val description: String = "Valid content-type strings for a file"
}
