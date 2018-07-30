"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var commonInterfaces_1 = require("../modules/commonInterfaces");
var EditorManagerModule = require("../modules/editorManager");
var ASTManagerModule = require("../modules/astManager");
var ValidationManagerModule = require("../modules/validationManager");
var StructureManagerModule = require("../modules/structureManager");
var CompletionManagerModule = require("../modules/completionManager");
var FixedActionsManagerModule = require("../modules/fixedActionsManager");
var DetailsManagerModule = require("../modules/detailsManager");
var CustomActionsManagerModule = require("../modules/customActionsManager");
var Server = /** @class */ (function () {
    function Server(connection) {
        this.connection = connection;
        /**
         * Map from module name to module.
         */
        this.modules = {};
        /**
         * Map from module name to its enablement state.
         */
        this.modulesEnablementState = {};
        var editorManagerModule = EditorManagerModule.createManager(connection);
        this.registerModule(editorManagerModule);
        var astManagerModule = ASTManagerModule.createManager(connection, editorManagerModule);
        this.registerModule(astManagerModule);
        this.registerModule(ValidationManagerModule.createManager(connection, astManagerModule, editorManagerModule));
        this.registerModule(StructureManagerModule.createManager(connection, astManagerModule, editorManagerModule));
        this.registerModule(CompletionManagerModule.createManager(connection, astManagerModule, editorManagerModule));
        this.registerModule(FixedActionsManagerModule.createManager(connection, astManagerModule, editorManagerModule));
        var customActionsManager = CustomActionsManagerModule.createManager(connection, astManagerModule, editorManagerModule);
        this.registerModule(customActionsManager, true);
        this.registerModule(DetailsManagerModule.createManager(connection, astManagerModule, editorManagerModule, customActionsManager), true);
    }
    Server.prototype.registerModule = function (module, defaultEnablementState) {
        if (defaultEnablementState === void 0) { defaultEnablementState = true; }
        var moduleName = module.getModuleName();
        if (!moduleName) {
            this.connection.error("No name for module!", "server", "registerModule");
        }
        this.modules[moduleName] = module;
        this.modulesEnablementState[moduleName] = defaultEnablementState;
    };
    Server.prototype.enableModule = function (moduleName) {
        this.connection.debugDetail("Changing module enablement of " + moduleName +
            " to true", "server", "onSetServerConfiguration");
        if (this.modulesEnablementState[moduleName]) {
            this.connection.debugDetail("Module already enabled " + moduleName, "server", "onSetServerConfiguration");
            return;
        }
        var module = this.modules[moduleName];
        if (!module) {
            this.connection.error("Cant not enable unknown module " + moduleName, "server", "enableModule");
        }
        module.launch();
        this.modulesEnablementState[moduleName] = true;
    };
    Server.prototype.disableModule = function (moduleName) {
        this.connection.debugDetail("Changing module enablement of " + moduleName +
            " to false", "server", "onSetServerConfiguration");
        if (!this.modulesEnablementState[moduleName]) {
            this.connection.debugDetail("Module already disabled " + moduleName, "server", "onSetServerConfiguration");
            return;
        }
        var module = this.modules[moduleName];
        if (!module) {
            this.connection.error("Cant not enable unknown module " + moduleName, "server", "disableModule");
        }
        if (commonInterfaces_1.isDisposableModule(module)) {
            module.dispose();
        }
        else {
            this.connection.warning("Attempt to disable non-disposable module " + moduleName, "server", "disableModule");
        }
        this.modulesEnablementState[moduleName] = false;
    };
    Server.prototype.listen = function () {
        var _this = this;
        this.connection.onSetServerConfiguration(function (configuration) {
            if (!configuration.modulesConfiguration) {
                return;
            }
            _this.checkAndChangeEnablement(configuration.modulesConfiguration.enableCustomActionsModule, "CUSTOM_ACTIONS_MANAGER");
            _this.checkAndChangeEnablement(configuration.modulesConfiguration.enableDetailsModule, "DETAILS_MANAGER");
            _this.checkAndChangeEnablement(configuration.modulesConfiguration.enableASTManagerModule, "AST_MANAGER");
            _this.checkAndChangeEnablement(configuration.modulesConfiguration.enableCompletionManagerModule, "COMPLETION_MANAGER");
            _this.checkAndChangeEnablement(configuration.modulesConfiguration.enableEditorManagerModule, "EDITOR_MANAGER");
            _this.checkAndChangeEnablement(configuration.modulesConfiguration.enableFixedActionsModule, "FIXED_ACTIONS_MANAGER");
            _this.checkAndChangeEnablement(configuration.modulesConfiguration.enableStructureManagerModule, "STRUCTURE_MANAGER");
            _this.checkAndChangeEnablement(configuration.modulesConfiguration.enableValidationManagerModule, "VALIDATION_MANAGER");
            if (configuration.modulesConfiguration.allModules != null) {
                _this.connection.debugDetail("Changing module enablement of all modules" +
                    " to " + configuration.modulesConfiguration.allModules, "server", "onSetServerConfiguration");
                for (var moduleName in _this.modules) {
                    if (_this.modules.hasOwnProperty(moduleName)) {
                        configuration.modulesConfiguration.allModules ?
                            _this.enableModule(moduleName) : _this.disableModule(moduleName);
                    }
                }
            }
        });
        for (var moduleName in this.modules) {
            if (this.modules.hasOwnProperty(moduleName)) {
                if (this.modulesEnablementState[moduleName]) {
                    this.modules[moduleName].launch();
                }
            }
        }
    };
    Server.prototype.checkAndChangeEnablement = function (enablementFlag, moduleId) {
        if (enablementFlag != null) {
            this.connection.debug("Changing module enablement of " + moduleId +
                " to " + enablementFlag, "server", "onSetServerConfiguration");
            enablementFlag ?
                this.enableModule(moduleId) : this.disableModule(moduleId);
        }
    };
    return Server;
}());
exports.Server = Server;
//# sourceMappingURL=server.js.map