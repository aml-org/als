import {
    IServerConnection
} from "../../server/core/connections";

import {
    IChangedDocument,
    ILocation,
    ILoggerSettings,
    IOpenedDocument,
    IRange,
    IStructureReport,
    ITextEdit,
    IValidationIssue,
    IValidationReport,
    MessageSeverity,
    StructureCategories,
    StructureNodeJSON,
    Suggestion, IDetailsReport
} from "../../common/typeInterfaces";

import {
    AbstractServerConnection
} from "../../server/core/connectionsImpl";

import utils = require("../../common/utils");

import {
    AbstractMSServerConnection
} from "../common/server/abstractServer";

import fs = require("fs");

class ExtendedConnection extends AbstractMSServerConnection {

    constructor(private vsCodeConnection: IConnection) {
        super("ExtendedConnection");
    }

    public sendMessage(message: ProtocolMessage<MessageToClientType>): void {
        //require('fs').writeFileSync("/Users/dreamflyer/Desktop/rls/raml-language-server/src/entryPoints/common/messageDispatcher2.txt", JSON.stringify(message));

        this.vsCodeConnection.sendRequest("CUSTOM_REQUEST", message);
    }

    public exists(path: string): Promise<boolean> {
        return new Promise((resolve) => {
            fs.exists(path, (result) => {resolve(result); });
        });
    }

    public readDir(path: string): Promise<string[]> {
        return new Promise((resolve) => {
            fs.readdir(path, (err, result) => {resolve(result); });
        });
    }

    public isDirectory(path: string): Promise<boolean> {
        return new Promise((resolve) => {
            fs.stat(path, (err, stats) => {resolve(stats.isDirectory()); });
        });
    }

    public content(path: string): Promise<string> {
        return new Promise(function(resolve, reject) {

            fs.readFile(path, (err, data) => {
                if (err != null) {
                    return reject(err);
                }

                const content = data.toString();
                resolve(content);
            });
        });
    }
}

import {
    CompletionItem, CompletionItemKind,
    CompletionList, createConnection, Diagnostic,
    DiagnosticSeverity, DocumentHighlight, DocumentSymbolParams, IConnection,
    InitializeParams, InitializeResult, IPCMessageReader,
    IPCMessageWriter, Location, Position, Range,
    ReferenceParams, RenameParams, SymbolInformation, SymbolKind,
    TextDocument, TextDocumentEdit, TextDocumentPositionParams,
    TextDocuments, TextDocumentSyncKind, TextEdit, WorkspaceEdit
} from "vscode-languageserver";
import {ProtocolMessage, MessageToClientType} from "../common/protocol";

export class ProxyServerConnection extends AbstractServerConnection implements IServerConnection {

    private loggerSettings: ILoggerSettings;
    private documents: TextDocuments;
    
    private extendedConnection: ExtendedConnection;

    constructor(private vsCodeConnection: IConnection) {
        super();

        this.extendedConnection = new ExtendedConnection(vsCodeConnection);
    }

    public listen(): void {

        // Create a simple text document manager. The text document manager
        // supports full document sync only
        this.documents = new TextDocuments();

        // Make the text document manager launch on the connection
        // for open, change and close text document events
        this.documents.listen(this.vsCodeConnection);

        // The content of a text document has changed. This event is emitted
        // when the text document first opened or when its content has changed.
        this.documents.onDidChangeContent((change) => {

            this.debug(`${change.document.uri} changed`, "ProxyServerConnection");

            for (const listener of this.changeDocumentListeners) {
                listener({
                    uri: change.document.uri,
                    text: change.document.getText()
                });
            }
        });

        this.vsCodeConnection.onDocumentSymbol((symbolParams: DocumentSymbolParams) => {

            return this.getSymbols(symbolParams.textDocument.uri);
        });

        // This handler provides the initial list of the completion items.
        this.vsCodeConnection.onCompletion(
            (textDocumentPosition: TextDocumentPositionParams): Promise<CompletionList> => {

            return this.getCompletion(textDocumentPosition.textDocument.uri, textDocumentPosition.position);
        });

        this.vsCodeConnection.onDefinition((textDocumentPosition: TextDocumentPositionParams): Promise<Location[]> => {

            return this.openDeclaration(textDocumentPosition.textDocument.uri, textDocumentPosition.position);
        });

        this.vsCodeConnection.onReferences((referenceParams: ReferenceParams): Promise<Location[]> => {

            return this.findReferences(referenceParams.textDocument.uri, referenceParams.position);
        });

        this.vsCodeConnection.onDocumentHighlight(
            (textDocumentPosition: TextDocumentPositionParams): Promise<DocumentHighlight[]> => {

            return this.documentHighlight(textDocumentPosition.textDocument.uri, textDocumentPosition.position);
        });

        this.vsCodeConnection.onRenameRequest((renameParams: RenameParams): WorkspaceEdit => {

            return this.rename(renameParams.textDocument.uri,
                renameParams.position, renameParams.newName);
        });

        this.vsCodeConnection.onRequest("CUSTOM_REQUEST", (message) => {
            this.extendedConnection.handleRecievedMessage(message);
            
            return Promise.resolve("OK");
        })
    }

    public onChangePosition(listener) {
        this.extendedConnection.onChangePosition(listener);
    }

    public onChangeDetailValue(listener) {
        this.extendedConnection.onChangeDetailValue(listener);
    }

    public onDocumentDetails(listener) {
        this.extendedConnection.onDocumentDetails(listener);
    }

    public onDocumentStructure(listener) {
        this.extendedConnection.onDocumentStructure(listener);
    }
    
    public detailsAvailable(report: IDetailsReport) {
        this.extendedConnection.detailsAvailable(report);
    }

    public structureAvailable(report: IStructureReport) {
        this.extendedConnection.structureAvailable(report);
    }
    
    public onSetServerConfiguration(listener) {
        this.extendedConnection.onSetServerConfiguration(listener);
    }

    public onOpenDocument(listener) {
        super.onOpenDocument(listener);
        this.extendedConnection.onOpenDocument(listener);
    }

    public onChangeDocument(listener) {
        super.onChangeDocument(listener);
        this.extendedConnection.onChangeDocument(listener);
    }
    
    public onAllEditorContextActions(listener) {
        this.extendedConnection.onAllEditorContextActions(listener);
    }

    public onCalculateEditorContextActions(listener) {
        this.extendedConnection.onCalculateEditorContextActions(listener);
    }

    public onExecuteContextAction(listener) {
        this.extendedConnection.onExecuteContextAction(listener);
    }

    public onExecuteDetailsAction(listener) {        
        this.extendedConnection.onExecuteDetailsAction(listener);
    }

    public displayActionUI(data) {
        return this.extendedConnection.displayActionUI(data);
    }

    /**
     * Reports latest validation results
     * @param report
     */
    public validated(report: IValidationReport): void {
        this.debug("HERE WE HAVE FRESH NEW VALIDATION REPORT for uri: " + report.pointOfViewUri,
            "ProxyServerConnection", "validated");
        this.debugDetail("Number of issues: " + (report.issues != null ? report.issues.length : 0),
            "ProxyServerConnection", "validated");

        const diagnostics: Diagnostic[] = [];

        if (report && report.issues) {
            for (const issue of report.issues) {
                this.debugDetail("Issue text: " + issue.text,
                    "ProxyServerConnection", "validated");

                let originalIssueUri = issue.filePath;
                if (!originalIssueUri) {
                    originalIssueUri = report.pointOfViewUri;
                }

                this.debugDetail("Issue original uri: " + originalIssueUri,
                    "ProxyServerConnection", "validated");

                const issueUri = utils.transformUriToOriginalFormat(report.pointOfViewUri, originalIssueUri);
                this.debugDetail("Issue uri: " + issueUri,
                    "ProxyServerConnection", "validated");

                const document = this.documents.get(issueUri);
                this.debugDetail("Document found: " + (document != null ? "true" : "false"),
                    "ProxyServerConnection", "validated");

                const start = document.positionAt(issue.range.start);
                const end = document.positionAt(issue.range.end);

                diagnostics.push({
                    severity: issue.type === "Error" ? DiagnosticSeverity.Error : DiagnosticSeverity.Warning,
                    range: {
                        start,
                        end
                    },
                    message: issue.text,
                    source: "ex"
                });

                this.debugDetail("ISSUE: " + issue.text,
                    "ProxyServerConnection", "validated");
                this.debugDetail("ISSUE, document found: " + (document != null),
                    "ProxyServerConnection", "validated");
            }
        }

        this.vsCodeConnection.sendDiagnostics({
            uri: report.pointOfViewUri,
            diagnostics
        });
    }

    public getSymbols(uri: string): Promise<SymbolInformation[]> {
        this.debug("ServerConnection:getSymbols called for uri: " + uri,
            "ProxyServerConnection", "getSymbols");

        if (this.documentStructureListeners.length === 0) {
            return Promise.resolve([]);
        }

        // TODO handle many structure providers?
        const structurePromise: Promise<{[categoryName: string]: StructureNodeJSON}> =
            this.documentStructureListeners[0](uri);

        this.debugDetail("ServerConnection:getSymbols got structure promise: " + (structurePromise != null),
            "ProxyServerConnection", "getSymbols");

        if (!structurePromise) {
            return Promise.resolve([]);
        }

        return structurePromise.then((structure) => {
            this.debugDetail("ServerConnection:getSymbols got structure: " + (structure != null),
                "ProxyServerConnection", "getSymbols");

            if (!structure) {
                return [];
            }

            const document = this.documents.get(uri);
            this.debugDetail("ServerConnection:getSymbols got document: " + (document != null),
                "ProxyServerConnection", "getSymbols");
            if (!document) {
                return [];
            }

            let result: SymbolInformation[] = [];
            for (const categoryName in structure) {
                if (structure.hasOwnProperty(categoryName)) {
                    let vsKind: SymbolKind = null;

                    if (StructureCategories[StructureCategories.ResourcesCategory] === categoryName) {
                        vsKind = SymbolKind.Function;
                    } else if (
                        StructureCategories[StructureCategories.ResourceTypesAndTraitsCategory] === categoryName) {
                        vsKind = SymbolKind.Interface;
                    } else if (StructureCategories[StructureCategories.SchemasAndTypesCategory] === categoryName) {
                        vsKind = SymbolKind.Class;
                    } else if (StructureCategories[StructureCategories.OtherCategory] === categoryName) {
                        vsKind = SymbolKind.Constant;
                    }

                    const topLevelNode = structure[categoryName];
                    const items = topLevelNode.children;
                    if (items) {
                        result = result.concat(items.map((item) => {
                            const start = document.positionAt(item.start);
                            const end = document.positionAt(item.end);
                            this.debugDetail("ServerConnection:getSymbols converting item " + item.text,
                                "ProxyServerConnection", "getSymbols");

                            const symbolInfo: SymbolInformation = {
                                name: item.text,
                                kind: vsKind,
                                location: {
                                    uri,
                                    range: {
                                        start,
                                        end
                                    }
                                }
                            };
                            return symbolInfo;
                        }));
                    }
                }
            }

            return result;
        });
    }

    public getCompletion(uri: string, position: Position): Promise<CompletionList> {
        this.debug("getCompletion called for uri: " + uri,
            "ProxyServerConnection", "getCompletion");

        if (this.documentCompletionListeners.length === 0) {
            return Promise.resolve({
                isIncomplete: true,
                items: []
            });
        }

        const document = this.documents.get(uri);
        this.debugDetail("got document: " + (document != null),
            "ProxyServerConnection", "getCompletion");
        if (!document) {
            return Promise.resolve({
                isIncomplete: true,
                items: []
            });
        }

        const offset = document.offsetAt(position);

        this.debugDetail("offset is: " + offset,
            "ProxyServerConnection", "getCompletion");

        const promises = [];
        for (const listener of this.documentCompletionListeners) {
            this.debugDetail("Calling a listener",
                "ProxyServerConnection", "getCompletion");

            const listenerResult = listener(uri, offset);
            if (listenerResult) {
                promises.push(listenerResult);
            }
        }

        return Promise.all(promises).then((resolvedResults) => {

            const result: CompletionItem[]  = [];

            this.debugDetail("Got suggestion promises resolved: "
                + (resolvedResults ? resolvedResults.length : 0),
                "ProxyServerConnection", "getCompletion");

            for (const currentPromiseResult of resolvedResults) {

                const suggestions = currentPromiseResult;

                this.debugDetail("Got suggestions: " + (suggestions ? suggestions.length : 0),
                    "ProxyServerConnection", "getCompletion");

                for (const suggestion of suggestions) {
                    let text = suggestion.text || suggestion.displayText;

                    this.debugDetail("adding suggestion: " + text,
                        "ProxyServerConnection", "getCompletion");

                    text = this.removeCompletionPreviousLineIndentation(text);

                    if(suggestion.extra && suggestion.displayText) {
                        result.push({
                            label: suggestion.displayText,
                            insertText: suggestion.extra + text,
                            kind: CompletionItemKind.Text
                        });
                    } else {
                        result.push({
                            label: text,
                            kind: CompletionItemKind.Text
                        });
                    }
                }
            }

            return {
                isIncomplete: true,
                items: result
            };
        });
    }

    public openDeclaration(uri: string, position: Position): Promise<Location[]> {
        this.debug("openDeclaration called for uri: " + uri,
            "ProxyServerConnection", "openDeclaration");

        if (this.openDeclarationListeners.length === 0) {
            return Promise.resolve([]);
        }

        const document = this.documents.get(uri);
        this.debugDetail("got document: " + (document != null),
            "ProxyServerConnection", "openDeclaration");
        if (!document) {
            return Promise.resolve([]);
        }

        const offset = document.offsetAt(position);

        return this.openDeclarationListeners[0](uri, offset).then((locations) => {
            const result: Location[]  = [];

            this.debugDetail("Got locations: " + (locations ? locations.length : 0),
                "ProxyServerConnection", "openDeclaration");

            if (locations) {
                for (const location of locations) {
                    const start = document.positionAt(location.range.start);
                    const end = document.positionAt(location.range.end);
                    result.push({
                        uri: location.uri,
                        range: {
                            start,
                            end
                        }
                    });
                }
            }

            return result;
        });

    }

    public findReferences(uri: string, position: Position): Promise<Location[]> {
        this.debug("findReferences called for uri: " + uri,
            "ProxyServerConnection", "findReferences");

        if (this.findreferencesListeners.length === 0) {
            return Promise.resolve([]);
        }

        const document = this.documents.get(uri);
        this.debugDetail("got document: " + (document != null),
            "ProxyServerConnection", "findReferences");
        if (!document) {
            return Promise.resolve([]);
        }

        const offset = document.offsetAt(position);

        return this.findreferencesListeners[0](uri, offset).then((locations) => {
            const result: Location[]  = [];

            this.debugDetail("Got locations: " + (locations ? locations.length : 0),
                "ProxyServerConnection", "findReferences");

            if (locations) {
                for (const location of locations) {
                    const start = document.positionAt(location.range.start);
                    const end = document.positionAt(location.range.end);
                    result.push({
                        uri: location.uri,
                        range: {
                            start,
                            end
                        }
                    });
                }
            }

            this.debugDetail("Returning: " + JSON.stringify(result),
                "ProxyServerConnection", "findReferences");

            return result;
        });

    }

    /**
     * Returns whether path/url exists.
     * @param fullPath
     */
    public exists(path: string): Promise<boolean> {
        return new Promise((resolve) => {
            fs.exists(path, (result) => {resolve(result); });
        });
    }

    /**
     * Returns directory content list.
     * @param fullPath
     */
    public readDir(path: string): Promise<string[]> {
        return new Promise((resolve) => {
            fs.readdir(path, (err, result) => {resolve(result); });
        });
    }

    /**
     * Returns whether path/url represents a directory
     * @param path
     */
    public isDirectory(path: string): Promise<boolean> {
        return new Promise((resolve) => {
            fs.stat(path, (err, stats) => {resolve(stats.isDirectory()); });
        });
    }

    /**
     * File contents by full path/url.
     * @param path
     */
    public content(path: string): Promise<string> {
        return new Promise(function(resolve, reject) {

            fs.readFile(path, (err, data) => {
                if (err != null) {
                    return reject(err);
                }

                const content = data.toString();
                resolve(content);
            });
        });
    }

    /**
     * Logs a message
     * @param message - message text
     * @param severity - message severity
     * @param component - component name
     * @param subcomponent - sub-component name
     */
    public log(message: string, severity: MessageSeverity,
               component?: string, subcomponent?: string): void {

        const filtered = utils.filterLogMessage({
            message,
            severity,
            component,
            subcomponent
        }, this.loggerSettings);

        if (filtered) {
            this.internalLog(filtered.message, filtered.severity,
                filtered.component, filtered.subcomponent);
        }
    }

    /**
     * Logs a message
     * @param message - message text
     * @param severity - message severity
     * @param component - component name
     * @param subcomponent - sub-component name
     */
    public internalLog(message: string, severity: MessageSeverity,
                       component?: string, subcomponent?: string): void {

        let toLog = "";

        const currentDate = new Date();
        toLog += currentDate.getHours() + ":" + currentDate.getMinutes() + ":" +
            currentDate.getSeconds() + ":" + currentDate.getMilliseconds() + " ";

        if (component) {
            toLog += (component + ": ");
        }
        if (subcomponent) {
            toLog += (subcomponent + ": ");
        }

        toLog += message;

        if (severity === MessageSeverity.WARNING) {
            console.warn(toLog);
        } else if (severity === MessageSeverity.ERROR) {
            console.error(toLog);
        } else {
            console.log(toLog);
        }
    }

    /**
     * Logs a DEBUG severity message.
     * @param message - message text
     * @param component - component name
     * @param subcomponent - sub-component name
     */
    public debug(message: string,
                 component?: string, subcomponent?: string): void {
        this.log(message, MessageSeverity.DEBUG, component, subcomponent);
    }

    /**
     * Logs a DEBUG_DETAIL severity message.
     * @param message - message text
     * @param component - component name
     * @param subcomponent - sub-component name
     */
    public debugDetail(message: string,
                       component?: string, subcomponent?: string): void {
        this.log(message, MessageSeverity.DEBUG_DETAIL, component, subcomponent);
    }

    /**
     * Logs a DEBUG_OVERVIEW severity message.
     * @param message - message text
     * @param component - component name
     * @param subcomponent - sub-component name
     */
    public debugOverview(message: string,
                         component?: string, subcomponent?: string): void {
        this.log(message, MessageSeverity.DEBUG_OVERVIEW, component, subcomponent);
    }

    /**
     * Logs a WARNING severity message.
     * @param message - message text
     * @param component - component name
     * @param subcomponent - sub-component name
     */
    public warning(message: string,
                   component?: string, subcomponent?: string): void {
        this.log(message, MessageSeverity.WARNING, component, subcomponent);
    }

    /**
     * Logs an ERROR severity message.
     * @param message - message text
     * @param component - component name
     * @param subcomponent - sub-component name
     */
    public error(message: string,
                 component?: string, subcomponent?: string): void {
        this.log(message, MessageSeverity.ERROR, component, subcomponent);
    }

    /**
     * Sets connection logger configuration.
     * @param loggerSettings
     */
    public setLoggerConfiguration(loggerSettings: ILoggerSettings) {
        this.loggerSettings = loggerSettings;

        this.extendedConnection.setLoggerConfiguration(loggerSettings);
    }

    public documentHighlight(uri: string, position: Position): Promise<DocumentHighlight[]> {
        this.debug("documentHighlight called for uri: " + uri,
            "ProxyServerConnection", "documentHighlight");

        if (this.markOccurrencesListeners.length === 0) {
            return Promise.resolve([]);
        }

        const document = this.documents.get(uri);
        this.debugDetail("got document: " + (document != null),
            "ProxyServerConnection", "documentHighlight");
        if (!document) {
            return Promise.resolve([]);
        }

        const offset = document.offsetAt(position);

        return this.markOccurrencesListeners[0](uri, offset).then((locations) => {

            const result: DocumentHighlight[]  = [];

            this.debugDetail("Got locations: " + (locations ? locations.length : 0),
                "ProxyServerConnection", "documentHighlight");

            if (locations) {
                for (const location of locations) {
                    const start = document.positionAt(location.start);
                    const end = document.positionAt(location.end);
                    result.push({
                        kind: 1,
                        range: {
                            start,
                            end
                        }
                    });
                }
            }

            return result;
        });

    }

    public rename(uri: string, position: Position, newName: string): WorkspaceEdit {
        this.debug("rename called for uri: " + uri + " and name " + newName,
            "ProxyServerConnection", "rename");

        if (this.renameListeners.length === 0) {
            return null;
        }

        const document = this.documents.get(uri);
        this.debugDetail("got document: " + (document != null),
            "ProxyServerConnection", "rename");
        if (!document) {
            return null;
        }

        const offset = document.offsetAt(position);

        const uriToChanges: {[uri: string]: TextEdit[]} = {};
        // TODO same for document versions when they are introduced

        for (const listener of this.renameListeners) {

            const changedDocuments: IChangedDocument[] = listener(uri, offset, newName);

            this.debugDetail("Got changed documents: " +
                (changedDocuments ? changedDocuments.length : 0),
                "ProxyServerConnection", "rename");

            if (changedDocuments) {
                for (const changedDocument of changedDocuments) {

                    this.debugDetail("Converting changes in a document: " +
                        changedDocument.uri,
                        "ProxyServerConnection", "rename");

                    const existingDocument = this.documents.get(changedDocument.uri);
                    if (!existingDocument) {

                        this.error("Can not apply a full-content change of document " +
                            changedDocument.uri + " because its previous version is not found " +
                            "in the list of documents");
                        continue;
                    }

                    this.debugDetail("Found existing document: " +
                        (existingDocument ? "true" : "false"),
                        "ProxyServerConnection", "rename");

                    let existingChanges = uriToChanges[changedDocument.uri];
                    this.debugDetail("Found existing changes: " +
                        (existingChanges ? "true" : "false"),
                        "ProxyServerConnection", "rename");

                    if (!existingChanges) {
                        existingChanges = [];
                    }

                    let editsToApply: TextEdit[] = [];

                    if (changedDocument.text) {
                        this.debugDetail("Changed document has text set.",
                            "ProxyServerConnection", "rename");

                        const previousText = existingDocument.getText();
                        this.debugDetail("Old text:\n" + previousText,
                            "ProxyServerConnection", "rename");

                        const previousTextLength = previousText.length;

                        const startPosition = existingDocument.positionAt(0);
                        const endPosition = previousTextLength === 0 ?
                            existingDocument.positionAt(0) :
                            existingDocument.positionAt(previousTextLength - 1);

                        this.debugDetail("Edit start position: [" + startPosition.line +
                            " ," + startPosition.character + "]",
                            "ProxyServerConnection", "rename");
                        this.debugDetail("Edit end position: [" + endPosition.line +
                            " ," + endPosition.character + "]",
                            "ProxyServerConnection", "rename");
                        this.debugDetail("Edit text:\n" + changedDocument.text,
                            "ProxyServerConnection", "rename");

                        editsToApply.push({
                            range: {
                                start: startPosition,
                                end: endPosition
                            },
                            newText: changedDocument.text
                        });

                    } else if (changedDocument.textEdits) {
                        this.debugDetail("Changed document has edits set.",
                            "ProxyServerConnection", "rename");

                        editsToApply = changedDocument.textEdits.map((currentEdit) => {
                            const startPosition = existingDocument.positionAt(currentEdit.range.start);
                            const endPosition = existingDocument.positionAt(currentEdit.range.end);

                            return {
                                range: {
                                    start: startPosition,
                                    end: endPosition
                                },
                                newText: currentEdit.text
                            };
                        });
                    }

                    const newChanges = existingChanges.concat(editsToApply);
                    uriToChanges[changedDocument.uri] = newChanges;
                    this.debugDetail("Saving changes for uri " + changedDocument.uri + ": " +
                        uriToChanges[changedDocument.uri].length,
                        "ProxyServerConnection", "rename");
                }
            }
        }

        const uriChanges: TextDocumentEdit[] = [];
        for (const currentUri in uriToChanges) {
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

        const result: WorkspaceEdit = {
            // changes: uriChanges
            documentChanges: uriChanges
        } as any;

        this.debugDetail("Returning",
            "ProxyServerConnection", "rename");
        return result;
    }

    private removeCompletionPreviousLineIndentation(originalText: string) {
        const lastNewLineIndex = originalText.lastIndexOf("\n");
        if (lastNewLineIndex === -1 || lastNewLineIndex === originalText.length - 1) {
            return originalText;
        }

        const textAfterLastNewLine = originalText.substring(lastNewLineIndex + 1);
        if (textAfterLastNewLine.trim() !== "") {
            return originalText;
        }

        return originalText.substring(0, lastNewLineIndex + 1) + "  ";
    }
}

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
