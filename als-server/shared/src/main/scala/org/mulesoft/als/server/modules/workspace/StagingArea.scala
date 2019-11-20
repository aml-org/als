package org.mulesoft.als.server.modules.workspace

import amf.internal.environment.Environment
import org.mulesoft.als.server.modules.ast.NotificationKind
import org.mulesoft.als.server.textsync.EnvironmentProvider

class StagingArea(environmentProvider: EnvironmentProvider) {

  def isPending(): Boolean = pending.nonEmpty

  private var pending: Set[(String, NotificationKind)] = Set.empty

  def enqueue(files: Set[(String, NotificationKind)]): Unit =
    pending = pending ++ files

  def dequeue(files: Set[String]): Unit =
    pending = pending.filter(p => !files.contains(p._1))

  def snapshot(): Snapshot = synchronized {
    val environment                             = environmentProvider.environmentSnapshot()
    val actual: Set[(String, NotificationKind)] = pending
    pending = Set.empty
    Snapshot(environment, actual)
  }

}
case class Snapshot(environment: Environment, files: Set[(String, NotificationKind)]) {}
