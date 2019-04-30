package org.mulesoft.lsp.feature.completion

import org.mulesoft.lsp.feature.RequestType

case object CompletionRequestType extends RequestType[CompletionParams, Either[Seq[CompletionItem], CompletionList]]

case object CompletionResolveRequestType extends RequestType[CompletionItem, CompletionItem]
