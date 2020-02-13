package org.mulesoft.als.server.feature.serialization

case class SerializationMessage[S](uri: String, model: S)
