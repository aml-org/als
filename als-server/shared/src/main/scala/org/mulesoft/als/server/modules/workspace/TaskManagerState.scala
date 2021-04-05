package org.mulesoft.als.server.modules.workspace

sealed abstract class TaskManagerState(state: String)

object Blocked           extends TaskManagerState("BLOCKED")
object Idle              extends TaskManagerState("IDLE")
object ProcessingProject extends TaskManagerState("PROCESSING_PROJECT")
object NotAvailable      extends TaskManagerState("UNAVAILABLE")

case class ProcessingFile(actualFile: String) extends TaskManagerState("PROCESSING_FILE")
