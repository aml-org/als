package org.mulesoft.amfintegration.vocabularies.propertyterms.patched.oaslike.asyncapi2

import org.mulesoft.amfintegration.dialect.dialects.asyncapi20.AsyncApi20Dialect
import org.mulesoft.amfintegration.vocabularies.propertyterms.patched.PatchedKeyTerm

trait AsyncApi20PatchedKeyTerm extends PatchedKeyTerm {
  override lazy val dialectId: String = AsyncApi20Dialect.dialect.id
}
