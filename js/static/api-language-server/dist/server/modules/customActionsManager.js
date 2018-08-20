"use strict";
// This module provides RAML module structure
Object.defineProperty(exports, "__esModule", { value: true });
var rp = require("raml-1-parser");
var ramlActions = require("raml-actions");
var universes = rp.universes;
function createManager(connection, astManagerModule, editorManagerModule) {
    return new CustomActionsManager(connection, astManagerModule, editorManagerModule);
}
exports.createManager = createManager;
var EditorProvider = (function () {
    function EditorProvider(editorManagerModule, uri) {
        this.editorManagerModule = editorManagerModule;
        this.uri = uri;
    }
    EditorProvider.prototype.getCurrentEditor = function () {
        return this.editorManagerModule.getEditor(this.uri);
    };
    return EditorProvider;
}());
function initialize() {
    ramlActions.intializeStandardActions();
}
exports.initialize = initialize;
var ASTProvider = (function () {
    function ASTProvider(uri, astManagerModule, logger, position) {
        this.uri = uri;
        this.astManagerModule = astManagerModule;
        this.logger = logger;
        this.position = position;
    }
    ASTProvider.prototype.getASTRoot = function () {
        var result = this.astManagerModule.getCurrentAST(this.uri);
        this.logger.debugDetail("Got AST from AST provider: " + (result ? "true" : "false"), "CustomActionsManager", "ASTProvider#getASTRoot");
        return result;
    };
    ASTProvider.prototype.getSelectedNode = function () {
        var root = this.getASTRoot();
        if (!root) {
            return null;
        }
        return root.findElementAtOffset(this.position);
    };
    /**
     * Gets current AST root asynchronously.
     * Can return null.
     */
    ASTProvider.prototype.getASTRootAsync = function () {
        return Promise.resolve(this.getASTRoot());
    };
    /**
     * Gets current AST node asynchronously
     * Can return null.
     */
    ASTProvider.prototype.getSelectedNodeAsync = function () {
        return Promise.resolve(this.getSelectedNode());
    };
    return ASTProvider;
}());
var CollectingDocumentChangeExecutor = (function () {
    function CollectingDocumentChangeExecutor(logger) {
        this.logger = logger;
        this.changes = [];
    }
    CollectingDocumentChangeExecutor.prototype.changeDocument = function (change) {
        this.logger.debugDetail("Registering document change for document " + change.uri +
            ", text is:\n" + change.text, "CustomActionsManager", "CollectingDocumentChangeExecutor#changeDocument");
        this.changes.push(change);
        return Promise.resolve(null);
    };
    CollectingDocumentChangeExecutor.prototype.getChanges = function () {
        return this.changes;
    };
    return CollectingDocumentChangeExecutor;
}());
var ASTModifier = (function () {
    function ASTModifier(uri, changeExecutor) {
        this.uri = uri;
        this.changeExecutor = changeExecutor;
    }
    ASTModifier.prototype.deleteNode = function (node) {
        var parent = node.parent();
        if (parent) {
            parent.remove(node);
            parent.resetChildren();
        }
    };
    ASTModifier.prototype.updateText = function (node) {
        var newText = node.unit().contents();
        this.changeExecutor.changeDocument({
            uri: this.uri,
            text: newText,
        });
    };
    return ASTModifier;
}());
initialize();
var CustomActionsManager = (function () {
    function CustomActionsManager(connection, astManagerModule, editorManager) {
        this.connection = connection;
        this.astManagerModule = astManagerModule;
        this.editorManager = editorManager;
        this.changeExecutor = null;
        this.enableUIActions = true;
        this.disposed = false;
        this.changeExecutor = new CollectingDocumentChangeExecutor(connection);
    }
    CustomActionsManager.prototype.launch = function () {
        var _this = this;
        this.disposed = false;
        this.onCalculateEditorContextActionsListener = function (uri, position) {
            return _this.calculateEditorActions(uri, position);
        };
        this.connection.onCalculateEditorContextActions(this.onCalculateEditorContextActionsListener);
        this.getAllActionsListener = function () { return _this.getAllActions(); };
        this.connection.onAllEditorContextActions(this.getAllActionsListener);
        this.onExecuteContextActionListener = function (uri, actionId, position) {
            _this.connection.debug("onExecuteContextAction for uri " + uri, "CustomActionsManager", "calculateEditorActions");
            return _this.executeAction(uri, actionId, position);
        };
        this.connection.onExecuteContextAction(this.onExecuteContextActionListener);
        this.onSetServerConfigurationListener = function (configuration) {
            if (configuration.actionsConfiguration) {
                if (configuration.actionsConfiguration.enableUIActions !== null) {
                    _this.enableUIActions = configuration.actionsConfiguration.enableUIActions;
                }
            }
        };
        this.connection.onSetServerConfiguration(this.onSetServerConfigurationListener);
    };
    CustomActionsManager.prototype.dispose = function () {
        this.disposed = true;
        this.connection.onCalculateEditorContextActions(this.onCalculateEditorContextActionsListener, true);
        this.connection.onAllEditorContextActions(this.getAllActionsListener, true);
        this.connection.onExecuteContextAction(this.onExecuteContextActionListener, true);
        this.connection.onSetServerConfiguration(this.onSetServerConfigurationListener, true);
    };
    CustomActionsManager.prototype.isDisposed = function () {
        return this.disposed;
    };
    /**
     * Returns unique module name.
     */
    CustomActionsManager.prototype.getModuleName = function () {
        return "CUSTOM_ACTIONS_MANAGER";
    };
    CustomActionsManager.prototype.vsCodeUriToParserUri = function (vsCodeUri) {
        if (vsCodeUri.indexOf("file://") === 0) {
            return vsCodeUri.substring(7);
        }
        return vsCodeUri;
    };
    CustomActionsManager.prototype.getAllActions = function () {
        var _this = this;
        var result = ramlActions.allAvailableActions().filter(function (action) {
            if (!_this.enableUIActions && action.hasUI) {
                return false;
            }
            return true;
        }).map(function (action) { return ({
            id: action.id,
            name: action.name,
            target: action.target,
            category: action.category,
            label: action.label,
            hasUI: action.hasUI
        }); });
        return Promise.resolve(result);
    };
    CustomActionsManager.prototype.calculateEditorActions = function (uri, position) {
        var _this = this;
        this.connection.debug("Requested actions for uri " + uri
            + " and position " + position, "CustomActionsManager", "calculateEditorActions");
        if (!position) {
            position = this.editorManager.getEditor(uri).getCursorPosition();
        }
        var connection = this.connection;
        return this.astManagerModule.forceGetCurrentAST(uri).then(function (ast) {
            _this.initializeActionsFramework(uri, position);
            connection.debugDetail("Starting to calculate actions", "CustomActionsManager", "calculateEditorActions");
            var actions = ramlActions.calculateCurrentActions("TARGET_RAML_EDITOR_NODE");
            connection.debugDetail("Calculated actions: " + actions ? actions.length.toString() : "0", "CustomActionsManager", "calculateEditorActions");
            return actions.filter(function (action) {
                if (!_this.enableUIActions && action.hasUI) {
                    connection.debugDetail("Filtering out action: " + action.id + " as UI actions are disabled", "CustomActionsManager", "calculateEditorActions");
                    return false;
                }
                return true;
            }).map(function (action) {
                return {
                    id: action.id,
                    name: action.name,
                    target: action.target,
                    category: action.category,
                    hasUI: action.hasUI,
                    label: action.label
                };
            });
        });
    };
    CustomActionsManager.prototype.executeAction = function (uri, actionId, position) {
        var _this = this;
        this.connection.debug("Requested action execution for uri " + uri
            + " and position " + position + " and action" + actionId, "CustomActionsManager", "executeAction");
        if (!position) {
            position = this.editorManager.getEditor(uri).getCursorPosition();
        }
        var connection = this.connection;
        var editorManager = this.editorManager;
        return this.astManagerModule.forceGetCurrentAST(uri)
            .then(function (ast) {
            _this.initializeActionsFramework(uri, position);
            connection.debugDetail("Starting to execute action " + actionId, "CustomActionsManager", "executeAction");
            var actionMeta = ramlActions.findActionById(actionId, true);
            if (actionMeta.hasUI) {
                var executeActionPromise = ramlActions.executeAction(actionId);
                connection.debugDetail("Got executeAction promise: " +
                    ((executeActionPromise) ? "true" : "false"), "CustomActionsManager", "executeAction");
                return executeActionPromise.then(function () {
                    connection.debugDetail("Finished to execute action " + actionId, "CustomActionsManager", "executeAction");
                    editorManager.setDocumentChangeExecutor(null);
                    var changes = _this.getChangeExecutor().getChanges();
                    connection.debugDetail("Collected changes", "CustomActionsManager", "executeAction");
                    connection.debugDetail("Number of changes: " + changes ? changes.length.toString() : "0", "CustomActionsManager", "executeAction");
                    return changes;
                });
            }
            else {
                ramlActions.executeAction(actionId);
                connection.debugDetail("Finished to execute action " + actionId, "CustomActionsManager", "executeAction");
                editorManager.setDocumentChangeExecutor(null);
                var changes = _this.getChangeExecutor().getChanges();
                connection.debugDetail("Collected changes", "CustomActionsManager", "executeAction");
                connection.debugDetail("Number of changes: " + changes ? changes.length.toString() : "0", "CustomActionsManager", "executeAction");
                return changes;
            }
        }).catch(function (error) {
            editorManager.setDocumentChangeExecutor(null);
            throw error;
        });
    };
    CustomActionsManager.prototype.initializeActionsFramework = function (uri, position) {
        ramlActions.setEditorProvider(new EditorProvider(this.editorManager, uri));
        ramlActions.setASTProvider(new ASTProvider(uri, this.astManagerModule, this.connection, position));
        this.changeExecutor = new CollectingDocumentChangeExecutor(this.connection);
        ramlActions.setASTModifier(new ASTModifier(uri, this.changeExecutor));
        ramlActions.setDocumentChangeExecutor(this.changeExecutor);
        this.editorManager.setDocumentChangeExecutor(this.changeExecutor);
        var connection = this.connection;
        ramlActions.setExternalUIDisplayExecutor(function (actionId, externalDisplay) {
            connection.debugDetail("Requested to display UI for action ID " + actionId, "CustomActionsManager", "executeAction#setExternalUIDisplayExecutor");
            var action = ramlActions.findActionById(actionId);
            connection.debugDetail("Action found: " + action ? "true" : "false", "CustomActionsManager", "executeAction#setExternalUIDisplayExecutor");
            return function (initialUIState) {
                connection.debugDetail("Requested to display UI for action, got UI state", "CustomActionsManager", "executeAction#setExternalUIDisplayExecutor");
                var uiCode = externalDisplay.createUICode(initialUIState);
                connection.debugDetail("UI code generated: " + (uiCode ? "true" : "false"), "CustomActionsManager", "executeAction#setExternalUIDisplayExecutor");
                connection.debugDetail("Requesting client for UI code display.", "CustomActionsManager", "executeAction#setExternalUIDisplayExecutor");
                connection.debugDetail("UI state is: " + JSON.stringify(initialUIState), "CustomActionsManager", "executeAction#setExternalUIDisplayExecutor");
                return connection.displayActionUI({
                    action: {
                        id: action.id,
                        name: action.name,
                        target: action.target,
                        hasUI: action.hasUI,
                        category: action.category,
                        label: action.label
                    },
                    uiCode: uiCode,
                    initialUIState: initialUIState
                }).then(function (finalUIState) {
                    connection.debugDetail("Client answered with fina UI state for action " + actionId +
                        " , got final UI state: " + finalUIState ? "true" : "false", "CustomActionsManager", "executeAction#setExternalUIDisplayExecutor");
                    return finalUIState;
                });
            };
        });
        ramlActions.setLogger(this.connection);
    };
    CustomActionsManager.prototype.getChangeExecutor = function () {
        return this.changeExecutor;
    };
    return CustomActionsManager;
}());
//# sourceMappingURL=customActionsManager.js.map