package org.mulesoft.als.server.modules.workspace

import amf.internal.environment.Environment
import org.mulesoft.als.server.modules.ast.{NotificationKind, WORKSPACE_KILLED}
import org.mulesoft.als.server.textsync.EnvironmentProvider

import scala.collection.mutable

class StagingArea(environmentProvider: EnvironmentProvider) {

  def shouldDie: Boolean = pending.values.toList.contains(WORKSPACE_KILLED)

  def isPending: Boolean = pending.nonEmpty

  private val pending: mutable.Map[String, NotificationKind] = mutable.Map.empty

  def enqueue(files: List[(String, NotificationKind)]): Unit =
    files.foreach(f => enqueue(f._1, f._2))

  def enqueue(file: String, kind: NotificationKind): Unit = pending.update(file, kind)

  def dequeue(files: Set[String]): Unit =
    files.foreach(pending.remove)

  def snapshot(): Snapshot = synchronized {
    val environment = environmentProvider.environmentSnapshot()
    val actual: List[(String, NotificationKind)] = pending.map {
      case (p, notification) => (p, notification)
    }.toList
    pending.clear()
    Snapshot(environment, actual)
  }

}
case class Snapshot(environment: Environment, files: List[(String, NotificationKind)]) {}
