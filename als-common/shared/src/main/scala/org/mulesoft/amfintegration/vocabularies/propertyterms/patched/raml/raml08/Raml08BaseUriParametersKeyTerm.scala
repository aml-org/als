package org.mulesoft.amfintegration.vocabularies.propertyterms.patched.raml.raml08

import org.mulesoft.amfintegration.vocabularies.propertyterms.patched.PatchedTopLevelKeyTerm

object Raml08BaseUriParametersKeyTerm extends Raml08PatchedKeyTerm with PatchedTopLevelKeyTerm {
  override val key: String = "baseUriParameters"
  override val description: String =
    "Template variables appearing in the baseURI can be further described in baseUriParameters"
}
