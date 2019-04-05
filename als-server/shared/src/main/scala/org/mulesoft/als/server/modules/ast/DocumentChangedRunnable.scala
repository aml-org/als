package org.mulesoft.als.server.modules.ast

import amf.core.model.document.BaseUnit
import org.mulesoft.als.server.modules.common.reconciler.Runnable

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
