package org.mulesoft.als.server.modules.workspace

sealed abstract class WorkspaceState(state: String)

object Idle              extends WorkspaceState("IDLE")
object ProssessinProject extends WorkspaceState("POSSESSING_PROJECT")

case class ProssessingFile(actualFile: String) extends WorkspaceState("POSSESSING_FILE")
