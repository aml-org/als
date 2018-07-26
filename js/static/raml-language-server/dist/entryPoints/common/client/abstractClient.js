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
var messageDispatcher_1 = require("../../common/messageDispatcher");
var typeInterfaces_1 = require("../../../client/typeInterfaces");
var clientVersionManager_1 = require("./clientVersionManager");
var utils_1 = require("../../../common/utils");
var AbstractClientConnection = /** @class */ (function (_super) {
    __extends(AbstractClientConnection, _super);
    function AbstractClientConnection(name) {
        var _this = _super.call(this, name) || this;
        _this.validationReportListeners = [];
        _this.structureReportListeners = [];
        _this.onExistsListeners = [];
        _this.onReadDirListeners = [];
        _this.onIsDirectoryListeners = [];
        _this.onContentListeners = [];
        _this.onDetailsReportListeners = [];
        _this.onDisplayActionUIListeners = [];
        _this.versionManager = new clientVersionManager_1.VersionedDocumentManager(_this);
        return _this;
    }
    /**
     * Sets server configuration.
     * @param serverSettings
     */
    AbstractClientConnection.prototype.setServerConfiguration = function (serverSettings) {
        // changing server configuration
        this.send({
            type: "SET_SERVER_CONFIGURATION",
            payload: serverSettings
        });
    };
    AbstractClientConnection.prototype.onValidationReport = function (listener) {
        this.validationReportListeners.push(listener);
    };
    AbstractClientConnection.prototype.onStructureReport = function (listener) {
        this.structureReportListeners.push(listener);
    };
    AbstractClientConnection.prototype.documentOpened = function (document) {
        var commonOpenedDocument = this.versionManager.registerOpenedDocument(document);
        if (!commonOpenedDocument) {
            return;
        }
        this.send({
            type: "OPEN_DOCUMENT",
            payload: commonOpenedDocument
        });
    };
    AbstractClientConnection.prototype.documentChanged = function (document) {
        var commonChangedDocument = this.versionManager.registerChangedDocument(document);
        if (!commonChangedDocument) {
            return;
        }
        this.send({
            type: "CHANGE_DOCUMENT",
            payload: commonChangedDocument
        });
    };
    AbstractClientConnection.prototype.documentClosed = function (uri) {
        // this.versionManager.unregisterDocument(uri);
        this.send({
            type: "CLOSE_DOCUMENT",
            payload: uri
        });
    };
    AbstractClientConnection.prototype.getStructure = function (uri) {
        return this.sendWithResponse({
            type: "GET_STRUCTURE",
            payload: uri
        });
    };
    AbstractClientConnection.prototype.getSuggestions = function (uri, position) {
        return this.sendWithResponse({
            type: "GET_SUGGESTIONS",
            payload: {
                uri: uri,
                position: position
            }
        });
    };
    /**
     * Requests server for the positions of the declaration of the element defined
     * at the given document position.
     * @param uri - document uri
     * @param position - position in the document
     */
    AbstractClientConnection.prototype.openDeclaration = function (uri, position) {
        return this.sendWithResponse({
            type: "OPEN_DECLARATION",
            payload: {
                uri: uri,
                position: position
            }
        });
    };
    /**
     * Requests server for the positions of the references of the element defined
     * at the given document position.
     * @param uri - document uri
     * @param position - position in the document
     */
    AbstractClientConnection.prototype.findReferences = function (uri, position) {
        return this.sendWithResponse({
            type: "FIND_REFERENCES",
            payload: {
                uri: uri,
                position: position
            }
        });
    };
    /**
     * Requests server for the positions of the references of the element defined
     * at the given document position.
     * @param uri - document uri
     * @param position - position in the document
     */
    AbstractClientConnection.prototype.markOccurrences = function (uri, position) {
        return this.sendWithResponse({
            type: "MARK_OCCURRENCES",
            payload: {
                uri: uri,
                position: position
            }
        });
    };
    /**
     * Requests server for rename of the element
     * at the given document position.
     * @param uri - document uri
     * @param position - position in the document
     */
    AbstractClientConnection.prototype.rename = function (uri, position, newName) {
        return this.sendWithResponse({
            type: "RENAME",
            payload: {
                uri: uri,
                position: position,
                newName: newName
            }
        });
    };
    /**
     * Requests server for the document+position details.
     * @param uri
     */
    AbstractClientConnection.prototype.getDetails = function (uri, position) {
        return this.sendWithResponse({
            type: "GET_DETAILS",
            payload: {
                uri: uri,
                position: position
            }
        });
    };
    /**
     * Changes value of details item.
     * @param uri
     * @param position
     * @param itemID
     * @param value
     */
    AbstractClientConnection.prototype.changeDetailValue = function (uri, position, itemID, value) {
        return this.sendWithResponse({
            type: "CHANGE_DETAIL_VALUE",
            payload: {
                uri: uri,
                position: position,
                itemID: itemID,
                value: value
            }
        });
    };
    /**
     * Sets connection logger configuration, both for the server and for the client.
     * @param loggerSettings
     */
    AbstractClientConnection.prototype.setLoggerConfiguration = function (loggerSettings) {
        // changing client configuration
        this.loggerSettings = loggerSettings;
        // changing server configuration
        this.send({
            type: "SET_LOGGER_CONFIGURATION",
            payload: loggerSettings
        });
    };
    AbstractClientConnection.prototype.VALIDATION_REPORT = function (report) {
        for (var _i = 0, _a = this.validationReportListeners; _i < _a.length; _i++) {
            var listener = _a[_i];
            listener(report);
        }
    };
    AbstractClientConnection.prototype.STRUCTURE_REPORT = function (report) {
        for (var _i = 0, _a = this.structureReportListeners; _i < _a.length; _i++) {
            var listener = _a[_i];
            listener(report);
        }
    };
    AbstractClientConnection.prototype.EXISTS = function (path) {
        for (var _i = 0, _a = this.onExistsListeners; _i < _a.length; _i++) {
            var listener = _a[_i];
            var result = listener(path);
            if (result !== null) {
                return result;
            }
        }
        return null;
    };
    AbstractClientConnection.prototype.READ_DIR = function (path) {
        for (var _i = 0, _a = this.onReadDirListeners; _i < _a.length; _i++) {
            var listener = _a[_i];
            var result = listener(path);
            if (result !== null) {
                return result;
            }
        }
        return null;
    };
    AbstractClientConnection.prototype.IS_DIRECTORY = function (path) {
        for (var _i = 0, _a = this.onIsDirectoryListeners; _i < _a.length; _i++) {
            var listener = _a[_i];
            var result = listener(path);
            if (result !== null) {
                return result;
            }
        }
        return null;
    };
    AbstractClientConnection.prototype.CONTENT = function (path) {
        for (var _i = 0, _a = this.onContentListeners; _i < _a.length; _i++) {
            var listener = _a[_i];
            var result = listener(path);
            if (result !== null) {
                return result;
            }
        }
        return null;
    };
    AbstractClientConnection.prototype.DETAILS_REPORT = function (report) {
        for (var _i = 0, _a = this.onDetailsReportListeners; _i < _a.length; _i++) {
            var listener = _a[_i];
            listener(report);
        }
    };
    /**
     * Gets latest document version.
     * @param uri
     */
    AbstractClientConnection.prototype.getLatestVersion = function (uri) {
        var version = this.versionManager.getLatestDocumentVersion(uri);
        return Promise.resolve(version);
    };
    /**
     * Logs a message
     * @param message - message text
     * @param severity - message severity
     * @param component - component name
     * @param subcomponent - sub-component name
     */
    AbstractClientConnection.prototype.log = function (message, severity, component, subcomponent) {
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
    /**
     * Logs a message
     * @param message - message text
     * @param severity - message severity
     * @param component - component name
     * @param subcomponent - sub-component name
     */
    AbstractClientConnection.prototype.internalLog = function (message, severity, component, subcomponent) {
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
        if (severity === typeInterfaces_1.MessageSeverity.WARNING) {
            console.warn(toLog);
        }
        else if (severity === typeInterfaces_1.MessageSeverity.ERROR) {
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
    AbstractClientConnection.prototype.debug = function (message, component, subcomponent) {
        this.log(message, typeInterfaces_1.MessageSeverity.DEBUG, component, subcomponent);
    };
    /**
     * Logs a DEBUG_DETAIL severity message.
     * @param message - message text
     * @param component - component name
     * @param subcomponent - sub-component name
     */
    AbstractClientConnection.prototype.debugDetail = function (message, component, subcomponent) {
        this.log(message, typeInterfaces_1.MessageSeverity.DEBUG_DETAIL, component, subcomponent);
    };
    /**
     * Logs a DEBUG_OVERVIEW severity message.
     * @param message - message text
     * @param component - component name
     * @param subcomponent - sub-component name
     */
    AbstractClientConnection.prototype.debugOverview = function (message, component, subcomponent) {
        this.log(message, typeInterfaces_1.MessageSeverity.DEBUG_OVERVIEW, component, subcomponent);
    };
    /**
     * Logs a WARNING severity message.
     * @param message - message text
     * @param component - component name
     * @param subcomponent - sub-component name
     */
    AbstractClientConnection.prototype.warning = function (message, component, subcomponent) {
        this.log(message, typeInterfaces_1.MessageSeverity.WARNING, component, subcomponent);
    };
    /**
     * Logs an ERROR severity message.
     * @param message - message text
     * @param component - component name
     * @param subcomponent - sub-component name
     */
    AbstractClientConnection.prototype.error = function (message, component, subcomponent) {
        this.log(message, typeInterfaces_1.MessageSeverity.ERROR, component, subcomponent);
    };
    /**
     * Listens to the server requests for FS path existence, answering whether
     * a particular path exists on FS.
     */
    AbstractClientConnection.prototype.onExists = function (listener) {
        this.onExistsListeners.push(listener);
    };
    /**
     * Listens to the server requests for directory contents, answering with a list
     * of files in a directory.
     */
    AbstractClientConnection.prototype.onReadDir = function (listener) {
        this.onReadDirListeners.push(listener);
    };
    /**
     * Listens to the server requests for directory check, answering whether
     * a particular path is a directory.
     */
    AbstractClientConnection.prototype.onIsDirectory = function (listener) {
        this.onIsDirectoryListeners.push(listener);
    };
    /**
     * Listens to the server requests for file contents, answering what contents file has.
     */
    AbstractClientConnection.prototype.onContent = function (listener) {
        this.onContentListeners.push(listener);
    };
    /**
     * Reports to the server the position (cursor) change on the client.
     * @param uri - document uri.
     * @param position - curtsor position, starting from 0.
     */
    AbstractClientConnection.prototype.positionChanged = function (uri, position) {
        this.send({
            type: "CHANGE_POSITION",
            payload: {
                uri: uri,
                position: position
            }
        });
    };
    /**
     * Report from the server that the new details are calculated
     * for particular document and position.
     * @param listener
     */
    AbstractClientConnection.prototype.onDetailsReport = function (listener) {
        this.onDetailsReportListeners.push(listener);
    };
    /**
     * Executes the specified details action.
     * @param uri - document uri
     * @param actionID - ID of the action to execute.
     * @param position - optional position in the document.
     * If not provided, the last reported by positionChanged method will be used.
     */
    AbstractClientConnection.prototype.executeDetailsAction = function (uri, actionID, position) {
        return this.sendWithResponse({
            type: "EXECUTE_DETAILS_ACTION",
            payload: {
                uri: uri,
                position: position,
                actionId: actionID
            }
        });
    };
    /**
     * Calculates the list of executable actions avilable in the current context.
     *
     * @param uri - document uri.
     * @param position - optional position in the document.
     * If not provided, the last reported by positionChanged method will be used.
     */
    AbstractClientConnection.prototype.calculateEditorContextActions = function (uri, position) {
        return this.sendWithResponse({
            type: "CALCULATE_ACTIONS",
            payload: {
                uri: uri,
                position: position
            }
        });
    };
    /**
     * Calculates the list of all available executable actions.
     */
    AbstractClientConnection.prototype.allAvailableActions = function () {
        return this.sendWithResponse({
            type: "ALL_ACTIONS",
            payload: {}
        });
    };
    /**
     * Executes the specified action. If action has UI, causes a consequent
     * server->client UI message resulting in onDisplayActionUI listener call.
     * @param uri - document uri
     * @param action - action to execute.
     * @param position - optional position in the document.
     * If not provided, the last reported by positionChanged method will be used.
     */
    AbstractClientConnection.prototype.executeContextAction = function (uri, action, position) {
        return this.sendWithResponse({
            type: "EXECUTE_ACTION",
            payload: {
                uri: uri,
                position: position,
                actionId: action.id
            }
        });
    };
    /**
     * Executes the specified action. If action has UI, causes a consequent
     * server->client UI message resulting in onDisplayActionUI listener call.
     * @param uri - document uri
     * @param actionID - actionID to execute.
     * @param position - optional position in the document.
     * If not provided, the last reported by positionChanged method will be used.
     */
    AbstractClientConnection.prototype.executeContextActionByID = function (uri, actionID, position) {
        return this.sendWithResponse({
            type: "EXECUTE_ACTION",
            payload: {
                uri: uri,
                position: position,
                actionId: actionID
            }
        });
    };
    /**
     * Adds a listener to display action UI.
     * @param listener - accepts UI display request, should result in a promise
     * returning final UI state to be transferred to the server.
     */
    AbstractClientConnection.prototype.onDisplayActionUI = function (listener) {
        this.onDisplayActionUIListeners.push(listener);
    };
    AbstractClientConnection.prototype.DISPLAY_ACTION_UI = function (uiDisplayRequest) {
        if (!this.onDisplayActionUIListeners) {
            return Promise.reject(new Error("No handler for DISPLAY_ACTION_UI"));
        }
        return this.onDisplayActionUIListeners[0](uiDisplayRequest);
    };
    return AbstractClientConnection;
}(messageDispatcher_1.MessageDispatcher));
exports.AbstractClientConnection = AbstractClientConnection;
//# sourceMappingURL=abstractClient.js.map