# Features and modules

###### The main goal of the ALS is to provide [Language Server Protocol](https://microsoft.github.io/language-server-protocol/) support.  Based on the context described in the previous section, a basic set of features where the initial target://this sentence is incomplete
#### [Initialize](https://microsoft.github.io/language-server-protocol/specification#initialize):
The first message sent from the client to the server. With this request, the client informs the server about the client-supported capabilities. As a response, the server returns the commomn capabilities that are supported both by the client and the sevrer.
+ Flow: Client sends requests to the server and the server responds with the capabilities to be used.
+ Triggered by:
  + Initialization Request
+ Emits:
  + Initialization Result
  
##### ALS Initialize Params:
ALS Initialize Params extends LSP's [InitializeParams](https://microsoft.github.io/language-server-protocol/specifications/specification-3-16/#initializeParams)
<pre>
{
  /** Start of LSP default params**/
  processId: integer | null;
  clientInfo?: {
    name: string;
    version?: string;
  };
  locale?: string;
  rootPath?: string | null;
  rootUri: DocumentUri | null;
  initializationOptions?: any;
  capabilities: ClientCapabilities;
  trace?: TraceValue;
  workspaceFolders?: WorkspaceFolder[] | null;
  /** End of LSP default params **/

  /** ALS custom params **/

  /**
  * Project configuration style:
  * Defines how the workspaces containing a project should be configured.
  * "command": It will only accept configuration by `didChangeConfiguration` command.
  * "file": It will try and read configuration from `exchange.json` file. It will ignore `didChangeConfiguration` command.
  *
  * Defaults to "file" configuration style.
  */
  projectConfigurationStyle: {
    style?: "command" | "file";
  }
}
</pre>

#### [Initialized](https://microsoft.github.io/language-server-protocol/specification#initialized):
The final message in a three-way handshake between the client and the server (Initialize Request, Initialize Response, Initialized Notification).
+ Flow: The client sends notification to the server.
+ Triggered by:
  + Initialized notification
+ Emits nothing.

#### Open File:
The message used by the client to inform the server about the contents of an unregistered file.
The server uses this information to keep track of each file and its contents.
+ Flow: The client sends notification to the server.
+ Triggered by:
  + [Did Open](https://microsoft.github.io/language-server-protocol/specification#textDocument_didOpen) (from Client to Server)
+ Emits nothing

#### Change a File:
The message used by the client to inform the server about the contents of a registered file.
The server uses this information to update the contents of each file.
+ Flow: The client sends notification to the server.
+ Triggered by:
  + [Did Change](https://microsoft.github.io/language-server-protocol/specification#textDocument_didChange) (from Client to Server)
+ Emits nothing

#### Close a File:
The message used by the client to inform the server that a file that has been closed.
The Server uses this information to unregister the file (content no longer tracked). In case this file is referenced, the content is read from the source, as in the case of any other file.
+ Flow: The client sends notification to the server.
+ Triggered by:
  + [Did Close](https://microsoft.github.io/language-server-protocol/specification#textDocument_didClose) (from Client to Server)
+ Emits nothing

#### [Command](https://microsoft.github.io/language-server-protocol/specification#workspace_executeCommand):
Command is an LSP feature that allows the client to request the server to execute a known instruction. The “OnFocus” command is used to trigger a regeneration of the modeled document (using the current document as root).
A command support (“OnFocus”) is implemented to trigger validation refresh.
The server then recreates the model, using the selected file as root.
+ Flow: The client sends request to the server.
+ Trigger by:
  + Execute Command (“On Focus” command name with the focused file URI)
+ Emits nothing

#### Diagnostics (a.k.a. Validations):
The feature allows any LS client to receive notifications about an active document. After activating this module, the client must be ready to receive the information.
+ Flow: The server sends notification to the client.
+ Any changes triggered on the model (files or root file), can be caused by:
  + DidOpen
  + DidChange
  + DidFocus
  + [DEPRECATED] Execute Command<DidFocus>
+ Emits:
  + [Publish Diagnostics](https://microsoft.github.io/language-server-protocol/specification#textDocument_publishDiagnostics)


#### [Document Symbol](https://microsoft.github.io/language-server-protocol/specification#textDocument_documentSymbol) (a.k.a. Structure/Outline) 
This feature allows the server to inform the client about an abstraction of the model. The 'Document Symbol' shows a visual representation of the structure for the API.
+ Flow: The client sends a request to the server and the server responds to the request.
+ Triggered by direct request from client:
  + Document Symbol Request
+ Emits:
  + DocumentSymbol Response

#### [Completion](https://microsoft.github.io/language-server-protocol/specification#textDocument_completion) (a.k.a. Suggestions) 
With the file and position specified, the server informs the client about possible values to insert (known structure keys, values, types, parameter names, paths, etc) 
+ Flow: The client sends a request to the server, and the server responds to the request.
+ Triggered by direct request from client:
  + Completion Request
+ Emits:
  + Completion Response
