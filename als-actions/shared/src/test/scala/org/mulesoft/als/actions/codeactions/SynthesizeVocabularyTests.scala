package org.mulesoft.als.actions.codeactions

import org.mulesoft.als.actions.codeactions.plugins.base.CodeActionFactory
import org.mulesoft.als.actions.codeactions.plugins.vocabulary.SynthesizeVocabularyCodeAction
import org.mulesoft.als.common.dtoTypes.{Position, PositionRange}

class SynthesizeVocabularyTests extends BaseCodeActionTests {
  val basePath = "vocabulary/synthesize/"
  behavior of "Synthesize Vocabulary"

  it should "Synthesize when there is no nodeMapping or property mapping with an associated term" in {
    val elementUri                       = s"${basePath}dialect01.yaml"
    val range                            = PositionRange(Position(1, 12), Position(1, 14))
    val pluginFactory: CodeActionFactory = SynthesizeVocabularyCodeAction

    runTest(elementUri, range, pluginFactory)
  }

  it should "Skip properties and node mappings that have an associated term with a local vocabulary" in {
    val elementUri                       = s"${basePath}dialect02.yaml"
    val range                            = PositionRange(Position(1, 12), Position(1, 14))
    val pluginFactory: CodeActionFactory = SynthesizeVocabularyCodeAction

    runTest(elementUri, range, pluginFactory)
  }

  it should "Skip properties and node mappings that have an associated term with an external vocabulary" in {
    val elementUri                       = s"${basePath}dialect03.yaml"
    val range                            = PositionRange(Position(1, 12), Position(1, 14))
    val pluginFactory: CodeActionFactory = SynthesizeVocabularyCodeAction

    runTest(elementUri, range, pluginFactory)
  }

  it should "Skip properties and node mappings that have an associated term with either an external or local vocabulary" in {
    val elementUri                       = s"${basePath}dialect04.yaml"
    val range                            = PositionRange(Position(1, 12), Position(1, 14))
    val pluginFactory: CodeActionFactory = SynthesizeVocabularyCodeAction

    runTest(elementUri, range, pluginFactory)
  }

  it should "Add 'uses' key if not present" in {
    val elementUri                       = s"${basePath}dialect05.yaml"
    val range                            = PositionRange(Position(1, 12), Position(1, 14))
    val pluginFactory: CodeActionFactory = SynthesizeVocabularyCodeAction

    runTest(elementUri, range, pluginFactory)
  }

}
