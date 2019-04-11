package org.mulesoft.lsp.common

/** Represents a location inside a resource, such as a line inside a text file.
  *
  * @param uri   Document URI
  * @param range Range represented by the location
  */

case class Location(uri: String, range: Range)