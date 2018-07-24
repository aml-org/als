/* --------------------------------------------------------------------------------------------
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 * ------------------------------------------------------------------------------------------ */
"use strict";

import {
    CompletionItem, CompletionItemKind,
    createConnection, Diagnostic, DiagnosticSeverity,
    IConnection, InitializeParams, InitializeResult, IPCMessageReader,
    IPCMessageWriter, TextDocument, TextDocumentPositionParams,
    TextDocuments, TextDocumentSyncKind
} from "vscode-languageserver";

import {
    ProxyServerConnection
} from "./serverConnection";

import {
    Server
} from "../../server/core/server";

// Create a connection for the server. The connection uses Node's IPC as a transport
const connection: IConnection = createConnection(new IPCMessageReader(process), new IPCMessageWriter(process));

// After the server has started the client sends an initialize request. The server receives
// in the passed params the rootPath of the workspace plus the client capabilities.
let workspaceRoot: string;
connection.onInitialize((params): InitializeResult => {
    workspaceRoot = params.rootPath;
    return {
        capabilities: {
            // Tell the client that the server works in FULL text document sync mode
            textDocumentSync: TextDocumentSyncKind.Full,
            documentSymbolProvider: true,
            // Tell the client that the server support code complete
            completionProvider: {
                resolveProvider: false,
                triggerCharacters: [
                    "a", "b", "c", "d", "e", "f", "g",
                    "h", "i", "j", "k", "l", "m", "n", "o", "p",
                    "q", "r", "s", "t", "u", "v", "w", "x", "y", "z",
                    "A", "B", "C", "D", "E", "F", "G",
                    "H", "I", "J", "K", "L", "M", "N", "O", "P",
                    "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z",
                    "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"
                ]
            },
            definitionProvider : true,
            referencesProvider: true,
            documentHighlightProvider: true,
            renameProvider: true
        }
    };
});

const proxyConnection = new ProxyServerConnection(connection);

const server = new Server(proxyConnection);

server.listen();
proxyConnection.listen();
connection.listen();
