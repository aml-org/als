package org.mulesoft.als.suggestions

import org.mulesoft.als.common.dtoTypes.Position

case class HeaderCompletionParams(uri: String, content: String, position: Position)