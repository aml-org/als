package org.mulesoft.als.client.convert

import amf.client.convert.ClientPayloadPluginConverter
import amf.client.plugins.{AMFPlugin, ClientAMFPayloadValidationPlugin}
import org.mulesoft.als.client.configuration.JsAmfConfiguration
import org.mulesoft.lsp.server.AmfConfiguration

object AmfConfigurationConverter {

  implicit class Converter(client: JsAmfConfiguration) {
    def asInternal: AmfConfiguration =
      new AmfConfiguration(client.plugins.toSeq.map(clientToAMFPlugin), client.jsServerSystemConf)

    // todo other ClientAMFPLugins? js trait not supports instance of
    private def clientToAMFPlugin(client: ClientAMFPayloadValidationPlugin): AMFPlugin = {
      ClientPayloadPluginConverter.convert(client)
    }
  }
}
