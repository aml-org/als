# Visual Studio Code client

## Installation and Usage

`npm install raml-language-server`

RAML Server is supposed to be added as an npm dependency of the Visual Studio Code extension.

## Simple client sample

Create new Visual Code extension via generation or download a sample.

Add vscode, language client and raml language server as a dependency to the `package.json` of the extension:
```
"dependencies": {
    "vscode": "^1.1.5",
    "vscode-languageclient": "^3.3.0",
    "raml-language-server":"*"
}
```

Add binding to activation events:

```
"activationEvents": [
    "onLanguage:plaintext"
],
```

Add language contribution:

```
  "contributes": {
    "configuration": {
      "type": "object",
      "title": "Client of the RAML server",
      "properties": {
        "languageServerExample.maxNumberOfProblems": {
          "type": "number",
          "default": 100,
          "description": "Controls the maximum number of problems produced by the server."
        },
        "languageServerExample.trace.server": {
          "type": "string",
          "enum": [
            "off",
            "messages",
            "verbose"
          ],
          "default": "off",
          "description": "Traces the communication between VSCode and the raml server service."
        }        
      }
    },
    "languages": [{
        "id": "raml",
        "aliases": ["RAML", "raml"],
        "extensions": [".raml"],
        "configuration": "./raml.configuration.json"
    }]
  }

```

`raml.configuration.json` file contains client-side language configuration:
```
{
	"comments": {
		"lineComment": "#"
	},
	"brackets": [
		["[", "]"]
	]
}
```

`extension.ts` file in it's `activate` method should point to the `server.js` file located in `dist/entryPoints/vscode` folder of `raml-language-server` module dependency:

```
// The server is implemented as node module
let serverModule = context.asAbsolutePath(path.join('node_modules', 'raml-language-server', 'dist', 'entryPoints', 'vscode', 'server.js'));

// The debug options for the server
let debugOptions = { execArgv: ["--nolazy", "--debug=6009"] };

// If the extension is launched in debug mode then the debug server options are used
// Otherwise the run options are used
let serverOptions: ServerOptions = {
    run : { module: serverModule, transport: TransportKind.ipc },
    debug: { module: serverModule, transport: TransportKind.ipc, options: debugOptions }
}

```
Binding to `raml` documents:

```
// Options to control the language client
let clientOptions: LanguageClientOptions = {
    // Register the server for plain text documents
    documentSelector: ['raml'],
    synchronize: {
        // Synchronize the setting section 'languageServerExample' to the server
        configurationSection: 'languageServerExample',
        // Notify the server about file changes to '.clientrc files contain in the workspace
        fileEvents: workspace.createFileSystemWatcher('**/.clientrc')
    }
}
```

And launching the client:

```
// Create the language client and start the client.
let disposable = new LanguageClient('languageServerExample', 'Language Server Example', serverOptions, clientOptions).start();

// Push the disposable to the context's subscriptions so that the 
// client can be deactivated on extension deactivation
context.subscriptions.push(disposable);
```

## Code highlights

Server is located in `src/entryPoints/vscode/server.ts` file.

Server-side connection implementation is located in `src/entryPoints/vscode/serverConnection.ts` file.