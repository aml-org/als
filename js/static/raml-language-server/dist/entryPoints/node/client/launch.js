"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var childProcess = require("child_process");
var path = require("path");
var client_1 = require("../../../client/client");
var client_2 = require("./client");
var clientConnection = null;
function getConnection() {
    if (!clientConnection) {
        clientConnection = launch();
    }
    return clientConnection;
}
exports.getConnection = getConnection;
function launch() {
    // const serverProcess = (childProcess as any).fork(
    //     path.resolve(__dirname, "../server/serverProcess.js"), ["--inspect=6010"], {
    //     silent: true
    // });
    var serverProcess = childProcess.fork(path.resolve(__dirname, "../server/serverProcess.js"), [], {
        silent: true
    });
    var connection = new client_2.NodeProcessClientConnection(serverProcess);
    connection.setLoggerConfiguration({
        maxSeverity: client_1.MessageSeverity.ERROR,
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
//# sourceMappingURL=launch.js.map