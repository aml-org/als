package org.mulesoft.als.actions.codeactions

import org.mulesoft.als.actions.codeactions.plugins.base.CodeActionFactory
import org.mulesoft.als.actions.codeactions.plugins.declarations.resourcetype.ExtractResourceTypeCodeAction
import org.mulesoft.als.actions.codeactions.plugins.declarations.samefile.ExtractElementCodeAction
import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}

class ExtractResourceTypeTest extends BaseCodeActionTests {
  behavior of "Extract resource type"

  it should "extract basic endpoint" in {
    val elementUri                       = "extract-element/resource-types/raml10.raml"
    val range                            = PositionRange(Position(45, 4), Position(45, 5))
    val pluginFactory: CodeActionFactory = ExtractResourceTypeCodeAction

    runTest(elementUri, range, pluginFactory, golden = Some("extract-element/resource-types/basic.golden.yaml"))
  }

  it should "extract multiple path level endpoint" in {
    val elementUri                       = "extract-element/resource-types/raml10.raml"
    val range                            = PositionRange(Position(38, 4), Position(39, 13))
    val pluginFactory: CodeActionFactory = ExtractResourceTypeCodeAction

    runTest(elementUri, range, pluginFactory, golden = Some("extract-element/resource-types/fullpath.golden.yaml"))
  }

  it should "extract endpoint with children" in {
    val elementUri                       = "extract-element/resource-types/raml10.raml"
    val range                            = PositionRange(Position(16, 4), Position(16, 5))
    val pluginFactory: CodeActionFactory = ExtractResourceTypeCodeAction

    runTest(elementUri, range, pluginFactory, golden = Some("extract-element/resource-types/children.golden.yaml"))
  }

  it should "extract child endpoint" in {
    val elementUri                       = "extract-element/resource-types/raml10.raml"
    val range                            = PositionRange(Position(17, 5), Position(17, 7))
    val pluginFactory: CodeActionFactory = ExtractResourceTypeCodeAction

    runTest(elementUri, range, pluginFactory, golden = Some("extract-element/resource-types/child.golden.yaml"))
  }

  it should "extract third level endpoint" in {
    val elementUri                       = "extract-element/resource-types/raml10.raml"
    val range                            = PositionRange(Position(24, 7), Position(24, 10))
    val pluginFactory: CodeActionFactory = ExtractResourceTypeCodeAction

    runTest(elementUri, range, pluginFactory, golden = Some("extract-element/resource-types/third.golden.yaml"))
  }

  it should "append to existing rt declaration key" in {
    val elementUri                       = "extract-element/resource-types/will-append-to-existing-declaration-key.raml"
    val range                            = PositionRange(Position(7, 3), Position(7, 6))
    val pluginFactory: CodeActionFactory = ExtractResourceTypeCodeAction

    runTest(elementUri, range, pluginFactory)
  }

  it should "not extract to declaration on RAML ResourceType fragments" in {
    val elementUri                       = "extract-element/resource-types/resource-type-fragment.raml"
    val range                            = PositionRange(Position(1, 2), Position(1, 3))
    val pluginFactory: CodeActionFactory = ExtractResourceTypeCodeAction
    runTestNotApplicable(elementUri, range, pluginFactory)
  }

}
