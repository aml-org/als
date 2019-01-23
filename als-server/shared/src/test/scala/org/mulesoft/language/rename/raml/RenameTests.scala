package org.mulesoft.language.rename.raml

class RenameTests extends RAMLRenameTest {

  ignore("test 01") {
    runTest("test001/api.raml", "MyType2")
  }
}
