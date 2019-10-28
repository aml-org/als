package org.mulesoft.als.actions.definition

import org.mulesoft.als.actions.common.ActionTools
import org.scalatest.{FlatSpec, Matchers}

class DefinitionFileTests extends FlatSpec with Matchers {
  behavior of "ActionTools"

  it should "extract protocols from relativePath" in {
    ActionTools.extractProtocol("file://fragment") should be("")
    ActionTools.extractProtocol("file:///fragment") should be("file")
    ActionTools.extractProtocol("/fragment") should be("")
    ActionTools.extractProtocol("http://localhost:8080/test") should be("http")
  }
}
