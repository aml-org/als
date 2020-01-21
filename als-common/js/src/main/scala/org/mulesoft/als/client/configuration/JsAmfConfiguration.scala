package org.mulesoft.als.client.configuration

import amf.client.plugins.{ClientAMFPayloadValidationPlugin, ClientAMFPlugin}

import scala.scalajs.js

case class JsAmfConfiguration(plugins: js.Array[ClientAMFPayloadValidationPlugin],
                              jsServerSystemConf: JsServerSystemConf)

object DefaultJsAmfConfiguration extends JsAmfConfiguration(js.Array.apply(), DefaultJsServerSystemConf)
