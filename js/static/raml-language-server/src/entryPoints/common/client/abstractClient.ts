import clientInterfaces = require("../../../client/client");
import commonInterfaces = require("../../../common/typeInterfaces");

import {
    MessageToServerType,
    ProtocolMessage
} from "../../common/protocol";

import {
    MessageDispatcher
} from "../../common/messageDispatcher";

import {
    IChangedDocument,
    IExecutableAction,
    ILoggerSettings,
    IUIDisplayRequest,
    MessageSeverity
} from "../../../client/typeInterfaces";

import {
    VersionedDocumentManager
} from "./clientVersionManager";

import {
    filterLogMessage
} from "../../../common/utils";

import {
    IServerConfiguration
} from "../../../common/configuration";

export abstract class AbstractClientConnection extends MessageDispatcher<MessageToServerType>
    implements clientInterfaces.IClientConnection {

    private loggerSettings: ILoggerSettings;

    private validationReportListeners: {(report: clientInterfaces.IValidationReport): void}[] = [];
    private structureReportListeners: {(report: clientInterfaces.IStructureReport): void}[] = [];
    private versionManager: VersionedDocumentManager;

    private onExistsListeners: {(path: string): Promise<boolean>}[] = [];
    private onReadDirListeners: {(path: string): Promise<string[]>}[] = [];
    private onIsDirectoryListeners: {(path: string): Promise<boolean>}[] = [];
    private onContentListeners: {(path: string): Promise<string>}[] = [];
    private onDetailsReportListeners: {(report: clientInterfaces.IDetailsReport): void}[] = [];
    private onDisplayActionUIListeners: {(uiDisplayRequest: IUIDisplayRequest): Promise<any>}[] = [];

    constructor(name: string) {
        super(name);

        this.versionManager = new VersionedDocumentManager(this);
    }

    /**
     * Sends message to the counterpart.
     * @param message
     */
    public abstract sendMessage(message: ProtocolMessage<MessageToServerType>): void;

    /**
     * Stops the server.
     */
    public abstract stop(): void;

    /**
     * Sets server configuration.
     * @param serverSettings
     */
    public setServerConfiguration(serverSettings: IServerConfiguration): void {

        // changing server configuration
        this.send({
            type : "SET_SERVER_CONFIGURATION",
            payload : serverSettings
        });
    }

    public onValidationReport(listener: (report: clientInterfaces.IValidationReport) => void) {
        this.validationReportListeners.push(listener);
    }

    public onStructureReport(listener: (report: clientInterfaces.IStructureReport) => void) {
        this.structureReportListeners.push(listener);
    }

    public documentOpened(document: clientInterfaces.IOpenedDocument): void {

        const commonOpenedDocument = this.versionManager.registerOpenedDocument(document);
        if (!commonOpenedDocument) {
            return;
        }

        this.send({
            type : "OPEN_DOCUMENT",
            payload : commonOpenedDocument
        });
    }

    public documentChanged(document: IChangedDocument): void {

        const commonChangedDocument = this.versionManager.registerChangedDocument(document);
        if (!commonChangedDocument) {
            return;
        }

        this.send({
            type : "CHANGE_DOCUMENT",
            payload : commonChangedDocument
        });
    }

    public documentClosed(uri: string): void {

        // this.versionManager.unregisterDocument(uri);

        this.send({
            type : "CLOSE_DOCUMENT",
            payload : uri
        });
    }

    public getStructure(uri: string): Promise<{[categoryName: string]: clientInterfaces.StructureNodeJSON}> {

        return this.sendWithResponse({
            type : "GET_STRUCTURE",
            payload : uri
        });
    }

    public getSuggestions(uri: string, position: number): Promise<clientInterfaces.Suggestion[]> {
        return this.sendWithResponse({
            type : "GET_SUGGESTIONS",
            payload : {
                uri,
                position
            }
        });
    }

    /**
     * Requests server for the positions of the declaration of the element defined
     * at the given document position.
     * @param uri - document uri
     * @param position - position in the document
     */
    public openDeclaration(uri: string, position: number): Promise<clientInterfaces.ILocation[]> {
        return this.sendWithResponse({
            type : "OPEN_DECLARATION",
            payload : {
                uri,
                position
            }
        });
    }

    /**
     * Requests server for the positions of the references of the element defined
     * at the given document position.
     * @param uri - document uri
     * @param position - position in the document
     */
    public findReferences(uri: string, position: number): Promise<clientInterfaces.ILocation[]> {
        return this.sendWithResponse({
            type : "FIND_REFERENCES",
            payload : {
                uri,
                position
            }
        });
    }

    /**
     * Requests server for the positions of the references of the element defined
     * at the given document position.
     * @param uri - document uri
     * @param position - position in the document
     */
    public markOccurrences(uri: string, position: number): Promise<clientInterfaces.IRange[]> {
        return this.sendWithResponse({
            type : "MARK_OCCURRENCES",
            payload : {
                uri,
                position
            }
        });
    }

    /**
     * Requests server for rename of the element
     * at the given document position.
     * @param uri - document uri
     * @param position - position in the document
     */
    public rename(uri: string, position: number, newName: string): Promise<clientInterfaces.IChangedDocument[]> {
        return this.sendWithResponse({
            type : "RENAME",
            payload : {
                uri,
                position,
                newName
            }
        });
    }

    /**
     * Requests server for the document+position details.
     * @param uri
     */
    public getDetails(uri: string, position: number): Promise<clientInterfaces.DetailsItemJSON> {
        return this.sendWithResponse({
            type : "GET_DETAILS",
            payload : {
                uri,
                position
            }
        });
    }

    /**
     * Changes value of details item.
     * @param uri
     * @param position
     * @param itemID
     * @param value
     */
    public changeDetailValue(uri: string, position: number, itemID: string,
                             value: string | number| boolean): Promise<clientInterfaces.IChangedDocument[]> {
        return this.sendWithResponse({
            type : "CHANGE_DETAIL_VALUE",
            payload : {
                uri,
                position,
                itemID,
                value
            }
        });
    }

    /**
     * Sets connection logger configuration, both for the server and for the client.
     * @param loggerSettings
     */
    public setLoggerConfiguration(loggerSettings: ILoggerSettings): void {

        // changing client configuration
        this.loggerSettings = loggerSettings;

        // changing server configuration
        this.send({
            type : "SET_LOGGER_CONFIGURATION",
            payload : loggerSettings
        });
    }

    public VALIDATION_REPORT(report: clientInterfaces.IValidationReport): void {
        for (const listener of this.validationReportListeners) {
            listener(report);
        }
    }

    public STRUCTURE_REPORT(report: clientInterfaces.IStructureReport): void {
        for (const listener of this.structureReportListeners) {
            listener(report);
        }
    }

    public EXISTS(path: string): Promise<boolean> {

        for (const listener of this.onExistsListeners) {
            const result = listener(path);
            if (result !== null) {
                return result;
            }
        }

        return null;
    }

    public READ_DIR(path: string): Promise<string[]> {

        for (const listener of this.onReadDirListeners) {
            const result = listener(path);
            if (result !== null) {
                return result;
            }
        }

        return null;
    }

    public IS_DIRECTORY(path: string): Promise<boolean> {

        for (const listener of this.onIsDirectoryListeners) {
            const result = listener(path);
            if (result !== null) {
                return result;
            }
        }

        return null;
    }

    public CONTENT(path: string): Promise<string> {

        for (const listener of this.onContentListeners) {
            const result = listener(path);
            if (result !== null) {
                return result;
            }
        }

        return null;
    }

    public DETAILS_REPORT(report: clientInterfaces.IDetailsReport): void {
        for (const listener of this.onDetailsReportListeners) {
            listener(report);
        }
    }

    /**
     * Gets latest document version.
     * @param uri
     */
    public getLatestVersion(uri: string): Promise<number> {
        const version = this.versionManager.getLatestDocumentVersion(uri);

        return Promise.resolve(version);
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

        const filtered = filterLogMessage({
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
     * Listens to the server requests for FS path existence, answering whether
     * a particular path exists on FS.
     */
    public onExists(listener: (path: string) => Promise<boolean>): void {
        this.onExistsListeners.push(listener);
    }

    /**
     * Listens to the server requests for directory contents, answering with a list
     * of files in a directory.
     */
    public onReadDir(listener: (path: string) => Promise<string[]>): void {
        this.onReadDirListeners.push(listener);
    }

    /**
     * Listens to the server requests for directory check, answering whether
     * a particular path is a directory.
     */
    public onIsDirectory(listener: (path: string) => Promise<boolean>): void {
        this.onIsDirectoryListeners.push(listener);
    }

    /**
     * Listens to the server requests for file contents, answering what contents file has.
     */
    public onContent(listener: (path: string) => Promise<string>): void {
        this.onContentListeners.push(listener);
    }

    /**
     * Reports to the server the position (cursor) change on the client.
     * @param uri - document uri.
     * @param position - curtsor position, starting from 0.
     */
    public positionChanged(uri: string, position: number): void {
        this.send({
            type : "CHANGE_POSITION",
            payload : {
                uri,
                position
            }
        });
    }

    /**
     * Report from the server that the new details are calculated
     * for particular document and position.
     * @param listener
     */
    public onDetailsReport(listener: (IDetailsReport) => void) {
        this.onDetailsReportListeners.push(listener);
    }

    /**
     * Executes the specified details action.
     * @param uri - document uri
     * @param actionID - ID of the action to execute.
     * @param position - optional position in the document.
     * If not provided, the last reported by positionChanged method will be used.
     */
    public executeDetailsAction(uri: string,
                                actionID: string, position?: number): Promise<IChangedDocument[]> {

        return this.sendWithResponse({
            type : "EXECUTE_DETAILS_ACTION",
            payload : {
                uri,
                position,
                actionId: actionID
            }
        });
    }

    /**
     * Calculates the list of executable actions avilable in the current context.
     *
     * @param uri - document uri.
     * @param position - optional position in the document.
     * If not provided, the last reported by positionChanged method will be used.
     */
    public calculateEditorContextActions(uri: string,
                                         position?: number): Promise<IExecutableAction[]> {

        return this.sendWithResponse({
            type : "CALCULATE_ACTIONS",
            payload : {
                uri,
                position
            }
        });
    }

    /**
     * Calculates the list of all available executable actions.
     */
    allAvailableActions(): Promise<IExecutableAction[]> {
        return this.sendWithResponse({
            type : "ALL_ACTIONS",
            payload : {}
        });
    }

    /**
     * Executes the specified action. If action has UI, causes a consequent
     * server->client UI message resulting in onDisplayActionUI listener call.
     * @param uri - document uri
     * @param action - action to execute.
     * @param position - optional position in the document.
     * If not provided, the last reported by positionChanged method will be used.
     */
    public executeContextAction(uri: string, action: IExecutableAction,
                                position?: number): Promise<IChangedDocument[]> {

        return this.sendWithResponse({
            type : "EXECUTE_ACTION",
            payload : {
                uri,
                position,
                actionId: action.id
            }
        });
    }

    /**
     * Executes the specified action. If action has UI, causes a consequent
     * server->client UI message resulting in onDisplayActionUI listener call.
     * @param uri - document uri
     * @param actionID - actionID to execute.
     * @param position - optional position in the document.
     * If not provided, the last reported by positionChanged method will be used.
     */
    public executeContextActionByID(uri: string,
                                    actionID: string, position?: number): Promise<IChangedDocument[]> {
        return this.sendWithResponse({
            type : "EXECUTE_ACTION",
            payload : {
                uri,
                position,
                actionId: actionID
            }
        });
    }

    /**
     * Adds a listener to display action UI.
     * @param listener - accepts UI display request, should result in a promise
     * returning final UI state to be transferred to the server.
     */
    public onDisplayActionUI(
        listener: (uiDisplayRequest: IUIDisplayRequest) => Promise<any>) {

        this.onDisplayActionUIListeners.push(listener);
    }

    public DISPLAY_ACTION_UI(uiDisplayRequest: IUIDisplayRequest): Promise<any> {
        if (!this.onDisplayActionUIListeners) {
            return Promise.reject(new Error("No handler for DISPLAY_ACTION_UI"));
        }

        return this.onDisplayActionUIListeners[0](uiDisplayRequest);
    }
}
