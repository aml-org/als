package org.mulesoft.als.suggestions.interfaces

trait Suggestion {
    def text: String

    def description: String

    def displayText: String

    def prefix: String

    def category: String

    def trailingWhitespace:String
}
