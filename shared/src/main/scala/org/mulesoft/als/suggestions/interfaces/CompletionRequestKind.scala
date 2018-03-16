package org.mulesoft.als.suggestions.interfaces

sealed class CompletionRequestKind{}

object CompletionRequestKind {

    val PROPERTY_NAMES:CompletionRequestKind = new CompletionRequestKind

    val PROPERTY_VALUES:CompletionRequestKind = new CompletionRequestKind

}
