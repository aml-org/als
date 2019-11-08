package org.mulesoft.als.server.modules.workspace

sealed case class WorkspaceState(state: String)

object IDLE                extends WorkspaceState("IDLE")
object PROSSESSING_PROJECT extends WorkspaceState("POSSESSING_PROJECT")
object PROSSESING_FILE     extends WorkspaceState("POSSESSING_PROJECT")
