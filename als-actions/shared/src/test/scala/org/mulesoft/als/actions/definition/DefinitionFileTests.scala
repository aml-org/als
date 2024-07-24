package org.mulesoft.als.actions.definition

import org.mulesoft.amfintegration.platform.AlsPlatformSecrets
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.ExecutionContext

class DefinitionFileTests extends AsyncFlatSpec with Matchers with AlsPlatformSecrets {
  override val executionContext: ExecutionContext =
    scala.concurrent.ExecutionContext.Implicits.global

  behavior of "Find Definition"

}
