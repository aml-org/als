package org.mulesoft.als.server.workspace

import java.util.concurrent.atomic.AtomicBoolean
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}

class FutureLock() {
  private val lock = new AtomicBoolean(false)

  private val queue: SynchronizedList[Job[_]] = SynchronizedList()

  private def tryBlock(): Unit =
    synchronized(() => {
      if (isBlocked) {
        tryBlock()
      } else {
        lock.set(true)
      }
    })

  def isBlocked: Boolean = lock.get()

  def enqueue[T](fn: () => Future[T]): Future[T] = {
    if (!isBlocked) {
      fn()
    } else {
      val job = new Job(fn, this)
      queue += job
      job.promise.future
    }
  }

  def enqueueBlocking[T](fn: () => Future[T]): Future[T] = {
    for {
      _ <- block()
      r <- fn()
    } yield {
      release()
      r
    }
  }

  def enqueue[T](job: Job[T]): Future[T] =
    enqueue(job.fn)

  def block(): Future[Unit] =
    Future({
      tryBlock()
    })

  def release(): Unit =
    synchronized(() => {
      lock.set(false)
      queue.foreach(_.run())
      queue.clear()
    })

}

class Job[T](val fn: () => Future[T], val lock: FutureLock) {
  val promise: Promise[T] = Promise()

  def run(): Future[T] =
    promise.completeWith(lock.enqueue(this)).future
}
