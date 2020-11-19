```typescript
/* --------------------------------------------------------------------------------------------
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 * ------------------------------------------------------------------------------------------ */

import * as net from 'net';
import * as child_process from "child_process";
import { workspace, ExtensionContext } from 'vscode';

import {
    LanguageClient,
    StreamInfo,
    LanguageClientOptions
} from 'vscode-languageclient';

let client: LanguageClient;

export function activate(context: ExtensionContext) {
    const documentSelector = [
        { language: 'raml' },
        { language: 'oas-yaml' },
        { language: 'oas-json' }
    ]
    // Options to control the language client
    let clientOptions: LanguageClientOptions = {
        // Register the server for plain text documents
        documentSelector: documentSelector,
        synchronize: {
            configurationSection: 'amlLanguageServer',
            // Notify the server about file changes to '.clientrc files contained in the workspace
            fileEvents: workspace.createFileSystemWatcher('**/.clientrc')
        }
    };

    // Create the language client and start the client.
    client = new LanguageClient(
        'amlLanguageServer',
        'AML Language Server',
        createServer,
        clientOptions
    );

    // Start the client. This will also launch the server
    client.start();

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
                const extensionPath = context.extensionPath
                const storagePath = context.storagePath || context.globalStoragePath
                const jarPath = '/path/to/als-server.jar' // TODO: Change me!


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
                console.log("[ALS] Spawning at port: " + port);
                const process = child_process.spawn("java", args, options)

            });
        });
    };
}

export function deactivate(): Thenable<void> | undefined {
    if (!client) {
        return undefined;
    }
    return client.stop();
}
```