package org.mulesoft.als.actions.codeactions

import org.mulesoft.als.actions.codeactions.plugins.base.CodeActionFactory
import org.mulesoft.als.actions.codeactions.plugins.declarations.fragment.{
  ExtractRamlDeclarationToFragmentCodeAction,
  ExtractRamlTypeToFragmentCodeAction
}
import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}

class ExtractToFragmentTest extends BaseCodeActionTests {
  behavior of "Extract raml inlined element to fragment"

  it should "extract inlined type from RAML in range" in {
    val elementUri                       = "extract-element/raml-fragments/inlined-scalar.raml"
    val range                            = PositionRange(Position(9, 19), Position(10, 21))
    val pluginFactory: CodeActionFactory = ExtractRamlTypeToFragmentCodeAction

    runTest(elementUri, range, pluginFactory)
  }

  behavior of "Extract declared element to fragment"

  it should "extract declared simple type from RAML in range" in {
    val elementUri                       = "extract-element/raml-fragments/declared-type-simple.raml"
    val range                            = PositionRange(Position(3, 8), Position(3, 10))
    val pluginFactory: CodeActionFactory = ExtractRamlTypeToFragmentCodeAction

    runTest(elementUri, range, pluginFactory)
  }

  it should "extract declared complex type from RAML in range" in {
    val elementUri                       = "extract-element/raml-fragments/declared-type-complex.raml"
    val range                            = PositionRange(Position(3, 8), Position(3, 10))
    val pluginFactory: CodeActionFactory = ExtractRamlTypeToFragmentCodeAction

    runTest(elementUri, range, pluginFactory)
  }

  it should "extract declared nested type from RAML in range" in {
    val elementUri                       = "extract-element/raml-fragments/declared-type-nested.raml"
    val range                            = PositionRange(Position(4, 8), Position(4, 10))
    val pluginFactory: CodeActionFactory = ExtractRamlTypeToFragmentCodeAction

    runTest(elementUri, range, pluginFactory)
  }

  it should "extract declared annotation type from RAML" in {
    val elementUri                       = "extract-element/raml-fragments/declared-annotation-type-simple.raml"
    val range                            = PositionRange(Position(3, 7), Position(3, 7))
    val pluginFactory: CodeActionFactory = ExtractRamlDeclarationToFragmentCodeAction

    runTest(elementUri, range, pluginFactory)
  }

  it should "extract declared resource type from RAML" in {
    val elementUri                       = "extract-element/raml-fragments/declared-resource-type-simple.raml"
    val range                            = PositionRange(Position(4, 9), Position(4, 15))
    val pluginFactory: CodeActionFactory = ExtractRamlDeclarationToFragmentCodeAction

    runTest(elementUri, range, pluginFactory)
  }

  it should "extract declared trait from RAML" in {
    val elementUri                       = "extract-element/raml-fragments/declared-trait-simple.raml"
    val range                            = PositionRange(Position(4, 9), Position(4, 15))
    val pluginFactory: CodeActionFactory = ExtractRamlDeclarationToFragmentCodeAction

    runTest(elementUri, range, pluginFactory)
  }

  ignore should "extract documentation item from RAML" in {
    val elementUri                       = "extract-element/raml-fragments/declared-documentation-item-simple.raml"
    val range                            = PositionRange(Position(4, 9), Position(4, 9))
    val pluginFactory: CodeActionFactory = ExtractRamlDeclarationToFragmentCodeAction

    runTest(elementUri, range, pluginFactory)
  }

  it should "extract inlined RAML type from Async 2 in range" in {
    val elementUri                       = "extract-element/async-fragments/raml-type.yaml"
    val range                            = PositionRange(Position(13, 13), Position(14, 14))
    val pluginFactory: CodeActionFactory = ExtractRamlTypeToFragmentCodeAction

    runTest(elementUri, range, pluginFactory)
  }

  it should "extract inlined RAML type defined with Json schema" in {
    val elementUri                       = "extract-element/raml-fragments/inlined-raml-json-schema-type.raml"
    val range                            = PositionRange(Position(12, 34), Position(15, 44))
    val pluginFactory: CodeActionFactory = ExtractRamlTypeToFragmentCodeAction

    runTest(elementUri, range, pluginFactory)
  }

  it should "extract whole Json schema when requested over property inside RAML type defined with Json schema" in {
    val elementUri                       = "extract-element/raml-fragments/inlined-raml-json-schema-type.raml"
    val range                            = PositionRange(Position(16, 43), Position(16, 44))
    val pluginFactory: CodeActionFactory = ExtractRamlTypeToFragmentCodeAction

    runTest(elementUri, range, pluginFactory)
  }

  it should "extract declared RAML type defined with Json schema" in {
    val elementUri                       = "extract-element/raml-fragments/declared-raml-json-schema-type.raml"
    val range                            = PositionRange(Position(12, 16), Position(12, 17))
    val pluginFactory: CodeActionFactory = ExtractRamlTypeToFragmentCodeAction

    runTest(elementUri, range, pluginFactory)
  }

}
