package org.mulesoft.als.suggestions.antlr.plugins.grpc

import org.mulesoft.als.suggestions.antlr.plugins.CompletionPlugin
import org.mulesoft.als.suggestions.antlr.plugins.grpc.snippets.RootSnippets
import org.mulesoft.amfintegration.amfconfiguration.executioncontext.Implicits.global
import org.mulesoft.lsp.feature.completion.CompletionItem

import scala.concurrent.Future

object GRPCSnippetsCompletionPlugin extends CompletionPlugin {
  // just to test, if we go this way we need to add logic for positioning and repeated keys (only syntax?)
  // ideally the place where each suggestion could be called should be determined based on the BaseUnit, but let's see what AMF returns
  override def suggest(): Future[Seq[CompletionItem]] =
    Future(
      RootSnippets.getAll.map(_.toCompletionItem)
    )
}

