package org.mulesoft.als.server.modules.serialization

import amf.aml.client.scala.AMLConfiguration
import amf.core.client.scala.config.RenderOptions
import amf.core.client.scala.model.document.BaseUnit
import org.mulesoft.als.configuration.AlsConfigurationReader
import org.mulesoft.als.logger.Logger
import org.mulesoft.als.server.feature.serialization.{
  SerializationClientCapabilities,
  SerializationResult,
  SerializationServerOptions
}
import org.mulesoft.als.server.{ClientNotifierModule, SerializationProps}
import org.mulesoft.amfintegration.AmfImplicits._
import org.mulesoft.amfintegration.amfconfiguration.AMLSpecificConfiguration

abstract class BaseSerializationNotifier[S](
    props: SerializationProps[S],
    configurationReader: AlsConfigurationReader,
    renderProps: RenderProps
) extends ClientNotifierModule[SerializationClientCapabilities, SerializationServerOptions] {

  protected def enabled: Boolean = BaseSerializationNotifierState.enabled

  protected def serialize(baseUnit: BaseUnit, amlConfiguration: AMLConfiguration): SerializationResult[S] = {
    val value = props.newDocBuilder(configurationReader.getShouldPrettyPrintSerialization)
    AMLSpecificConfiguration(amlConfiguration).asJsonLD(baseUnit, value, renderOptions)
    SerializationResult(baseUnit.identifier, value.result)
  }

  private val renderOptions = {
    var options = RenderOptions()
    if (renderProps.compactUris)
      options = options.withCompactUris
    if (!renderProps.sourceMaps)
      options = options.withoutSourceMaps
    else options = options.withSourceMaps
    options
  }

  protected def serializeAndNotify(baseUnit: BaseUnit, amlConfiguration: AMLConfiguration): Unit =
    props.alsClientNotifier.notifySerialization(serialize(baseUnit, amlConfiguration))

  override def applyConfig(config: Option[SerializationClientCapabilities]): SerializationServerOptions = {
    config.foreach(c => BaseSerializationNotifierState.enabled = c.acceptsNotification)
    Logger.debug(s"Serialization manager enabled: $enabled", "SerializationManager", "applyConfig")
    SerializationServerOptions(true)
  }
}

private object BaseSerializationNotifierState {
  var enabled = false
}
