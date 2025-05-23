package org.mulesoft.als.server.modules.completion.grpc

import scala.concurrent.ExecutionContext

class StructureTests extends GRPCSuggestionTestServer {

  override implicit val executionContext = ExecutionContext.Implicits.global

  test("test grpc empty") {
    runTest("empty.proto", Set(
      "package", "extend", "service", ";", "import", "enum", "message", "option",
      """message ${1:name} {
      |  $2
      |}$0""".stripMargin,
      """enum ${1:name} {
      |  $2
      |}$0""".stripMargin,
      "import ${1| |weak |public |}\"$2\";\n$0",
      """option ${1:option name} = ${2:option};
        |$0""".stripMargin,
      "syntax = \"proto3\";\n$0",
      "package ${1:package name};\n$0"
    ))
  }
}
