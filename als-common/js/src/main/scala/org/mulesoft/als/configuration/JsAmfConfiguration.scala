package org.mulesoft.als.configuration

import amf.client.plugins.ClientAMFPayloadValidationPlugin

import scala.scalajs.js

case class JsAmfConfiguration(plugins: js.Array[ClientAMFPayloadValidationPlugin],
                              jsServerSystemConf: JsServerSystemConf)

object DefaultJsAmfConfiguration extends JsAmfConfiguration(js.Array.apply(), DefaultJsServerSystemConf)
