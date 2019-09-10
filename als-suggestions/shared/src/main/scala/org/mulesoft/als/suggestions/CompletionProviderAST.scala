package org.mulesoft.als.suggestions

import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.postfixOps

class CompletionProviderAST(request: AmlCompletionRequest) extends CompletionProvider {

  private def brothersAndPrefix(prefix: String)(s: RawSuggestion): Boolean =
    !(request.yPartBranch.isKey && (request.yPartBranch.brothersKeys contains s.newText)) &&
      s.newText.startsWith(prefix)

  private def arraySiblings(value: String): Boolean =
    request.yPartBranch.arraySiblings.contains(value)

  override def suggest(): Future[Seq[Suggestion]] = {

    CompletionsPluginHandler
      .pluginSuggestions(request)
      .map(suggestions => {
        (suggestions filter brothersAndPrefix(request.prefix))
          .filterNot(rs => arraySiblings(rs.newText))
          .map(request.styler.rawToStyledSuggestion)
      })
  }
}

object CompletionProviderAST {
  def apply(request: AmlCompletionRequest): CompletionProviderAST =
    new CompletionProviderAST(request)
}
