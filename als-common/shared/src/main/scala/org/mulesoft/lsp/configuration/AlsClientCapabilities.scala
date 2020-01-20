package org.mulesoft.lsp.configuration

import org.mulesoft.lsp.feature.serialization.SerializationClientCapabilities

case class AlsClientCapabilities(serialization: Option[SerializationClientCapabilities])
