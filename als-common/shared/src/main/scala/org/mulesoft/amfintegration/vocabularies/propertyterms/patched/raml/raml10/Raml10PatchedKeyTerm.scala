package org.mulesoft.amfintegration.vocabularies.propertyterms.patched.raml.raml10

import org.mulesoft.amfintegration.dialect.dialects.raml.raml10.Raml10TypesDialect
import org.mulesoft.amfintegration.vocabularies.propertyterms.patched.PatchedKeyTerm

trait Raml10PatchedKeyTerm extends PatchedKeyTerm {
  override final lazy val dialectId: String = Raml10TypesDialect.dialect.id
}
