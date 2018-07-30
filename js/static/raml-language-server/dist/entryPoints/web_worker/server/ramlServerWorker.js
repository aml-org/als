"use strict";
var __extends = (this && this.__extends) || (function () {
    var extendStatics = Object.setPrototypeOf ||
        ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
        function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
Object.defineProperty(exports, "__esModule", { value: true });
var actions = require("raml-actions");
actions.setIsPackaged();
var abstractServer_1 = require("../../common/server/abstractServer");
var server_1 = require("../../../server/core/server");
var WebWorkerServerConnection = /** @class */ (function (_super) {
    __extends(WebWorkerServerConnection, _super);
    function WebWorkerServerConnection() {
        return _super.call(this, "WebServerConnection") || this;
    }
    WebWorkerServerConnection.prototype.sendMessage = function (message) {
        postMessage(message);
    };
    return WebWorkerServerConnection;
}(abstractServer_1.AbstractMSServerConnection));
var connection = new WebWorkerServerConnection();
var server = new server_1.Server(connection);
server.listen();
self.addEventListener("message", function (e) {
    connection.handleRecievedMessage(e.data);
}, false);
//# sourceMappingURL=ramlServerWorker.js.map