package org.mulesoft.als.server.modules.workspace

import amf.internal.environment.Environment
import org.mulesoft.als.common.SyncFunction
import org.mulesoft.als.server.logger.Logger
import org.mulesoft.als.server.modules.ast._
import org.mulesoft.als.server.textsync.EnvironmentProvider

import scala.collection.mutable

trait StagingArea[Parameter] extends SyncFunction {

  protected val pending: mutable.Map[String, Parameter] = mutable.Map.empty

  def enqueue(file: String, kind: Parameter): Unit =
    sync(() => pending.update(file, kind))

  def enqueue(files: List[(String, Parameter)]): Unit =
    sync(() => files.foreach(f => enqueue(f._1, f._2)))

  def dequeue(files: Set[String]): Unit =
    sync(() => files.foreach(pending.remove))

  def dequeue(file: String, p: Parameter): Unit =
    sync(() =>
      pending.get(file) match {
        case Some(param) if param equals p => pending.remove(file)
        case _                             =>
    })

  def dequeue(): (String, Parameter) =
    sync(() => {
      val r = pending.head
      pending.remove(r._1)
      r
    })

  def shouldDie: Boolean = false

  def hasPending: Boolean = pending.nonEmpty

  override def toString: String = pending.toString
}

class ResolverStagingArea extends StagingArea[BaseUnitListenerParams]

class ParserStagingArea(environmentProvider: EnvironmentProvider, logger: Logger)
    extends StagingArea[NotificationKind] {

  override def enqueue(file: String, kind: NotificationKind): Unit = synchronized {
    pending.get(file) match {
      case Some(CHANGE_FILE) if kind == OPEN_FILE =>
        logger.warning(s"file opened without closing $file", "ParserStagingArea", "enqueue")
        super.enqueue(file, kind)
      case Some(CLOSE_FILE) if kind == CHANGE_FILE =>
        logger.warning(s"file changed after closing $file", "ParserStagingArea", "enqueue")
        super.enqueue(file, kind)
      case Some(CLOSE_FILE) if kind == OPEN_FILE =>
        super.enqueue(file, CHANGE_FILE)
      case _ => super.enqueue(file, kind)
    }
  }

  override def shouldDie: Boolean = pending.values.toList.contains(WORKSPACE_TERMINATED)

  def shouldBlock: Boolean = pending.values.toList.contains(BLOCK_WORKSPACE)

  def shouldUnblock: Boolean = pending.values.toList.contains(UNBLOCK_WORKSPACE)

  def snapshot(): Snapshot = synchronized {
    val environment                              = environmentProvider.environmentSnapshot()
    val actual: List[(String, NotificationKind)] = pending.toList
    pending.clear()
    Snapshot(environment, actual)
  }

  def contains(uri: String): Boolean = pending.contains(uri)
}
case class Snapshot(environment: Environment, files: List[(String, NotificationKind)]) {}
