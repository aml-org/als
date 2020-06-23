package org.mulesoft.als.suggestions

import org.mulesoft.als.common.dtoTypes.Position
import org.mulesoft.amfintegration.AmfInstance

case class HeaderCompletionParams(uri: String, content: String, position: Position, amfInstance: AmfInstance)
