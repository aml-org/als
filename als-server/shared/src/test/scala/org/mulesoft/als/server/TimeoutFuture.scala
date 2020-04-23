package org.mulesoft.als.server

import java.util.{Timer, TimerTask}

import scala.concurrent.{ExecutionContext, Future, Promise, TimeoutException}
import scala.concurrent.duration.FiniteDuration

trait TimeoutFuture {

  def setTimeout[T](f: Future[T], duration: FiniteDuration): Unit = {
    //FutureTimeout.forFuture(f, duration)
  }

  def timeoutFuture[T](f: Future[T], millis: Long)(implicit ec: ExecutionContext): Future[T] = {
    val p = Promise[T]()

    val task = new TimerTask {
      override def run(): Unit = {
        p.tryFailure(new TimeoutException)
      }
    }

    val timer = new Timer("Timer")
    timer.schedule(task, millis)

    f.onComplete { result =>
      p.tryComplete(result)
      timer.cancel()
    }
    p.future
  }
}
