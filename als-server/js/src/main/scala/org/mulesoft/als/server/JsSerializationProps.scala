package org.mulesoft.als.server

import org.mulesoft.als.server.client.platform.AlsClientNotifier
import org.yaml.builder.{DocBuilder, JsOutputBuilder}

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

@JSExportAll
@JSExportTopLevel("JsSerializationProps")
case class JsSerializationProps(override val alsClientNotifier: AlsClientNotifier[js.Any])
    extends SerializationProps[js.Any](alsClientNotifier) {
  override def newDocBuilder(prettyPrint: Boolean): DocBuilder[js.Any] =
    JsOutputBuilder() // TODO: JsOutputBuilder with prettyPrint
}
