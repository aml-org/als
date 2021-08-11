package org.mulesoft.als.actions.codeactions

import org.mulesoft.als.actions.codeactions.plugins.base.CodeActionFactory
import org.mulesoft.als.actions.codeactions.plugins.conversions.RamlTypeToJsonSchema
import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}

class RamlTypeToJsonSchemaTest extends BaseCodeActionTests {
  behavior of "Raml Type conversion to Json schema"

  it should "extract declared raml type" in {
    val elementUri                       = "conversions/declared-type.raml"
    val range                            = PositionRange(Position(4, 8), Position(4, 10))
    val pluginFactory: CodeActionFactory = RamlTypeToJsonSchema

    runTest(elementUri, range, pluginFactory)
  }

  it should "extract inlined raml type" in {
    val elementUri                       = "conversions/declared-type.raml"
    val range                            = PositionRange(Position(15, 14), Position(15, 17))
    val pluginFactory: CodeActionFactory = RamlTypeToJsonSchema

    runTest(elementUri, range, pluginFactory, golden = Some("conversions/inlined-result.raml.golden.yaml"))
  }

  it should "extract raml type with external example" in {
    val elementUri                       = "conversions/complex.raml"
    val range                            = PositionRange(Position(22, 14), Position(24, 17))
    val pluginFactory: CodeActionFactory = RamlTypeToJsonSchema

    runTest(elementUri, range, pluginFactory, golden = Some("conversions/external-example.raml.golden.yaml"))
  }

  it should "extract raml type with external in properties" in {
    val elementUri                       = "conversions/complex.raml"
    val range                            = PositionRange(Position(29, 11), Position(32, 18))
    val pluginFactory: CodeActionFactory = RamlTypeToJsonSchema

    runTest(elementUri, range, pluginFactory, golden = Some("conversions/external-in-props.raml.golden.yaml"))
  }

  it should "extract raml type resolved when referencing an internal declaration" in {
    val elementUri                       = "conversions/complex.raml"
    val range                            = PositionRange(Position(13, 9), Position(13, 12))
    val pluginFactory: CodeActionFactory = RamlTypeToJsonSchema

    runTest(elementUri, range, pluginFactory, golden = Some("conversions/resolve-internal-reference.raml.golden.yaml"))
  }

  it should "extract type with inlined json schema" in {
    val elementUri                       = "conversions/inlined-json.raml"
    val range                            = PositionRange(Position(4, 6), Position(5, 7))
    val pluginFactory: CodeActionFactory = RamlTypeToJsonSchema

    runTest(elementUri, range, pluginFactory, golden = Some("conversions/type-with-inlined-json.raml.golden.yaml"))
  }

  it should "not extract properties" in {
    val elementUri                       = "conversions/declared-type.raml"
    val range                            = PositionRange(Position(6, 15), Position(6, 17))
    val pluginFactory: CodeActionFactory = RamlTypeToJsonSchema

    runTestNotApplicable(elementUri, range, pluginFactory)
  }
}
