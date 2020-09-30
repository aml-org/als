package org.mulesoft.als.actions.codeactions

import amf.plugins.document.vocabularies.model.document.Dialect
import org.mulesoft.als.actions.codeactions.plugins.base.CodeActionFactory
import org.mulesoft.als.actions.codeactions.plugins.declarations.library.ExtractRamlToLibraryCodeAction
import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}
import org.mulesoft.amfintegration.dialect.dialects.raml.raml10.Raml10TypesDialect

class ExtractToLibraryTest extends BaseCodeActionTests {
  behavior of "Extract declared element to fragment"

  it should "extract declared simple type from RAML in range" in {
    val elementUri                       = "extract-element/raml-libraries/declared-type-simple.raml"
    val range                            = PositionRange(Position(3, 8), Position(3, 10))
    val dialect: Dialect                 = Raml10TypesDialect.dialect
    val pluginFactory: CodeActionFactory = ExtractRamlToLibraryCodeAction

    runTest(elementUri, range, pluginFactory)
  }

  it should "extract multiple types from RAML in range" in {
    val elementUri                       = "extract-element/raml-libraries/declared-type-multiple.raml"
    val range                            = PositionRange(Position(3, 8), Position(8, 10))
    val dialect: Dialect                 = Raml10TypesDialect.dialect
    val pluginFactory: CodeActionFactory = ExtractRamlToLibraryCodeAction

    runTest(elementUri, range, pluginFactory)
  }

  it should "extract declared complex type from RAML in range" in {
    val elementUri                       = "extract-element/raml-libraries/declared-type-complex.raml"
    val range                            = PositionRange(Position(3, 8), Position(3, 10))
    val dialect: Dialect                 = Raml10TypesDialect.dialect
    val pluginFactory: CodeActionFactory = ExtractRamlToLibraryCodeAction

    runTest(elementUri, range, pluginFactory)
  }

  ignore should "extract declared nested type from RAML in range" in {
    val elementUri                       = "extract-element/raml-libraries/declared-type-nested.raml"
    val range                            = PositionRange(Position(4, 8), Position(4, 10))
    val dialect: Dialect                 = Raml10TypesDialect.dialect
    val pluginFactory: CodeActionFactory = ExtractRamlToLibraryCodeAction

    runTest(elementUri, range, pluginFactory)
  }

  it should "extract declared annotation type from RAML" in {
    val elementUri                       = "extract-element/raml-libraries/declared-annotation-type-simple.raml"
    val range                            = PositionRange(Position(3, 7), Position(3, 7))
    val dialect: Dialect                 = Raml10TypesDialect.dialect
    val pluginFactory: CodeActionFactory = ExtractRamlToLibraryCodeAction

    runTest(elementUri, range, pluginFactory)
  }

  it should "extract declared resource type from RAML" in {
    val elementUri                       = "extract-element/raml-libraries/declared-resource-type-simple.raml"
    val range                            = PositionRange(Position(4, 9), Position(4, 15))
    val dialect: Dialect                 = Raml10TypesDialect.dialect
    val pluginFactory: CodeActionFactory = ExtractRamlToLibraryCodeAction

    runTest(elementUri, range, pluginFactory)
  }

  it should "extract declared trait from RAML" in {
    val elementUri                       = "extract-element/raml-libraries/declared-trait-simple.raml"
    val range                            = PositionRange(Position(4, 9), Position(4, 15))
    val dialect: Dialect                 = Raml10TypesDialect.dialect
    val pluginFactory: CodeActionFactory = ExtractRamlToLibraryCodeAction

    runTest(elementUri, range, pluginFactory)
  }

  ignore should "extract documentation item from RAML" in {
    val elementUri                       = "extract-element/raml-libraries/declared-documentation-item-simple.raml"
    val range                            = PositionRange(Position(4, 9), Position(4, 9))
    val dialect: Dialect                 = Raml10TypesDialect.dialect
    val pluginFactory: CodeActionFactory = ExtractRamlToLibraryCodeAction

    runTest(elementUri, range, pluginFactory)
  }
}
