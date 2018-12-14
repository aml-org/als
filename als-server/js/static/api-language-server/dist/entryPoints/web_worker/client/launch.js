"use strict";
// import {
//     IClientConnection,
//     MessageSeverity
// } from '../../../client/client'
//
// import {
//     RAMLClientConnection
// } from './client'
//
// var clientConnection = null;
//
// export function getConnection() : IClientConnection {
//     if (!clientConnection) clientConnection = launch();
//
//     return clientConnection;
// }
//
// export function launch(workerFilePath = "./worker.bundle.js") : IClientConnection {
//
//     let worker = new Worker(workerFilePath);
//
//     clientConnection = new RAMLClientConnection(worker);
//
//     clientConnection.setLoggerConfiguration({
//         // allowedComponents: [
//         //     "CompletionManagerModule"
//         // ],
//         maxSeverity: MessageSeverity.ERROR,
//         maxMessageLength: 50
//     });
//
//     return clientConnection;
// }
Object.defineProperty(exports, "__esModule", { value: true });
var client_1 = require("./client");
var typeInterfaces_1 = require("../../../common/typeInterfaces");
exports.StructureCategories = typeInterfaces_1.StructureCategories;
var reconciler_1 = require("../../../common/reconciler");
exports.Reconciler = reconciler_1.Reconciler;
var clientConnection = null;
function getConnection() {
    if (!clientConnection) {
        clientConnection = launch();
    }
    return clientConnection;
}
exports.getConnection = getConnection;
exports.textEditProcessor = require("../../../common/textEditProcessor");
function loadExternalJSAsWorker(url) {
    var ajax = new XMLHttpRequest();
    ajax.open("GET", url, false); // <-- the 'false' makes it synchronous
    var worker = null;
    ajax.onreadystatechange = function () {
        var script = ajax.response || ajax.responseText;
        if (ajax.readyState === 4) {
            switch (ajax.status) {
                case 200:
                    worker = new Worker(URL.createObjectURL(new Blob([script.toString()], { type: "text/javascript" })));
                default:
                    console.log("ERROR: script not loaded: ", url);
            }
        }
    };
    ajax.send(null);
    return worker;
}
function launch(workerFilePath) {
    if (workerFilePath === void 0) { workerFilePath = "./worker.bundle.js"; }
    var worker = loadExternalJSAsWorker(workerFilePath);
    // let worker = new Worker(workerFilePath);
    clientConnection = new client_1.RAMLClientConnection(worker);
    // clientConnection.setLoggerConfiguration({
    //     // allowedComponents: [
    //     //     "CompletionManagerModule"
    //     // ],
    //     maxSeverity: MessageSeverity.ERROR,
    //     maxMessageLength: 50
    // });
    return clientConnection;
}
exports.launch = launch;
//# sourceMappingURL=launch.js.map