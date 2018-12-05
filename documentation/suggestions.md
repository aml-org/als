# Suggestions module
## Scala Interface

`org.mulesoft.als.suggestions.Core` class is the entry point to the suggestions.

`init()` method returning void/unit future must be called before starting the work with the system.

After that user should call the following method for unit text:
```
def prepareText(text:String, offset:Int, syntax:Syntax):String
```
This method modifies the potentially broken text to make it readable by the parser. 

Prepared/modified text should be passed as a unit text to other suggestions methods unless said otherwise.
 
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
This text must be first prepared by `prepareText` method.

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

And `withOriginalContent` should contain unit contents before it was pre-patched to fix grammar errors.

The result is obtained by `suggest` method, it returns a list of: 

```
trait ISuggestion {

    def text: String

    def description: String

    def displayText: String

    def prefix: String

    def category: String
}
```

Where `text` is actual suggestion test, `displayText` is what is displayed to the user, and `prefix` is what should be replaced by suggestion text.
`description` and `category` provide additional suggestion data for the users.

## Js Interface
Besides scala interface, suggestions module also contains JS proxy, which simplifies usage from JS tooling.

This interface assumes that suggestions module launches code parsing and text patching internally. 

Those who wish to control this process can create their own JS proxy basing on this one.

Initialization should be performed first by calling
`def init(fsProvider: IFSProvider): js.Promise[Unit]`

FSProvider instance must provide file system information:
```
trait IFSProvider extends js.Object {

  def contentAsync(fullPath: String): Promise[String] = js.native

  def dirName(fullPath: String): String = js.native

  def name(fullPath: String): String = js.native

  def existsAsync(path: String): Promise[Boolean] = js.native

  def resolve(contextPath: String, relativePath: String): Option[String] = js.native

  def isDirectory(fullPath: String): Boolean = js.native

  def readDirAsync(path: String): Promise[js.Array[String]] = js.native

  def isDirectoryAsync(path: String): Promise[Boolean] = js.native

  def content(fullPath: String): String

  def exists(fullPath: String): Boolean

  def readDir(fullPath: String): js.Array[String]

  def separatorChar(): String
}
```

After initialization is finished (promise is resolved), suggestions can be obtained by calling:
`def suggest(language: String, url: String, position: Int): js.Promise[js.Array[ISuggestion]]`

Here an array of suggestions is returned from the promise:
```
class ISuggestion (

    val text: String,

    val description: String,

    val displayText: String,

    val prefix: String,

    val category: String
) extends js.Object
{

}
```

Where `text` is actual suggestion test, `displayText` is what is displayed to the user, and `prefix` is what should be replaced by suggestion text.
`description` and `category` provide additional suggestion data for the users.
