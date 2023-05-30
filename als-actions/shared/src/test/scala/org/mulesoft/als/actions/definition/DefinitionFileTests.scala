package org.mulesoft.als.actions.definition

import amf.core.internal.unsafe.PlatformSecrets
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.ExecutionContext

class DefinitionFileTests extends AsyncFlatSpec with Matchers with PlatformSecrets {
  override val executionContext: ExecutionContext =
    scala.concurrent.ExecutionContext.Implicits.global

  behavior of "Find Definition"

}
