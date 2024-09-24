package org.mulesoft.als.suggestions

import org.mulesoft.als.common.YPartBranch
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces._
import org.mulesoft.lsp.feature.completion.CompletionItem

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.postfixOps

class CompletionProviderAST(request: AmlCompletionRequest) extends CompletionProvider {

  private def brothersAndPrefix(prefix: String)(s: RawSuggestion): Boolean =
    !(request.astPartBranch.isKey && (request.astPartBranch.brothersKeys contains s.newText)) &&
      s.newText.startsWith(prefix)

  private def arraySiblings(value: String): Boolean = request.astPartBranch match {
    case yPart: YPartBranch => yPart.arraySiblings.contains(value)
    case _                  => false
  }

  override def suggest(): Future[Seq[CompletionItem]] =
    if (request.astPartBranch.isMultiline || request.astPartBranch.isPositionOutsideLastEndNode) Future.successful(Nil)
    else
      request.completionsPluginHandler
        .pluginSuggestions(request)
        .map(suggestions => {
          suggestions
            .filter(brothersAndPrefix(request.prefix))
            .filterNot(rs => arraySiblings(rs.newText))
            .map(request.styler.rawToStyledSuggestion)
        })
}

object CompletionProviderAST {
  def apply(request: AmlCompletionRequest): CompletionProviderAST =
    new CompletionProviderAST(request)
}
