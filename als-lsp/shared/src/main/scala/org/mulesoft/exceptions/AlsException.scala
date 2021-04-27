package org.mulesoft.exceptions

class AlsException(message: String, uri: String, uuid: String) extends Exception(message) {
  def getUri: String  = uri
  def getUuid: String = uuid
}
