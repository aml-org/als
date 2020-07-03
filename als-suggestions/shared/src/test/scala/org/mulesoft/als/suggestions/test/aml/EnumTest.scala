package org.mulesoft.als.suggestions.test.aml

import org.mulesoft.als.suggestions.plugins.aml.webapi.{Oas20CommonMediaTypes, OasCommonMediaTypes, OasResponseCodes}

class EnumTest extends AMLSuggestionsTest {
  override def rootPath: String = "AML/enums"

  test("AllowMultiple Enum - Single Value") {
    withDialect(
      "instances/testArraysInstance02.yaml",
      Set("\n- First", "\n- Second", "\n- Third", "\n- Fourth"),
      "dialects/testArraysDialect.yaml"
    )
  }
  test("AllowMultiple Enum - Multiple Value") {
    withDialect("instances/testArraysInstance01.yaml",
                Set("Second", "Third", "Fourth"),
                "dialects/testArraysDialect.yaml")
  }
  test("AllowMultiple Enum - Single Value w/prefix") {
    withDialect("instances/testArraysInstance03.yaml",
                Set("\n- First", "\n- Fourth"),
                "dialects/testArraysDialect.yaml")
  }
  test("AllowMultiple Enum - Multiple Value w/prefix") {
    withDialect("instances/testArraysInstance04.yaml",
                Set("Fourth"), // todo: is logic?
                "dialects/testArraysDialect.yaml")
  }

  ignore("Known Values - Response Codes") {
    withDialect("instances/responseCodes.yaml",
                OasResponseCodes.all.map(r => s"$r:\n      ").toSet,
                "dialects/knownValuesDialect.yaml")
  }

  ignore("Known Values - Media Types") {
    withDialect("instances/mediaTypes.yaml",
                Oas20CommonMediaTypes.all.map(r => s"$r:\n      ").toSet,
                "dialects/knownValuesDialect.yaml")
  }

}
