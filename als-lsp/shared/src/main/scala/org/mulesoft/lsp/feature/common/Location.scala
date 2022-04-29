package org.mulesoft.lsp.feature.common

import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

/** Represents a location inside a resource, such as a line inside a text file.
  *
  * @param uri
  *   Document URI
  * @param range
  *   Range represented by the location
  */
@JSExportAll
@JSExportTopLevel("Location")
case class Location(uri: String, range: Range)
