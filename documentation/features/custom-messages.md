# Features and modules

## Custom methods

Currently the following custom methods are supported:

- serializeJSONLD
- updateConfiguration
- filesInProject
- cleanDiagnosticTree
- fileUsage
- conversion
- serialization
- didChangeConfiguration command
- renameFile

### serializeJSONLD

After initialization, provides notifications with the JSONLD when available (updates with changes).

Notification message from server to client:

<pre>
{
  "uri": "<a href="https://microsoft.github.io/language-server-protocol/specifications/specification-3-16/#uri">DocumentUri</a>",
  "model": "string"
}
</pre>

### updateConfiguration

Client notification to update the ALS-specific configuration.

Notification message from client to server:

<pre>
{
    "formattingOptions": {
      "string": {
        "tabSize": "integer",
        "insertSpaces": "boolean"
      }
    },
    "genericOptions": {
      "string": "any"
    },              
    "templateType": "NONE|SIMPLE|FULL"
}
</pre>

`templateType` values:
- NONE: Turns templates off.
- SIMPLE: Template will only contain the first level.
- FULL: Template will contain the first level and the levels after that one too, building the whole structure required for the definition.

### filesInProject

After initialization, provides notifications about the files included in a project (updates with changes).

Notification message from server to client:

<pre>
{
    "uris": "<a href="https://microsoft.github.io/language-server-protocol/specifications/specification-3-16/#uri">DocumentUri[]</a>"
}
</pre>

### cleanDiagnosticTree

Performs validation on a given document (without the use of cache).

Request message from client to server:

#### Request

<pre>
{
  "textDocument": "<a href="https://microsoft.github.io/language-server-protocol/specifications/specification-3-16/#textDocumentIdentifier">TextDocumentIdentifier</a>"
}
</pre>

#### Response

<pre>
{
  "uri": "<a href="https://microsoft.github.io/language-server-protocol/specifications/specification-3-16/#uri">DocumentUri</a>",
  "diagnostics": "<a href="https://microsoft.github.io/language-server-protocol/specifications/specification-3-16/#diagnostic">Diagnostic[]</a>",
  "profile": "string"
}
</pre>

### fileUsage

For a given document, generates a list of other documents that reference it.

Request message from client to server:

#### Request

<pre>
{
  "uri": "<a href="https://microsoft.github.io/language-server-protocol/specifications/specification-3-16/#uri">DocumentUri</a>"
}
</pre>

#### Response

<pre>
[ "<a href="https://microsoft.github.io/language-server-protocol/specifications/specification-3-16/#location">Location</a>" ]
</pre>

### conversion

For a given document, performs AMF conversion for compatible specifications.

Request message from client to server:

#### Request

<pre>
{
  "uri": "<a href="https://microsoft.github.io/language-server-protocol/specifications/specification-3-16/#uri">DocumentUri</a>",
  "target": "string",          
  "syntax?": "string"
}
</pre>

`target` values:
- OAS 2.0
- OAS 3.0
- RAML 0.8
- RAML 1.0
- ASYNC 2.0
- AMF Graph

`syntax` values:
- json
- yaml
- raml

#### Response

<pre>
{
  "uri": "<a href="https://microsoft.github.io/language-server-protocol/specifications/specification-3-16/#uri">DocumentUri</a>",
  "model": "string"
}
</pre>

### serialization

For a given document, serializes AMF towards a specific model (similar to the JSONLD notifications, but on demand

Request message from client to server:

#### Request

<pre>
{
  "documentIdentifier": "<a href="https://microsoft.github.io/language-server-protocol/specifications/specification-3-16/#textDocumentIdentifier">TextDocumentIdentifier</a>"
}
</pre>

#### Response

<pre>
{
  "uri": "<a href="https://microsoft.github.io/language-server-protocol/specifications/specification-3-16/#uri">DocumentUri</a>",
  "model": "string"
}
</pre>
###### In JS artifact, instead of a string this will return a JS Object (for better performance)
### textDocument/didFocus

Sent from the client to server to notice a new focus for a file, which will trigger new parse and diagnostic on isolated files (similar to [`textdocument/didOpen`](https://microsoft.github.io/language-server-protocol/specifications/specification-3-17/#textDocument_didOpen)).

Notification from client to server:

<pre>
{
  "uri": "<a href="https://microsoft.github.io/language-server-protocol/specifications/specification-3-16/#uri">DocumentUri</a>",
  "version": "integer"
}
</pre>


### getWorkspaceConfiguration

Request from client to server to get the Workspace Configuration applied for a specific file.

Please bear in mind that the Workspace Configuration is not the same as the global Als Configuration.

#### Request

<pre>
{
  "textDocument": "<a href="https://microsoft.github.io/language-server-protocol/specifications/specification-3-16/#textDocumentIdentifier">TextDocumentIdentifier</a>" 
}
</pre>

#### Response

<pre>
{
  "workspace": string,
  "configuration": {
    "mainPath": string,
    "folder"?: string,
    "dependencies": string[],
    "customValidationProfiles": string[],
    "semanticExtensions": string[]
  }
}
</pre>

Where:
- `workspace` is the root folder of the workspace containing the file
- `mainPath` is the main file uri of said `workspace`
- `folder` same as `workspace` [optional]
- `dependencies` uris of all immutable dependencies of the project
- `customValidationProfiles` uris of all the active validation profiles on the workspace
- `semanticExtensions` uris of all the active semantic extensions on the workspace  (Not yet implemented)


### didChangeConfiguration

Request message from client to server's [`workspace/executeCommand`](https://microsoft.github.io/language-server-protocol/specifications/specification-current/#workspace_executeCommand), to modify a workspace configuration.

#### Request

<pre>
{
  "command": "didChangeConfiguration",
  "arguments": [
    {
      "mainPath"?: string,
      "folder": string,
      "dependencies": [
        {
          "uri": string,
          "scope"?: string 
        }
      ]
    }
  ]
}
</pre>

Where:
- `folder` uri pointing to the workspace for which this configuration change will be applied
- `mainPath` [optional] sets the main file path of the workspace.
- `dependencies` the project's dependencies.
  - `file` the dependency URI
  - `scope` [optional] The scope given to the dependency, can have the following values:
    - `"custom-validation"`: Custom Validation Profile
    - `"semantic-extension"`: AML semantic extensions
    - `"dialect"`: AML dialects
    - `"dependency"`: cacheable dependency

#### Response

<pre>
{}
</pre>

## Deprecated

> **WARNING:** The use of these custom methods is discouraged and they will be removed in future release.

### renameFile

Deprecated in favor of `willRename`, which is set to be implemented in the short run.

For a given document, provides all the requirements to rename the file and it's references inside a project.

Request message from client to server:

#### Request

<pre>
{
  "oldDocument": "<a href="https://microsoft.github.io/language-server-protocol/specifications/specification-3-16/#textDocumentIdentifier">TextDocumentIdentifier</a>",
  "newDocument": "<a href="https://microsoft.github.io/language-server-protocol/specifications/specification-3-16/#textDocumentIdentifier">TextDocumentIdentifier</a>"
}
</pre>

#### Response

<pre>
{
  "edits": "<a href="https://microsoft.github.io/language-server-protocol/specifications/specification-3-16/#workspaceEdit">WorkspaceEdit</a>"
}
</pre>

### didFocusChange

Deprecated in favor of the [`textDocument/didFocus`](#textdocumentdidfocus) notification.

Request message from client to server's [`workspace/executeCommand`](https://microsoft.github.io/language-server-protocol/specifications/specification-current/#workspace_executeCommand):

#### Request

<pre>
{
  "command": "didFocusChange",
  "arguments": [
    {
      "uri": "<a href="https://microsoft.github.io/language-server-protocol/specifications/specification-3-16/#uri">DocumentUri</a>",
      "version": "integer"
    }
  ]
}
</pre>

#### Response

<pre>
{}
</pre>
