package org.mulesoft.amfintegration.dialect.jsonschemas

import org.mulesoft.amfintegration.dialect.InMemoryDocument

trait InMemoryJsonSchema extends InMemoryDocument {
  override final val extension: String = "json"
}