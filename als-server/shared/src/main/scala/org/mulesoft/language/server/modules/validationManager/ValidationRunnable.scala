package org.mulesoft.language.server.modules.validationManager

import org.mulesoft.language.common.dtoTypes.IValidationReport
import org.mulesoft.language.server.common.reconciler.Runnable

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success}

class ValidationRunnable(var uri: String, task: () => Future[IValidationReport]) extends Runnable[IValidationReport] {
  private var canceled = false

  private val kind = "ValidationRunnable"

  def run(): Promise[IValidationReport] = {
    val promise = Promise[IValidationReport]()

    task() andThen {
      case Success(report) => promise.success(report)

      case Failure(error) => promise.failure(error)
    }

    promise
  }

  def conflicts(other: Runnable[Any]): Boolean =
    other.asInstanceOf[ValidationRunnable].kind == kind && uri == other.asInstanceOf[ValidationRunnable].uri

  def cancel() {
    canceled = true
  }

  def isCanceled(): Boolean = canceled
}
