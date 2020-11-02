#AML Language Server
## Overview
### What is ALS?
AML Language Server (ALS for short), is our implementation of [Microsoft's Language Server Protocol (LSP)](https://microsoft.github.io/language-server-protoco) targeted towards API tooling.

The objective is to fully support IDE tooling, in order to ease API development. Not only that, but also eventually enable to define a custom dialect, and provide out of the box tooling for it's instance definitions.

#### About API Models (AML/AMF)
ALS uses [Anything Modeling Language (AML)](https://a.ml/docbook/overview_aml.html) capability to define a [Dialect](https://aml-org.github.io/aml-spec/dialects/) used to define it's instances.
Using this Dialect we are able to validate and deduce possible values given a position in the instance document.

Also, using this representation of the API in a Model, we are able to generate an outline or [Document Symbol](https://microsoft.github.io/language-server-protocol/specification#textDocument_documentSymbol) to give a more visual abstraction of the Instance.
 
##### Out of the box accepted definitions

+ [RAML](https://github.com/raml-org/raml-spec/blob/master/versions/raml-10/raml-10.md) (0.8 and 1.0)
+ [OpenApi2](https://github.com/OAI/OpenAPI-Specification)
+ [OpenApi3](https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.0.md)
+ [AsyncAPI2](https://github.com/asyncapi/asyncapi/blob/master/versions/2.0.0/asyncapi.md) (AsyncApi 2.0)

+ [Dialects](https://aml-org.github.io/aml-spec/dialects/) (AML)

###### playground for als-features at: https://als-playground.herokuapp.com/

### About LSP
[Microsoft's Language Server Protocol (LSP)](https://microsoft.github.io/language-server-protocol/overview), is a tooling standardization for IDEs, which defines a Client-Server architecture used to abstract a general implementation of used language-specific smarts.

The idea behind this Client-Server architecture, is that the Server works as a standalone (write once and always behave the same) functionality provider.

With this server, it's up to the client (each IDE plugin) to just understand LSP messaging and communicate, agnostic of the server implementation.

#### Currently supported LSP features
Currently, we support the following LSP capabilities:
+ [Completion](https://microsoft.github.io/language-server-protocol/specification#textDocument_completion)
+ [DocumentSymbol](https://microsoft.github.io/language-server-protocol/specification#textDocument_documentSymbol)
+ [Diagnostics](https://microsoft.github.io/language-server-protocol/specification#textDocument_publishDiagnostics)
+ [PrepareRename](https://microsoft.github.io/language-server-protocol/specification#textDocument_prepareRename)
+ [Rename](https://microsoft.github.io/language-server-protocol/specification#textDocument_rename)
+ [Find Reference](https://microsoft.github.io/language-server-protocol/specification#textDocument_references)
+ [Goto Definition](https://microsoft.github.io/language-server-protocol/specification#textDocument_definition)
+ [Goto Type Definition](https://microsoft.github.io/language-server-protocol/specification#textDocument_typeDefinition)
+ [Goto Implementation](https://microsoft.github.io/language-server-protocol/specification#textDocument_implementation)
+ [DocumentLink](https://microsoft.github.io/language-server-protocol/specification#textDocument_documentLink)
+ [Document Highlight](https://microsoft.github.io/language-server-protocol/specification#textDocument_documentHighlight)
+ [Folding Range](https://microsoft.github.io/language-server-protocol/specification#textDocument_foldingRange)
+ [Hover](https://microsoft.github.io/language-server-protocol/specification#textDocument_hover)
+ [Selection Range](https://microsoft.github.io/language-server-protocol/specification#textDocument_selectionRange)
+ [Code Action](https://microsoft.github.io/language-server-protocol/specification#textDocument_codeAction)
    - Extract to Declaration
    - Extract to Fragment
    - Extract to Library
    - Delete declaration (cascade)

#### Currently extended features (not defined in LSP)
Besides LSP Messages, we also support the following Custom operations:
+ Clean Validation
+ Project Configuration
+ Serialization
+ Conversions
+ Find File Usages
+ Get/Set Configurations


###### [Contributing](./documentation/CONTRIBUTING.md)