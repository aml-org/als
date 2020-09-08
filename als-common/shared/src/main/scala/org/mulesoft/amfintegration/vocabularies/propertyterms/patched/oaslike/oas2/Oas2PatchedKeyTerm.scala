package org.mulesoft.amfintegration.vocabularies.propertyterms.patched.oaslike.oas2

import org.mulesoft.amfintegration.dialect.dialects.oas.OAS20Dialect
import org.mulesoft.amfintegration.vocabularies.propertyterms.patched.PatchedKeyTerm

trait Oas2PatchedKeyTerm extends PatchedKeyTerm {
  override lazy val dialectId: String = OAS20Dialect.dialect.id
}
