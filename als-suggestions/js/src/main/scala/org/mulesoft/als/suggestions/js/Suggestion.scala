package org.mulesoft.als.suggestions.js

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExportTopLevel, ScalaJSDefined}

@ScalaJSDefined
@JSExportTopLevel(name = "Suggestion")
class Suggestion(
                  val text: String,
                  val description: String,
                  val displayText: String,
                  val prefix: String,
                  val category: String
                ) extends js.Object {}
