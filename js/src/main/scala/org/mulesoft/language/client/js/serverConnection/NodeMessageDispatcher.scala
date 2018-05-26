package org.mulesoft.language.client.js.serverConnection

import org.mulesoft.language.client.js.CustomPicklerConfig.{macroRW, read, write, ReadWriter => RW}
import org.mulesoft.language.client.js.Globals
import org.mulesoft.language.client.js.dtoTypes.ProtocolMessagePayload
import org.mulesoft.language.common.logger.ILogger
import org.mulesoft.language.entryPoints.common.{MessageDispatcher, ProtocolMessage => SharedProtocolMessage}

import scala.scalajs.js
import scala.scalajs.js.JSON
import scala.util.{Failure, Success, Try}

trait NodeMessageDispatcher extends MessageDispatcher[ProtocolMessagePayload, NodeMsgTypeMeta] with ILogger {

  implicit def rw: RW[ProtocolMessage[ProtocolMessagePayload]] = macroRW

  /**
    * To be implemented by the trait users. Sends JSON message.
    * @param message - message to send.
    */
  protected def internalSendJSONMessage(message: js.Object): Unit

  /**
    * To be called by the trait user to handle recieved JSON messages.
    * @param message - error message that was recieved
    */
  def handleJSONMessageRecieved(message: js.Any): Unit = {

    this.debugDetail("NodeMessageDispatcher", "handleJSONMessageRecieved",
      "Recieved message: " + JSON.stringify(message))

    val dynamicMessage = message.asInstanceOf[js.Dynamic]

    val messageType: String = dynamicMessage.`type`.asInstanceOf[String]

    if (messageType != "SET_SERVER_CONFIGURATION") {

      val protocolMessageTry = this.deserializeMessage(message);

      this.debugDetail("NodeMessageDispatcher", "handleJSONMessageRecieved",
        "Deserialized message: " +
          (if(protocolMessageTry.isSuccess) protocolMessageTry.get.`type` else "failed"))

      protocolMessageTry match {
        case Success(protocolMessage) =>
          this.internalHandleRecievedMessage(protocolMessage)

        case Failure(exception) => exception.printStackTrace()
      }
    }
  }

  /**
    * Performs actual message sending.
    * Not intended to be called directly, instead is being used by
    * send() and sendWithResponse() methods
    * Called by the trait.
    * @param message - message to send
    */
  def internalSendMessage(message: SharedProtocolMessage[ProtocolMessagePayload]): Unit = {

    this.debugDetail("NodeMessageDispatcher", "internalSendMessage",
      "Sending message of type: " + message.`type`)

    val protocolMessage = this.serializeMessage(message)

    this.debugDetail("NodeMessageDispatcher", "internalSendMessage",
      "Serialized message: " + JSON.stringify(protocolMessage))

    this.internalSendJSONMessage(protocolMessage.asInstanceOf[js.Object])
  }

  protected def deserializeMessage(message: js.Any) : Try[ProtocolMessage[ProtocolMessagePayload]] = {

    try {
      this.debugDetail("NodeMessageDispatcher", "deserializeMessage",
        "Original recieved message: " + JSON.stringify(message))

      val dynamicMessage = message.asInstanceOf[js.Dynamic]

      val messageType: String = dynamicMessage.`type`.asInstanceOf[String]


      val typeMeta = this.getMessageTypeMeta(messageType)
      if (typeMeta.isDefined) {

        if (typeMeta.get.wrapInput) {
          val oldPayload = dynamicMessage.payload
          val newPayload = js.Dynamic.literal()

          dynamicMessage.payload = newPayload
          newPayload.wrapped = oldPayload
        }

        val typeToPatchWith = typeMeta.get.inputTypeName
        dynamicMessage.payload.$type = typeToPatchWith

        val serializedJson = Globals.JSON.stringify(dynamicMessage)

        this.debugDetail("NodeMessageDispatcher", "deserializeMessage",
          "Patched recieved message: " + serializedJson)

        val protocolMessage = read[ProtocolMessage[ProtocolMessagePayload]](
          serializedJson)

        Success(protocolMessage)

      } else {
        Failure(
          new Exception(s"Cant deserialize payload as no metadata is found for message type ${messageType}"));
      }

    } catch {
      case e: Exception => Failure(e)
    }
  }

  protected def serializeMessage(
    message: ProtocolMessage[ProtocolMessagePayload]) : js.Any = {
    val messageString = write(message);

    Globals.JSON.parse(messageString)
  }
}
