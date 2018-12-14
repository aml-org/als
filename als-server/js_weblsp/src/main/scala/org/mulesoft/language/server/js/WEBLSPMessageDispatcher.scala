package org.mulesoft.language.server.js

import CustomPicklerConfig._
import CustomPicklerConfig.{macroRW, ReadWriter => RW}

import scala.scalajs.js
import js.JSON

import scala.util.{Failure, Success, Try}

import org.mulesoft.language.entryPoints.common.MessageDispatcher
import org.mulesoft.language.entryPoints.common.{ProtocolMessage => SharedProtocolMessage}

trait WEBLSPMessageDispatcher extends MessageDispatcher[ProtocolMessagePayload, NodeMsgTypeMeta] {
	implicit def rw: RW[ProtocolMessage[ProtocolMessagePayload]] = macroRW
	
	/**
	  * To be implemented by the trait users. Sends JSON message.
	  * @param message - message to send.
	  */
	
	protected def internalSendJSONMessage(message: js.Any): Unit
	
	/**
	  * To be called by the trait user to handle recieved JSON messages.
	  * @param message - error message that was recieved
	  */
	def handleJSONMessageRecieved(message: js.Any): Unit = {
		
		this.debugDetail("NodeMessageDispatcher", "handleJSONMessageRecieved",
			"Recieved message: " + JSON.stringify(message))
		
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
		
		this.internalSendJSONMessage(protocolMessage)
	}
	
	protected def deserializeMessage(message: js.Any) : Try[ProtocolMessage[ProtocolMessagePayload]] = {
		
		try {
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
				
				val serializedJson = JSON.stringify(dynamicMessage)
				
				val protocolMessage = read[ProtocolMessage[ProtocolMessagePayload]](serializedJson)
				
				
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
		
		JSON.parse(messageString)
	}
}

