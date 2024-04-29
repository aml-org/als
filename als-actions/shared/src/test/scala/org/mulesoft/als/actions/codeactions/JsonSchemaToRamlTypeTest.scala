package org.mulesoft.als.actions.codeactions

import org.mulesoft.als.actions.codeactions.plugins.base.CodeActionFactory
import org.mulesoft.als.actions.codeactions.plugins.conversions.JsonSchemaToRamlType
import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}

class JsonSchemaToRamlTypeTest extends BaseCodeActionTests {
  behavior of "Json schema conversion to Raml type"

  it should "convert inlined json schema" in {
    val elementUri                       = "conversions/json-schema-to-raml-type.raml"
    val range                            = PositionRange(Position(8, 30), Position(8, 31))
    val pluginFactory: CodeActionFactory = JsonSchemaToRamlType

    runTest(elementUri, range, pluginFactory, Some("conversions/inlined-json-schema-to-raml-type.raml.golden.yaml"))
  }

  it should "convert inlined json schema when called from properties" in {
    val elementUri                       = "conversions/json-schema-to-raml-type.raml"
    val range                            = PositionRange(Position(18, 22), Position(18, 23))
    val pluginFactory: CodeActionFactory = JsonSchemaToRamlType

    runTest(
      elementUri,
      range,
      pluginFactory,
      Some("conversions/inlined-json-schema-to-raml-type-from-properties.raml.golden.yaml")
    )
  }

  it should "convert inlined json schema when selecting a range" in {
    val elementUri                       = "conversions/json-schema-to-raml-type.raml"
    val range                            = PositionRange(Position(9, 19), Position(20, 22))
    val pluginFactory: CodeActionFactory = JsonSchemaToRamlType

    runTest(elementUri, range, pluginFactory, Some("conversions/inlined-json-schema-to-raml-range.raml.golden.yaml"))
  }

  it should "not be available in an inherited shape" in {
    val elementUri                       = "conversions/json-schema-to-raml-type.raml"
    val range                            = PositionRange(Position(27, 9), Position(29, 22))
    val pluginFactory: CodeActionFactory = JsonSchemaToRamlType

    runTestNotApplicable(elementUri, range, pluginFactory)
  }

  it should "convert inlined json schema at payload" in {
    val elementUri                       = "conversions/json-schema-to-raml-type.raml"
    val range                            = PositionRange(Position(48, 21), Position(48, 27))
    val pluginFactory: CodeActionFactory = JsonSchemaToRamlType

    runTest(
      elementUri,
      range,
      pluginFactory,
      Some("conversions/inlined-json-schema-to-raml-type-at-payload.raml.golden.yaml")
    )
  }

}
