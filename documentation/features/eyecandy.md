# Features and modules
## Eye-Candy features
##### We currently support the following features for eye-candy: Hover, Folding Ranges, Rename.
### Hover
![Playground Hover1](../../images/playground/hover1.png)
![Playground Hover2](../../images/playground/hover2.png)
###### We use AML Vocabularies to extract a description for each field, with which we populate LSP request for Hover.
### Document Highlight
![Playground Highlight1](../../images/playground/highlight.png)
![Playground Highlight2](../../images/playground/highlight.gif)
###### We use our lexical information and relationship graph to highlight each referenced node inside a file for a given position.
### Rename
![Playground Rename1](../../images/playground/rename.gif)
![Playground Rename2](../../images/playground/rename2.gif)
![Playground Rename3](../../images/playground/rename3.gif)
###### We use our relationship graph to detect and propagate each renaming action. This can be further managed with the "prepare rename" request which we also provide.
### Selection Range
![Playground Selection](../../images/playground/selection-range.gif)
###### Using our lexical information, we provide LSP Selection Range Request functionality on multiple cursors.
### Folding Range
![Playground Rename1](../../images/playground/folding.gif)
###### Using our lexical information, we provide LSP Folding Ranges Request functionality.