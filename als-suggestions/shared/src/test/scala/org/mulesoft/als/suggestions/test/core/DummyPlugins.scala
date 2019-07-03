package org.mulesoft.als.suggestions.test.core

import org.mulesoft.als.suggestions.interfaces.{CompletionParams, CompletionPlugin, RawSuggestion}
import org.mulesoft.lsp.edit.TextEdit

import scala.concurrent.Future

trait DummyPlugins {

  class DummyInvalidCompletionPlugin extends CompletionPlugin {

    override def id = "DummyInvalidCompletionPlugin"

    override def resolve(params: CompletionParams): Future[Seq[RawSuggestion]] = Future.successful(Seq())
  }

  object DummyInvalidCompletionPlugin {
    def apply() = new DummyInvalidCompletionPlugin()
  }

  class DummyCompletionPlugin extends CompletionPlugin {

    override def id = "DummyCompletionPlugin"

    override def resolve(params: CompletionParams) =
      Future.successful(Seq(new RawSuggestion {
        override def newText: String = "dummy newText"

        override def displayText: String = "dummy displayText"

        override def description: String = "dummy description"

        override def textEdits: Seq[TextEdit] = Seq()

        override def whiteSpacesEnding: String = "dummy whiteSpaces"
      }))
  }

  object DummyCompletionPlugin {
    def apply() = new DummyCompletionPlugin()
  }
}
