package org.mulesoft.als.suggestions.interfaces

trait IEditorStateProvider {
    def getText: String

    def getPath: String

    def getBaseName: String

    def getOffset: Int
}
