# Developing the server side

## Installation of development environment

`git clone https://github.com/raml-org/raml-language-server.git --branch "develop"`

`cd raml-language-server`

`npm install`

`sudo npm run devInstall`

`sudo npm run buildall`

Using `sudo` is optional, but, depending on current user access rights it may be required.

To test:

`npm run test`

## Folder structure and modules

`src/server/core` contains the core part of the server, including the definition of server connection, which is located in `src/server/core/connections.ts`

`src/server/modules` contains server modules.

Modules communicate to the server using server connection connection. Calls to (and from) the server connection are the being transferred to the appropriate clients with accordance to client interfaces. 
There are a number of server modules, each providing a feature and, supposedly, binding on one or more of client connection methods.

The current list of modules, which is going to expand:
* Editor Manager - handles RAML documents, their contents, conversion of absolute positions to lines and columns etc.
* AST Manager - provides AST data to other modules, both on-demand and notification-based.
* Validation Manager - handles RAML validation reports.
* Structure Manager - handles RAML structure requests.
* Completion Manager - handles RAML suggestions.
* Details Manager - handles details of RAML element.
* Custom Actions manager - handles custom context-dependent actions.
* Fixed Actions Manager - central point to register fixed actions sub-modules.
    * Find References Action - provides the respective fixed action.
    * Open Declaration Action - provides the respective fixed action.
    * Mark Occurrences Action - provides the respective fixed action.
    * Rename Action - provides the respective fixed action.

## Server Interface

### Modules registry

`src/server/core/server.ts` is the main registry for server modules.

Each module recieves server connection and a link to any other needed modules and can then subscribe to the events and call connection methods directly to communicate to the clients.

### Editor Manager module

Editor Manager module uses the following connection methods to track, which documents the client operates with:

```js
    /**
     * Adds a listener to document open notification. Must notify listeners in order of registration.
     * @param listener
     */
    onOpenDocument(listener: (document: IOpenedDocument) => void);
```

```js
    /**
     * Adds a listener to document change notification. Must notify listeners in order of registration.
     * @param listener
     */
    onChangeDocument(listener: (document: IChangedDocument) => void);
```

```js
    /**
     * Adds a listener to document close notification. Must notify listeners in order of registration.
     * @param listener
     */
    onCloseDocument(listener: (uri: string) => void);
```

The client may also notify the server regarding cursor position:

```js
    /**
     * Adds a listener to document cursor position change notification.
     * Must notify listeners in order of registration.
     * @param listener
     */
    onChangePosition(listener: (uri: string, position: number) => void);
```

Cursor position is being used to calculate context-dependent reports (actions, details, etc).

### Validation Manager module

Validation is being performed automatically by the server when the client reports content changes. It is not guaranteed that each change will cause re-validation, instead server for the pause in a chain of rapid changes before launching the validation.

As soon as the new validation report is available, it is being sent to the client by calling:

```js
    /**
     * Reports latest validation results
     * @param report
     */
    validated(report: IValidationReport): void;
```

### Structure Manager module

RAML file structure report is sent to the client by calling:

```js
    /**
     * Reports new calculated structure when available.
     * @param report - structure report.
     */
    structureAvailable(report: IStructureReport);
```

The module also listens to the client requests for structure by calling: 

```js
    /**
     * Adds a listener to document structure request. Must notify listeners in order of registration.
     * @param listener
     */
    onDocumentStructure(listener: (uri: string) => Promise<{[categoryName: string]: StructureNodeJSON}>);
```

### Code Completion Manager module

The module listens to client completion requests by calling:

```js
    /**
     * Adds a listener to document completion request. Must notify listeners in order of registration.
     * @param listener
     */
    onDocumentCompletion(listener: (uri: string, position: number) => Promise<Suggestion[]>);
```

### Details manager module

The module sends details reports to the client by calling:

```js
    /**
     * Reports new calculated details when available.
     * @param report - details report.
     */
    detailsAvailable(report: IDetailsReport);
```

The module also listens to the client requests for details by calling: 


```js
    /**
     * Adds a listener to document details request. Must notify listeners in order of registration.
     * @param listener
     */
    onDocumentDetails(listener: (uri: string, position: number) => Promise<DetailsItemJSON>);
```

### Fixed Actions modules

Renaming module binds to client requests by calling:

```js
    /**
     * Finds the set of document (and non-document files) edits to perform the requested rename.
     * @param listener
     */
    onRename(listener: (uri: string, position: number, newName: string) => IChangedDocument[]);
```

Open declaration module binds to client requests by calling:

```js
    /**
     * Adds a listener to document open declaration request.  Must notify listeners in order of registration.
     * @param listener
     */
    onOpenDeclaration(listener: (uri: string, position: number) => ILocation[]);
```

Find references module binds to client requests by calling:

```js
    /**
     * Adds a listener to document find references request.  Must notify listeners in order of registration.
     * @param listener
     */
    onFindReferences(listener: (uri: string, position: number) => ILocation[]);
```

Mark occurrences module binds to client requests by calling:

```js
    /**
     * Marks occurrences of a symbol under the cursor in the current document.
     * @param listener
     */
    onMarkOccurrences(listener: (uri: string, position: number) => IRange[]);
```

### Custom Actions Manager module

To calculate the list of the custom actions available in the current context, the server listens to:

```js
    /**
     * Calculates the list of executable actions available in the current context.
     *
     * @param uri - document uri.
     * @param position - optional position in the document.
     * If not provided, the last reported by positionChanged method will be used.
     * @param target - option target argument.
     *
     * "TARGET_RAML_EDITOR_NODE" and "TARGET_RAML_TREE_VIEWER_NODE" are potential values
     * for actions based on the editor state and tree viewer state.
     * "TARGET_RAML_EDITOR_NODE" is default.
     */
    onCalculateEditorContextActions(listener: (uri: string,
                                               position?: number) => Promise<IExecutableAction[]>): void;
```

After user makes decision, whether/which action to execute, server recieves execution request:

```js

    /**
     * Adds a listener for specific action execution.
     * If action has UI, causes a consequent displayActionUI call.
     * @param uri - document uri
     * @param action - action to execute.
     * @param position - optional position in the document.
     * If not provided, the last reported by positionChanged method will be used.
     */
    onExecuteContextAction(listener: (uri: string, actionId: string,
                                      position?: number) => Promise<IChangedDocument[]>): void;
```

If custom action has UI to display on the client side, in-between the `executeContextAction` promise resolving, server should come back to the client and ask to display the UI by calling:

```js
    /**
     * Adds a listener to display action UI.
     * @param uiDisplayRequest - display request
     * @return final UI state.
     */
    displayActionUI(uiDisplayRequest: IUIDisplayRequest): Promise<any>;
```

### Asking the client for file system data

For the server to know not only the content of RAML files, opened in the editors, but also other fragments and libraries, some of the server modules use the following requests being sent to the client:

```js
    /**
     * Returns whether path/url exists.
     * @param fullPath
     */
    exists(path: string): Promise<boolean>;
```

```js
    /**
     * Returns directory content list.
     * @param fullPath
     */
    readDir(path: string): Promise<string[]>;
```

```js
    /**
     * Returns whether path/url represents a directory
     * @param path
     */
    isDirectory(path: string): Promise<boolean>;
```

```js
    /**
     * File contents by full path/url.
     * @param fullPath
     */
    content(fullPath: string): Promise<string>;
```
