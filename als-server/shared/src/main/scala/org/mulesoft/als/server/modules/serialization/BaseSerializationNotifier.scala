package org.mulesoft.als.server.modules.serialization

import amf.aml.client.scala.AMLConfiguration
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
    logger: Logger
) extends ClientNotifierModule[SerializationClientCapabilities, SerializationServerOptions] {

  protected var enabled = false

  protected def serialize(baseUnit: BaseUnit, amlConfiguration: AMLConfiguration): SerializationResult[S] = {
    val value = props.newDocBuilder(configurationReader.getShouldPrettyPrintSerialization)
    AMLSpecificConfiguration(amlConfiguration).asJsonLD(baseUnit, value)
    SerializationResult(baseUnit.identifier, value.result)
  }

  protected def serializeAndNotify(baseUnit: BaseUnit, amlConfiguration: AMLConfiguration): Unit =
    props.alsClientNotifier.notifySerialization(serialize(baseUnit, amlConfiguration))

  override def applyConfig(config: Option[SerializationClientCapabilities]): SerializationServerOptions = {
    config.foreach(c => enabled = c.acceptsNotification)
    logger.debug(s"Serialization manager enabled: $enabled", "SerializationManager", "applyConfig")
    SerializationServerOptions(true)
  }
}
