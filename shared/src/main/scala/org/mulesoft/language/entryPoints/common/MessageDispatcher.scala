package org.mulesoft.language.entryPoints.common

import org.mulesoft.language.common.logger.ILogger

import scala.concurrent.{Future, Promise}
import scala.collection.mutable
import scala.util.{Failure, Random, Success, Try}
import scala.concurrent.ExecutionContext.Implicits.global

trait MessageDispatcher[PayloadType, MessageTypeMetaType] extends ILogger {

  /**
    * Handlers returning nothing
    */
  private val voidHandlers =
    new mutable.HashMap[String, (PayloadType)=>Unit]

  /**
    * Handlers returning promise
    */
  private val promiseHandlers =
    new mutable.HashMap[String, (PayloadType)=>Future[PayloadType]]

  /**
    * Handlers returning descendants of the payload type
    */
  private val directHandlers =
    new mutable.HashMap[String, (PayloadType)=>PayloadType]

  /**
    * Handlers returning descendants of the payload type
    */
  private val tryHandlers =
    new mutable.HashMap[String, (PayloadType)=>Try[PayloadType]]

  /**
    * Handlers returning promise with array
    */
  private val promiseSeqHandlers =
    new mutable.HashMap[String, (PayloadType)=>Future[Seq[PayloadType]]]

  private val messageTypeMetas =
    new mutable.HashMap[String, MessageTypeMetaType]

  /**
    * Map from message ID to a Promise that needs to be resolved when
    * the answer for the related message is recieved
    */
  private val callbacks =
    new mutable.HashMap[String, Promise[PayloadType]]()

  private val randomGenerator = new Random()

  /**
    * Performs actual message sending.
    * Not intended to be called directly, instead is being used by
    * send() and sendWithResponse() methods
    * Called by the trait.
    * @param message - message to send
    */
  def internalSendMessage(message: ProtocolMessage[PayloadType]): Unit;

  /**
    * Performs actual message sending.
    * Not intended to be called directly, instead is being used by
    * send() and sendWithResponse() methods
    * Called by the trait.
    * @param message - message to send
    */
  def internalSendSeqMessage(message: ProtocolSeqMessage[PayloadType]): Unit

  /**
    * To be called by the trait user to handle recieved messages
    * @param message - error message that was recieved
    */
  def internalHandleRecievedMessage(message: ProtocolMessage[PayloadType]): Unit = {

    if (message.id.isDefined && this.callbacks.contains(message.id.get)) {
      // handling the case when this message is an answer for a previously sent message

      val callBackPromise = this.callbacks(message.id.get)
      this.callbacks.remove(message.id.get)

      if (message.payload.isDefined) {

        callBackPromise.success(message.payload.get)

      } else if (message.errorMessage.isDefined) {

        callBackPromise.failure(new Throwable(message.errorMessage.get))
      }

    } else {
      // handling the case when the message needs to be passed to the handling functions

      val voidHandler = this.voidHandlers.get(message.`type`);
      val promiseHandler = this.promiseHandlers.get(message.`type`);
      val directHandler = this.directHandlers.get(message.`type`);
      val tryHandler = this.tryHandlers.get(message.`type`);
      val promiseSeqHandler = this.promiseSeqHandlers.get(message.`type`);

      if (voidHandler.isDefined) {

        val handler = voidHandler.get

        handler(message.payload.get)

      } else if (promiseHandler.isDefined) {

        val handler = promiseHandler.get
        val future = handler(message.payload.get)

        future.onComplete {

          case Success(result) => this.internalSendMessage(
            ProtocolMessage[PayloadType](
              `type` =  message.`type`,
              payload = Option(result),
              id = message.id,
              errorMessage = None
            )
          )
          case Failure(failure) => this.internalSendMessage(
            ProtocolMessage[PayloadType](
              `type` =  message.`type`,
              payload = None,
              id = message.id,
              errorMessage = Option(failure.getStackTrace.map(_.toString).mkString("\n"))
            )
          )
        }

      } else if (directHandler.isDefined) {

        val handler = directHandler.get
        val result = handler(message.payload.get)

        this.internalSendMessage(
          ProtocolMessage[PayloadType](
            `type` =  message.`type`,
            payload = Option(result),
            id = message.id,
            errorMessage = None
          )
        )
      } else if (tryHandler.isDefined) {

        val handler = tryHandler.get
        val tryResult = handler(message.payload.get)

        tryResult match {
          case Success(result) => this.internalSendMessage(
            ProtocolMessage[PayloadType](
              `type` =  message.`type`,
              payload = Option(result),
              id = message.id,
              errorMessage = None
            )
          )
          case Failure(failure) => this.internalSendMessage(
            ProtocolMessage[PayloadType](
              `type` =  message.`type`,
              payload = None,
              id = message.id,
              errorMessage = Option(failure.toString)
            )
          )
        }

      } else if (promiseSeqHandler.isDefined) {

        val handler = promiseSeqHandler.get
        val future = handler(message.payload.get)

        future.onComplete {

          case Success(result) => this.internalSendSeqMessage(
            ProtocolSeqMessage[PayloadType](
              `type` =  message.`type`,
              payload = result,
              id = message.id,
              errorMessage = None
            )
          )
          case Failure(failure) => this.internalSendMessage(
            ProtocolMessage[PayloadType](
              `type` =  message.`type`,
              payload = None,
              id = message.id,
              errorMessage = Option(failure.getStackTrace.map(_.toString).mkString("\n"))
            )
          )
        }

      }

    }
  }

  /**
    * To be used by
    * @param messageType
    * @param payload
    * @tparam ResultType
    * @return
    */
  def sendWithResponse[ResultType <: PayloadType](messageType: String,
                                                  payload: PayloadType): Future[ResultType] = {

    val messageID = this.newRandomId();

    val promise = Promise[ResultType]()
    callbacks(messageID) = promise.asInstanceOf[Promise[PayloadType]];

    this.internalSendMessage(
      ProtocolMessage[PayloadType](
        `type` =  messageType,
        payload = Option(payload),
        id = Option(messageID),
        errorMessage = None
      )
    )

    promise.future
  }

  def send[ResultType](messageType: String,
                       payload: PayloadType): Unit = {

    this.internalSendMessage(
      ProtocolMessage[PayloadType](
        `type` =  messageType,
        payload = Option(payload),
        id = None,
        errorMessage = None
      )
    )
  }

  def getMessageTypeMeta(messageType: String): Option[MessageTypeMetaType] = {

    val result = this.messageTypeMetas.get(messageType)

    this.debugDetail("MessageDispatcher", "getMessageTypeMeta",
      s"Meta for type ${messageType} found: ${result.isDefined}")

    result
  }
  
  def newMeta(messageType: String, messageTypeMeta: Option[MessageTypeMetaType]) {
    this.registerMeta(messageType, messageTypeMeta);
  }

  def newVoidHandler[ArgType <: PayloadType](
                                              messageType: String,
                                              handler: (ArgType)=>Unit,
                                              messageTypeMeta: Option[MessageTypeMetaType] = None): Unit = {

    this.voidHandlers(messageType) = handler.asInstanceOf[(PayloadType)=>Unit]

    this.registerMeta(messageType, messageTypeMeta)
  }

  def newFutureHandler[ArgType <: PayloadType, ResultType <: PayloadType](
                                                                           messageType: String,
                                                                           handler: (ArgType)=>Future[ResultType],
                                                                           messageTypeMeta: Option[MessageTypeMetaType] = None): Unit = {

    this.promiseHandlers(messageType) = handler.asInstanceOf[(PayloadType)=>Future[PayloadType]]

    this.registerMeta(messageType, messageTypeMeta)
  }

  def newDirectHandler[ArgType <: PayloadType, ResultType <: PayloadType](
                                                                           messageType: String,
                                                                           handler: (ArgType)=>ResultType,
                                                                           messageTypeMeta: Option[MessageTypeMetaType] = None): Unit = {

    this.directHandlers(messageType) = handler.asInstanceOf[(PayloadType)=>PayloadType]

    this.registerMeta(messageType, messageTypeMeta)
  }

  def newTryHandler[ArgType <: PayloadType, ResultType <: PayloadType](
                                                                        messageType: String,
                                                                        handler: (ArgType)=>Try[PayloadType],
                                                                        messageTypeMeta: Option[MessageTypeMetaType] = None): Unit = {

    this.tryHandlers(messageType) = handler.asInstanceOf[(PayloadType)=>Try[PayloadType]]

    this.registerMeta(messageType, messageTypeMeta)
  }

  def newFutureSeqHandler[ArgType <: PayloadType, ResultType <: PayloadType](
                                                                           messageType: String,
                                                                           handler: (ArgType)=>Future[Seq[ResultType]],
                                                                           messageTypeMeta: Option[MessageTypeMetaType] = None): Unit = {

    this.promiseSeqHandlers(messageType) = handler.asInstanceOf[(PayloadType)=>Future[Seq[PayloadType]]]

    this.registerMeta(messageType, messageTypeMeta)
  }

  private def newRandomId(): String = {

    val stringBuilder = new mutable.StringBuilder()
    val charIterator = this.randomGenerator.alphanumeric.iterator


    for {i <- 0 until 10}
      stringBuilder += charIterator.next()

    stringBuilder.toString()
  }

  private def registerMeta(messageType: String, meta: Option[MessageTypeMetaType]): Unit = {

    if (meta.isDefined) {
      this.debugDetail("MessageDispatcher", "registerMeta", "Registering meta for type: " + messageType)
      this.messageTypeMetas(messageType) = meta.get
    }
  }
}
