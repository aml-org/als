package org.mulesoft.language.server.modules.astManager

import amf.core.model.document.BaseUnit
import org.mulesoft.language.server.common.reconciler.Runnable

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success}

class DocumentChangedRunnable(var uri: String, task: () => Future[BaseUnit]) extends Runnable[BaseUnit] {
  private var canceled = false

  private val kind = "DocumentChanged"

  def run(): Promise[BaseUnit] = {
    val promise = Promise[BaseUnit]()

    task() andThen {
      case Success(unit) => promise.success(unit)
      case Failure(error) => promise.failure(error)
    }

    promise
  }

  def conflicts(other: Runnable[Any]): Boolean = other.asInstanceOf[DocumentChangedRunnable].kind == kind && uri == other.asInstanceOf[DocumentChangedRunnable].uri

  def cancel() {canceled = true}

  def isCanceled(): Boolean = canceled
}
