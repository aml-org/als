## JS (VS Code)
###### Extension.ts
The following code is adapted from [LSP client sample](https://github.com/microsoft/vscode-extension-samples/blob/master/lsp-sample/client/src/extension.ts)

It uses the previously generated jar `sbt serverJVM/assembly`
```typescript
'use strict'


import * as net from 'net'
import * as child_process from "child_process"

import { window, workspace, ExtensionContext } from 'vscode'
import { LanguageClient, LanguageClientOptions, StreamInfo } from 'vscode-languageclient'

var upath = require("upath")

export function activate(context: ExtensionContext) {

	function createServer(): Promise<StreamInfo> {
		return new Promise((resolve, reject) => {
			const server = net.createServer(socket => {
				console.log("[ALS] Socket created")

				resolve({
					reader: socket,
					writer: socket,
				});

				socket.on('end', () => console.log("[ALS] Disconnected"))
			}).on('error', (err) => { throw err })
			server.listen(() => {
				const jarPath = `als-server.jar`
				const address = server.address()
				const port = typeof address === 'object' ? address.port : 0

				const options = { 
					cwd: workspace.rootPath,
				}

				const args = [
					'-jar',
					jarPath,
					'--port',
					port.toString()
				]

				const process = child_process.spawn('java', args, options)
			});
		});
	};

	const clientOptions: LanguageClientOptions = {
		documentSelector: [
			{ language: 'raml' },
			{ language: 'oas-yaml' },
			{ language: 'oas-json' },
			{ language: 'async-api' }
		],
		synchronize: {
			configurationSection: 'amlLanguageServer',
			fileEvents: workspace.createFileSystemWatcher('**/.clientrc')
		}
	}

	const languageClient = new LanguageClient(
		'amlLanguageServer', 
		'AML Language Server', 
		createServer, 
		clientOptions)

	const disposable = languageClient.start()


       // Specific ALS command
	window.onDidChangeActiveTextEditor(() => {
		if (window.activeTextEditor) {
			languageClient.sendRequest("workspace/executeCommand", {
				"command": "didFocusChange",
				"arguments": {
					"uri":window.activeTextEditor.document.uri.toString(),
					"version": window.activeTextEditor.document.version
				}
			})
		}
	})
	
	context.subscriptions.push(disposable)
}
```