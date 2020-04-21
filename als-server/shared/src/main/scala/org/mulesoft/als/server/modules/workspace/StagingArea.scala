package org.mulesoft.als.server.modules.workspace

import amf.internal.environment.Environment
import org.mulesoft.als.server.modules.ast.{BaseUnitListenerParams, NotificationKind, WORKSPACE_TERMINATED}
import org.mulesoft.als.server.textsync.EnvironmentProvider

import scala.collection.mutable

trait StagingArea[Parameter] {
  protected val pending: mutable.Map[String, Parameter] = mutable.Map.empty

  def enqueue(file: String, kind: Parameter): Unit = pending.update(file, kind)

  def enqueue(files: List[(String, Parameter)]): Unit =
    files.foreach(f => enqueue(f._1, f._2))

  def dequeue(files: Set[String]): Unit =
    files.foreach(pending.remove)

  def dequeue(): (String, Parameter) = {
    val r = pending.head
    pending.remove(r._1)
    r
  }

  def shouldDie: Boolean = false

  def hasPending: Boolean = pending.nonEmpty
}

class ResolverStagingArea extends StagingArea[BaseUnitListenerParams]

class ParserStagingArea(environmentProvider: EnvironmentProvider) extends StagingArea[NotificationKind] {

  override def shouldDie: Boolean = pending.values.toList.contains(WORKSPACE_TERMINATED)

  def snapshot(): Snapshot = synchronized {
    val environment                              = environmentProvider.environmentSnapshot()
    val actual: List[(String, NotificationKind)] = pending.toList
    pending.clear()
    Snapshot(environment, actual)
  }

}
case class Snapshot(environment: Environment, files: List[(String, NotificationKind)]) {}
