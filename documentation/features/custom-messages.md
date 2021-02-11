# Features and modules
## Custom messages
##### Currently the following custom features are supported:
- SerializeJSONLD
- UpdateConfiguration
- FilesInProject
- CleanDiagnosticTree
- FileUsage
- Conversion
- Serialization
- RenameFile


### SerializeJSONLD
After initialization, provides notifications with the JSONLD when available (updates with changes).
Notification message from server to client:
```json
{
  "uri": "string",
  "model": "any"
}
```

### UpdateConfiguration
Client notification to update the ALS-specific configuration.
Notification message from client to server:
```json
{
    "formattingOptions": {
      "string": {
        "tabSize": "number",
        "insertSpaces": "boolean"
      }
    },
    "genericOptions": {
      "string": "any"
    },              
    "templateType": "NONE|SIMPLE|FULL"
}
```
`templateType` values:
- NONE: Turns templates off
- SIMPLE: Template will only contain the first level
- FULL: Template will contain the first level and the levels after that one too, building the whole structure required for the definition.

### FilesInProject
After initialization, provides notifications about the files included in a project (updates with changes).
Notification message from server to client:
```json
{
    "uris": "string[]"
}
```


### CleanDiagnosticTree
Performs validation on a given document(without the use of cache).

Request message from client to server:
##### request
```json
{
  "textDocument": "TextDocumentIdentifier"
}
```
##### response
```json
{
  "uri": "string",
  "diagnostics": "Diagnostic[]",
  "profile": "string"
}
```


### FileUsage
For a given document, generates a list of other documents that reference it.
Request message from client to server:
##### request
```json
{
  "uri": "string"
}
```
##### response
```json
[ "Location" ]
```


### Conversion
For a given document, performs AMF conversion for compatible specifications.
Request message from client to server:
##### request
```json
{
  "uri": "string",
  "target": "string",          
  "syntax": "string?"
}
```
##### response
```json
[
  {
    "uri": "string",
    "document": "string"
  }
]
```


### Serialization
For a given document, serializes AMF towards a specific model (similar to the JSONLD notifications, but on demand).
Request message from client to server:
##### request
```json
{
  "uri": "string"
}
```
##### response
```json
{
  "uri": "string",
  "model": "any"
}
```


### RenameFile
For a given document, provides all the requirements to rename the file and it's references inside a project.
Request message from client to server:
##### request
```json
{
  "oldDocument": "TextDocumentIdentifier",
  "newDocument": "TextDocumentIdentifier"
}
```
##### response
```json
{
  "edits": "WorkspaceEdit"
}
```
