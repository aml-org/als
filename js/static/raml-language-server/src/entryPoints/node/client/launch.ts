import childProcess = require("child_process");

import path = require("path");

import {
    IClientConnection,
    MessageSeverity
} from "../../../client/client";

import {
    NodeProcessClientConnection
} from "./client";

let clientConnection = null;

export function getConnection(): IClientConnection {
    if (!clientConnection) {
        clientConnection = launch();
    }

    return clientConnection;
}

function launch(): IClientConnection {

    // const serverProcess = (childProcess as any).fork(
    //     path.resolve(__dirname, "../server/serverProcess.js"), ["--inspect=6010"], {
    //     silent: true
    // });

    const serverProcess = (childProcess as any).fork(
        path.resolve(__dirname, "../server/serverProcess.js"), [], {
            silent: true
        });

    const connection = new NodeProcessClientConnection(serverProcess);

    connection.setLoggerConfiguration({
        maxSeverity: MessageSeverity.ERROR,
        maxMessageLength: 50
    });

    // connection.setLoggerConfiguration({
    //     allowedComponents: [
    //         "server",
    //         "DetailsManager",
    //         "MessageDispatcher:NodeProcessClientConnection",
    //         "MessageDispatcher:NodeProcessServerConnection",
    //         "CustomActionsManager",
    //         "CompleteBodyStateCalculator",
    //         "contextActions"
    //     ],
    //     maxSeverity: MessageSeverity.DEBUG_DETAIL,
    //     maxMessageLength: 5000
    // });

    return connection;
}
