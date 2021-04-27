package org.mulesoft.als.server.workspace

import org.mulesoft.als.server.modules.workspace._
import org.mulesoft.amfintegration.UnitWithNextReference

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success}

/**
  * UnitTaskManager is a template designed to keep track of a kind of unit.
  * Given a request (stored in StagingArea), there is a synchronized process
  * which resolves the Unit and stores it into a caché.
  * It provides the possibility to retrieve the current known unit from caché
  *
  * This will process just one unit at a time
  *
  * @tparam UnitType Type of unit that is being processed and stored
  * @tparam ResultUnit usually a wrap of UnitType with the reference for the next process [UnitWithNextReference]
  * @tparam StagingAreaNotifications
  */
trait UnitTaskManager[UnitType, ResultUnit <: UnitWithNextReference, StagingAreaNotifications] {

  def getUnit(uri: String): Future[ResultUnit] =
    repository.getUnit(uri) match {
      case Some(unit) =>
        Future(toResult(uri, unit))
      case _ =>
        getNext(uri)
          .getOrElse(fail(uri))
    }

  def disable(): Future[Unit] = {
    changeState(NotAvailable)
    disableTasks().map(_ => isDisabled.success())
  }

  def stage(uri: String, parameter: StagingAreaNotifications): Unit = synchronized {
    if (state == NotAvailable) throw new UnavailableTaskManagerException
    stagingArea.enqueue(uri, parameter)
    if (canProcess) current = process()
  }

  def shutdown(): Future[Unit] = isDisabled.future

  protected val stagingArea: StagingArea[StagingAreaNotifications]
  protected val repository: Repository[UnitType]
  protected def log(msg: String)
  protected def disableTasks(): Future[Unit]
  protected def processTask(): Future[Unit]
  protected def toResult(uri: String, unit: UnitType): ResultUnit

  protected var state: TaskManagerState = Idle

  protected def changeState(newState: TaskManagerState): Unit = synchronized {
    if (state == NotAvailable) throw new UnavailableTaskManagerException
    state = newState
  }

  private var current: Future[Unit] = Future.unit
  private val isDisabled            = Promise[Unit]()

  private def canProcess: Boolean = state == Idle && current.isCompleted

  private def next(f: Future[Unit]): Future[Unit] =
    f.recoverWith({
        case e =>
          log(Option(e.getMessage).getOrElse(e.toString))
          Future.unit
      })
      .andThen {
        case Success(_) =>
          current = process()
        case Failure(e) =>
          log(e.getMessage)
          current = process()
      }

  private def process(): Future[Unit] =
    if (state == NotAvailable) throw new UnavailableTaskManagerException
    else if (stagingArea.shouldDie) disable()
    else if (stagingArea.hasPending) next(processTask())
    else goIdle()

  protected def getNext(uri: String): Option[Future[ResultUnit]] =
    if (canProcess)
      None
    else Some(current.flatMap(_ => getUnit(uri)))

  protected def fail(uri: String) = {
    log(s"StagingArea: $stagingArea")
    log(s"State: $state")
    log(s"Repo uris: ${repository.getAllFilesUris}")
    log(s"UnitNotFoundException for: $uri")
    throw UnitNotFoundException(uri, "async")
  }

  private def goIdle(): Future[Unit] = synchronized {
    changeState(Idle)
    Future.unit
  }
}
