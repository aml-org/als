package org.mulesoft.als.suggestions.js

import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel, ScalaJSDefined}
import scala.scalajs.js

@ScalaJSDefined
@JSExportTopLevel(name = "ISuggestion")
class ISuggestion (

    val text: String,

    val description: String,

    val displayText: String,

    val prefix: String,

    val category: String
) extends js.Object
{

}
