package org.mulesoft.als.server.lsp4j.extension

import java.io.StringWriter

import org.mulesoft.als.server.feature.serialization.{
  SerializationMessage,
  SerializationParams => InternalSerializationParams
}
import org.mulesoft.lsp.feature.RequestType

object JvmSerializationRequestType extends RequestType[InternalSerializationParams, SerializationMessage[StringWriter]]
