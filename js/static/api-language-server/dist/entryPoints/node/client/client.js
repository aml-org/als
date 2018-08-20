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
var abstractClient_1 = require("../../common/client/abstractClient");
var NodeProcessClientConnection = (function (_super) {
    __extends(NodeProcessClientConnection, _super);
    function NodeProcessClientConnection(serverProcess) {
        var _this = _super.call(this, "NodeProcessClientConnection") || this;
        _this.serverProcess = serverProcess;
        serverProcess.on("message", function (serverMessage) {
            _this.handleRecievedMessage(serverMessage);
        });
        var logger = _this;
        serverProcess.stdout.on("data", function (data) {
            console.log(data.toString());
        });
        serverProcess.stderr.on("data", function (data) {
            console.log(data.toString());
        });
        serverProcess.on("close", function (code) {
            console.log("Validation process exited with code " + code, "NodeProcessClientConnection");
        });
        return _this;
    }
    NodeProcessClientConnection.prototype.stop = function () {
        this.serverProcess.kill();
    };
    NodeProcessClientConnection.prototype.sendMessage = function (message) {
        this.serverProcess.send(message);
    };
    return NodeProcessClientConnection;
}(abstractClient_1.AbstractClientConnection));
exports.NodeProcessClientConnection = NodeProcessClientConnection;
//# sourceMappingURL=client.js.map