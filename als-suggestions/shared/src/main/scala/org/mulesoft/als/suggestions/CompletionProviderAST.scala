package org.mulesoft.als.suggestions

import org.mulesoft.als.common.{ASTPartBranch, YPartBranch, YamlUtils}
import org.mulesoft.als.suggestions.aml.AmlCompletionRequest
import org.mulesoft.als.suggestions.interfaces._
import org.mulesoft.common.client.lexical.Position
import org.mulesoft.lsp.feature.completion.CompletionItem
import org.yaml.model.YComment

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
    if (request.astPartBranch.isMultiline) Future.successful(Nil)
    else if (isInsideComment(request.position.toAmfPosition, request.astPartBranch)) Future.successful(Nil)
    else
      request.completionsPluginHandler
        .pluginSuggestions(request)
        .map(suggestions => {
          suggestions
            .filter(brothersAndPrefix(request.prefix))
            .filterNot(rs => arraySiblings(rs.newText))
            .map(request.styler.rawToStyledSuggestion)
        })

  private def isInsideComment(position: Position, astPartBranch: ASTPartBranch): Boolean =
    astPartBranch match {
      case yPart: YPartBranch =>
        // we are inside a comment if the current node is an empty YScalar and the request position matches a YComment on the parent node
        yPart.isEmptyNode && yPart.parentEntry.exists(parent => {
          parent.children.exists {
            case comment: YComment if YamlUtils.contains(comment.range, position) => true
            case _ => false
          }

        })
      case _ => false
    }
}

object CompletionProviderAST {
  def apply(request: AmlCompletionRequest): CompletionProviderAST =
    new CompletionProviderAST(request)
}
