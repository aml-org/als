"use strict";
// This module provides a fixed action for finding declaration of RAML node
Object.defineProperty(exports, "__esModule", { value: true });
var rp = require("raml-1-parser");
var search = rp.search;
var utils = require("../../../common/utils");
var fixedActionCommon = require("./fixedActionsCommon");
function createManager(connection, astManagerModule, editorManagerModule) {
    return new OpenDeclarationActionModule(connection, astManagerModule, editorManagerModule);
}
exports.createManager = createManager;
/**
 * Handles "open declaration" action.
 */
var OpenDeclarationActionModule = (function () {
    function OpenDeclarationActionModule(connection, astManagerModule, editorManagerModule) {
        this.connection = connection;
        this.astManagerModule = astManagerModule;
        this.editorManagerModule = editorManagerModule;
    }
    OpenDeclarationActionModule.prototype.launch = function () {
        var _this = this;
        this.onOpenDeclarationListener = function (uri, position) {
            return _this.openDeclaration(uri, position);
        };
        this.connection.onOpenDeclaration(this.onOpenDeclarationListener);
    };
    OpenDeclarationActionModule.prototype.dispose = function () {
        this.connection.onOpenDeclaration(this.onOpenDeclarationListener, true);
    };
    /**
     * Returns unique module name.
     */
    OpenDeclarationActionModule.prototype.getModuleName = function () {
        return "OPEN_DECLARATION_ACTION";
    };
    OpenDeclarationActionModule.prototype.openDeclaration = function (uri, position) {
        var _this = this;
        this.connection.debug("Called for uri: " + uri, "FixedActionsManager", "openDeclaration");
        this.connection.debugDetail("Uri extname: " + utils.extName(uri), "FixedActionsManager", "openDeclaration");
        if (utils.extName(uri) !== ".raml") {
            return Promise.resolve([]);
        }
        var connection = this.connection;
        return this.astManagerModule.forceGetCurrentAST(uri).then(function (ast) {
            connection.debugDetail("Found AST: " + (ast ? "true" : false), "FixedActionsManager", "openDeclaration");
            if (!ast) {
                return [];
            }
            var unit = ast.lowLevel().unit();
            var decl = search.findDeclaration(unit, position);
            connection.debugDetail("Found declaration: " + (decl ? "true" : false), "FixedActionsManager", "openDeclaration");
            if (!decl) {
                return [];
            }
            if (!decl.absolutePath) {
                var location_1 = fixedActionCommon.lowLevelNodeToLocation(uri, decl.lowLevel(), _this.editorManagerModule, connection);
                if (!location_1) {
                    return [];
                }
                return [location_1];
            }
            else {
                var absolutePath = decl.absolutePath();
                if (utils.isHTTPUri(absolutePath)) {
                    return [];
                }
                return [{
                        uri: absolutePath,
                        range: {
                            start: 0,
                            end: 0
                        }
                    }];
            }
        });
    };
    return OpenDeclarationActionModule;
}());
//# sourceMappingURL=openDeclarationAction.js.map