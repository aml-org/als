package org.mulesoft.amfintegration.vocabularies.propertyterms.patched.oaslike.oas3

import org.mulesoft.amfintegration.dialect.dialects.oas.OAS30Dialect
import org.mulesoft.amfintegration.vocabularies.propertyterms.patched.PatchedKeyTerm

trait Oas3PatchedKeyTerm extends PatchedKeyTerm {
  override lazy val dialectId: String = OAS30Dialect.dialect.id
}
