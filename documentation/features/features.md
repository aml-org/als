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
