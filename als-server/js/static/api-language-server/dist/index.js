"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var typeInterfaces_1 = require("./common/typeInterfaces");
exports.StructureCategories = typeInterfaces_1.StructureCategories;
var reconciler_1 = require("./common/reconciler");
exports.Reconciler = reconciler_1.Reconciler;
var launch_1 = require("./entryPoints/node/client/launch");
/**
 * Launches node entry point (separate node server process) and returns client connection.
 * @return {IClientConnection}
 */
function getNodeClientConnection() {
    return launch_1.getConnection();
}
exports.getNodeClientConnection = getNodeClientConnection;
exports.textEditProcessor = require("./common/textEditProcessor");
var abstractClient_1 = require("./entryPoints/common/client/abstractClient");
exports.AbstractClientConnection = abstractClient_1.AbstractClientConnection;
//# sourceMappingURL=index.js.map