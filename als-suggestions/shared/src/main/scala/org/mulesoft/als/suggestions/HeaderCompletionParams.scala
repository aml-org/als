package org.mulesoft.als.suggestions

import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.als.configuration.AlsConfigurationReader
import org.mulesoft.amfintegration.amfconfiguration.AmfParseContext

case class HeaderCompletionParams(uri: String,
                                  content: String,
                                  position: Position,
                                  parseContext: AmfParseContext,
                                  configuration: AlsConfigurationReader)
