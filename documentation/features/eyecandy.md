# Features and modules
## Eye-Candy features
###### The eye-candy classification refers to features that are focused on adding visual information or modifying the way the content is being viewed but doesn't change the actual content"

##### The following features are supported for eye-candy: Hover, Folding Ranges, and Rename.

### [Hover](https://microsoft.github.io/language-server-protocol/specification#textDocument_hover)
![Playground Hover1](../../images/playground/hover1.png)
![Playground Hover2](../../images/playground/hover2.png)
###### AML Vocabularies are used to extract a description for each field, with which LSP requests are populated for Hover.
### [Document Highlight](https://microsoft.github.io/language-server-protocol/specification#textDocument_documentHighlight)
![Playground Highlight1](../../images/playground/highlight.png)
![Playground Highlight2](../../images/playground/highlight.gif)
###### Relationship graphs are used to detect and propagate each renaming action. You can further manage this with the "prepare rename" request (also provided).
### [Selection Range](https://microsoft.github.io/language-server-protocol/specification#textDocument_selectionRange)
![Playground Selection](../../images/playground/selection-range.gif)
###### Using the lexical information, LSP Selection Range Requests are provided on multiple cursors.
### [Folding Range](https://microsoft.github.io/language-server-protocol/specification#textDocument_foldingRange)
![Playground Rename1](../../images/playground/folding.gif)
###### Using the  lexical information, LSP Folding Ranges Requests functionality is provided.
