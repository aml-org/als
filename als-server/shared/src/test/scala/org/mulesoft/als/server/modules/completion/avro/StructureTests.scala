package org.mulesoft.als.server.modules.completion.avro

import scala.concurrent.ExecutionContext

class StructureTests extends AVROSuggestionTestServer {

  override implicit val executionContext = ExecutionContext.Implicits.global

  test("test avro empty") {
    runTest("base/dir1/avro-schema-01.avsc", Set("\"name\": \"$1\"", "\"type\": \"$1\"", "\"default\": \"$1\"", "\"logicalType\": \"$1\""))
  }

  test("test avro types") {
    runTest(
      "base/dir1/avro-schema-02.avsc",
      Set(
        "\"null\"",
        "\"boolean\"",
        "\"int\"",
        "\"long\"",
        "\"float\"",
        "\"double\"",
        "\"bytes\"",
        "\"string\"",
        "\"record\"",
        "\"enum\"",
        "\"array\"",
        "\"map\"",
        "\"fixed\""
      )
    )
  }

  test("test avro nested schema") {
    runTest("base/dir1/avro-schema-03.avsc", Set("\"name\": \"$1\"", "\"type\": \"$1\"", "\"default\": \"$1\"", "\"logicalType\": \"$1\""))
  }

  test("test avro nested types") {
    runTest(
      "base/dir1/avro-schema-04.avsc",
      Set(
        "\"null\"",
        "\"boolean\"",
        "\"int\"",
        "\"long\"",
        "\"float\"",
        "\"double\"",
        "\"bytes\"",
        "\"string\"",
        "\"record\"",
        "\"enum\"",
        "\"array\"",
        "\"map\"",
        "\"fixed\"",
        "\"org.apache.avro.file.Header\"",
        "\"Sync\""
      )
    )
  }
}
