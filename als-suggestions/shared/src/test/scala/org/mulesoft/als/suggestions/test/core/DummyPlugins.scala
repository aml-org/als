package org.mulesoft.als.suggestions.test.core

import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin
import org.mulesoft.als.suggestions.{AMLCompletionParams, RawSuggestion}

import scala.concurrent.Future

trait DummyPlugins {

  class DummyInvalidCompletionPlugin extends AMLCompletionPlugin {

    override def id = "DummyInvalidCompletionPlugin"

    override def resolve(params: AMLCompletionParams): Future[Seq[RawSuggestion]] =
      Future.successful(Seq())
  }

  object DummyInvalidCompletionPlugin {
    def apply() = new DummyInvalidCompletionPlugin()
  }

  class DummyCompletionPlugin extends AMLCompletionPlugin {

    override def id = "DummyCompletionPlugin"

    override def resolve(params: AMLCompletionParams): Future[Seq[RawSuggestion]] =
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
