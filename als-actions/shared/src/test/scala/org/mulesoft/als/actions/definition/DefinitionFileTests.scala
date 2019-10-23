package org.mulesoft.als.actions.definition

import org.mulesoft.als.actions.definition.files.FindDefinitionFile
import org.scalatest.{FlatSpec, Matchers}

class DefinitionFileTests extends FlatSpec with Matchers with FindDefinitionFile {
  behavior of "FindSourceFile"

  it should "extract protocols from relativePath" in {
    extractProtocol("file://fragment") should be("")
    extractProtocol("file:///fragment") should be("file")
    extractProtocol("/fragment") should be("")
    extractProtocol("http://localhost:8080/test") should be("http")
  }
}
