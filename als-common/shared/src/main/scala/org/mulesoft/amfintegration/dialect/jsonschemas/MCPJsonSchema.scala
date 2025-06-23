package org.mulesoft.amfintegration.dialect.jsonschemas

import amf.mcp.internal.plugins.parse.schema.MCPSchema

object MCPJsonSchema extends InMemoryJsonSchema {
  override val name: String = "mcp-schema"

  override val fileContent: String = MCPSchema.schema
}