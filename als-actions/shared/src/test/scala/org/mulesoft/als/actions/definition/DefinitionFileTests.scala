package org.mulesoft.als.actions.definition

import amf.core.unsafe.PlatformSecrets
import org.scalatest.{AsyncFlatSpec, Matchers}

import scala.concurrent.ExecutionContext

class DefinitionFileTests extends AsyncFlatSpec with Matchers with PlatformSecrets {
  override val executionContext: ExecutionContext =
    scala.concurrent.ExecutionContext.Implicits.global

  behavior of "Find Definition"

}
