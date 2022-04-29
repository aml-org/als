package org.mulesoft.als.suggestions.test.core

import org.mulesoft.als.suggestions.RawSuggestion
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces.AMLCompletionPlugin

import scala.concurrent.Future

trait DummyPlugins {

  class DummyInvalidCompletionPlugin extends AMLCompletionPlugin {

    override def id = "DummyInvalidCompletionPlugin"

    override def resolve(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] =
      Future.successful(Seq())
  }

  object DummyInvalidCompletionPlugin {
    def apply() = new DummyInvalidCompletionPlugin()
  }

  class DummyCompletionPlugin extends AMLCompletionPlugin {

    override def id = "DummyCompletionPlugin"

    override def resolve(params: AmlCompletionRequest): Future[Seq[RawSuggestion]] =
      Future.successful(
        Seq(RawSuggestion("dummy newText", "dummy displayText", "dummy description", Seq(), "dummy whiteSpaces"))
      )
  }

  object DummyCompletionPlugin {
    def apply() = new DummyCompletionPlugin()
  }
}
