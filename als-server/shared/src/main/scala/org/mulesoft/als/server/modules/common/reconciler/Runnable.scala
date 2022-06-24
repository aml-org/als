// $COVERAGE-OFF$
package org.mulesoft.als.server.modules.common.reconciler

import scala.concurrent.Promise

trait Runnable[ResultType] {
  def run(): Promise[ResultType]

  def conflicts(other: Runnable[Any]): Boolean

  def cancel(): Unit

  def isCanceled: Boolean
}

class TestRunnable(var message: String, var kind: String) extends Runnable[String] {
  private var canceled = false

  def run(): Promise[String] = Promise().success(message)

  def conflicts(other: Runnable[Any]): Boolean = other.asInstanceOf[TestRunnable].kind == kind

  def cancel() {
    canceled = true
  }

  def isCanceled: Boolean = canceled
}

// $COVERAGE-ON$
