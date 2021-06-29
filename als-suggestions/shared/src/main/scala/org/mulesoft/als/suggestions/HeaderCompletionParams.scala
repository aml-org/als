package org.mulesoft.als.suggestions

import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.configuration.AlsConfigurationReader
import org.mulesoft.amfintegration.amfconfiguration.AmfConfigurationWrapper

case class HeaderCompletionParams(uri: String,
                                  content: String,
                                  position: Position,
                                  amfConfiguration: AmfConfigurationWrapper,
                                  configuration: AlsConfigurationReader)
