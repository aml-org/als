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
    configurationReader: AlsConfigurationReader
) extends ClientNotifierModule[SerializationClientCapabilities, SerializationServerOptions] {

  protected def enabled: Boolean = BaseSerializationNotifierState.enabled

  protected def serialize(
      baseUnit: BaseUnit,
      amlConfiguration: AMLConfiguration,
      sourcemaps: Boolean
  ): SerializationResult[S] = {
    val value = props.newDocBuilder(configurationReader.getShouldPrettyPrintSerialization)
    AMLSpecificConfiguration(amlConfiguration).asJsonLD(baseUnit, value, renderOptions(sourcemaps))
    SerializationResult(baseUnit.identifier, value.result)
  }

  private def renderOptions(sourcemaps: Boolean) =
    if (sourcemaps) RenderOptions().withCompactUris.withSourceMaps.withSourceInformation
    else RenderOptions().withCompactUris.withoutSourceMaps

  protected def serializeAndNotify(baseUnit: BaseUnit, amlConfiguration: AMLConfiguration): Unit =
    props.alsClientNotifier.notifySerialization(serialize(baseUnit, amlConfiguration, sourcemaps = false))

  override def applyConfig(config: Option[SerializationClientCapabilities]): SerializationServerOptions = {
    config.foreach(c => BaseSerializationNotifierState.enabled = c.acceptsNotification)
    Logger.debug(s"Serialization manager enabled: $enabled", "SerializationManager", "applyConfig")
    SerializationServerOptions(true)
  }
}

private object BaseSerializationNotifierState {
  var enabled = false
}
