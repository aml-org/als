package org.mulesoft.als.common

import org.mulesoft.common.client.lexical.{Position => AmfPosition}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers
import org.yaml.model.{YMap, YNodePlain, YScalar}
import org.yaml.parser.{JsonParser, YamlParser}

class YNodeBranchBuilderTest extends AnyFunSuite with Matchers {

  test("Range for empty key with brothers") {
    val text     = "a: b\n"
    val root     = YamlParser(text).documents().head.node
    val position = AmfPosition(2, 0)
    val branch   = NodeBranchBuilder.build(root, position, strict = false)
    assert(branch.node.isInstanceOf[YMap])
  }

  test("Range for empty key with brothers no root") {
    val text = "a:\n  b:\n    key: value\n    "

    val root     = YamlParser(text).documents().head.node
    val position = AmfPosition(4, 4)
    val branch   = NodeBranchBuilder.build(root, position, strict = false)
    assert(branch.node.isInstanceOf[YMap])
  }

  test("Range for empty key with empty line after") {
    val text = "a:\n  key: value\n  \n"

    val root     = YamlParser(text).documents().head.node
    val position = AmfPosition(3, 2)
    val branch   = NodeBranchBuilder.build(root, position, strict = false)
    assert(branch.node.isInstanceOf[YMap])
    assert(!branch.isAtRoot)
    assert(branch.brothersKeys.contains("key"))
  }

  test("Detect if we are inside a flow") {
    val text = """key:
                 |  subkey:
                 |    - a
                 |    - b
                 |    - c
                 |  subkey2: ["scalar", "scalar2"]
                 |  subkey3: {
                 |    key: val, key1: val, key2: val
                 |  }
                 |""".stripMargin

    val root = YamlParser(text).documents(true).head.node

    val inA     = AmfPosition(3, 6)
    val aBranch = NodeBranchBuilder.build(root, inA, strict = false)
    aBranch.strict should be(false)

    val inScalar2     = AmfPosition(6, 25)
    val scalar2Branch = NodeBranchBuilder.build(root, inScalar2, strict = false)
    scalar2Branch.strict should be(true)

    val inVal     = AmfPosition(8, 10)
    val valBranch = NodeBranchBuilder.build(root, inVal, strict = false)
    valBranch.strict should be(true)

  }

  test("Detect if we are in a key or not YAML") {
    val text = """key1:
                 |  subKey1: value
                 |  subKey2: {
                 |    key2: val
                 |  }
                 |  subKey3:
                 |    - a
                 |    - b
                 |""".stripMargin

    val root = YamlParser(text).documents(true).head.node

    val inKey1  = AmfPosition(1, 2)
    val aBranch = NodeBranchBuilder.build(root, inKey1, strict = false)
    aBranch.isKey should be(true)

    val subKey1 = AmfPosition(2, 3)
    val bBranch = NodeBranchBuilder.build(root, subKey1, strict = false)
    bBranch.isKey should be(true)

    val subKey2 = AmfPosition(3, 4)
    val cBranch = NodeBranchBuilder.build(root, subKey2, strict = false)
    cBranch.isKey should be(true)

    val inSubKey3 = AmfPosition(8, 7)
    val dBranch   = NodeBranchBuilder.build(root, inSubKey3, strict = false)
    dBranch.isKey should be(false)

  }

  test("Detect if we are in an array") {
    val text = """key:
                 |  subKey1:
                 |    - value1
                 |    - value2
                 |    - value3
                 |  subKey2: value
                 |  subkey3: [value4, value5, value6]
                 |""".stripMargin

    val root = YamlParser(text).documents(true).head.node

    val inArray = AmfPosition(3, 12)
    val aBranch = NodeBranchBuilder.build(root, inArray, strict = false)
    aBranch.isInArray should be(true)

    val inArray2 = AmfPosition(6, 8)
    val bBranch  = NodeBranchBuilder.build(root, inArray2, strict = false)
    bBranch.isInArray should be(false)

    val inArray3 = AmfPosition(5, 8)
    val cBranch  = NodeBranchBuilder.build(root, inArray3, strict = false)
    cBranch.isInArray should be(true)

    val inArray4 = AmfPosition(7, 13)
    val dBranch  = NodeBranchBuilder.build(root, inArray4, strict = false)
    dBranch.strict should be(true)
  }

  test("Detect if we are in root") {
    val text = """key:
                 |  subKey1:
                 |    subKey2:
                 |      subKey3:
                 |      subKey4: scalar1
                 |      subKey5: {
                 |        subKey6: scalar2,
                 |        subKey7: scalar3
                 |      }
                 |    subKey8: scalar4
                 |key2:
                 |  subKey9: value
                 |""".stripMargin

    val root = YamlParser(text).documents(true).head.node

    val key     = AmfPosition(1, 2)
    val aBranch = NodeBranchBuilder.build(root, key, strict = false)
    assert(aBranch.isAtRoot)

    val key2    = AmfPosition(11, 1)
    val bBranch = NodeBranchBuilder.build(root, key2, strict = false)
    assert(bBranch.isAtRoot)

    val subkey8 = AmfPosition(10, 5)
    val cBranch = NodeBranchBuilder.build(root, subkey8, strict = false)
    assert(!cBranch.isAtRoot)
  }

  test("Detect scalars in different levels") {
    val text = """key:
                 |  subKey1:
                 |    subKey2:
                 |      subKey3:
                 |      subKey4: scalar1
                 |      subKey5: {
                 |        subKey6: scalar2,
                 |        subKey7: scalar3
                 |      }
                 |    subKey8: scalar4
                 |""".stripMargin

    val root = YamlParser(text).documents(true).head.node

    val scalar1 = AmfPosition(5, 18)
    val aBranch = NodeBranchBuilder.build(root, scalar1, strict = false)
    assert(aBranch.node.asInstanceOf[YNodePlain].value.isInstanceOf[YScalar])

    val scalar3 = AmfPosition(8, 18)
    val bBranch = NodeBranchBuilder.build(root, scalar3, strict = false)
    assert(bBranch.node.asInstanceOf[YNodePlain].value.isInstanceOf[YScalar])

    val scalar4 = AmfPosition(10, 14)
    val cBranch = NodeBranchBuilder.build(root, scalar4, strict = false)
    assert(cBranch.node.asInstanceOf[YNodePlain].value.isInstanceOf[YScalar])
  }

  test("Detect brothers in different levels") {
    val text = """key:
                 |  subKey1:
                 |    subKey2:
                 |      subKey3:
                 |      subKey4: scalar1
                 |      subKey5: {
                 |        subKey6: scalar2,
                 |        subKey7: scalar3
                 |      }
                 |    subKey8: scalar4
                 |""".stripMargin

    val root = YamlParser(text).documents(true).head.node

    val subKey6   = AmfPosition(7, 9)
    val sk6Branch = NodeBranchBuilder.build(root, subKey6, strict = false)
    assert(sk6Branch.brothersKeys.head == "subKey7")

    val scalar2 = AmfPosition(10, 9)
    val bBranch = NodeBranchBuilder.build(root, scalar2, strict = false)
    assert(bBranch.brothersKeys.head == "subKey2")
    assert(!bBranch.brothersKeys.contains("subKey1"))
    assert(!bBranch.brothersKeys.contains("subKey3"))
  }

  test("Detect indentation in different levels") {
    val text = """key:
                 |  subKey1:
                 |    subKey2:
                 |      params:
                 |        - param1: scalar1
                 |
                 |          param2: scalar2
                 |      subKey3:
                 |        param4: value
                 |
                 |""".stripMargin

    val root = YamlParser(text).documents(true).head.node

    val param1Brother = NodeBranchBuilder.build(root, AmfPosition(6, 10), strict = false)
    assert(param1Brother.node.isInstanceOf[YMap])
    assert(param1Brother.brothersKeys.contains("param1"))

    val subKey3BrotherBranch = NodeBranchBuilder.build(root, AmfPosition(10, 6), strict = false)
    assert(subKey3BrotherBranch.node.isInstanceOf[YMap])
    assert(subKey3BrotherBranch.brothersKeys.contains("subKey3"))

    val param5BrotherBranch = NodeBranchBuilder.build(root, AmfPosition(10, 8), strict = false)
    assert(param5BrotherBranch.node.isInstanceOf[YMap])
    assert(param5BrotherBranch.brothersKeys.contains("param4"))

    val rootBranch = NodeBranchBuilder.build(root, AmfPosition(10, 0), strict = false)
    assert(rootBranch.node.isInstanceOf[YMap])
    assert(rootBranch.isAtRoot)

    val anotherRootBranch = NodeBranchBuilder.build(root, AmfPosition(6, 0), strict = false)
    assert(anotherRootBranch.node.isInstanceOf[YMap])
    assert(anotherRootBranch.isAtRoot)

  }

  test("Json Range for empty key with brothers") {
    val text = """{"a":"b"
                     |}""".stripMargin
    val root     = JsonParser(text).documents(true).head.node
    val position = AmfPosition(2, 1)
    val branch   = NodeBranchBuilder.build(root, position, strict = true)
    assert(branch.node.isInstanceOf[YMap])
  }

}
