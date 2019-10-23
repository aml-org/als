package org.mulesoft.als.actions.definition

import org.mulesoft.als.actions.definition.files.FindDefinitionFile
import org.mulesoft.als.common.NodeBranchBuilder
import org.mulesoft.als.common.dtoTypes.Position
import org.scalatest.{FlatSpec, Matchers}
import org.yaml.parser.YamlParser

class DefinitionFileTests extends FlatSpec with Matchers with FindDefinitionFile {
  behavior of "FindSourceFile"

  it should "extract protocols from relativePath" in {
    extractProtocol("file://fragment") should be("")
    extractProtocol("file:///fragment") should be("file")
    extractProtocol("/fragment") should be("")
    extractProtocol("http://localhost:8080/test") should be("http")
  }

  it should "identify root 'uses' node declarations" in {
    val textOK =
      """uses:
        |  lib: test.yaml
        |""".stripMargin

    val textWrong =
      """types:
        |  uses:
        |    lib: test.yaml
        |""".stripMargin

    val yPartBranchWrong =
      NodeBranchBuilder.build(YamlParser(textWrong, "file").parse(false).head, Position(3, 11).toAmfPosition)
    isInUsesRef(yPartBranchWrong) should be(false)

    val yPartBranchOK =
      NodeBranchBuilder.build(YamlParser(textOK, "file").parse(false).head, Position(2, 9).toAmfPosition)
    isInUsesRef(yPartBranchOK) should be(true)
  }
}
