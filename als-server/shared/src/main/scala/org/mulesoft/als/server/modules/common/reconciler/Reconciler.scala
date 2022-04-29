package org.mulesoft.als.server.modules.common.reconciler

import java.util.{Timer, TimerTask}

import org.mulesoft.als.logger.Logger

import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Promise
import scala.util.{Failure, Success}

class Reconciler(
    logger: Logger,
    timeout: Int,
    setTimeout: (() => Unit, Int) => Unit = (task: () => Unit, timeout: Int) => {
      new Timer().schedule(
        new TimerTask {
          def run = task()
        },
        timeout
      )
    }
) {
  private var waitingList: ListBuffer[Runnable[Any]] = ListBuffer[Runnable[Any]]()
  private var runningList: ListBuffer[Runnable[Any]] = ListBuffer[Runnable[Any]]()

  def schedule[ResultType](runnable: Runnable[ResultType]): Promise[ResultType] = synchronized {
    val result = Promise[ResultType]()

    addToWaitingList(runnable)

    setTimeout(
      () => {
        if (runnable.isCanceled())
          removeFromWaitingList(runnable)
        else
          findConflictingInRunningList(runnable) match {
            case Some(_) => schedule(runnable)
            case _ =>
              removeFromWaitingList(runnable)
              addToRunningList(runnable)
              run(runnable).future.andThen {
                case Success(value) => result.success(value)
                case Failure(error) => result.failure(error)
              }
          }
      },
      timeout
    )

    result
  }

  private def run[ResultType](runnable: Runnable[ResultType]): Promise[ResultType] = {
    val result = Promise[ResultType]()
    runnable.run().future.andThen {
      case Success(success) => {
        removeFromRunningList(runnable)
        result.success(success)
      }
      case Failure(error) => {
        removeFromRunningList(runnable)
        result.failure(error)
      }
    }
    result
  }

  private def addToWaitingList[ResultType](runnable: Runnable[ResultType]) {
    if (!waitingList.contains(runnable)) {
      waitingList = waitingList.filterNot(current => {
        val conflicts = runnable.conflicts(current)
        if (conflicts) current.cancel()
        conflicts
      })
      waitingList += runnable.asInstanceOf[Runnable[Any]]
    } else
      logger.debug("Adding to waiting list element that's already there", "Reconciler", "addToWaitingList")
  }

  private def addToRunningList[ResultType](runnable: Runnable[ResultType]) {
    synchronized(runningList += runnable.asInstanceOf[Runnable[Any]])
  }

  private def removeFromWaitingList[ResultType](runnable: Runnable[ResultType]) {
    synchronized(waitingList = waitingList.filterNot(_.eq(runnable)))
  }

  private def removeFromRunningList[ResultType](runnable: Runnable[ResultType]) {
    synchronized(runningList = runningList.filterNot(_.eq(runnable)))
  }

  private def findConflictingInRunningList[ResultType](runnable: Runnable[ResultType]): Option[Runnable[ResultType]] =
    runningList.find(runnable.conflicts).asInstanceOf[Option[Runnable[ResultType]]]
}
