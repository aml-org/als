package org.mulesoft.als.suggestions.interfaces

trait ICompletionResponse {
    def kind: LocationKind
	
    def request: ICompletionRequest

    def suggestions: Seq[ISuggestion]

    def isEmpty:Boolean

    def nonEmpty:Boolean
}
