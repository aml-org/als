package org.mulesoft.als.suggestions.test.aml

import org.scalatest.Assertion

import scala.concurrent.Future

class UnionSuggestionsTest extends SuggestionsWithDialectTest {
  override def rootPath: String = "AML/union"

  test("Test explicit discriminator at root level") {
    runTest("root.yaml", "dialect.yaml")
  }

  test("Test explicit discriminator at non-root level") {
    runTest("non-root-union.yaml", "dialect.yaml")
  }

  test("Test type discriminator value") {
    runTest("discriminator-value.yaml", "dialect.yaml")
  }

  test("Give correct mappings on typeA") {
    runTest("typeA.yaml", "dialect.yaml")
  }

  test("Give correct mappings on typeB") {
    runTest("typeB.yaml", "dialect.yaml")
  }

  test("Suggestions on root with non-explicit discriminator") {
    runTest("non-explicit-root.yaml", "dialect-no-explicit-discriminator.yaml")
  }

  test("Suggestions non-explicit discriminator with shared property") {
    runTest("non-explicit-shared-prop.yaml", "dialect-no-explicit-discriminator.yaml")
  }

  test("Suggestions non-explicit discriminator with not shared property") {
    runTest("non-explicit-not-shared-prop.yaml", "dialect-no-explicit-discriminator.yaml")
  }

  test("Suggestions non-explicit discriminator with not mandatory property") {
    runTest("non-explicit-not-mandatory-prop.yaml", "dialect-no-explicit-discriminator.yaml")
  }

  test("Suggestions in declaration node") {
    runTest("instance-declares.yaml", "dialect-declares.yaml")
  }

  test("Suggestions in declaration node - distinct") {
    runTest("instance-declares-distinct.yaml", "dialect-declares.yaml")
  }

  test("Suggestions in declaration node - discriminator") {
    runTest("instance-declares-with-discriminator.yaml", "dialect-declares-with-discriminator.yaml")
  }

  test("Suggestions in declaration node - in discriminator value") {
    runTest("instance-declares-with-discriminator-value.yaml", "dialect-declares-with-discriminator.yaml")
  }
}
