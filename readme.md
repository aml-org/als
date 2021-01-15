# AML Language Server
[![Build Status](https://jenkins.build.msap.io/buildStatus/icon?job=ALS/als/master/)](https://jenkins.build.msap.io/job/ALS/job/als/job/master/)

## Overview

### What is ALS?
AML Language Server (ALS for short), is an implementation of [Microsoft's Language Server Protocol (LSP)](https://microsoft.github.io/language-server-protoco) with a primary focus towards API tooling and API specifications such as RAML, OpenAPI and AsyncAPI. The objective is to ease API development by relying on LSP to support those API specifications in as many IDEs as possible. That being said, ALS can be extended to support any type of documents, beyong API tooling and API specifications.

Supported types of documents:
+ [RAML](https://github.com/raml-org/raml-spec/blob/master/versions/raml-10/raml-10.md) (0.8 and 1.0)
+ [OpenAPI](https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.0.md) (2.0 and 3.0)
+ [AsyncAPI](https://github.com/asyncapi/asyncapi/blob/master/versions/2.0.0/asyncapi.md) (2.0)
+ [Any other type of documents via AML dialect](https://aml-org.github.io/aml-spec/dialects/) (1.0)

### What does ALS do?
ALS relies on the [Anything Modeling Language (AML)](https://a.ml/docbook/overview_aml.html), more specifically AML's [dialect](https://aml-org.github.io/aml-spec/dialects/) functionality. ALS uses AML to provide language server functionalities, such as validation, over documents also referred to as "instances" or "instance documents". It can also deduce possible contextual values in any given position of an instance document.

Using the representation of an API in a model, it is possible to generate an outline or [Document Symbol](https://microsoft.github.io/language-server-protocol/specification#textDocument_documentSymbol) to give a more visual abstraction of an instance.

### Who should use ALS?
ALS is for anyone interested in authoring specification documents in their favorite IDEs, including out-of-the-box API specification documents (aka "API definitions"), but also any custom document using AML's dialect functionality. It can also be used by developers to create IDE plugins based on LSP.

### What relies on ALS today?
Today, ALS is used in MuleSoft Anypoint Design Center as well as in [Anypoint Studio](https://www.mulesoft.com/platform/studio) to provide live feedback while authoring API definitions. You can see a [preview](#previews) below.


## Getting started

### Using ALS as a language server
ALS can run as a standalone language server which can then be used with any IDE. Most modern IDEs either natively support LSP or provide a way to support LSP via plugin.

The ALS server JAR can be downloaded as follows:

```shell
$ curl https://repository-master.mulesoft.org/nexus/content/repositories/releases/org/mule/als/als-server_2.12/3.x.x/als-server_2.12-3.x.x.jar -o als-server.jar
```

and then ran independently like so:

```shell
$ java -jar als-server.jar --port {XXXX}
```

### Adapting Microsoft LSP example for VSCode
The [Microsoft's VSCode LSP example](https://github.com/microsoft/vscode-extension-samples/tree/master/lsp-sample/client) can be adapted by following [these instructions](./documentation/vscode-client-example/vscode-howto.md) which will guide you through running an ALS client in VSCode.

### Other examples
- [VS Code extension](./documentation/vscode-client-example/vscode-howto.md): instructions on how to plug ALS into the VSCode LSP client
- [Sublime Text 3 extension](./documentation/sublime-3-example/st3-howto.md): instructions for plugin ALS into ST3 LSP client   
- [IntelliJ extension](./documentation/intellij-example/intellij-howto.md): instructions for plugin ALS into IntelliJ's LSP client
- [Java LSP4J](./documentation/java-client-example/client.md): example on how to connect to a LSP server in Java using LSP4J, which can then be used for connecting IDE features to a plugin

### Previews

#### MuleSoft Anypoint Design Center example:
![AD Example](./images/usage-example-ad.gif)

#### Visual Studio Code completion example (LSP):
![VS Code Example](./images/usage-example-vscode.gif)

#### Visual Studio Code document symbol example (LSP):
![VS Code Example](./images/document-symbol-vscode.gif)
****

## Other considerations

### LSP support in ALS
[Microsoft's Language Server Protocol (LSP)](https://microsoft.github.io/language-server-protocol/overview), is a tooling standardization for IDEs, which defines a Client-Server architecture used to abstract a general implementation of used language-specific smarts.

The idea behind this Client-Server architecture, is that the Server works as a standalone (write once and always behave the same) functionality provider.

With this server, it's up to the client (each IDE plugin) to just understand LSP messaging and communicate, agnostic of the server implementation.

#### Currently supported LSP features
Currently, we support the following LSP capabilities:
##### [Basic features](./documentation/features/features.md)
+ [Completion](https://microsoft.github.io/language-server-protocol/specification#textDocument_completion)
+ [DocumentSymbol](https://microsoft.github.io/language-server-protocol/specification#textDocument_documentSymbol)
+ [Diagnostics](https://microsoft.github.io/language-server-protocol/specification#textDocument_publishDiagnostics)

##### [Navigation features](./documentation/features/navigation.md)
+ [Find Reference](https://microsoft.github.io/language-server-protocol/specification#textDocument_references)
+ [Goto Definition](https://microsoft.github.io/language-server-protocol/specification#textDocument_definition)
+ [Goto Type Definition](https://microsoft.github.io/language-server-protocol/specification#textDocument_typeDefinition)
+ [Goto Implementation](https://microsoft.github.io/language-server-protocol/specification#textDocument_implementation)
+ [DocumentLink](https://microsoft.github.io/language-server-protocol/specification#textDocument_documentLink)

##### [Refactor features](./documentation/features/refactor.md)
+ [PrepareRename](https://microsoft.github.io/language-server-protocol/specification#textDocument_prepareRename)
+ [Rename](https://microsoft.github.io/language-server-protocol/specification#textDocument_rename)
+ [Code Action](https://microsoft.github.io/language-server-protocol/specification#textDocument_codeAction)
+ [Formatting](https://microsoft.github.io/language-server-protocol/specification#textDocument_formatting)
+ [RangeFormatting](https://microsoft.github.io/language-server-protocol/specification#textDocument_rangeFormatting)

##### [EyeCandy features](./documentation/features/eyecandy.md)
+ [Document Highlight](https://microsoft.github.io/language-server-protocol/specification#textDocument_documentHighlight)
+ [Folding Range](https://microsoft.github.io/language-server-protocol/specification#textDocument_foldingRange)
+ [Hover](https://microsoft.github.io/language-server-protocol/specification#textDocument_hover)
+ [Selection Range](https://microsoft.github.io/language-server-protocol/specification#textDocument_selectionRange)


#### Currently extended features (not defined in LSP)
Besides LSP Messages, we also support other Custom operations, which are detailed [here](./documentation/features/custom-messages.md)

## Contributing
If you are interested in contributing to this project, please make sure to read our [contributing guidelines](./documentation/CONTRIBUTING.md).
