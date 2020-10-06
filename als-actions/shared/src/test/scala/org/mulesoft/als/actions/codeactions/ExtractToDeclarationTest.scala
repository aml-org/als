package org.mulesoft.als.actions.codeactions

import amf.plugins.document.vocabularies.model.document.Dialect
import org.mulesoft.als.actions.codeactions.plugins.base.CodeActionFactory
import org.mulesoft.als.actions.codeactions.plugins.declarations.samefile.{
  ExtractElementCodeAction,
  ExtractRamlTypeCodeAction
}
import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.amfintegration.dialect.dialects.oas.OAS30Dialect
import org.mulesoft.amfintegration.dialect.dialects.raml.raml10.Raml10TypesDialect

class ExtractToDeclarationTest extends BaseCodeActionTests {
  behavior of "Extract element to declaration"

  it should "extract a schema from open api 3 parameter" in {
    val elementUri                       = "extract-element/schema-from-oas/schema.yaml"
    val range                            = PositionRange(Position(10, 15), Position(10, 16))
    val pluginFactory: CodeActionFactory = ExtractElementCodeAction

    runTest(elementUri, range, pluginFactory)
  }

  it should "extract a schema from oas 3 json (without components key)" in {
    val elementUri                       = "extract-element/schema-from-oas/schema2.json"
    val range                            = PositionRange(Position(10, 19), Position(10, 20))
    val pluginFactory: CodeActionFactory = ExtractElementCodeAction

    runTest(elementUri, range, pluginFactory)
  }

  it should "extract a schema from oas 3 json (with components key, no other child)" in {
    val elementUri                       = "extract-element/schema-from-oas/schema1.json"
    val range                            = PositionRange(Position(11, 19), Position(11, 20))
    val pluginFactory: CodeActionFactory = ExtractElementCodeAction

    runTest(elementUri, range, pluginFactory)
  }

  it should "extract a schema from oas 3 json (with componets key and one other)" in {
    val elementUri                       = "extract-element/schema-from-oas/schema.json"
    val range                            = PositionRange(Position(13, 19), Position(13, 20))
    val pluginFactory: CodeActionFactory = ExtractElementCodeAction

    runTest(elementUri, range, pluginFactory)
  }

  it should "extract a schema from oas 3 json (with componets key and one schema)" in {
    val elementUri                       = "extract-element/schema-from-oas/schema3.json"
    val range                            = PositionRange(Position(20, 19), Position(20, 20))
    val pluginFactory: CodeActionFactory = ExtractElementCodeAction

    runTest(elementUri, range, pluginFactory)
  }

  it should "extract a type from RAML 1.0 payload" in {
    val elementUri                       = "extract-element/raml-type/raml-type.raml"
    val range                            = PositionRange(Position(15, 27), Position(16, 26))
    val pluginFactory: CodeActionFactory = ExtractRamlTypeCodeAction

    runTest(elementUri, range, pluginFactory)
  }

  it should "extract a type from RAML 1.0 inlined scalar property" in {
    val elementUri                       = "extract-element/raml-type/scalar-range.raml"
    val range                            = PositionRange(Position(8, 14), Position(9, 14))
    val pluginFactory: CodeActionFactory = ExtractRamlTypeCodeAction

    runTest(elementUri, range, pluginFactory)
  }

  it should "DON'T extract payload from RAML" in {
    val elementUri                       = "extract-element/raml-type/payload-range.raml"
    val range                            = PositionRange(Position(7, 15), Position(9, 10))
    val pluginFactory: CodeActionFactory = ExtractRamlTypeCodeAction

    runTestNotApplicable(elementUri, range, pluginFactory)
  }

  it should "extract type from RAML in range" in {
    val elementUri                       = "extract-element/raml-type/property-range.raml"
    val range                            = PositionRange(Position(9, 19), Position(10, 21))
    val pluginFactory: CodeActionFactory = ExtractRamlTypeCodeAction

    runTest(elementUri, range, pluginFactory)
  }

  it should "extract inlined scalar type from RAML in range" in {
    val elementUri                       = "extract-element/raml-type/inlined-scalar.raml"
    val range                            = PositionRange(Position(10, 19), Position(10, 24))
    val pluginFactory: CodeActionFactory = ExtractRamlTypeCodeAction

    runTest(elementUri, range, pluginFactory)
  }
}
