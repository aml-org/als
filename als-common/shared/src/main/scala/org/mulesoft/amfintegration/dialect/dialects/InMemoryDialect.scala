package org.mulesoft.amfintegration.dialect.dialects

import org.mulesoft.amfintegration.dialect.InMemoryDocument

trait InMemoryDialect extends InMemoryDocument {
  override final val extension: String = "yaml"
}
