# Suggestions module
## Interface

`org.mulesoft.als.suggestions.Core` class is the entry point to the suggestions.

`init()` method returning void/unit future must be called before starting the work with the system.

After that user should call the following method for unit text:
```
def prepareText(text:String, offset:Int, syntax:Syntax):String
```
This method modifies the potentially broken text to make it readable by the parser.
 
`org.mulesoft.als.suggestions.CompletionProvider` is dedicated to return actual suggestions.

```
class CompletionProvider {
def withConfig(cfg:ICompletionConfig):CompletionProvider
def suggest: Future[Seq[ISuggestion]]
def suggest(filterByPrefix:Boolean): Future[Seq[ISuggestion]]
...
```

The initial task is to construct the proper provider.
Usual code looks like this:

```
      val completionConfig = new CompletionConfig()
        .withEditorStateProvider(editorStateProvider)
        .withAstProvider(astProvider)
        .withFsProvider(platformFSProvider)
        .withOriginalContent(unmodifiedContent)

      CompletionProvider().withConfig(completionConfig)
```

Editor state provider provides unit text:
```
trait IEditorStateProvider {
    def getText: String

    def getPath: String

    def getBaseName: String

    def getOffset: Int
}
```
AST provider provides the pre-parsed high-level AST:
```
trait IASTProvider {
    def getASTRoot: IHighLevelNode

    def getSelectedNode: Option[IParseResult]

    def language:Vendor

    def syntax: Syntax
}
```

FS provider should provide the AST:
```
trait IFSProvider {

    def content(fullPath: String): Future[String]

    def dirName(fullPath: String): String

    def existsAsync(path: String): Future[Boolean]

    def resolve(contextPath: String, relativePath: String): Option[String]

    def isDirectory(fullPath: String): Boolean

    def readDirAsync(path: String): Future[Seq[String]]

    def isDirectoryAsync(path: String): Future[Boolean]
}
```

And original contents should contain unit contents before it was pre-patched to fix grammar errors.

The result is obtained by `suggest` method, it returns a list of 

```
trait ISuggestion {
    def text: String

    def description: String

    def displayText: String

    def prefix: String

    def category: String

    def trailingWhitespace:String
}
```

Where text is actual suggestion test, display text is what is displayed to the user, and prefix is what should be replaced by suggestion text.