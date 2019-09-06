# Overview
## What is ALS?
AML Language Server (ALS for short), is our implementation of [Microsoft's Language Server Protocol (LSP)](https://microsoft.github.io/language-server-protoco) targeted towards API tooling.

The objective is to fully support IDE tooling, in order to ease API development. Not only that, but also eventually enable to define a custom dialect, and provide out of the box tooling for it's instance definitions.

### About API Models (AML/AMF)
ALS uses [Anything Modeling Language (AML)](https://a.ml/docbook/overview_aml.html) capability to define a [Dialect](https://aml-org.github.io/aml-spec/dialects/) used to define it's instances.
Using this Dialect we are able to validate and deduce possible values given a position in the instance document.

Also, using this representation of the API in a Model, we are able to generate an outline or [Document Symbol](https://microsoft.github.io/language-server-protocol/specification#textDocument_documentSymbol) to give a more visual abstraction of the Instance.
 
#### Out of the box accepted definitions

+ [RAML](https://github.com/raml-org/raml-spec/blob/master/versions/raml-10/raml-10.md) (0.8 and 1.0)
+ [OpenApi](https://github.com/OAI/OpenAPI-Specification) (Swagger 2.0)

+ [Dialect](https://aml-org.github.io/aml-spec/dialects/) (AML)
  - [AsyncAPI](https://www.asyncapi.com/docs/getting-started/) (currently 1.0)
 
### About LSP
[Microsoft's Language Server Protocol (LSP)](https://microsoft.github.io/language-server-protocol/overview), is a tooling standardization for IDEs, which defines a Client-Server architecture used to abstract a general implementation of used language-specific smarts.

The idea behind this Client-Server architecture, is that the Server works as a standalone (write once and always behave the same) functionality provider.

With this server, it's up to the client (each IDE plugin) to just understand LSP messaging and communicate, agnostic of the server implementation.

Currently we support the following capabilities:
+ [Completion](https://microsoft.github.io/language-server-protocol/specification#textDocument_completion)
+ [DocumentSymbol](https://microsoft.github.io/language-server-protocol/specification#textDocument_documentSymbol)
+ [Diagnostics](https://microsoft.github.io/language-server-protocol/specification#textDocument_publishDiagnostics)

We have other features planned:
+ Custom Actions:
  - [Rename](https://microsoft.github.io/language-server-protocol/specification#textDocument_rename)
  - [Goto Declaration](https://microsoft.github.io/language-server-protocol/specification#textDocument_declaration)
  - [Find Reference](https://microsoft.github.io/language-server-protocol/specification#textDocument_references)

![Modules](images/LSP-diagram.png)
# Setup

```
git clone https://github.com/mulesoft/als.git
```

## Mulesoft dependency references

### Repositories
```
git clone https://github.com/aml-org/amf
git clone https://github.com/aml-org/amf-aml
git clone https://github.com/aml-org/amf-core
git clone https://github.com/mulesoft/syaml
git clone https://github.com/mulesoft/scala-common
```

# Build

## Compilation

Launching compilation manually is optional, it is being automatically launched in artifact generation steps.
```
sbt compile
```

## Java generation

Generate Java artifact as `als/als-server/jvm/target/scala-2.12/als-server-assembly-{VERSION}}.jar` file

```
sbt serverJVM/assembly
```
This file is the LSP server jar.
To run this from the client application, you must create a local socket and provide the port as a parameter:

JS server development is in process of being developed, taking advantage of ScalaJS code-reusability features.

## Generate test coverage report
```
sbt clean coverage testJVM coverageReport
```

# Architecture
## ALS as a LSP Server
Api Language Server (ALS) integrates different modules with specific objectives, in one server module which orchestrates functionalities while respecting the [Language Server Protocol (LSP)](https://microsoft.github.io/language-server-protocol/overview).

The Server Module communicates through a socket towards the client.

In order to achieve this, the client must create a Server Socket, and provide the selected port at the server initialization

![Modules](images/LSP-complete-diagram.png)

Thanks to the LSP architecture, the client is completely agnostic of the Server language, which means that the `als-server.jar` can be used in a JS or Python plugin.

```
java -jar server.jar --port {XXXX}
```
During the server initialization process, it will connect to the provided Socket `(localhost, {port})` (connection which the client should accept)

Through this channel, the [LSP protocol](https://microsoft.github.io/language-server-protocol/specification) is respected. Server and client exchange information about accepted actions, and notifications/request can occur

# Features and modules

###### [Language Server Protocol](https://microsoft.github.io/language-server-protocol/) support is the main ALS goal.  Based on the context described above, a basic set of features where the initial target:
#### [Initialize](https://microsoft.github.io/language-server-protocol/specification#initialize):
It’s the first message sent from the client to the server, with this request, the Client informs the Server which Capabilities it supports. As a response, the Server returns the intersection of the Client Capabilities with it’s own (a set of Capabilities both support).
+ Flow: Client sends request to Server and the Server responds with the Capabilities to be used
+ Triggered by:
  + Initialization Request
+ Emits:
  + Initialization Result

#### [Initialized](https://microsoft.github.io/language-server-protocol/specification#initialized):
It is the final message in a three-way handshake between Client and Server (Initialize Request, Initialize Response, Initialized notification).
+ Flow: Client sends notification to the Server
+ Triggered by:
  + Initialized notification
+ Emits nothing.

#### Open File:
Used by the client to inform the content of an unregistered file to the Server.
The Server will use this information to keep track of each file and it’s contents
+ Flow: Client sends notification to Server
+ Triggered by:
  + [DidOpen](https://microsoft.github.io/language-server-protocol/specification#textDocument_didOpen) (from Client to Server)
+ Emits nothing

#### Change a File:
Used by the client to inform the content of a registered file to the Server.
The Server will use this information to update of each file content
+ Flow: Client sends notification to Server
+ Triggered by:
  + [DidChange](https://microsoft.github.io/language-server-protocol/specification#textDocument_didChange) (from Client to Server)
+ Emits nothing

#### Close a File:
Used by the client to inform the Server that a file has been closed.
The Server will use this information to unregister the file (which means it won’t keep track of the content). In case this file is referenced, the content will be read from the source as with any other file.
+ Flow: Client sends notification to Server
+ Triggered by:
  + [DidClose](https://microsoft.github.io/language-server-protocol/specification#textDocument_didClose) (from Client to Server)
+ Emits nothing

#### [Command](https://microsoft.github.io/language-server-protocol/specification#workspace_executeCommand):
Command is a LSP feature which allows the Client to ask the Server to execute a known instruction. We created a “OnFocus” command, which we use to trigger a regeneration of the Modeled document (using the current document as Root).
A command support was implemented (“OnFocus”) to trigger a validations refresh.
This will result in the Server remaking the Model, using the selected file as root.
+ Flow: Client sends request to Server
+ Trigger by:
  + Execute Command (with “On Focus” command name with the focused file URI)
+ Emits nothing

#### Diagnostics (a.k.a. Validations):
The feature allows any LS client to receive notifications about an active document. After activating this module, client should be ready to receive information.
+ Flow: Server sends notification to Client
+ Triggered by any change on the model (files or root file), can be caused by:
  + DidOpen
  + DidChange
  + Execute Command<DidFocus>
+ Emits:
  + [Publish Diagnostics](https://microsoft.github.io/language-server-protocol/specification#textDocument_publishDiagnostics)


#### [Document Symbol](https://microsoft.github.io/language-server-protocol/specification#textDocument_documentSymbol) (a.k.a. Structure/Outline) 
This feature allows the Server to inform of an abstraction of the Model to the Client. It shows a more visual representation of the structure for the API
+ Flow: Client sends a request to the Server, to which the Server responses
+ Triggered by direct request from client:
  + Document Symbol Request
+ Emits:
  + DocumentSymbol Response

#### [Completion](https://microsoft.github.io/language-server-protocol/specification#textDocument_completion) (a.k.a. Suggestions) 
Given a file and position, the Server informs the Client of possible values he might want to insert (known structure keys, values, types, parameter names, paths, etc) 
+ Flow: Client sends a request to the Server, to which the Server responses
+ Triggered by direct request from client:
  + Completion Request
+ Emits:
  + Completion Response

# Usage examples

#### [JAVA (LSP4J)](./documentation/java-client-example/client.md) sample code

#### [JS (VS Code)](./documentation/js-client-example/client.md) sample code

#### Design Center example:
![AD Example](./images/usage-example-ad.gif)

Design Center currently uses Completion as a separated module for it's platform, not going through LSP.
#### Visual Studio Code completion example (LSP):
![VS Code Example](./images/usage-example-vscode.gif)

#### Visual Studio Code document symbol example (LSP):
![VS Code Example](./images/document-symbol-vscode.gif)

****
