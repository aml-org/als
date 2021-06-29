package org.mulesoft.als.common

import amf.core.client.common.position.{Position => AmfPosition}
import org.scalatest.FlatSpec
import org.yaml.model.YScalar

class YamlWrapperTest extends FlatSpec {

  behavior of "Flow cases"
  private val regularFlowCase =
    """key:
      |  subKey1:
      |    subKey2:
      |      subKey3: value
      |      subKey4: value
      |      subKey5: {
      |        entry1: value,
      |      entry2: value,
      |          entry3: value,
      |        entry4: value
      |      }
      |    subKey8: ["scalar", "scalar2"]
      |""".stripMargin

  private val emptyScalarInflow =
    """k: [a, b, ]""".stripMargin

  private val emptyEntryWithMapInflow =
    """key: [ , {k:"scalar"}, {k:"scalar2"}]
      |key2: [{k:"scalar"}, , {k:"scalar2"}]
      |key3: [{k:"scalar", {k:"scalar2"}, ]
      |""".stripMargin

  private val incompleteKey =
    """key:
      |  sub
      |  subKey2: value
      |""".stripMargin

  it should "identify a correct Root" in {
    val nodes: FlatAstBuilder = FlatAstBuilder(regularFlowCase)
    assert(nodes.size() == 61)
    assert(nodes.assertScalarValue(AmfPosition(8, 8), "entry2"))
    assert(nodes.assertScalarValue(AmfPosition(9, 13), "entry3"))
    assert(nodes.assertScalarValue(AmfPosition(12, 25), "scalar2"))
  }

  it should "Check number of nodes" in {
    val nodes: FlatAstBuilder = FlatAstBuilder(regularFlowCase)
    assert(nodes.getNodeForPosition(AmfPosition(3, 4)).size == 10)
    assert(nodes.getNodeForPosition(AmfPosition(8, 8)).size == 17)
    assert(nodes.getNodeForPosition(AmfPosition(9, 20)).size == 17)
    assert(nodes.getNodeForPosition(AmfPosition(12, 25)).size == 13)
  }

  ignore should "Find empty entry in array" in {
    val nodes = FlatAstBuilder(emptyScalarInflow)
    assert(nodes.assertScalarValue(AmfPosition(1, 9), ""))
  }

  ignore should "Find empty map entry inflow" in {
    val nodes = FlatAstBuilder(emptyEntryWithMapInflow)
    assert(nodes.assertScalarValue(AmfPosition(1, 10), "k"))
  }

  ignore should "Find incomplete key on map" in {
    val nodes = FlatAstBuilder(incompleteKey)
    assert(nodes.getNodeForPosition(AmfPosition(2, 5)).size == 4)
    assert(nodes.getNodeForPosition(AmfPosition(2, 5)).size == 3)
    assert(nodes.assertScalarValue(AmfPosition(2, 4), "sub"))
  }

  behavior of "Array cases"
  private val arrayCase =
    """key:
      |  sk:
      |    - a: b
      |      c: d
      |      
      |    - e
      |    - f
      |  sk2: value
      |key2: value
      |""".stripMargin

  private val variantsArrayCase =
    """k:
      |  - a
      |  - b
      |    c
      |  d
      |  
      |  - g
      |""".stripMargin

  private val arrayCaseJson =
    """{
      |"a": {
      |  "b": "c",
      |  "d": ["e", 
      |  "f"]
      |  }
      |}""".stripMargin

  it should "Check inside array" in {
    val nodes = FlatAstBuilder(arrayCase)
    assert(nodes.assertScalarValue(AmfPosition(4, 6), "c"))
    assert(nodes.assertScalarValue(AmfPosition(7, 6), "f"))
    assert(nodes.assertScalarValue(AmfPosition(9, 3), "key2"))
  }

  ignore should "Check inside array with invalid entry" in {
    val nodes = FlatAstBuilder(variantsArrayCase)
    assert(nodes.assertScalarValue(AmfPosition(4, 4), "c"))
    assert(nodes.assertScalarValue(AmfPosition(5, 2), "d"))
  }

  it should "Check Json array" in {
    val nodes = FlatAstBuilder(arrayCaseJson, false)
    assert(nodes.assertScalarValue(AmfPosition(2, 2), "a"))
  }

  behavior of "Primitive cases"

  private val primitivesCase =
    """key:
      |  string: "this is a string"
      |  int: 23
      |  bool: true
      |  float: 22.3
      |  longString: "this is
      |  another string"
      |""".stripMargin

  private val primitivesCaseJson =
    """{
      |"string" : "this is a string",
      |"int": 23,
      |"bool": true,
      |"float": 23.3
      |}
      |""".stripMargin

  it should "Check primitives in YAML" in {
    val nodes   = FlatAstBuilder(primitivesCase)
    val intNode = nodes.getLastNode(AmfPosition(3, 8))
    assert(intNode.isInstanceOf[YScalar] && intNode.asInstanceOf[YScalar].value == 23)
    assert(nodes.assertScalarValue(AmfPosition(2, 15), "this is a string"))
    val doubleNode = nodes.getLastNode(AmfPosition(5, 10))
    assert(doubleNode.isInstanceOf[YScalar] && doubleNode.asInstanceOf[YScalar].value.toString.toDouble == 22.3)
    val booleanNode = nodes.getLastNode(AmfPosition(4, 10))
    assert(booleanNode.isInstanceOf[YScalar] && booleanNode.asInstanceOf[YScalar].value.toString.toBoolean)
    assert(nodes.assertScalarValue(AmfPosition(6, 18), "this is another string"))
  }

  it should "Check primitives Json" in {
    val nodes = FlatAstBuilder(primitivesCaseJson, false)
    assert(nodes.assertScalarValue(AmfPosition(2, 15), "this is a string"))
    val intNode = nodes.getLastNode(AmfPosition(3, 7))
    assert(intNode.isInstanceOf[YScalar] && intNode.asInstanceOf[YScalar].value == 23)
    val booleanNode = nodes.getLastNode(AmfPosition(4, 8))
    assert(booleanNode.isInstanceOf[YScalar] && booleanNode.asInstanceOf[YScalar].value.toString.toBoolean)
    val doubleNode = nodes.getLastNode(AmfPosition(5, 9))
    assert(doubleNode.isInstanceOf[YScalar] && doubleNode.asInstanceOf[YScalar].value == 23.3)
  }
}
