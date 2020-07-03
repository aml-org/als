# Features and modules
## Navigation features
##### Currently supported LSP navigation messages are: Document Link, Go to Definition, Type Definition, Implementation, and Find References
### Document Link
![Playground Document Link](../../images/playground/document_link.gif)
###### We detect each reference to a whole file (without a specified target range), and use this to populate LSP request for a given file document links
### Go to Definition
![Playground Definition1](../../images/playground/definition.gif)
![Playground Definition2](../../images/playground/rename3.gif)
###### We detect each link between nodes, for example specific schemas, types, aliases, etc., And provide LSP functionality accordingly 
### Go to Type Definition
![Playground Type Definition](../../images/playground/typedef_implem.gif)
###### We detect template/implementation type relationships (for example RAML Resource Types and Traits), And provide LSP functionality accordingly
### Go to Type Implementation
![Playground Implementation](../../images/playground/typedef_implem.gif)
###### We detect template/implementation type relationships (for example RAML Resource Types and Traits), And provide LSP functionality accordingly. This is an inverse reference as Go To Type Definiton
### Find References
![Playground References1](../../images/playground/reference.gif)
![Playground References2](../../images/playground/rename3.gif)
###### We detect each link between nodes, for example specific schemas, types, aliases, etc., And provide LSP functionality accordingly. This is an inverse reference as Go To Definiton