"use strict";
// This module provides RAML module structure
Object.defineProperty(exports, "__esModule", { value: true });
var rp = require("raml-1-parser");
var ramlOutline = require("raml-outline");
var outlineManagerCommons = require("./outlineManagerCommons");
var universes = rp.universes;
function createManager(connection, astManagerModule, editorManagerModule, customActionsManager) {
    return new DetailsManager(connection, astManagerModule, editorManagerModule, customActionsManager);
}
exports.createManager = createManager;
function initialize() {
    outlineManagerCommons.initialize();
}
exports.initialize = initialize;
initialize();
var DetailsManager = /** @class */ (function () {
    function DetailsManager(connection, astManagerModule, editorManager, actionManagerModule) {
        this.connection = connection;
        this.astManagerModule = astManagerModule;
        this.editorManager = editorManager;
        this.actionManagerModule = actionManagerModule;
        /**
         * Whether direct calculation is on.
         * @type {boolean}
         */
        this.calculatingDetailsOnDirectRequest = false;
        /**
         * Remembering positions for opened documents.
         * @type {{}}
         */
        this.uriToPositions = {};
    }
    DetailsManager.prototype.launch = function () {
        var _this = this;
        this.onDocumentDetailsListener = function (uri, position) {
            return _this.getDetails(uri, position);
        };
        this.connection.onDocumentDetails(this.onDocumentDetailsListener);
        this.onNewASTAvailableListener = function (uri, version, ast) {
            _this.connection.debug("Got new AST report for uri " + uri, "DetailsManager", "listen");
            _this.calculateAndSendDetailsReport(uri, version);
        };
        this.astManagerModule.onNewASTAvailable(this.onNewASTAvailableListener);
        this.onChangePositionListener = function (uri, position) {
            _this.connection.debug("Got new position report for uri " + uri + " : " + position, "DetailsManager", "listen");
            _this.uriToPositions[uri] = position;
            var editor = _this.editorManager.getEditor(uri);
            if (!editor) {
                return;
            }
            var version = editor.getVersion();
            _this.calculateAndSendDetailsReport(uri, version);
        };
        this.connection.onChangePosition(this.onChangePositionListener);
        this.onChangeDetailValueListener = function (uri, position, itemID, value) {
            return _this.changeDetailValue(uri, position, itemID, value);
        };
        this.connection.onChangeDetailValue(this.onChangeDetailValueListener);
        this.onExecuteDetailsActionListener = function (uri, itemID, position) {
            return _this.onExecuteDetailsAction(uri, itemID, position);
        };
        this.connection.onExecuteDetailsAction(this.onExecuteDetailsActionListener);
    };
    DetailsManager.prototype.dispose = function () {
        this.connection.onDocumentDetails(this.onDocumentDetailsListener, true);
        this.astManagerModule.onNewASTAvailable(this.onNewASTAvailableListener, true);
        this.connection.onChangePosition(this.onChangePositionListener, true);
        this.connection.onChangeDetailValue(this.onChangeDetailValueListener, true);
        this.connection.onExecuteDetailsAction(this.onExecuteDetailsActionListener, true);
    };
    /**
     * Returns unique module name.
     */
    DetailsManager.prototype.getModuleName = function () {
        return "DETAILS_MANAGER";
    };
    DetailsManager.prototype.vsCodeUriToParserUri = function (vsCodeUri) {
        if (vsCodeUri.indexOf("file://") === 0) {
            return vsCodeUri.substring(7);
        }
        return vsCodeUri;
    };
    DetailsManager.prototype.getDetails = function (uri, position) {
        var _this = this;
        this.connection.debug("Requested details for uri " + uri + " and position " + position, "DetailsManager", "getDetails");
        this.calculatingDetailsOnDirectRequest = true;
        return this.calculateDetails(uri, position).then(function (calculated) {
            _this.connection.debug("Calculation result is not null:" +
                (calculated != null ? "true" : "false"), "DetailsManager", "getDetails");
            _this.calculatingDetailsOnDirectRequest = false;
            return calculated;
        }).catch(function (error) {
            _this.calculatingDetailsOnDirectRequest = false;
            throw error;
        });
    };
    DetailsManager.prototype.calculateDetails = function (uri, position) {
        var _this = this;
        this.connection.debug("Called for uri: " + uri, "DetailsManager", "calculateDetails");
        // Forcing current AST to exist
        return this.astManagerModule.forceGetCurrentAST(uri).then(function (currentAST) {
            outlineManagerCommons.setOutlineASTProvider(uri, _this.astManagerModule, _this.editorManager, _this.connection);
            var result = ramlOutline.getDetailsJSON(position);
            _this.connection.debug("Calculation result is not null:" +
                (result != null ? "true" : "false"), "DetailsManager", "calculateDetails");
            if (result) {
                _this.connection.debugDetail("Calculation result: "
                    + JSON.stringify(result, null, 2), "DetailsManager", "calculateDetails");
            }
            return _this.addActionsToDetails(result, uri, position);
        });
    };
    DetailsManager.prototype.calculateAndSendDetailsReport = function (uri, version) {
        var _this = this;
        // we do not want reporting while performing the calculation
        if (this.calculatingDetailsOnDirectRequest) {
            return;
        }
        this.connection.debug("Calculating details", "DetailsManager", "calculateAndSendDetailsReport");
        var knownPosition = this.uriToPositions[uri];
        this.connection.debug("Found position: " + knownPosition, "DetailsManager", "calculateAndSendDetailsReport");
        if (knownPosition != null) {
            this.calculateDetails(uri, knownPosition).then(function (detailsForUri) {
                _this.connection.debug("Calculation result is not null:" +
                    (detailsForUri != null ? "true" : "false"), "DetailsManager", "calculateAndSendDetailsReport");
                if (detailsForUri) {
                    _this.connection.detailsAvailable({
                        uri: uri,
                        position: knownPosition,
                        version: version,
                        details: detailsForUri
                    });
                }
            });
        }
    };
    DetailsManager.prototype.addActionsToDetails = function (root, uri, position) {
        var _this = this;
        if (!this.actionManagerModule || this.actionManagerModule.isDisposed()) {
            return Promise.resolve(root);
        }
        return this.actionManagerModule.calculateEditorActions(uri, position).then(function (actions) {
            var generalCategory = _this.findOrCreateGeneralCategory(root);
            if (actions && actions.length > 0) {
                for (var _i = 0, actions_1 = actions; _i < actions_1.length; _i++) {
                    var action = actions_1[_i];
                    var actionItem = {
                        title: action.name,
                        description: "Activate " + action.name,
                        type: "DETAILS_ACTION",
                        error: null,
                        children: [],
                        id: action.id,
                        subType: "CUSTOM_ACTION"
                    };
                    generalCategory.children.push(actionItem);
                }
            }
            return root;
        });
    };
    DetailsManager.prototype.findOrCreateGeneralCategory = function (root) {
        var foundCategory = null;
        root.children.forEach(function (child) {
            if (child.type === "CATEGORY" && child.title == "General") {
                foundCategory = child;
            }
        });
        if (!foundCategory) {
            foundCategory = {
                title: "General",
                description: "",
                type: "CATEGORY",
                error: null,
                children: [],
                id: "Category#General"
            };
        }
        return foundCategory;
    };
    DetailsManager.prototype.changeDetailValue = function (uri, position, itemID, value) {
        var _this = this;
        return this.astManagerModule.forceGetCurrentAST(uri).then(function (currentAST) {
            outlineManagerCommons.setOutlineASTProvider(uri, _this.astManagerModule, _this.editorManager, _this.connection);
            var result = ramlOutline.changeDetailValue(position, itemID, value);
            _this.connection.debug("Change documentn result is not null:" +
                (result != null ? "true" : "false"), "DetailsManager", "changeDetailValue");
            if (result) {
                _this.connection.debugDetail("Calculation result: "
                    + JSON.stringify(result, null, 2), "DetailsManager", "changeDetailValue");
            }
            return [{
                    uri: uri,
                    text: result.text
                }];
        });
    };
    DetailsManager.prototype.onExecuteDetailsAction = function (uri, itemID, position) {
        var _this = this;
        return this.astManagerModule.forceGetCurrentAST(uri).then(function (currentAST) {
            outlineManagerCommons.setOutlineASTProvider(uri, _this.astManagerModule, _this.editorManager, _this.connection);
            var result = ramlOutline.runDetailsAction(position, itemID);
            _this.connection.debug("Change documentn result is not null:" +
                (result != null ? "true" : "false"), "DetailsManager", "changeDetailValue");
            if (result) {
                _this.connection.debugDetail("Calculation result: "
                    + JSON.stringify(result, null, 2), "DetailsManager", "changeDetailValue");
            }
            return [{
                    uri: uri,
                    text: result.text
                }];
        });
    };
    return DetailsManager;
}());
//# sourceMappingURL=detailsManager.js.map