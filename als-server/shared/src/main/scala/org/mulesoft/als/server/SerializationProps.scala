package org.mulesoft.als.server

import org.mulesoft.als.server.client.AlsClientNotifier
import org.mulesoft.als.server.feature.serialization.{SerializationResult, SerializationParams}
import org.mulesoft.lsp.feature.RequestType
import org.yaml.builder.DocBuilder

abstract class SerializationProps[S](val alsClientNotifier: AlsClientNotifier[S]) {

  def newDocBuilder(prettyPrint: Boolean): DocBuilder[S]
  val requestType: RequestType[SerializationParams, SerializationResult[S]] =
    new RequestType[SerializationParams, SerializationResult[S]] {}
}
