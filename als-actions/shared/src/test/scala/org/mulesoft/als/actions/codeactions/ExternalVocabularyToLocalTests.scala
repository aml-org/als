package org.mulesoft.als.actions.codeactions

import org.mulesoft.als.actions.codeactions.plugins.base.CodeActionFactory
import org.mulesoft.als.actions.codeactions.plugins.vocabulary.{
  ExternalVocabularyToLocalCodeAction,
  SynthesizeVocabularyCodeAction
}
import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}

class ExternalVocabularyToLocalTests extends BaseCodeActionTests {
  val basePath = "vocabulary/external2local/"
  behavior of "Convert external vocabulary to a local vocabulary"

  it should "Convert external vocabulary to a local vocabulary basic test" in {
    val elementUri                       = s"${basePath}dialect01.yaml"
    val range                            = PositionRange(Position(5, 5), Position(5, 8))
    val pluginFactory: CodeActionFactory = ExternalVocabularyToLocalCodeAction

    runTest(elementUri, range, pluginFactory)
  }

  it should "Keep external key if there is another external" in {
    val elementUri                       = s"${basePath}dialect02.yaml"
    val range                            = PositionRange(Position(5, 5), Position(5, 8))
    val pluginFactory: CodeActionFactory = ExternalVocabularyToLocalCodeAction

    runTest(elementUri, range, pluginFactory)
  }

  it should "Have no classTerms/propertyTerms if none is used" in {
    val elementUri                       = s"${basePath}dialect03.yaml"
    val range                            = PositionRange(Position(5, 5), Position(5, 8))
    val pluginFactory: CodeActionFactory = ExternalVocabularyToLocalCodeAction

    runTest(elementUri, range, pluginFactory)
  }

  it should "Add reference to existing 'uses' key if present" in {
    val elementUri                       = s"${basePath}dialect04.yaml"
    val range                            = PositionRange(Position(6, 4), Position(6, 22))
    val pluginFactory: CodeActionFactory = ExternalVocabularyToLocalCodeAction

    runTest(elementUri, range, pluginFactory)
  }

  it should "Resolve correctly mixed ranges for propertyTerms and merge properties for classTerms" in {
    val elementUri                       = s"${basePath}dialect05.yaml"
    val range                            = PositionRange(Position(4, 3), Position(4, 20))
    val pluginFactory: CodeActionFactory = ExternalVocabularyToLocalCodeAction

    runTest(elementUri, range, pluginFactory)
  }

}
