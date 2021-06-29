package org.mulesoft.amfintegration.amfconfiguration

class NoDefinitionFoundException(id: String) extends Exception {
  override def getMessage: String = s"No definition found for $id"
}
