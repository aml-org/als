package common

import amf.core.parser.{Position => AmfPosition}
import org.mulesoft.als.common.NodeBranchBuilder
import org.scalatest.{FunSuite, Matchers}
import org.yaml.model.YMap
import org.yaml.parser.YamlParser

class YNodeBranchBuilderTest extends FunSuite with Matchers {

  test("Range for empty key with brothers") {
    val text     = "a: b\n"
    val root     = YamlParser(text).documents().head.node
    val position = AmfPosition(2, 0)
    val branch   = NodeBranchBuilder.build(root, position, isJson = false)
    assert(branch.node.isInstanceOf[YMap])
  }

  test("Range for empty key with brothers no root") {
    val text = "a:\n  b:\n    key: value\n    "

    val root     = YamlParser(text).documents().head.node
    val position = AmfPosition(4, 4)
    val branch   = NodeBranchBuilder.build(root, position, isJson = false)
    assert(branch.node.isInstanceOf[YMap])
  }

  test("Range for empty key with empty line after") {
    val text = "a:\n  key: value\n  \n"

    val root     = YamlParser(text).documents().head.node
    val position = AmfPosition(3, 2)
    val branch   = NodeBranchBuilder.build(root, position, isJson = false)
    assert(branch.node.isInstanceOf[YMap])
  }

  test("Detect if we are inside a flow") {
    val text = """key:
                 |  subkey:
                 |    - a
                 |    - b
                 |    - c
                 |  subkey2: ["scalar", "scalar2"]
                 |  subkey3: {
                 |    key: val
                 |  }
                 |""".stripMargin

    val root = YamlParser(text).documents(true).head.node

    val inA     = AmfPosition(3, 6)
    val aBranch = NodeBranchBuilder.build(root, inA, isJson = false)
    aBranch.isInFlow should be(false)

    val inScalar2     = AmfPosition(6, 25)
    val scalar2Branch = NodeBranchBuilder.build(root, inScalar2, isJson = false)
    scalar2Branch.isInFlow should be(true)

    val inVal     = AmfPosition(8, 10)
    val valBranch = NodeBranchBuilder.build(root, inVal, isJson = false)
    valBranch.isInFlow should be(true)

  }

}
