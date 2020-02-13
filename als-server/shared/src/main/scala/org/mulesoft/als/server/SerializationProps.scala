package org.mulesoft.als.server

import org.mulesoft.als.server.client.AlsClientNotifier
import org.mulesoft.als.server.feature.serialization.{SerializationMessage, SerializationParams}
import org.mulesoft.lsp.feature.RequestType
import org.yaml.builder.DocBuilder

abstract class SerializationProps[S](val alsClientNotifier: AlsClientNotifier[S]) {

  def newDocBuilder(): DocBuilder[S]
  val requestType: RequestType[SerializationParams, SerializationMessage[S]] =
    new RequestType[SerializationParams, SerializationMessage[S]] {}
}
