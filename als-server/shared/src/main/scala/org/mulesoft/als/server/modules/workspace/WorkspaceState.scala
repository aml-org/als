package org.mulesoft.als.server.modules.workspace

sealed abstract class WorkspaceState(state: String)

object Idle              extends WorkspaceState("IDLE")
object ProcessingProject extends WorkspaceState("PROCESSING_PROJECT")
object NotAvailable      extends WorkspaceState("UNAVAILABLE")

case class ProcessingFile(actualFile: String) extends WorkspaceState("PROCESSING_FILE")
