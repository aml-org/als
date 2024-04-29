package org.mulesoft.als.vscode

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport
import scala.scalajs.js.|

/** vscode-jsonrpc */
@js.native
trait MessageReader extends js.Object {
  def listen(callback: DataCallback): Unit

  def dispose(): Unit
}

@js.native
trait MessageWriter extends js.Object {
  def write(message: Message): Unit

  def dispose(): Unit
}

@js.native
trait DataCallback extends js.Function1[Message, Unit] {
  def apply(message: Message): Unit
}

@js.native
trait Message extends js.Object {
  val jsonrpc: String
}

@js.native
trait Logger extends js.Object {
  def error(message: String): Unit

  def warn(message: String): Unit

  def info(message: String): Unit

  def log(message: String): Unit
}

@js.native
@JSImport("vscode-jsonrpc", "NullLogger")
object NullLogger extends Logger {
  override def error(message: String): Unit = js.native

  override def warn(message: String): Unit = js.native

  override def info(message: String): Unit = js.native

  override def log(message: String): Unit = js.native
}

@js.native
@JSImport("vscode-jsonrpc", "MessageSignature")
abstract class MessageSignature extends js.Object {
  val method: String
  val numberOfParams: Int
  val parameterStructures: ParameterStructures
}

@js.native
trait ParameterStructures extends js.Any {}

@js.native
@JSImport("vscode-languageserver-protocol", "ParameterStructures")
object ParameterStructures extends js.Object {
  val auto: ParameterStructures       = js.native
  val byName: ParameterStructures     = js.native
  val byPosition: ParameterStructures = js.native
}

@js.native
@JSImport("vscode-jsonrpc", "AbstractMessageSignature")
abstract class AbstractMessageSignature(
    _method: String,
    _numberOfParams: Int,
    _parameterStructures: ParameterStructures
) extends MessageSignature {
  val method: String
  val numberOfParams: Int                      = js.native
  val parameterStructures: ParameterStructures = js.native
}

@js.native
@JSImport("vscode-jsonrpc", "RequestType0")
class RequestType0[R, E](override val method: String, override val parameterStructures: ParameterStructures)
    extends AbstractMessageSignature(js.native, js.native, js.native) {}

@js.native
@JSImport("vscode-jsonrpc", "RequestType")
class RequestType[P, R, E](override val method: String, override val parameterStructures: ParameterStructures)
    extends AbstractMessageSignature(js.native, js.native, js.native) {}

@js.native
trait GenericRequestHandler[R, E]
    extends js.Function1[js.Array[js.Any], R | ResponseError[E] | Thenable[R] | Thenable[ResponseError[E]] | Thenable[
      R | ResponseError[E]
    ]] {
  def apply(
      params: js.Any*
  ): R | ResponseError[E] | Thenable[R] | Thenable[ResponseError[E]] | Thenable[R | ResponseError[E]]
}

@js.native
trait RequestHandler0[R, E]
    extends js.Function1[CancellationToken, R | ResponseError[E] | Thenable[R] | Thenable[ResponseError[E]] | Thenable[
      R | ResponseError[E]
    ]] {
  def apply(
      token: CancellationToken
  ): R | ResponseError[E] | Thenable[R] | Thenable[ResponseError[E]] | Thenable[R | ResponseError[E]]
}

@js.native
trait RequestHandler[P, R, E]
    extends js.Function2[P, CancellationToken, R | ResponseError[E] | Thenable[R] | Thenable[
      ResponseError[E]
    ] | Thenable[R | ResponseError[E]]] {
  def apply(
      param: P,
      token: CancellationToken
  ): R | ResponseError[E] | Thenable[R] | Thenable[ResponseError[E]] | Thenable[R | ResponseError[E]]
}

@js.native
trait NotificationMessage extends Message {
  val method: String
  val params: js.UndefOr[js.Any]
}

@js.native
@JSImport("vscode-jsonrpc", "NotificationType")
class NotificationType[P](
    override val method: String,
    override val parameterStructures: ParameterStructures = ParameterStructures.auto
) extends AbstractMessageSignature(js.native, js.native, js.native) {}

@js.native
@JSImport("vscode-jsonrpc", "NotificationType0")
class NotificationType0(override val method: String, override val parameterStructures: ParameterStructures)
    extends AbstractMessageSignature(js.native, js.native, js.native) {}

@js.native
@JSImport("vscode-jsonrpc", "NotificationType1")
class NotificationType1[P1](override val method: String, override val parameterStructures: ParameterStructures)
    extends AbstractMessageSignature(js.native, js.native, js.native) {}

@js.native
trait GenericNotificationHandler extends js.Function1[js.Array[js.Any], Unit] {
  def apply(params: js.Any*): Unit
}

@js.native
trait NotificationHandler0 extends js.Function1[CancellationToken, Unit] {
  def apply(token: CancellationToken): Unit
}

@js.native
trait NotificationHandler[P] extends js.Function2[P, CancellationToken, Unit] {
  def apply(param: P, token: CancellationToken): Unit
}

@js.native
trait CancellationToken extends js.Object {

  /** Is `true` when the token has been cancelled, `false` otherwise.
    */
  val isCancellationRequested: Boolean

  /** An [event](#Event) which fires upon cancellation.
    */
  val onCancellationRequested: Event[js.Any]
}

@js.native
trait Disposable extends js.Object {

  /** Dispose this object.
    */
  def dispose(): Unit
}

@js.native
trait Event[T] extends js.Function3[T => js.Any, js.Any, js.Array[Disposable], Disposable] {

  /** @param listener
    *   The listener function will be call when the event happens.
    * @param thisArgs
    *   The 'this' which will be used when calling the event listener.
    * @param disposables
    *   An array to which a {{IDisposable}} will be added. The
    * @return
    */
  def apply(
      listener: T => js.Any,
      thisArgs: js.Any = js.native,
      disposables: js.Array[Disposable] = js.native
  ): Disposable
}

@js.native
trait Thenable[T] extends js.Promise[T]

@js.native
@JSImport("vscode-jsonrpc", "ResponseError")
class ResponseError[D](code: Int, message: String, data: js.UndefOr[D] = js.native) extends js.Object {
  def toJson(): ResponseErrorLiteral[D] = js.native
}

@js.native
trait ResponseErrorLiteral[D] extends js.Object {
  val code: Int           = js.native
  val message: String     = js.native
  val data: js.UndefOr[D] = js.native
}

@js.native
trait Trace extends js.Any

@js.native
@JSImport("vscode-jsonrpc", "Trace")
object Trace extends js.Object {
  val Off: Trace      = js.native
  val Messages: Trace = js.native
  val Verbose: Trace  = js.native
}

@js.native
trait Tracer extends js.Object {
  def log(dataObject: js.Any): Unit

  def log(message: String, data: js.UndefOr[String]): Unit
}

@js.native
trait TraceOptions extends js.Object {
  val sendNotification: js.UndefOr[Boolean]
  val traceFormat: js.UndefOr[TraceFormat]
}

@js.native
trait TraceFormat extends js.Any

@js.native
@JSImport("vscode-jsonrpc", "TraceFormat")
object TraceFormat extends js.Object {
  val Text: TraceFormat = js.native
  val JSON: TraceFormat = js.native
}

@js.native
@JSImport("vscode-jsonrpc", "createServerSocketTransport")
object ServerSocketTransport extends js.Function2[Int, String, js.Tuple2[MessageReader, MessageWriter]] {
  def apply(port: Int, encoding: String = js.native): js.Tuple2[MessageReader, MessageWriter] = js.native
}
