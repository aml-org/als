package org.mulesoft.als.suggestions.test.aml

import org.scalatest.Assertion

import scala.concurrent.Future

class UnionSuggestionsTest extends SuggestionsWithDialectTest {
  override def rootPath: String = "AML/union"

  def runTest(file: String, dialect: String): Future[Assertion] =
    withDialect(file, s"expected/$file.json", dialect)

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

}
