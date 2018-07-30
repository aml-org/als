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
var connections_1 = require("../../../server/core/connections");
var messageDispatcher_1 = require("../../common/messageDispatcher");
var utils_1 = require("../../../common/utils");
var AbstractMSServerConnection = /** @class */ (function (_super) {
    __extends(AbstractMSServerConnection, _super);
    function AbstractMSServerConnection(name) {
        var _this = _super.call(this, name) || this;
        _this.openDocumentListeners = [];
        _this.changeDocumentListeners = [];
        _this.closeDocumentListeners = [];
        _this.documentStructureListeners = [];
        _this.documentCompletionListeners = [];
        _this.openDeclarationListeners = [];
        _this.findReferencesListeners = [];
        _this.markOccurrencesListeners = [];
        _this.renameListeners = [];
        _this.documentDetailsListeners = [];
        _this.changeDetailValueListeners = [];
        _this.changePositionListeners = [];
        _this.serverConfigurationListeners = [];
        _this.calculateEditorContextActionsListeners = [];
        _this.getAllEditorContextActionsListeners = [];
        _this.executeContextActionListeners = [];
        _this.executeDetailsActionListeners = [];
        return _this;
    }
    /**
     * Adds a listener to document open notification. Must notify listeners in order of registration.
     * @param listener
     */
    AbstractMSServerConnection.prototype.onOpenDocument = function (listener, unsubsribe) {
        if (unsubsribe === void 0) { unsubsribe = false; }
        this.addListener(this.openDocumentListeners, listener, unsubsribe);
    };
    /**
     * Adds a listener to document change notification. Must notify listeners in order of registration.
     * @param listener
     */
    AbstractMSServerConnection.prototype.onChangeDocument = function (listener, unsubsribe) {
        if (unsubsribe === void 0) { unsubsribe = false; }
        this.addListener(this.changeDocumentListeners, listener, unsubsribe);
    };
    /**
     * Adds a listener to document close notification. Must notify listeners in order of registration.
     * @param listener
     */
    AbstractMSServerConnection.prototype.onCloseDocument = function (listener, unsubsribe) {
        if (unsubsribe === void 0) { unsubsribe = false; }
        this.addListener(this.closeDocumentListeners, listener, unsubsribe);
    };
    /**
     * Adds a listener to document structure request. Must notify listeners in order of registration.
     * @param listener
     */
    AbstractMSServerConnection.prototype.onDocumentStructure = function (listener, unsubsribe) {
        if (unsubsribe === void 0) { unsubsribe = false; }
        this.addListener(this.documentStructureListeners, listener, unsubsribe);
    };
    /**
     * Adds a listener to document completion request. Must notify listeners in order of registration.
     * @param listener
     */
    AbstractMSServerConnection.prototype.onDocumentCompletion = function (listener, unsubsribe) {
        if (unsubsribe === void 0) { unsubsribe = false; }
        this.addListener(this.documentCompletionListeners, listener, unsubsribe);
    };
    /**
     * Adds a listener to document open declaration request.  Must notify listeners in order of registration.
     * @param listener
     */
    AbstractMSServerConnection.prototype.onOpenDeclaration = function (listener, unsubsribe) {
        if (unsubsribe === void 0) { unsubsribe = false; }
        this.addListener(this.openDeclarationListeners, listener, unsubsribe);
    };
    /**
     * Adds a listener to document find references request.  Must notify listeners in order of registration.
     * @param listener
     */
    AbstractMSServerConnection.prototype.onFindReferences = function (listener, unsubsribe) {
        if (unsubsribe === void 0) { unsubsribe = false; }
        this.addListener(this.findReferencesListeners, listener, unsubsribe);
    };
    /**
     * Finds the set of document (and non-document files) edits to perform the requested rename.
     * @param listener
     */
    AbstractMSServerConnection.prototype.onRename = function (listener, unsubsribe) {
        if (unsubsribe === void 0) { unsubsribe = false; }
        this.addListener(this.renameListeners, listener, unsubsribe);
    };
    /**
     * Reports latest validation results
     * @param report
     */
    AbstractMSServerConnection.prototype.validated = function (report) {
        this.send({
            type: "VALIDATION_REPORT",
            payload: report
        });
    };
    /**
     * Reports new calculated structure when available.
     * @param uri - document uri
     * @param structure - structure for the document
     */
    AbstractMSServerConnection.prototype.structureAvailable = function (report) {
        this.send({
            type: "STRUCTURE_REPORT",
            payload: report
        });
    };
    /**
     * Returns whether path/url exists.
     * @param fullPath
     */
    AbstractMSServerConnection.prototype.exists = function (path) {
        return this.sendWithResponse({
            type: "EXISTS",
            payload: path
        });
    };
    /**
     * Returns directory content list.
     * @param fullPath
     */
    AbstractMSServerConnection.prototype.readDir = function (path) {
        return this.sendWithResponse({
            type: "READ_DIR",
            payload: path
        });
    };
    /**
     * Returns whether path/url represents a directory
     * @param path
     */
    AbstractMSServerConnection.prototype.isDirectory = function (path) {
        return this.sendWithResponse({
            type: "IS_DIRECTORY",
            payload: path
        });
    };
    /**
     * File contents by full path/url.
     * @param path
     */
    AbstractMSServerConnection.prototype.content = function (path) {
        return this.sendWithResponse({
            type: "CONTENT",
            payload: path
        });
    };
    /**
     * Marks occurrences of a symbol under the cursor in the current document.
     * @param listener
     */
    AbstractMSServerConnection.prototype.onMarkOccurrences = function (listener, unsubsribe) {
        if (unsubsribe === void 0) { unsubsribe = false; }
        this.addListener(this.markOccurrencesListeners, listener, unsubsribe);
    };
    /**
     * Adds a listener to document details request. Must notify listeners in order of registration.
     * @param listener
     */
    AbstractMSServerConnection.prototype.onDocumentDetails = function (listener, unsubsribe) {
        if (unsubsribe === void 0) { unsubsribe = false; }
        this.addListener(this.documentDetailsListeners, listener, unsubsribe);
    };
    /**
     * Adds a listener for specific details action execution.
     * @param uri - document uri
     * @param action - action to execute.
     * @param position - optional position in the document.
     * If not provided, the last reported by positionChanged method will be used.
     */
    AbstractMSServerConnection.prototype.onExecuteDetailsAction = function (listener, unsubsribe) {
        if (unsubsribe === void 0) { unsubsribe = false; }
        this.addListener(this.executeDetailsActionListeners, listener, unsubsribe);
    };
    /**
     * Adds a listener to document details value change request.
     * @param listener
     * @param unsubscribe - if true, existing listener will be removed. False by default.
     */
    AbstractMSServerConnection.prototype.onChangeDetailValue = function (listener, unsubsribe) {
        this.addListener(this.changeDetailValueListeners, listener, unsubsribe);
    };
    /**
     * Adds a listener to document cursor position change notification.
     * Must notify listeners in order of registration.
     * @param listener
     */
    AbstractMSServerConnection.prototype.onChangePosition = function (listener, unsubsribe) {
        if (unsubsribe === void 0) { unsubsribe = false; }
        this.addListener(this.changePositionListeners, listener, unsubsribe);
    };
    /**
     * Reports new calculated details when available.
     * @param report - details report.
     */
    AbstractMSServerConnection.prototype.detailsAvailable = function (report) {
        this.send({
            type: "DETAILS_REPORT",
            payload: report
        });
    };
    /**
     * Sets server configuration.
     * @param loggerSettings
     */
    AbstractMSServerConnection.prototype.onSetServerConfiguration = function (listener, unsubsribe) {
        if (unsubsribe === void 0) { unsubsribe = false; }
        this.addListener(this.serverConfigurationListeners, listener, unsubsribe);
    };
    /**
     * Handler of OPEN_DOCUMENT message.
     * @param document
     * @constructor
     */
    AbstractMSServerConnection.prototype.OPEN_DOCUMENT = function (document) {
        for (var _i = 0, _a = this.openDocumentListeners; _i < _a.length; _i++) {
            var listener = _a[_i];
            listener(document);
        }
    };
    /**
     * Handler of CHANGE_DOCUMENT message.
     * @param document
     * @constructor
     */
    AbstractMSServerConnection.prototype.CHANGE_DOCUMENT = function (document) {
        for (var _i = 0, _a = this.changeDocumentListeners; _i < _a.length; _i++) {
            var listener = _a[_i];
            listener(document);
        }
    };
    /**
     * Handler of CLOSE_DOCUMENT message.
     * @param uri
     * @constructor
     */
    AbstractMSServerConnection.prototype.CLOSE_DOCUMENT = function (uri) {
        for (var _i = 0, _a = this.closeDocumentListeners; _i < _a.length; _i++) {
            var listener = _a[_i];
            listener(uri);
        }
    };
    /**
     * Handler of GET_STRUCTURE message.
     * @param uri
     * @constructor
     */
    AbstractMSServerConnection.prototype.GET_STRUCTURE = function (uri) {
        if (this.documentStructureListeners.length === 0) {
            return Promise.resolve({});
        }
        return this.documentStructureListeners[0](uri);
    };
    /**
     * Handler of GET_SUGGESTIONS message.
     * @param uri - document uri
     * @param position - offset in the document starting from 0
     * @constructor
     */
    AbstractMSServerConnection.prototype.GET_SUGGESTIONS = function (payload) {
        if (this.documentCompletionListeners.length === 0) {
            return Promise.resolve([]);
        }
        var promises = [];
        for (var _i = 0, _a = this.documentCompletionListeners; _i < _a.length; _i++) {
            var listener = _a[_i];
            this.debugDetail("Calling a listener", "ProxyServerConnection", "getCompletion");
            var listenerResult = listener(payload.uri, payload.position);
            if (listenerResult) {
                promises.push(listenerResult);
            }
        }
        return Promise.all(promises).then(function (resolvedResults) {
            var result = [];
            for (var _i = 0, resolvedResults_1 = resolvedResults; _i < resolvedResults_1.length; _i++) {
                var currentPromiseResult = resolvedResults_1[_i];
                result = result.concat(currentPromiseResult);
            }
            return result;
        });
    };
    /**
     * Handler of OPEN_DECLARATION message.
     * @param uri - document uri
     * @param position - offset in the document starting from 0
     * @constructor
     */
    AbstractMSServerConnection.prototype.OPEN_DECLARATION = function (payload) {
        if (this.openDeclarationListeners.length === 0) {
            return Promise.resolve([]);
        }
        return this.openDeclarationListeners[0](payload.uri, payload.position);
    };
    /**
     * Handler of FIND_REFERENCES message.
     * @param uri - document uri
     * @param position - offset in the document starting from 0
     * @constructor
     */
    AbstractMSServerConnection.prototype.FIND_REFERENCES = function (payload) {
        if (this.findReferencesListeners.length === 0) {
            return Promise.resolve([]);
        }
        return this.findReferencesListeners[0](payload.uri, payload.position);
    };
    /**
     * Handler of MARK_OCCURRENCES message.
     * @param uri - document uri
     * @param position - offset in the document starting from 0
     * @constructor
     */
    AbstractMSServerConnection.prototype.MARK_OCCURRENCES = function (payload) {
        if (this.markOccurrencesListeners.length === 0) {
            return Promise.resolve([]);
        }
        return this.markOccurrencesListeners[0](payload.uri, payload.position);
    };
    /**
     * Handler of RENAME message.
     * @param uri - document uri
     * @param position - offset in the document starting from 0
     * @param newName - new name
     * @constructor
     */
    AbstractMSServerConnection.prototype.RENAME = function (payload) {
        if (this.renameListeners.length === 0) {
            return [];
        }
        var result = [];
        for (var _i = 0, _a = this.renameListeners; _i < _a.length; _i++) {
            var listener = _a[_i];
            result = result.concat(listener(payload.uri, payload.position, payload.newName));
        }
        return result;
    };
    AbstractMSServerConnection.prototype.SET_LOGGER_CONFIGURATION = function (payload) {
        this.setLoggerConfiguration(payload);
    };
    /**
     * Handler of GET_STRUCTURE message.
     * @param uri
     * @constructor
     */
    AbstractMSServerConnection.prototype.GET_DETAILS = function (payload) {
        if (this.documentDetailsListeners.length === 0) {
            return Promise.resolve(null);
        }
        return this.documentDetailsListeners[0](payload.uri, payload.position);
    };
    /**
     * Handler of GET_STRUCTURE message.
     * @param uri
     * @constructor
     */
    AbstractMSServerConnection.prototype.CHANGE_DETAIL_VALUE = function (payload) {
        if (this.changeDetailValueListeners.length === 0) {
            return Promise.resolve(null);
        }
        return this.changeDetailValueListeners[0](payload.uri, payload.position, payload.itemID, payload.value);
    };
    /**
     * Handler for CHANGE_POSITION message.
     * @param payload
     * @constructor
     */
    AbstractMSServerConnection.prototype.CHANGE_POSITION = function (payload) {
        for (var _i = 0, _a = this.changePositionListeners; _i < _a.length; _i++) {
            var listener = _a[_i];
            listener(payload.uri, payload.position);
        }
    };
    /**
     * Handler for SET_SERVER_CONFIGURATION message.
     * @param payload
     * @constructor
     */
    AbstractMSServerConnection.prototype.SET_SERVER_CONFIGURATION = function (payload) {
        for (var _i = 0, _a = this.serverConfigurationListeners; _i < _a.length; _i++) {
            var listener = _a[_i];
            listener(payload);
        }
    };
    /**
     * Logs a message
     * @param message - message text
     * @param severity - message severity
     * @param component - component name
     * @param subcomponent - sub-component name
     */
    AbstractMSServerConnection.prototype.log = function (message, severity, component, subcomponent) {
        var filtered = utils_1.filterLogMessage({
            message: message,
            severity: severity,
            component: component,
            subcomponent: subcomponent
        }, this.loggerSettings);
        if (filtered) {
            this.internalLog(filtered.message, filtered.severity, filtered.component, filtered.subcomponent);
        }
    };
    AbstractMSServerConnection.prototype.internalLog = function (message, severity, component, subcomponent) {
        var toLog = "";
        var currentDate = new Date();
        toLog += currentDate.getHours() + ":" + currentDate.getMinutes() + ":" +
            currentDate.getSeconds() + ":" + currentDate.getMilliseconds() + " ";
        if (component) {
            toLog += (component + ": ");
        }
        if (subcomponent) {
            toLog += (subcomponent + ": ");
        }
        toLog += message;
        if (severity === connections_1.MessageSeverity.WARNING) {
            console.warn(toLog);
        }
        else if (severity === connections_1.MessageSeverity.ERROR) {
            console.error(toLog);
        }
        else {
            console.log(toLog);
        }
    };
    /**
     * Logs a DEBUG severity message.
     * @param message - message text
     * @param component - component name
     * @param subcomponent - sub-component name
     */
    AbstractMSServerConnection.prototype.debug = function (message, component, subcomponent) {
        this.log(message, connections_1.MessageSeverity.DEBUG, component, subcomponent);
    };
    /**
     * Logs a DEBUG_DETAIL severity message.
     * @param message - message text
     * @param component - component name
     * @param subcomponent - sub-component name
     */
    AbstractMSServerConnection.prototype.debugDetail = function (message, component, subcomponent) {
        this.log(message, connections_1.MessageSeverity.DEBUG_DETAIL, component, subcomponent);
    };
    /**
     * Logs a DEBUG_OVERVIEW severity message.
     * @param message - message text
     * @param component - component name
     * @param subcomponent - sub-component name
     */
    AbstractMSServerConnection.prototype.debugOverview = function (message, component, subcomponent) {
        this.log(message, connections_1.MessageSeverity.DEBUG_OVERVIEW, component, subcomponent);
    };
    /**
     * Logs a WARNING severity message.
     * @param message - message text
     * @param component - component name
     * @param subcomponent - sub-component name
     */
    AbstractMSServerConnection.prototype.warning = function (message, component, subcomponent) {
        this.log(message, connections_1.MessageSeverity.WARNING, component, subcomponent);
    };
    /**
     * Logs an ERROR severity message.
     * @param message - message text
     * @param component - component name
     * @param subcomponent - sub-component name
     */
    AbstractMSServerConnection.prototype.error = function (message, component, subcomponent) {
        this.log(message, connections_1.MessageSeverity.ERROR, component, subcomponent);
    };
    /**
     * Sets connection logger configuration.
     * @param loggerSettings
     */
    AbstractMSServerConnection.prototype.setLoggerConfiguration = function (loggerSettings) {
        this.loggerSettings = loggerSettings;
    };
    /**
     * Calculates the list of executable actions available in the current context.
     *
     * @param uri - document uri.
     * @param position - optional position in the document.
     * If not provided, the last reported by positionChanged method will be used.
     * @param target - option target argument.
     *
     * "TARGET_RAML_EDITOR_NODE" and "TARGET_RAML_TREE_VIEWER_NODE" are potential values
     * for actions based on the editor state and tree viewer state.
     * "TARGET_RAML_EDITOR_NODE" is default.
     */
    AbstractMSServerConnection.prototype.onCalculateEditorContextActions = function (listener, unsubsribe) {
        if (unsubsribe === void 0) { unsubsribe = false; }
        this.addListener(this.calculateEditorContextActionsListeners, listener, unsubsribe);
    };
    /**
     * Calculates the list of all available executable actions.
     */
    AbstractMSServerConnection.prototype.onAllEditorContextActions = function (listener, unsubsribe) {
        if (unsubsribe === void 0) { unsubsribe = false; }
        this.addListener(this.getAllEditorContextActionsListeners, listener, unsubsribe);
    };
    /**
     * Adds a listener for specific action execution.
     * If action has UI, causes a consequent displayActionUI call.
     * @param uri - document uri
     * @param action - action to execute.
     * @param position - optional position in the document.
     * If not provided, the last reported by positionChanged method will be used.
     */
    AbstractMSServerConnection.prototype.onExecuteContextAction = function (listener, unsubsribe) {
        if (unsubsribe === void 0) { unsubsribe = false; }
        this.addListener(this.executeContextActionListeners, listener, unsubsribe);
    };
    /**
     * Adds a listener to display action UI.
     * @param uiDisplayRequest - display request
     * @return final UI state.
     */
    AbstractMSServerConnection.prototype.displayActionUI = function (uiDisplayRequest) {
        return this.sendWithResponse({
            type: "DISPLAY_ACTION_UI",
            payload: uiDisplayRequest
        });
    };
    AbstractMSServerConnection.prototype.EXECUTE_DETAILS_ACTION = function (payload) {
        this.debugDetail("Called", "ProxyServerConnection", "EXECUTE_DETAILS_ACTION");
        if (this.executeDetailsActionListeners.length === 0) {
            return Promise.resolve([]);
        }
        this.debugDetail("Before execution", "ProxyServerConnection", "EXECUTE_DETAILS_ACTION");
        try {
            var result = this.executeDetailsActionListeners[0](payload.uri, payload.actionId, payload.position);
            return result;
        }
        catch (Error) {
            this.debugDetail("Failed listener execution: " + Error.message, "ProxyServerConnection", "EXECUTE_DETAILS_ACTION");
        }
    };
    AbstractMSServerConnection.prototype.CALCULATE_ACTIONS = function (payload) {
        if (this.calculateEditorContextActionsListeners.length === 0) {
            return Promise.resolve([]);
        }
        return this.calculateEditorContextActionsListeners[0](payload.uri, payload.position);
    };
    AbstractMSServerConnection.prototype.ALL_ACTIONS = function (payload) {
        if (this.getAllEditorContextActionsListeners.length === 0) {
            return Promise.resolve([]);
        }
        return this.getAllEditorContextActionsListeners[0]();
    };
    AbstractMSServerConnection.prototype.EXECUTE_ACTION = function (payload) {
        this.debugDetail("Called", "ProxyServerConnection", "EXECUTE_ACTION");
        if (this.executeContextActionListeners.length === 0) {
            return Promise.resolve([]);
        }
        this.debugDetail("Before execution", "ProxyServerConnection", "EXECUTE_ACTION");
        try {
            var result = this.executeContextActionListeners[0](payload.uri, payload.actionId, payload.position);
            return result;
        }
        catch (Error) {
            this.debugDetail("Failed listener execution: " + Error.message, "ProxyServerConnection", "EXECUTE_ACTION");
        }
    };
    /**
     * Adds listener.
     * @param memberListeners - member containing array of listeners
     * @param listener - listener to add
     * @param unsubscribe - whether to unsubscribe this listener
     */
    AbstractMSServerConnection.prototype.addListener = function (memberListeners, listener, unsubscribe) {
        if (unsubscribe === void 0) { unsubscribe = false; }
        if (unsubscribe) {
            var index = memberListeners.indexOf(listener);
            if (index !== -1) {
                memberListeners.splice(index, 1);
            }
        }
        else {
            memberListeners.push(listener);
        }
    };
    return AbstractMSServerConnection;
}(messageDispatcher_1.MessageDispatcher));
exports.AbstractMSServerConnection = AbstractMSServerConnection;
//# sourceMappingURL=abstractServer.js.map