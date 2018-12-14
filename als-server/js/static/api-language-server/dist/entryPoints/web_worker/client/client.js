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
var RAMLClientConnection = (function (_super) {
    __extends(RAMLClientConnection, _super);
    function RAMLClientConnection(worker) {
        var _this = _super.call(this, "NodeProcessClientConnection") || this;
        _this.worker = worker;
        worker.onmessage = function (event) {
            _this.handleRecievedMessage(event.data);
        };
        return _this;
    }
    RAMLClientConnection.prototype.stop = function () {
        this.worker.terminate();
    };
    RAMLClientConnection.prototype.sendMessage = function (message) {
        this.worker.postMessage(message);
    };
    return RAMLClientConnection;
}(abstractClient_1.AbstractClientConnection));
exports.RAMLClientConnection = RAMLClientConnection;
//# sourceMappingURL=client.js.map