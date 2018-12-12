package org.mulesoft.als.suggestions.interfaces

trait ICompletionConfig {

    def astProvider:Option[IASTProvider]

    def editorStateProvider:Option[IEditorStateProvider]

    def fsProvider:Option[IExtendedFSProvider]

    def originalContent:Option[String]

    def withAstProvider(obj:IASTProvider):ICompletionConfig

    def withEditorStateProvider(obj:IEditorStateProvider):ICompletionConfig

    def withFsProvider(obj:IExtendedFSProvider):ICompletionConfig

    def withOriginalContent(str:String):ICompletionConfig
}
