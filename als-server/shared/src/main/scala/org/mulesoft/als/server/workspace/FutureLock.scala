package org.mulesoft.als.server.workspace

import java.util.concurrent.atomic.AtomicBoolean
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class FutureLock() {
  private val lock = new AtomicBoolean(false)

  private def tryBlock(): Unit =
    synchronized(() => {
      if (isBlocked) {
        tryBlock()
      } else {
        lock.set(true)
      }
    })

  def isBlocked: Boolean = lock.get()

  def block(): Future[Unit] = {
    Future({
      tryBlock()
    })
  }

  def release(): Unit = {
    lock.set(false)
  }
}
