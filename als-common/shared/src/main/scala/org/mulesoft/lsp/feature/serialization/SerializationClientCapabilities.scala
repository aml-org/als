package org.mulesoft.lsp.feature.serialization

/**
  * Capabilities specific to `workspace/serializedModel`.
  *
  * @param acceptsNotification Whether the clients accepts notification with the serialized resolved model.
  */
case class SerializationClientCapabilities(acceptsNotification: Boolean = false)
