package org.mulesoft.als.actions.codeactions

import org.mulesoft.als.actions.codeactions.plugins.base.CodeActionFactory
import org.mulesoft.als.actions.codeactions.plugins.declarations.`trait`.ExtractTraitCodeAction
import org.mulesoft.als.actions.codeactions.plugins.declarations.samefile.ExtractElementCodeAction
import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}

class ExtractTraitTest extends BaseCodeActionTests {
  behavior of "Extract trait"

  it should "extract in basic operation" in {
    val elementUri                       = "extract-element/traits/raml10.raml"
    val range                            = PositionRange(Position(44, 5), Position(44, 6))
    val pluginFactory: CodeActionFactory = ExtractTraitCodeAction

    runTest(elementUri, range, pluginFactory, golden = Some("extract-element/traits/basic.golden.yaml"))
  }

  it should "extract on operation with existing traits" in {
    val elementUri                       = "extract-element/traits/raml10.raml"
    val range                            = PositionRange(Position(79, 3), Position(79, 6))
    val pluginFactory: CodeActionFactory = ExtractTraitCodeAction

    runTest(elementUri, range, pluginFactory, golden = Some("extract-element/traits/existing-traits.golden.yaml"))
  }

  it should "not extract on operation in a resource type" in {
    // When we try to extract from an operation inside a resource type declaration
    // we do not realize we are inside an operation, because the Resource type is treated by amf
    // a ObjectNode rather than a RT.
    val elementUri                       = "extract-element/traits/raml10.raml"
    val range                            = PositionRange(Position(21, 6), Position(21, 9))
    val pluginFactory: CodeActionFactory = ExtractTraitCodeAction

    runTestNotApplicable(elementUri, range, pluginFactory)
  }

  it should "extract get" in {
    val elementUri                       = "extract-element/traits/raml10.raml"
    val range                            = PositionRange(Position(59, 6), Position(59, 7))
    val pluginFactory: CodeActionFactory = ExtractTraitCodeAction

    runTest(elementUri, range, pluginFactory, golden = Some("extract-element/traits/get.golden.yaml"))
  }
//
  it should "extract put" in {
    val elementUri                       = "extract-element/traits/raml10.raml"
    val range                            = PositionRange(Position(71, 5), Position(71, 7))
    val pluginFactory: CodeActionFactory = ExtractTraitCodeAction

    runTest(elementUri, range, pluginFactory, golden = Some("extract-element/traits/put.golden.yaml"))
  }
//
  it should "extract post" in {
    val elementUri                       = "extract-element/traits/raml10.raml"
    val range                            = PositionRange(Position(68, 6), Position(68, 6))
    val pluginFactory: CodeActionFactory = ExtractTraitCodeAction

    runTest(elementUri, range, pluginFactory, golden = Some("extract-element/traits/post.golden.yaml"))
  }

  it should "extract patch" in {
    val elementUri                       = "extract-element/traits/raml10.raml"
    val range                            = PositionRange(Position(54, 10), Position(54, 13))
    val pluginFactory: CodeActionFactory = ExtractTraitCodeAction

    runTest(elementUri, range, pluginFactory, golden = Some("extract-element/traits/patch.golden.yaml"))
  }

  it should "extract delete" in {
    val elementUri                       = "extract-element/traits/raml10.raml"
    val range                            = PositionRange(Position(50, 10), Position(50, 15))
    val pluginFactory: CodeActionFactory = ExtractTraitCodeAction

    runTest(elementUri, range, pluginFactory, golden = Some("extract-element/traits/delete.golden.yaml"))
  }

  it should "add declaration key" in {
    val elementUri                       = "extract-element/traits/will-add-declaration-key.raml"
    val range                            = PositionRange(Position(3, 4), Position(3, 7))
    val pluginFactory: CodeActionFactory = ExtractTraitCodeAction

    runTest(elementUri, range, pluginFactory)
  }

  it should "not extract to declaration on RAML Trait fragments" in {
    val elementUri                       = "extract-element/traits/trait-fragment.raml"
    val range                            = PositionRange(Position(1, 2), Position(1, 3))
    val pluginFactory: CodeActionFactory = ExtractTraitCodeAction
    runTestNotApplicable(elementUri, range, pluginFactory)
  }

}
