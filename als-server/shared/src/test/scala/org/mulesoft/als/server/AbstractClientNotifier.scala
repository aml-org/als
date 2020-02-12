package org.mulesoft.als.server

import java.io.StringWriter

import org.mulesoft.als.server.client.{AlsClientNotifier, ClientNotifier}
import org.mulesoft.als.server.feature.serialization.SerializationMessage
import org.mulesoft.lsp.feature.diagnostic.PublishDiagnosticsParams
import org.mulesoft.lsp.feature.telemetry.TelemetryMessage
import org.mulesoft.als.server.feature.workspace.FilesInProjectParams

import scala.collection.mutable
import scala.concurrent.{Future, Promise}

trait AbstractTestClientNotifier[T] {
  val promises: mutable.Queue[Promise[T]] = mutable.Queue.empty

  private def sync(fn: () => Any): Any = synchronized(fn())

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

class MockDiagnosticClientNotifier extends ClientNotifier with AbstractTestClientNotifier[PublishDiagnosticsParams] {

  override def notifyTelemetry(params: TelemetryMessage): Unit = {}

  override def notifyDiagnostic(msg: PublishDiagnosticsParams): Unit = notify(msg)
}

class MockAlsClientNotifier
    extends AlsClientNotifier[StringWriter]
    with AbstractTestClientNotifier[SerializationMessage[StringWriter]] {

  override def notifySerialization(params: SerializationMessage[StringWriter]): Unit = notify(params)

  override def notifyProjectFiles(params: FilesInProjectParams): Unit = {}
}

class MockTelemetryClientNotifier extends ClientNotifier with AbstractTestClientNotifier[TelemetryMessage] {

  override def notifyTelemetry(msg: TelemetryMessage): Unit = notify(msg)

  override def notifyDiagnostic(params: PublishDiagnosticsParams): Unit = {}
}

class MockFilesInClientNotifier extends AlsClientNotifier[Any] with AbstractTestClientNotifier[FilesInProjectParams] {

  override def notifyProjectFiles(params: FilesInProjectParams): Unit = notify(params)

  override def notifySerialization(params: SerializationMessage[Any]): Unit = {}
}
