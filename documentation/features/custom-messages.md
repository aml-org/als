# Features and modules
## Custom messages
##### We currently support the following custom features:
- SerializeJSONLD
- UpdateConfiguration
- FilesInProject
- CleanDiagnosticTree
- FileUsage
- Conversion
- Serialization
- RenameFile


### SerializeJSONLD
When initialized, provides notifications with the JSONLD when available (updates with changes)
Notification message from server to client:
```json
{
  "uri": "string",
  "model": "any"
}
```

### UpdateConfiguration
Client notification to update ALS specific configuration
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
    "disableTemplates": "boolean"
}
```


### FilesInProject
When initialized, provides notifications with the files included in a project (updates with changes)
Notification message from server to client:
```json
{
    "uris": "string[]"
}
```


### CleanDiagnosticTree
This requests on a given document, will result in a clean validation (without the use of cache).
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
This requests on a given document, will result in a list of every other document which references this one.
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
This requests on a given document, will result in an AMF conversion between compatible specifications.
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
This requests on a given document, will result in an AMF serialization towards a specific model (similar to the JSONLD notifications, but on demand).
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
This requests on a given document, provide all needed changes to rename the file, and it's references inside a project.
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
