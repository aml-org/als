package org.mulesoft.als.server

import java.io.StringWriter

import org.mulesoft.als.common.SyncFunction
import org.mulesoft.als.server.client.{AlsClientNotifier, ClientNotifier}
import org.mulesoft.als.server.feature.serialization.SerializationResult
import org.mulesoft.als.server.feature.workspace.FilesInProjectParams
import org.mulesoft.lsp.feature.diagnostic.PublishDiagnosticsParams
import org.mulesoft.lsp.feature.telemetry.{MessageTypes, TelemetryMessage}
import shapeless.|∨|

import scala.collection.mutable
import scala.concurrent.{Future, Promise}
import scala.concurrent.ExecutionContext.Implicits.global

trait AbstractTestClientNotifier[T] extends SyncFunction {
  val promises: mutable.Queue[Promise[T]] = mutable.Queue.empty

  def notify(msg: T): Unit =
    sync(
      () =>
        if (promises.forall(_.isCompleted)) promises.enqueue(Promise[T].success(msg))
        else promises.dequeueFirst(!_.isCompleted).map(_.success(msg)))

  def nextCall: Future[T] =
    sync(() =>
      if (promises.isEmpty) {
        val promise = Promise[T]()
        promises.enqueue(promise)
        promise.future
      } else promises.dequeue().future) match {
      case r: Future[T] => r
      case _            => Future.failed(new Exception("Wrong notification"))
    }
}

class MockCompleteClientNotifier(val timeoutMillis: Int = 1000)
    extends ClientNotifier
    with TimeoutFuture
    with SyncFunction {
  val promisesT: mutable.Queue[Promise[TelemetryMessage]]         = mutable.Queue.empty
  val promisesD: mutable.Queue[Promise[PublishDiagnosticsParams]] = mutable.Queue.empty

  override def notifyDiagnostic(params: PublishDiagnosticsParams): Unit =
    sync(
      () =>
        if (promisesD.forall(_.isCompleted)) promisesD.enqueue(Promise[PublishDiagnosticsParams].success(params))
        else promisesD.dequeueFirst(!_.isCompleted).map(_.success(params)))

  override def notifyTelemetry(params: TelemetryMessage): Unit =
    sync(
      () =>
        if (promisesT.forall(_.isCompleted)) promisesT.enqueue(Promise[TelemetryMessage].success(params))
        else promisesT.dequeueFirst(!_.isCompleted).map(_.success(params)))

  def nextCallT: Future[TelemetryMessage] =
    timeoutFuture(
      sync(() =>
        if (promisesT.isEmpty) {
          val promise = Promise[TelemetryMessage]()
          promisesT.enqueue(promise)
          promise.future
        } else promisesT.dequeue().future) match {
        case r: Future[TelemetryMessage] => r
        case _                           => Future.failed(new Exception("Wrong notification"))
      },
      timeoutMillis
    )

  def nextCallD: Future[PublishDiagnosticsParams] =
    timeoutFuture(
      sync(() =>
        if (promisesD.isEmpty) {
          val promise = Promise[PublishDiagnosticsParams]()
          promisesD.enqueue(promise)
          promise.future
        } else promisesD.dequeue().future) match {
        case r: Future[PublishDiagnosticsParams] => r
        case _                                   => Future.failed(new Exception("Wrong notification"))
      },
      timeoutMillis
    )
}

class MockDiagnosticClientNotifier(val timeoutMillis: Int = 1000)
    extends ClientNotifier
    with AbstractTestClientNotifier[PublishDiagnosticsParams]
    with TimeoutFuture {

  override def nextCall: Future[PublishDiagnosticsParams] = timeoutFuture(super.nextCall, timeoutMillis)

  override def notifyTelemetry(params: TelemetryMessage): Unit = {}

  override def notifyDiagnostic(msg: PublishDiagnosticsParams): Unit = notify(msg)
}

class MockAlsClientNotifier
    extends AlsClientNotifier[StringWriter]
    with AbstractTestClientNotifier[SerializationResult[StringWriter]] {

  override def notifySerialization(params: SerializationResult[StringWriter]): Unit = notify(params)

  override def notifyProjectFiles(params: FilesInProjectParams): Unit = {}
}

class MockTelemetryClientNotifier(val timeoutMillis: Int = 1000, ignoreErrors: Boolean = true)
    extends ClientNotifier
    with AbstractTestClientNotifier[TelemetryMessage]
    with TimeoutFuture {

  override def nextCall: Future[TelemetryMessage] =
    timeoutFuture(super.nextCall, timeoutMillis)
      .flatMap {
        case m if m.messageType == MessageTypes.ERROR_MESSAGE && ignoreErrors => nextCall
        case m                                                                => Future.successful(m)
      }

  override def notifyTelemetry(msg: TelemetryMessage): Unit = notify(msg)

  override def notifyDiagnostic(params: PublishDiagnosticsParams): Unit = {}
}

class MockTelemetryParsingClientNotifier(override val timeoutMillis: Int = 3000) extends MockTelemetryClientNotifier {

  override def notifyTelemetry(msg: TelemetryMessage): Unit = msg.messageType match {
    case MessageTypes.BEGIN_PARSE => notify(msg)
    case _                        =>
  }

}

class MockFilesInClientNotifier extends AlsClientNotifier[Any] with AbstractTestClientNotifier[FilesInProjectParams] {

  override def notifyProjectFiles(params: FilesInProjectParams): Unit = notify(params)

  override def notifySerialization(params: SerializationResult[Any]): Unit = {}
}
