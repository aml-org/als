package org.mulesoft.als.server.modules.workspace

import amf.internal.environment.Environment
import org.mulesoft.als.server.modules.ast.{NotificationKind, WORKSPACE_KILLED}
import org.mulesoft.als.server.textsync.EnvironmentProvider

import scala.collection.mutable

class StagingArea(environmentProvider: EnvironmentProvider) {

  def shouldDie: Boolean = pending.values.toList.contains(WORKSPACE_KILLED)

  def isPending: Boolean = pending.nonEmpty

  private val pending: mutable.Map[Option[String], NotificationKind] = mutable.Map.empty

  def enqueue(files: List[(String, NotificationKind)]): Unit =
    files.foreach(f => enqueue(Some(f._1), f._2))

  def enqueue(file: Option[String], kind: NotificationKind): Unit = pending.update(file, kind)

  def dequeue(files: Set[String]): Unit =
    files.map(Some(_)).foreach(pending.remove)

  def snapshot(): Snapshot = synchronized {
    val environment = environmentProvider.environmentSnapshot()
    val actual: List[(String, NotificationKind)] = pending.map {
      case (Some(p), notification) => (p, notification)
      case (None, notification)    => ("", notification)
    }.toList
    if (!shouldDie) pending.clear()
    Snapshot(environment, actual)
  }

}
case class Snapshot(environment: Environment, files: List[(String, NotificationKind)]) {}
