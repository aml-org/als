"use strict";
// This manager handles fixed actions as opposed to dynamic context-depended actions
Object.defineProperty(exports, "__esModule", { value: true });
var findReferencesModule = require("./fixedActions/findReferencesAction");
var markOccurrencesModule = require("./fixedActions/markOccurrencesAction");
var openDeclarationModule = require("./fixedActions/openDeclarationAction");
var renameModule = require("./fixedActions/renameAction");
function createManager(connection, astManagerModule, editorManagerModule) {
    return new FixedActionsManager(connection, astManagerModule, editorManagerModule);
}
exports.createManager = createManager;
var FixedActionsManager = /** @class */ (function () {
    function FixedActionsManager(connection, astManagerModule, editorManagerModule) {
        this.connection = connection;
        this.astManagerModule = astManagerModule;
        this.editorManagerModule = editorManagerModule;
        this.subModules = [];
        this.subModules.push(openDeclarationModule.createManager(this.connection, this.astManagerModule, this.editorManagerModule));
        this.subModules.push(findReferencesModule.createManager(this.connection, this.astManagerModule, this.editorManagerModule));
        this.subModules.push(markOccurrencesModule.createManager(this.connection, this.astManagerModule, this.editorManagerModule));
        this.subModules.push(renameModule.createManager(this.connection, this.astManagerModule, this.editorManagerModule));
    }
    FixedActionsManager.prototype.launch = function () {
        this.subModules.forEach(function (subModule) { return subModule.launch(); });
    };
    FixedActionsManager.prototype.dispose = function () {
        this.subModules.forEach(function (subModule) { return subModule.dispose(); });
    };
    /**
     * Returns unique module name.
     */
    FixedActionsManager.prototype.getModuleName = function () {
        return "FIXED_ACTIONS_MANAGER";
    };
    return FixedActionsManager;
}());
//# sourceMappingURL=fixedActionsManager.js.map