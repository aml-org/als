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
var typeInterfaces_1 = require("../../common/typeInterfaces");
var connectionsImpl_1 = require("../../server/core/connectionsImpl");
var utils = require("../../common/utils");
var abstractServer_1 = require("../common/server/abstractServer");
var fs = require("fs");
var ExtendedConnection = /** @class */ (function (_super) {
    __extends(ExtendedConnection, _super);
    function ExtendedConnection(vsCodeConnection) {
        var _this = _super.call(this, "ExtendedConnection") || this;
        _this.vsCodeConnection = vsCodeConnection;
        return _this;
    }
    ExtendedConnection.prototype.sendMessage = function (message) {
        //require('fs').writeFileSync("/Users/dreamflyer/Desktop/rls/raml-language-server/src/entryPoints/common/messageDispatcher2.txt", JSON.stringify(message));
        this.vsCodeConnection.sendRequest("CUSTOM_REQUEST", message);
    };
    ExtendedConnection.prototype.exists = function (path) {
        return new Promise(function (resolve) {
            fs.exists(path, function (result) { resolve(result); });
        });
    };
    ExtendedConnection.prototype.readDir = function (path) {
        return new Promise(function (resolve) {
            fs.readdir(path, function (err, result) { resolve(result); });
        });
    };
    ExtendedConnection.prototype.isDirectory = function (path) {
        return new Promise(function (resolve) {
            fs.stat(path, function (err, stats) { resolve(stats.isDirectory()); });
        });
    };
    ExtendedConnection.prototype.content = function (path) {
        return new Promise(function (resolve, reject) {
            fs.readFile(path, function (err, data) {
                if (err != null) {
                    return reject(err);
                }
                var content = data.toString();
                resolve(content);
            });
        });
    };
    return ExtendedConnection;
}(abstractServer_1.AbstractMSServerConnection));
var vscode_languageserver_1 = require("vscode-languageserver");
var ProxyServerConnection = /** @class */ (function (_super) {
    __extends(ProxyServerConnection, _super);
    function ProxyServerConnection(vsCodeConnection) {
        var _this = _super.call(this) || this;
        _this.vsCodeConnection = vsCodeConnection;
        _this.extendedConnection = new ExtendedConnection(vsCodeConnection);
        return _this;
    }
    ProxyServerConnection.prototype.listen = function () {
        var _this = this;
        // Create a simple text document manager. The text document manager
        // supports full document sync only
        this.documents = new vscode_languageserver_1.TextDocuments();
        // Make the text document manager launch on the connection
        // for open, change and close text document events
        this.documents.listen(this.vsCodeConnection);
        // The content of a text document has changed. This event is emitted
        // when the text document first opened or when its content has changed.
        this.documents.onDidChangeContent(function (change) {
            _this.debug(change.document.uri + " changed", "ProxyServerConnection");
            for (var _i = 0, _a = _this.changeDocumentListeners; _i < _a.length; _i++) {
                var listener = _a[_i];
                listener({
                    uri: change.document.uri,
                    text: change.document.getText()
                });
            }
        });
        this.vsCodeConnection.onDocumentSymbol(function (symbolParams) {
            return _this.getSymbols(symbolParams.textDocument.uri);
        });
        // This handler provides the initial list of the completion items.
        this.vsCodeConnection.onCompletion(function (textDocumentPosition) {
            return _this.getCompletion(textDocumentPosition.textDocument.uri, textDocumentPosition.position);
        });
        this.vsCodeConnection.onDefinition(function (textDocumentPosition) {
            return _this.openDeclaration(textDocumentPosition.textDocument.uri, textDocumentPosition.position);
        });
        this.vsCodeConnection.onReferences(function (referenceParams) {
            return _this.findReferences(referenceParams.textDocument.uri, referenceParams.position);
        });
        this.vsCodeConnection.onDocumentHighlight(function (textDocumentPosition) {
            return _this.documentHighlight(textDocumentPosition.textDocument.uri, textDocumentPosition.position);
        });
        this.vsCodeConnection.onRenameRequest(function (renameParams) {
            return _this.rename(renameParams.textDocument.uri, renameParams.position, renameParams.newName);
        });
        this.vsCodeConnection.onRequest("CUSTOM_REQUEST", function (message) {
            _this.extendedConnection.handleRecievedMessage(message);
            return Promise.resolve("OK");
        });
    };
    ProxyServerConnection.prototype.onChangePosition = function (listener) {
        this.extendedConnection.onChangePosition(listener);
    };
    ProxyServerConnection.prototype.onChangeDetailValue = function (listener) {
        this.extendedConnection.onChangeDetailValue(listener);
    };
    ProxyServerConnection.prototype.onDocumentDetails = function (listener) {
        this.extendedConnection.onDocumentDetails(listener);
    };
    ProxyServerConnection.prototype.onDocumentStructure = function (listener) {
        this.extendedConnection.onDocumentStructure(listener);
    };
    ProxyServerConnection.prototype.detailsAvailable = function (report) {
        this.extendedConnection.detailsAvailable(report);
    };
    ProxyServerConnection.prototype.structureAvailable = function (report) {
        this.extendedConnection.structureAvailable(report);
    };
    ProxyServerConnection.prototype.onSetServerConfiguration = function (listener) {
        this.extendedConnection.onSetServerConfiguration(listener);
    };
    ProxyServerConnection.prototype.onOpenDocument = function (listener) {
        _super.prototype.onOpenDocument.call(this, listener);
        this.extendedConnection.onOpenDocument(listener);
    };
    ProxyServerConnection.prototype.onChangeDocument = function (listener) {
        _super.prototype.onChangeDocument.call(this, listener);
        this.extendedConnection.onChangeDocument(listener);
    };
    ProxyServerConnection.prototype.onAllEditorContextActions = function (listener) {
        this.extendedConnection.onAllEditorContextActions(listener);
    };
    ProxyServerConnection.prototype.onCalculateEditorContextActions = function (listener) {
        this.extendedConnection.onCalculateEditorContextActions(listener);
    };
    ProxyServerConnection.prototype.onExecuteContextAction = function (listener) {
        this.extendedConnection.onExecuteContextAction(listener);
    };
    ProxyServerConnection.prototype.onExecuteDetailsAction = function (listener) {
        this.extendedConnection.onExecuteDetailsAction(listener);
    };
    ProxyServerConnection.prototype.displayActionUI = function (data) {
        return this.extendedConnection.displayActionUI(data);
    };
    /**
     * Reports latest validation results
     * @param report
     */
    ProxyServerConnection.prototype.validated = function (report) {
        this.debug("HERE WE HAVE FRESH NEW VALIDATION REPORT for uri: " + report.pointOfViewUri, "ProxyServerConnection", "validated");
        this.debugDetail("Number of issues: " + (report.issues != null ? report.issues.length : 0), "ProxyServerConnection", "validated");
        var diagnostics = [];
        if (report && report.issues) {
            for (var _i = 0, _a = report.issues; _i < _a.length; _i++) {
                var issue = _a[_i];
                this.debugDetail("Issue text: " + issue.text, "ProxyServerConnection", "validated");
                var originalIssueUri = issue.filePath;
                if (!originalIssueUri) {
                    originalIssueUri = report.pointOfViewUri;
                }
                this.debugDetail("Issue original uri: " + originalIssueUri, "ProxyServerConnection", "validated");
                var issueUri = utils.transformUriToOriginalFormat(report.pointOfViewUri, originalIssueUri);
                this.debugDetail("Issue uri: " + issueUri, "ProxyServerConnection", "validated");
                var document_1 = this.documents.get(issueUri);
                this.debugDetail("Document found: " + (document_1 != null ? "true" : "false"), "ProxyServerConnection", "validated");
                var start = document_1.positionAt(issue.range.start);
                var end = document_1.positionAt(issue.range.end);
                diagnostics.push({
                    severity: issue.type === "Error" ? vscode_languageserver_1.DiagnosticSeverity.Error : vscode_languageserver_1.DiagnosticSeverity.Warning,
                    range: {
                        start: start,
                        end: end
                    },
                    message: issue.text,
                    source: "ex"
                });
                this.debugDetail("ISSUE: " + issue.text, "ProxyServerConnection", "validated");
                this.debugDetail("ISSUE, document found: " + (document_1 != null), "ProxyServerConnection", "validated");
            }
        }
        this.vsCodeConnection.sendDiagnostics({
            uri: report.pointOfViewUri,
            diagnostics: diagnostics
        });
    };
    ProxyServerConnection.prototype.getSymbols = function (uri) {
        var _this = this;
        this.debug("ServerConnection:getSymbols called for uri: " + uri, "ProxyServerConnection", "getSymbols");
        if (this.documentStructureListeners.length === 0) {
            return Promise.resolve([]);
        }
        // TODO handle many structure providers?
        var structurePromise = this.documentStructureListeners[0](uri);
        this.debugDetail("ServerConnection:getSymbols got structure promise: " + (structurePromise != null), "ProxyServerConnection", "getSymbols");
        if (!structurePromise) {
            return Promise.resolve([]);
        }
        return structurePromise.then(function (structure) {
            _this.debugDetail("ServerConnection:getSymbols got structure: " + (structure != null), "ProxyServerConnection", "getSymbols");
            if (!structure) {
                return [];
            }
            var document = _this.documents.get(uri);
            _this.debugDetail("ServerConnection:getSymbols got document: " + (document != null), "ProxyServerConnection", "getSymbols");
            if (!document) {
                return [];
            }
            var result = [];
            var _loop_1 = function (categoryName) {
                if (structure.hasOwnProperty(categoryName)) {
                    var vsKind_1 = null;
                    if (typeInterfaces_1.StructureCategories[typeInterfaces_1.StructureCategories.ResourcesCategory] === categoryName) {
                        vsKind_1 = vscode_languageserver_1.SymbolKind.Function;
                    }
                    else if (typeInterfaces_1.StructureCategories[typeInterfaces_1.StructureCategories.ResourceTypesAndTraitsCategory] === categoryName) {
                        vsKind_1 = vscode_languageserver_1.SymbolKind.Interface;
                    }
                    else if (typeInterfaces_1.StructureCategories[typeInterfaces_1.StructureCategories.SchemasAndTypesCategory] === categoryName) {
                        vsKind_1 = vscode_languageserver_1.SymbolKind.Class;
                    }
                    else if (typeInterfaces_1.StructureCategories[typeInterfaces_1.StructureCategories.OtherCategory] === categoryName) {
                        vsKind_1 = vscode_languageserver_1.SymbolKind.Constant;
                    }
                    var topLevelNode = structure[categoryName];
                    var items = topLevelNode.children;
                    if (items) {
                        result = result.concat(items.map(function (item) {
                            var start = document.positionAt(item.start);
                            var end = document.positionAt(item.end);
                            _this.debugDetail("ServerConnection:getSymbols converting item " + item.text, "ProxyServerConnection", "getSymbols");
                            var symbolInfo = {
                                name: item.text,
                                kind: vsKind_1,
                                location: {
                                    uri: uri,
                                    range: {
                                        start: start,
                                        end: end
                                    }
                                }
                            };
                            return symbolInfo;
                        }));
                    }
                }
            };
            for (var categoryName in structure) {
                _loop_1(categoryName);
            }
            return result;
        });
    };
    ProxyServerConnection.prototype.getCompletion = function (uri, position) {
        var _this = this;
        this.debug("getCompletion called for uri: " + uri, "ProxyServerConnection", "getCompletion");
        if (this.documentCompletionListeners.length === 0) {
            return Promise.resolve({
                isIncomplete: true,
                items: []
            });
        }
        var document = this.documents.get(uri);
        this.debugDetail("got document: " + (document != null), "ProxyServerConnection", "getCompletion");
        if (!document) {
            return Promise.resolve({
                isIncomplete: true,
                items: []
            });
        }
        var offset = document.offsetAt(position);
        this.debugDetail("offset is: " + offset, "ProxyServerConnection", "getCompletion");
        var promises = [];
        for (var _i = 0, _a = this.documentCompletionListeners; _i < _a.length; _i++) {
            var listener = _a[_i];
            this.debugDetail("Calling a listener", "ProxyServerConnection", "getCompletion");
            var listenerResult = listener(uri, offset);
            if (listenerResult) {
                promises.push(listenerResult);
            }
        }
        return Promise.all(promises).then(function (resolvedResults) {
            var result = [];
            _this.debugDetail("Got suggestion promises resolved: "
                + (resolvedResults ? resolvedResults.length : 0), "ProxyServerConnection", "getCompletion");
            for (var _i = 0, resolvedResults_1 = resolvedResults; _i < resolvedResults_1.length; _i++) {
                var currentPromiseResult = resolvedResults_1[_i];
                var suggestions = currentPromiseResult;
                _this.debugDetail("Got suggestions: " + (suggestions ? suggestions.length : 0), "ProxyServerConnection", "getCompletion");
                for (var _a = 0, suggestions_1 = suggestions; _a < suggestions_1.length; _a++) {
                    var suggestion = suggestions_1[_a];
                    var text = suggestion.text || suggestion.displayText;
                    _this.debugDetail("adding suggestion: " + text, "ProxyServerConnection", "getCompletion");
                    text = _this.removeCompletionPreviousLineIndentation(text);
                    if (suggestion.extra && suggestion.displayText) {
                        result.push({
                            label: suggestion.displayText,
                            insertText: suggestion.extra + text,
                            kind: vscode_languageserver_1.CompletionItemKind.Text
                        });
                    }
                    else {
                        result.push({
                            label: text,
                            kind: vscode_languageserver_1.CompletionItemKind.Text
                        });
                    }
                }
            }
            return {
                isIncomplete: true,
                items: result
            };
        });
    };
    ProxyServerConnection.prototype.openDeclaration = function (uri, position) {
        var _this = this;
        this.debug("openDeclaration called for uri: " + uri, "ProxyServerConnection", "openDeclaration");
        if (this.openDeclarationListeners.length === 0) {
            return Promise.resolve([]);
        }
        var document = this.documents.get(uri);
        this.debugDetail("got document: " + (document != null), "ProxyServerConnection", "openDeclaration");
        if (!document) {
            return Promise.resolve([]);
        }
        var offset = document.offsetAt(position);
        return this.openDeclarationListeners[0](uri, offset).then(function (locations) {
            var result = [];
            _this.debugDetail("Got locations: " + (locations ? locations.length : 0), "ProxyServerConnection", "openDeclaration");
            if (locations) {
                for (var _i = 0, locations_1 = locations; _i < locations_1.length; _i++) {
                    var location_1 = locations_1[_i];
                    var start = document.positionAt(location_1.range.start);
                    var end = document.positionAt(location_1.range.end);
                    result.push({
                        uri: location_1.uri,
                        range: {
                            start: start,
                            end: end
                        }
                    });
                }
            }
            return result;
        });
    };
    ProxyServerConnection.prototype.findReferences = function (uri, position) {
        var _this = this;
        this.debug("findReferences called for uri: " + uri, "ProxyServerConnection", "findReferences");
        if (this.findreferencesListeners.length === 0) {
            return Promise.resolve([]);
        }
        var document = this.documents.get(uri);
        this.debugDetail("got document: " + (document != null), "ProxyServerConnection", "findReferences");
        if (!document) {
            return Promise.resolve([]);
        }
        var offset = document.offsetAt(position);
        return this.findreferencesListeners[0](uri, offset).then(function (locations) {
            var result = [];
            _this.debugDetail("Got locations: " + (locations ? locations.length : 0), "ProxyServerConnection", "findReferences");
            if (locations) {
                for (var _i = 0, locations_2 = locations; _i < locations_2.length; _i++) {
                    var location_2 = locations_2[_i];
                    var start = document.positionAt(location_2.range.start);
                    var end = document.positionAt(location_2.range.end);
                    result.push({
                        uri: location_2.uri,
                        range: {
                            start: start,
                            end: end
                        }
                    });
                }
            }
            _this.debugDetail("Returning: " + JSON.stringify(result), "ProxyServerConnection", "findReferences");
            return result;
        });
    };
    /**
     * Returns whether path/url exists.
     * @param fullPath
     */
    ProxyServerConnection.prototype.exists = function (path) {
        return new Promise(function (resolve) {
            fs.exists(path, function (result) { resolve(result); });
        });
    };
    /**
     * Returns directory content list.
     * @param fullPath
     */
    ProxyServerConnection.prototype.readDir = function (path) {
        return new Promise(function (resolve) {
            fs.readdir(path, function (err, result) { resolve(result); });
        });
    };
    /**
     * Returns whether path/url represents a directory
     * @param path
     */
    ProxyServerConnection.prototype.isDirectory = function (path) {
        return new Promise(function (resolve) {
            fs.stat(path, function (err, stats) { resolve(stats.isDirectory()); });
        });
    };
    /**
     * File contents by full path/url.
     * @param path
     */
    ProxyServerConnection.prototype.content = function (path) {
        return new Promise(function (resolve, reject) {
            fs.readFile(path, function (err, data) {
                if (err != null) {
                    return reject(err);
                }
                var content = data.toString();
                resolve(content);
            });
        });
    };
    /**
     * Logs a message
     * @param message - message text
     * @param severity - message severity
     * @param component - component name
     * @param subcomponent - sub-component name
     */
    ProxyServerConnection.prototype.log = function (message, severity, component, subcomponent) {
        var filtered = utils.filterLogMessage({
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
    ProxyServerConnection.prototype.internalLog = function (message, severity, component, subcomponent) {
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
    ProxyServerConnection.prototype.debug = function (message, component, subcomponent) {
        this.log(message, typeInterfaces_1.MessageSeverity.DEBUG, component, subcomponent);
    };
    /**
     * Logs a DEBUG_DETAIL severity message.
     * @param message - message text
     * @param component - component name
     * @param subcomponent - sub-component name
     */
    ProxyServerConnection.prototype.debugDetail = function (message, component, subcomponent) {
        this.log(message, typeInterfaces_1.MessageSeverity.DEBUG_DETAIL, component, subcomponent);
    };
    /**
     * Logs a DEBUG_OVERVIEW severity message.
     * @param message - message text
     * @param component - component name
     * @param subcomponent - sub-component name
     */
    ProxyServerConnection.prototype.debugOverview = function (message, component, subcomponent) {
        this.log(message, typeInterfaces_1.MessageSeverity.DEBUG_OVERVIEW, component, subcomponent);
    };
    /**
     * Logs a WARNING severity message.
     * @param message - message text
     * @param component - component name
     * @param subcomponent - sub-component name
     */
    ProxyServerConnection.prototype.warning = function (message, component, subcomponent) {
        this.log(message, typeInterfaces_1.MessageSeverity.WARNING, component, subcomponent);
    };
    /**
     * Logs an ERROR severity message.
     * @param message - message text
     * @param component - component name
     * @param subcomponent - sub-component name
     */
    ProxyServerConnection.prototype.error = function (message, component, subcomponent) {
        this.log(message, typeInterfaces_1.MessageSeverity.ERROR, component, subcomponent);
    };
    /**
     * Sets connection logger configuration.
     * @param loggerSettings
     */
    ProxyServerConnection.prototype.setLoggerConfiguration = function (loggerSettings) {
        this.loggerSettings = loggerSettings;
        this.extendedConnection.setLoggerConfiguration(loggerSettings);
    };
    ProxyServerConnection.prototype.documentHighlight = function (uri, position) {
        var _this = this;
        this.debug("documentHighlight called for uri: " + uri, "ProxyServerConnection", "documentHighlight");
        if (this.markOccurrencesListeners.length === 0) {
            return Promise.resolve([]);
        }
        var document = this.documents.get(uri);
        this.debugDetail("got document: " + (document != null), "ProxyServerConnection", "documentHighlight");
        if (!document) {
            return Promise.resolve([]);
        }
        var offset = document.offsetAt(position);
        return this.markOccurrencesListeners[0](uri, offset).then(function (locations) {
            var result = [];
            _this.debugDetail("Got locations: " + (locations ? locations.length : 0), "ProxyServerConnection", "documentHighlight");
            if (locations) {
                for (var _i = 0, locations_3 = locations; _i < locations_3.length; _i++) {
                    var location_3 = locations_3[_i];
                    var start = document.positionAt(location_3.start);
                    var end = document.positionAt(location_3.end);
                    result.push({
                        kind: 1,
                        range: {
                            start: start,
                            end: end
                        }
                    });
                }
            }
            return result;
        });
    };
    ProxyServerConnection.prototype.rename = function (uri, position, newName) {
        this.debug("rename called for uri: " + uri + " and name " + newName, "ProxyServerConnection", "rename");
        if (this.renameListeners.length === 0) {
            return null;
        }
        var document = this.documents.get(uri);
        this.debugDetail("got document: " + (document != null), "ProxyServerConnection", "rename");
        if (!document) {
            return null;
        }
        var offset = document.offsetAt(position);
        var uriToChanges = {};
        // TODO same for document versions when they are introduced
        for (var _i = 0, _a = this.renameListeners; _i < _a.length; _i++) {
            var listener = _a[_i];
            var changedDocuments = listener(uri, offset, newName);
            this.debugDetail("Got changed documents: " +
                (changedDocuments ? changedDocuments.length : 0), "ProxyServerConnection", "rename");
            if (changedDocuments) {
                var _loop_2 = function (changedDocument) {
                    this_1.debugDetail("Converting changes in a document: " +
                        changedDocument.uri, "ProxyServerConnection", "rename");
                    var existingDocument = this_1.documents.get(changedDocument.uri);
                    if (!existingDocument) {
                        this_1.error("Can not apply a full-content change of document " +
                            changedDocument.uri + " because its previous version is not found " +
                            "in the list of documents");
                        return "continue";
                    }
                    this_1.debugDetail("Found existing document: " +
                        (existingDocument ? "true" : "false"), "ProxyServerConnection", "rename");
                    var existingChanges = uriToChanges[changedDocument.uri];
                    this_1.debugDetail("Found existing changes: " +
                        (existingChanges ? "true" : "false"), "ProxyServerConnection", "rename");
                    if (!existingChanges) {
                        existingChanges = [];
                    }
                    var editsToApply = [];
                    if (changedDocument.text) {
                        this_1.debugDetail("Changed document has text set.", "ProxyServerConnection", "rename");
                        var previousText = existingDocument.getText();
                        this_1.debugDetail("Old text:\n" + previousText, "ProxyServerConnection", "rename");
                        var previousTextLength = previousText.length;
                        var startPosition = existingDocument.positionAt(0);
                        var endPosition = previousTextLength === 0 ?
                            existingDocument.positionAt(0) :
                            existingDocument.positionAt(previousTextLength - 1);
                        this_1.debugDetail("Edit start position: [" + startPosition.line +
                            " ," + startPosition.character + "]", "ProxyServerConnection", "rename");
                        this_1.debugDetail("Edit end position: [" + endPosition.line +
                            " ," + endPosition.character + "]", "ProxyServerConnection", "rename");
                        this_1.debugDetail("Edit text:\n" + changedDocument.text, "ProxyServerConnection", "rename");
                        editsToApply.push({
                            range: {
                                start: startPosition,
                                end: endPosition
                            },
                            newText: changedDocument.text
                        });
                    }
                    else if (changedDocument.textEdits) {
                        this_1.debugDetail("Changed document has edits set.", "ProxyServerConnection", "rename");
                        editsToApply = changedDocument.textEdits.map(function (currentEdit) {
                            var startPosition = existingDocument.positionAt(currentEdit.range.start);
                            var endPosition = existingDocument.positionAt(currentEdit.range.end);
                            return {
                                range: {
                                    start: startPosition,
                                    end: endPosition
                                },
                                newText: currentEdit.text
                            };
                        });
                    }
                    var newChanges = existingChanges.concat(editsToApply);
                    uriToChanges[changedDocument.uri] = newChanges;
                    this_1.debugDetail("Saving changes for uri " + changedDocument.uri + ": " +
                        uriToChanges[changedDocument.uri].length, "ProxyServerConnection", "rename");
                };
                var this_1 = this;
                for (var _b = 0, changedDocuments_1 = changedDocuments; _b < changedDocuments_1.length; _b++) {
                    var changedDocument = changedDocuments_1[_b];
                    _loop_2(changedDocument);
                }
            }
        }
        var uriChanges = [];
        for (var currentUri in uriToChanges) {
            if (uriToChanges.hasOwnProperty(currentUri)) {
                uriChanges.push({
                    textDocument: {
                        uri: currentUri,
                        version: 0
                    },
                    edits: uriToChanges[currentUri]
                });
            }
        }
        var result = {
            // changes: uriChanges
            documentChanges: uriChanges
        };
        this.debugDetail("Returning", "ProxyServerConnection", "rename");
        return result;
    };
    ProxyServerConnection.prototype.removeCompletionPreviousLineIndentation = function (originalText) {
        var lastNewLineIndex = originalText.lastIndexOf("\n");
        if (lastNewLineIndex === -1 || lastNewLineIndex === originalText.length - 1) {
            return originalText;
        }
        var textAfterLastNewLine = originalText.substring(lastNewLineIndex + 1);
        if (textAfterLastNewLine.trim() !== "") {
            return originalText;
        }
        return originalText.substring(0, lastNewLineIndex + 1) + "  ";
    };
    return ProxyServerConnection;
}(connectionsImpl_1.AbstractServerConnection));
exports.ProxyServerConnection = ProxyServerConnection;
// function asWorkspaceEdit(item) {
//     if (!item) {
//         return undefined;
//     }
//     let result = new code.WorkspaceEdit();
//     item.changes.forEach(change => {
//         result.set(_uriConverter(change.textDocument.uri), asTextEdits(change.edits));
//     });
//     return result;
// }
//# sourceMappingURL=serverConnection.js.map