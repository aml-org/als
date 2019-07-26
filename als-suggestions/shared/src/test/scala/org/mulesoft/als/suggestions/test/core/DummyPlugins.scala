package org.mulesoft.als.suggestions.test.core

import org.mulesoft.als.suggestions.interfaces.CompletionPlugin
import org.mulesoft.als.suggestions.{CompletionParams, RawSuggestion}

import scala.concurrent.Future

trait DummyPlugins {

  class DummyInvalidCompletionPlugin extends CompletionPlugin {

    override def id = "DummyInvalidCompletionPlugin"

    override def resolve(params: CompletionParams): Future[Seq[RawSuggestion]] =
      Future.successful(Seq())
  }

  object DummyInvalidCompletionPlugin {
    def apply() = new DummyInvalidCompletionPlugin()
  }

  class DummyCompletionPlugin extends CompletionPlugin {

    override def id = "DummyCompletionPlugin"

    override def resolve(params: CompletionParams): Future[Seq[RawSuggestion]] =
      Future.successful(
        Seq(
          RawSuggestion("dummy newText",
                        "dummy displayText",
                        "dummy description",
                        Seq(),
                        isKey = false,
                        "dummy whiteSpaces")))
  }

  object DummyCompletionPlugin {
    def apply() = new DummyCompletionPlugin()
  }
}
