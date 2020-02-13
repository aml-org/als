package org.mulesoft.als.server.modules.workspace

sealed abstract class WorkspaceState(state: String)

object Idle              extends WorkspaceState("IDLE")
object ProcessingProject extends WorkspaceState("PROCESSING_PROJECT")
object ShuttingDown      extends WorkspaceState("SHUTTING_DOWN")

case class ProcessingFile(actualFile: String) extends WorkspaceState("PROCESSING_FILE")
