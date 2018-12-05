# Setup

```
git clone https://github.com/mulesoft/als.git
```

## Dependencies setup

This step is optional if ALS dependencies are being grabbed from nexus and required if building dependencies from sources.

Currently als-outline is not published in nexus due to travis issues, so building from sources is the only option.

Checking out and publishing als-hl locally:
```
git clone https://github.com/mulesoft/als-hl.git
cd als-hl
sbt publish-local
cd ..
```

Checking out and publishing als-suggestions locally:
```
git clone https://github.com/mulesoft/als-suggestions.git
cd als-suggestions
sbt publish-local
cd ..
```

Checking out and publishing als-outline locally:
```
git clone https://github.com/mulesoft/als-outline.git
cd als-outline
sbt publish-local
cd ..
```

Note: this instruction assumes AMF being available from nexus.

# Build

## Compilation

Launching compilation manually is optional, it is being automatically launched in artifact generation steps. 
```
sbt compile
```

## JavaScript generation

Generate JS artifact as `js/target/artifact/serverProcess.js` file

```
sbt buildJS
```

This file is being used in Atom client as external server NPM process.
## Java generation

Generate Java artifact as `jvm/target/scala-2.12/server.jar` file

```
sbt coreJVM/assembly
```
This file is being used in Eclipse client in a separate server thread.

## Generate test coverage report
```
sbt clean coverage coreJVM/test coverageReport
```

# Architecture

API Language Server joins all the IDE services and provides them as an interface with maximum simplicity. No RAML/OAS parser AST goes out from the LSP server. The LSP server has the full control of when and how code is parsed. The clients should not care about these details.

The supported clients are divided into 3 types, by the type of the way the client launches the server and the environment, the server is executed in. For each type of the launch/environment we add its own supporting code, which should only care about the details of launch/transport. No business logics of how to handle RAML is located in there.

**Node-based**

This type of launch expects the client and the server to be running in node.js. An example is the API Worbench.

**Web worker-based**

This type of launch expects the client to be running in the browser, and the server to be running in web worker. An example is the Monaco-based editor.

**MS LSP**

This type of launch expects the client to be running in the unknown environment, which supports MS LSP, and the server to be running in node.js. This allows to potentially support lots of current IDEs. An example is the VS Code plug-in. Note: each additional LSP client requires its own code, but that code is thin.

![Modules](images/Modules.png)

No client module directly depends on the service modules or parser modules. The only point of connection for the clients is the server itself.

Server module contains the following major parts:

* Server connection and server modules - this part is pure business logics, which either contains a direct implementation of RAML/OAS-related functionality, or a communicator to the related RAML/OAS service module.
* An implementation of three types of launching, for each type of the client this server supports. Clients should not implement their own code of launching external node processes etc, it should be ease to launch the server.
* An implementation of the protocol for the client to communicate to the server. Each protocol implementation contains two parts: client interface and a mechanism to transfer client interface calls/messages to the server and backwards. In case of MS LSP client interface is not needed, at least initially as we suppose that until we exceed current LSP support, clients already support protocol features that we support at the server part.

## Features and modules

Most of the features available in the [Language Server Protocol](https://github.com/Microsoft/language-server-protocol) and [VSCode Extensions](https://code.visualstudio.com/docs/extensions/overview) have already been developed and battle tested in the [API Workbench](http://apiworkbench.com/) Atom package.

We are currently working on extracting these features as stand-alone components that can be used to implement the LSP server.

There are a number of server modules, each providing a feature and, supposedly, binding on one or more of client connection methods.

The current list of modules, which is going to expand:
* Editor Manager - handles RAML/OAS documents, their contents, conversion of absolute positions to lines and columns etc.
* AST Manager - provides AST data to other modules, both on-demand and notification-based.
* Validation Manager - handles RAML/OAS validation reports.
* Structure Manager - handles RAML/OAS structure requests.
* Completion Manager - handles RAML/OAS suggestions.
* Details Manager - handles details of RAML/OAS element.
* Custom Actions manager - handles custom context-dependent actions.
* Fixed Actions Manager - central point to register fixed actions sub-modules.
    * Find References Action - provides the respective fixed action.
    * Open Declaration Action - provides the respective fixed action.
    * Mark Occurrences Action - provides the respective fixed action.
    * Rename Action - provides the respective fixed action.

Modules are located in `src/server/modules` folder and its subfolders.

## Code highlights

### Node-based client

More details of how to use the client are [here](./documentation/node_client.md)

An interface for this client is custom and simple. It contains a single method per major functionality feature.

In example, client can notify the server that a document was opened by calling a method:
```js
/**
 * Notifies the server that document is opened.
 * @param document
 */
documentOpened(document: IOpenedDocument);
```
Where `IOpenedDocument` has only two fields: document URI and document text.

And get notified about new validation reports from the server by adding a listener:

```js
/**
 * Adds a listener for validation report coming from the server.
 * @param listener
 */
onValidationReport(listener : (report:IValidationReport)=>void);
```

Or finding references by calling:

```js
/**
 * Requests server for the positions of the references of the element defined
 * at the given document position.
 * @param uri - document uri
 * @param position - position in the document
 */
findReferences(uri: string, position: number) : Promise<ILocation[]>
```

It is possible that further along the road some data interfaces will change by receiving new fields, but the simplicity should be preserved.

Note the an emitter of an event can be both client and server. In example, client does not ask server for validation report, instead server notifies client that the new report is ready when the server has time to parse RAML/OAS and collection validation data. Server decides when and how to parse RAML/OAS and update IDE-related data, client can either subscribe to events, or ask for immediate/fast (but potentially outdated) results stored at the server.

Server implements node-based launching, a transport that transfers client/server calls via node messages and provides a single simple method, which launches the server and returns an instance of client connection.

### Web worker-based client

More details of how to use the client are [here](./documentation/web_client.md)

This type of client uses the same client interface as node-based client for unification.

Launching should handle web-worker related functionality and contain a simple method to launch the worker and return client connection. All transport should be handled by this type of launching and hidden from the client.

This is also the place where the “universal” server data like structure is converted to this particular client’s terms like outline if needed.

### MS LSP client

More details of how to use the client are [here](./documentation/mslsp_client.md)

This type of client has no client interface because this is something handled by the standard LSP clients, at least until we decide to extend what MS LSP currently provides.

Launching is represented by the proper LSP config, it is supposed that the client simply adds raml-language-client to dependencies list and refers it as a server module. For non-node clients it can be harder.

Communication is handled as server part by converting MS LSP server calls/data to/from server interface calls/data. This is also the place where the “universal” server data like structure is converted to this particular client’s terms like symbols if needed.

### Java MS LSP Eclipse Client

Based on org.eclipse.lsp4e library. Scala server is compiled to Java. 

The implementation is a shim between the server and the library.

In addition, outline part of the plugin is enhanced.
 
### Other Java-based clients

We have test Java-based connection implement, which can be used as a base for the other Java-based clients.

### Integration with AMF

Current version of ALS is developed in Scala to support both JavaScript and Java clients.

ALS is based on AMF as a parser. Modules consume native AMF model.

On top of native AMF model we build high-level AST layer.

The high-level AST is basically an abstract representing an arbitrary language tree with no fixed set of node types. Instead, each node can be dynamically checked for the type that describes what properties this node has, which sub-nodes it may have etc. This makes such a tree to be able to represent not only RAML, but generally, many languages, including OAS, and, potentially, dialects.

Information regarding the language structure that we call Definition System can be loaded from different sources. Current source is the static data built for each supported language.

This AST structure makes it easy to support structural code completion for arbitrary languages, included the self-changing ones (RAML facets).

In order to speed up the migration of those JavaScript ALS version modules, we created the analogue of the current RAML JS Parser high-level AST and partially reused JS raml-typesystem and raml-definition-system modules by porting them to Scala. raml-suggestions, raml-outline and raml-actions modules were also ported to be based on the new high-level AST.


### Server interface

More details of how add to develop the server side are [here](./documentation/server.md)

Server interface is represented by the server connection and is something server business logics communicates to in order to provide its functionality to the clients. It resembles the client one for node-based clients:

Get knowing about document being opened:

```js
/**
 * Adds a listener to document open notification. Must notify listeners in order of registration.
 * @param listener
 */
onOpenDocument(listener: (document: IOpenedDocument)=>void);
```

Notifying the client about new validation report:

```js
/**
 * Reports latest validation results
 * @param report
 */
validated(report:IValidationReport) : void;
```

Finding the references by the client request and letting the client know the results:

```js
/**
 * Adds a listener to document find references request.  Must notify listeners in order of registration.
 * @param listener
 */
onFindReferences(listener: (uri: string, position: number) => ILocation[])
```

In the current implementation prototype server interface is located in `src/server/core/connections.ts` file `IServerConnection` interface, implementation is located in `src/server/core` folder.
 
### Sub-modules
[High-level AST sub-module](./documentation/hl.md)
[Suggestions sub-module](./documentation/suggestions.md)
[Structure sub-module](./documentation/outline.md)