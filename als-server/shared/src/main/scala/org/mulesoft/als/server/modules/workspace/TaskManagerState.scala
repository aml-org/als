package org.mulesoft.als.server.modules.workspace

sealed abstract class TaskManagerState(val state: String)

object Idle              extends TaskManagerState("IDLE")
object ProcessingProject extends TaskManagerState("PROCESSING_PROJECT")
object ProcessingStaged  extends TaskManagerState("PROCESSING_STAGED")
object NotAvailable      extends TaskManagerState("UNAVAILABLE")
object ProcessingFile    extends TaskManagerState("PROCESSING_FILE")
