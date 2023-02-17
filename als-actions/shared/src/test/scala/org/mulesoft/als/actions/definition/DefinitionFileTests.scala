package org.mulesoft.als.actions.definition

import amf.core.internal.unsafe.PlatformSecrets

import scala.concurrent.ExecutionContext

class DefinitionFileTests extends AsyncFlatSpec with Matchers with PlatformSecrets {
  override val executionContext: ExecutionContext =
    scala.concurrent.ExecutionContext.Implicits.global

  behavior of "Find Definition"

}
