package org.mulesoft.als.suggestions.test.aml

class DeclarationReferenceTest extends AMLSuggestionsTest {
  override def rootPath: String = "AML/declarations/simple"

  test("test simple local reference") {

    withDialect("simple-ref.yaml", Set("aType"), "dialect.yaml")
  }

  test("test declared reference") {

    withDialect("declared-ref.yaml", Set("aType"), "dialect.yaml")
  }

  test("test simple local started ref") {

    withDialect("simple-ref-started.yaml", Set("aType"), "dialect.yaml")
  }

  test("test simple non existing started ref") {

    withDialect("simple-non-existing-started.yaml", Set.empty, "dialect.yaml")
  }

}
