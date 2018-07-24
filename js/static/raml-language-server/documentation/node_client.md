# Node-based clients

## Installation and Usage

`npm install raml-language-server`

RAML Server is supposed to be added as an npm dependency. Its `index.ts` contains suitable methods to obtain client connection that allows to launch and communicate with the server.

## Simple client sample

```
var ramlServer = require("raml-language-server");

// Launches the server and returns client connection
var connection = ramlServer.getNodeClientConnection();

var file = "/test.raml";

var ramlFileContents = loadFile(file);

// Will be triggered when server completes next (re)validation.
// Validations should not be triggered manually, instead, server tracks document changes
// and launches validation frequently.
connection.onValidationReport(function(validationReport){

    validationReport.issues.forEach(function(issue){
        console.log(issue.text + " : " + issue.range.start + " , " + issue.range.end)
    })

})

// Letting the server know that the new document is opened.
connection.documentOpened({
    uri: file,
    text: ramlFileContents
})

// Getting server suggestions (code completion)
connection.getSuggestions(file, 102).then(function(suggestions) {

    suggestions.forEach(function(suggestion){
        console.log(suggestion.text + "(" + suggestion.category + ")")
    })

})

// Letting server know document is changed at the client side
var newFileContents = loadFile(file)
connection.documentChanged({
    uri: file,
    text: newFileContents
})

// Letting the server know the document is closed
connection.documentClosed(file)

// Stops the server
connection.stop();
```

For more methods and abilities, please read the next Client Interface section.

## Client Interface

### Managing client connection

To get client connection, the index of the module provides `getNodeClientConnection` method. `stop` method shuts the server down.

Client interface is located in `src/client/client.ts` file `IClientConnection` interface, launching interface is located in `src/index.ts` `getNodeClientConnection()` method, launching implementation is located in `src/entryPoints/node` folder


### Document management

Editor Manager module provides following methods to let server know, which documents the client operates with:

```js
/**
 * Notifies the server that document is opened.
 * @param document
 */
documentOpened(document: IOpenedDocument);
```

```js
/**
 * Notified the server that document is closed.
 * @param uri
 */
documentClosed(uri: string);
```

```js
/**
 * Notifies the server that document is changed.
 * @param document
 */
documentChanged(document: IChangedDocument);
```

The client may also notify the server regarding cursor position by calling

```js
/**
 * Reports to the server the position (cursor) change on the client.
 * @param uri - document uri.
 * @param position - cursor position, starting from 0.
 */
positionChanged(uri: string, position: number): void;
```

Cursor position is being used to calculate context-dependent reports (actions, details, etc).

### Validation

Validation is being performed automatically by the server when the client reports content changes. It is not guaranteed that each change will cause re-validation, instead server for the pause in a chain of rapid changes before launching the validation.

As soon as the new validation report is available, it is being sent to the client, which may listen to the report by calling

```js
/**
 * Adds a listener for validation report coming from the server.
 * @param listener
 */
onValidationReport(listener: (report: IValidationReport) => void);
```

### Structure

RAML file structure can be obtained by calling

```js
/**
 * Requests server for the document structure.
 * @param uri
 */
getStructure(uri: string): Promise<{[categoryName: string]: StructureNodeJSON}>;
```

Instead of asking the server for the structure repeatedly, it is more efficient to subscribe to the structure reports that are calculated on real content changes and AST re-calculations:

```js
/**
 * Instead of calling getStructure to get immediate structure report for the document,
 * this method allows to launch to the new structure reports when those are available.
 * @param listener
 */
onStructureReport(listener: (report: IStructureReport) => void);
```

### Code completion

To get current suggestions the following should be called:

```js
/**
 * Requests server for the suggestions.
 * @param uri - document uri
 * @param position - offset in the document, starting from 0
 */
getSuggestions(uri: string, position: number): Promise<Suggestion[]>;
```

### Details

To request details directly, call:

```js
/**
 * Requests server for the document+position details.
 * @param uri
 */
getDetails(uri: string, position: number): Promise<DetailsItemJSON>;
```

Instead of asking the server for the details repeatedly, it is more efficient to subscribe to the details reports that are calculated on real content changes and AST re-calculations:


```js
/**
 * Report from the server that the new details are calculated
 * for particular document and position.
 * @param listener
 */
onDetailsReport(listener: (IDetailsReport) => void);
```

### Fixed Actions

To rename an element at position:

```js
/**
 * Requests server for rename of the element
 * at the given document position.
 * @param uri - document uri
 * @param position - position in the document
 */
rename(uri: string, position: number, newName: string): Promise<IChangedDocument[]>;
```

To find the location of the declaration of an element at position:

```js
/**
 * Requests server for the positions of the declaration of the element defined
 * at the given document position.
 * @param uri - document uri
 * @param position - position in the document
 */
openDeclaration(uri: string, position: number): Promise<ILocation[]>;
```

To find reference of an element at position:

```js
/**
 * Requests server for the positions of the references of the element defined
 * at the given document position.
 * @param uri - document uri
 * @param position - position in the document
 */
findReferences(uri: string, position: number): Promise<ILocation[]>;
```

To find occurrences of the element at position:

```js
/**
 * Requests server for the occurrences of the element defined
 * at the given document position.
 * @param uri - document uri
 * @param position - position in the document
 */
markOccurrences(uri: string, position: number): Promise<IRange[]>;
```

### Custom Actions

To calculate the list of the custom actions avilable in the current context, call:

```js
/**
 * Calculates the list of executable actions avilable in the current context.
 *
 * @param uri - document uri.
 * @param position - optional position in the document.
 * If not provided, the last reported by positionChanged method will be used.
 */
calculateEditorContextActions(uri: string,
                              position?: number): Promise<IExecutableAction[]>;
```

After user makes decision, whether/which action to execute, call `executeContextAction`, providing the action obtained on the call of `calculateEditorContextActions`:

```js
/**
 * Executes the specified action. If action has UI, causes a consequent
 * server->client UI message resulting in onDisplayActionUI listener call.
 * @param uri - document uri
 * @param action - action to execute.
 * @param position - optional position in the document.
 * If not provided, the last reported by positionChanged method will be used.
 */
executeContextAction(uri: string,
                     action: IExecutableAction, position?: number): Promise<IChangedDocument[]>;
```

If custom action has UI to display on the client side, in-between the `executeContextAction` promise resolving, server will come back to the client and ask to display the UI, so the client should subscribe to:

```js
/**
 * Adds a listener to display action UI.
 * @param listener - accepts UI display request, should result in a promise
 * returning final UI state to be transferred to the server.
 */
onDisplayActionUI(
    listener: (uiDisplayRequest: IUIDisplayRequest) => Promise<any>
);
```

### Providing client file system

For the server to know not only the content of RAML files, opened in the editors, but also other fragments and libraries, the client should be ready to answer to the following server's requests:

```js
/**
 * Listens to the server requests for FS path existence, answering whether
 * a particular path exists on FS.
 */
onExists(listener: (path: string) => Promise<boolean>): void;
```

```js
/**
 * Listens to the server requests for directory contents, answering with a list
 * of files in a directory.
 */
onReadDir(listener: (path: string) => Promise<string[]>): void;
```

```js
/**
 * Listens to the server requests for directory check, answering whether
 * a particular path is a directory.
 */
onIsDirectory(listener: (path: string) => Promise<boolean>): void;
```

```js
/**
 * Listens to the server requests for file contents, answering what contents file has.
 */
onContent(listener: (path: string) => Promise<string>): void;
```
