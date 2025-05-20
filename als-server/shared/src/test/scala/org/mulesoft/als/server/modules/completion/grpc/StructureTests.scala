package org.mulesoft.als.server.modules.completion.grpc

import scala.concurrent.ExecutionContext

class StructureTests extends GRPCSuggestionTestServer {

  override implicit val executionContext = ExecutionContext.Implicits.global

  test("test grpc empty") {
    runTest("empty.proto", Set("import",
      "enum",
      ";",
      "service",
      "option",
      "extend",
      "message",
      "package"))
  }
}
