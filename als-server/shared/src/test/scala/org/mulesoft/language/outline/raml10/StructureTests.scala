package org.mulesoft.language.outline.raml10

import org.mulesoft.language.outline.AlsStructureTest

class StructureTests extends AlsStructureTest with RAML10Test {

  test("/test 001") {
    runTest("structure/test001/api.raml", "structure/test001/api-outline.json")
  }

  test("/test 002") {
    runTest("structure/test002/api.raml", "structure/test002/api-outline.json")
  }

  test("/test 004") {
    runTest("structure/test004/api.raml", "structure/test004/api-outline.json")
  }

  test("/test 006") {
    runTest("structure/test006/api.raml", "structure/test006/api-outline.json")
  }

  ignore("/test 007") {
    runTest("structure/test007/api.raml", "structure/test007/api-outline.json")
  }

  test("/test 008") {
    runTest("structure/test008/api.raml", "structure/test008/api-outline.json")
  }

  test("/test 009") {
    runTest("structure/test009/api.raml", "structure/test009/api-outline.json")
  }

  test("/test 010") {
    runTest("structure/test010/api.raml", "structure/test010/api-outline.json")
  }
}
