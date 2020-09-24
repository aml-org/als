package org.mulesoft.amfintegration.vocabularies.propertyterms.patched.raml.raml08

import org.mulesoft.amfintegration.dialect.dialects.raml.raml08.Raml08TypesDialect
import org.mulesoft.amfintegration.vocabularies.propertyterms.patched.PatchedKeyTerm

trait Raml08PatchedKeyTerm extends PatchedKeyTerm {
  override final lazy val dialectId: String = Raml08TypesDialect.dialect.id
}
