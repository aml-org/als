package org.mulesoft.als.suggestions.test.aml

class DeclarationWithLibraryReferenceTest extends AMLSuggestionsTest {
  override def rootPath: String = "AML/declarations/with-library"

  test("test with lib simple") {

    withDialect("simple-empty.yaml", Set("defs.", "aType"), "dialect-lib.yaml")
  }

  test("test with aliased ref") {

    withDialect("aliased.yaml", Set("defs.libType1", "defs.libType2"), "dialect-lib.yaml")
  }

  test("test with invalid aliased") {

    withDialect("invalid-aliased.yaml", Set.empty, "dialect-lib.yaml")
  }

}
