package org.mulesoft.language.test.serverConnection

/**
  * Metadata for node protocol message type.
  * @param inputTypeName - full scala type name for input of this message.
  * @param wrapInput - whether to wrap input
  */
case class NodeMsgTypeMeta(
  inputTypeName: String,
  wrapInput: Boolean = false,
  wrapOutput: Boolean = false
)
{

}
