# Features and modules
## Navigation features
##### Currently supported LSP navigation messages are: Document Link, Go to Definition, Type Definition, Implementation, and Find References
### [DocumentLink](https://microsoft.github.io/language-server-protocol/specification#textDocument_documentLink)
![Playground Document Link](../../images/playground/document_link.gif)
###### We detect each reference to a whole file (without a specified target range), and use this to populate LSP request for a given file document links
### [Goto Definition](https://microsoft.github.io/language-server-protocol/specification#textDocument_definition)
![Playground Definition1](../../images/playground/definition.gif)
![Playground Definition2](../../images/playground/rename3.gif)
###### We detect each link between nodes, for example specific schemas, types, aliases, etc., And provide LSP functionality accordingly 
### [Goto Type Definition](https://microsoft.github.io/language-server-protocol/specification#textDocument_typeDefinition)
![Playground Type Definition](../../images/playground/typedef_implem.gif)
###### We detect template/implementation type relationships (for example RAML Resource Types and Traits), And provide LSP functionality accordingly
###[Goto Implementation](https://microsoft.github.io/language-server-protocol/specification#textDocument_implementation)
![Playground Implementation](../../images/playground/typedef_implem.gif)
###### We detect template/implementation type relationships (for example RAML Resource Types and Traits), And provide LSP functionality accordingly. This is an inverse reference as Go To Type Definiton
### [Find Reference](https://microsoft.github.io/language-server-protocol/specification#textDocument_references)
![Playground References1](../../images/playground/reference.gif)
![Playground References2](../../images/playground/rename3.gif)
###### We detect each link between nodes, for example specific schemas, types, aliases, etc., And provide LSP functionality accordingly. This is an inverse reference as Go To Definiton