package org.mulesoft.als.suggestions.styler

import org.mulesoft.als.common.dtoTypes.PositionRange

case class Styled(text: String, plain: Boolean, replacementRange: PositionRange)
