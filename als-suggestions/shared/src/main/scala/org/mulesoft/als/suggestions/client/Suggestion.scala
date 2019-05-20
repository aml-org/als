package org.mulesoft.als.suggestions.client

import org.mulesoft.als.common.dtoTypes.PositionRange

import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

@JSExportAll
@JSExportTopLevel(name = "Suggestion")
class Suggestion(val text: String,
                 val description: String,
                 val displayText: String,
                 val prefix: String,
                 val category: String,
                 val range: Option[PositionRange]) {}
