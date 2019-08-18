package org.mulesoft.als.suggestions.test.aml

import amf.ProfileName

class DeclarationWithLibraryReferenceTest extends AMLSuggestionsTest {
  override def rootPath: String = "AML/declarations/with-library"

  test("test with lib simple") {

    withDialect("simple-empty.yaml", Set("defs.", "aType"), "dialect-lib.yaml", ProfileName("References 1.0"))
  }

  test("test with aliased ref") {

    withDialect("aliased.yaml", Set("libType1", "libType2"), "dialect-lib.yaml", ProfileName("References 1.0"))
  }

  test("test with invalid aliased") {

    withDialect("invalid-aliased.yaml", Set.empty, "dialect-lib.yaml", ProfileName("References 1.0"))
  }

}
