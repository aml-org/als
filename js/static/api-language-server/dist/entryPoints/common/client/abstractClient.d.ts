import clientInterfaces = require("../../../client/client");
import { MessageToServerType, ProtocolMessage } from "../../common/protocol";
import { MessageDispatcher } from "../../common/messageDispatcher";
import { IChangedDocument, IExecutableAction, ILoggerSettings, IUIDisplayRequest, MessageSeverity } from "../../../client/typeInterfaces";
import { IServerConfiguration } from "../../../common/configuration";
export declare abstract class AbstractClientConnection extends MessageDispatcher<MessageToServerType> implements clientInterfaces.IClientConnection {
    private loggerSettings;
    private validationReportListeners;
    private structureReportListeners;
    private versionManager;
    private onExistsListeners;
    private onReadDirListeners;
    private onIsDirectoryListeners;
    private onContentListeners;
    private onDetailsReportListeners;
    private onDisplayActionUIListeners;
    constructor(name: string);
    /**
     * Sends message to the counterpart.
     * @param message
     */
    abstract sendMessage(message: ProtocolMessage<MessageToServerType>): void;
    /**
     * Stops the server.
     */
    abstract stop(): void;
    /**
     * Sets server configuration.
     * @param serverSettings
     */
    setServerConfiguration(serverSettings: IServerConfiguration): void;
    onValidationReport(listener: (report: clientInterfaces.IValidationReport) => void): void;
    onStructureReport(listener: (report: clientInterfaces.IStructureReport) => void): void;
    documentOpened(document: clientInterfaces.IOpenedDocument): void;
    documentChanged(document: IChangedDocument): void;
    documentClosed(uri: string): void;
    getStructure(uri: string): Promise<{
        [categoryName: string]: clientInterfaces.StructureNodeJSON;
    }>;
    getSuggestions(uri: string, position: number): Promise<clientInterfaces.Suggestion[]>;
    /**
     * Requests server for the positions of the declaration of the element defined
     * at the given document position.
     * @param uri - document uri
     * @param position - position in the document
     */
    openDeclaration(uri: string, position: number): Promise<clientInterfaces.ILocation[]>;
    /**
     * Requests server for the positions of the references of the element defined
     * at the given document position.
     * @param uri - document uri
     * @param position - position in the document
     */
    findReferences(uri: string, position: number): Promise<clientInterfaces.ILocation[]>;
    /**
     * Requests server for the positions of the references of the element defined
     * at the given document position.
     * @param uri - document uri
     * @param position - position in the document
     */
    markOccurrences(uri: string, position: number): Promise<clientInterfaces.IRange[]>;
    /**
     * Requests server for rename of the element
     * at the given document position.
     * @param uri - document uri
     * @param position - position in the document
     */
    rename(uri: string, position: number, newName: string): Promise<clientInterfaces.IChangedDocument[]>;
    /**
     * Requests server for the document+position details.
     * @param uri
     */
    getDetails(uri: string, position: number): Promise<clientInterfaces.DetailsItemJSON>;
    /**
     * Changes value of details item.
     * @param uri
     * @param position
     * @param itemID
     * @param value
     */
    changeDetailValue(uri: string, position: number, itemID: string, value: string | number | boolean): Promise<clientInterfaces.IChangedDocument[]>;
    /**
     * Sets connection logger configuration, both for the server and for the client.
     * @param loggerSettings
     */
    setLoggerConfiguration(loggerSettings: ILoggerSettings): void;
    VALIDATION_REPORT(report: clientInterfaces.IValidationReport): void;
    STRUCTURE_REPORT(report: clientInterfaces.IStructureReport): void;
    EXISTS(path: string): Promise<boolean>;
    READ_DIR(path: string): Promise<string[]>;
    IS_DIRECTORY(path: string): Promise<boolean>;
    CONTENT(path: string): Promise<string>;
    DETAILS_REPORT(report: clientInterfaces.IDetailsReport): void;
    /**
     * Gets latest document version.
     * @param uri
     */
    getLatestVersion(uri: string): Promise<number>;
    /**
     * Logs a message
     * @param message - message text
     * @param severity - message severity
     * @param component - component name
     * @param subcomponent - sub-component name
     */
    log(message: string, severity: MessageSeverity, component?: string, subcomponent?: string): void;
    /**
     * Logs a message
     * @param message - message text
     * @param severity - message severity
     * @param component - component name
     * @param subcomponent - sub-component name
     */
    internalLog(message: string, severity: MessageSeverity, component?: string, subcomponent?: string): void;
    /**
     * Logs a DEBUG severity message.
     * @param message - message text
     * @param component - component name
     * @param subcomponent - sub-component name
     */
    debug(message: string, component?: string, subcomponent?: string): void;
    /**
     * Logs a DEBUG_DETAIL severity message.
     * @param message - message text
     * @param component - component name
     * @param subcomponent - sub-component name
     */
    debugDetail(message: string, component?: string, subcomponent?: string): void;
    /**
     * Logs a DEBUG_OVERVIEW severity message.
     * @param message - message text
     * @param component - component name
     * @param subcomponent - sub-component name
     */
    debugOverview(message: string, component?: string, subcomponent?: string): void;
    /**
     * Logs a WARNING severity message.
     * @param message - message text
     * @param component - component name
     * @param subcomponent - sub-component name
     */
    warning(message: string, component?: string, subcomponent?: string): void;
    /**
     * Logs an ERROR severity message.
     * @param message - message text
     * @param component - component name
     * @param subcomponent - sub-component name
     */
    error(message: string, component?: string, subcomponent?: string): void;
    /**
     * Listens to the server requests for FS path existence, answering whether
     * a particular path exists on FS.
     */
    onExists(listener: (path: string) => Promise<boolean>): void;
    /**
     * Listens to the server requests for directory contents, answering with a list
     * of files in a directory.
     */
    onReadDir(listener: (path: string) => Promise<string[]>): void;
    /**
     * Listens to the server requests for directory check, answering whether
     * a particular path is a directory.
     */
    onIsDirectory(listener: (path: string) => Promise<boolean>): void;
    /**
     * Listens to the server requests for file contents, answering what contents file has.
     */
    onContent(listener: (path: string) => Promise<string>): void;
    /**
     * Reports to the server the position (cursor) change on the client.
     * @param uri - document uri.
     * @param position - curtsor position, starting from 0.
     */
    positionChanged(uri: string, position: number): void;
    /**
     * Report from the server that the new details are calculated
     * for particular document and position.
     * @param listener
     */
    onDetailsReport(listener: (IDetailsReport) => void): void;
    /**
     * Executes the specified details action.
     * @param uri - document uri
     * @param actionID - ID of the action to execute.
     * @param position - optional position in the document.
     * If not provided, the last reported by positionChanged method will be used.
     */
    executeDetailsAction(uri: string, actionID: string, position?: number): Promise<IChangedDocument[]>;
    /**
     * Calculates the list of executable actions avilable in the current context.
     *
     * @param uri - document uri.
     * @param position - optional position in the document.
     * If not provided, the last reported by positionChanged method will be used.
     */
    calculateEditorContextActions(uri: string, position?: number): Promise<IExecutableAction[]>;
    /**
     * Calculates the list of all available executable actions.
     */
    allAvailableActions(): Promise<IExecutableAction[]>;
    /**
     * Executes the specified action. If action has UI, causes a consequent
     * server->client UI message resulting in onDisplayActionUI listener call.
     * @param uri - document uri
     * @param action - action to execute.
     * @param position - optional position in the document.
     * If not provided, the last reported by positionChanged method will be used.
     */
    executeContextAction(uri: string, action: IExecutableAction, position?: number): Promise<IChangedDocument[]>;
    /**
     * Executes the specified action. If action has UI, causes a consequent
     * server->client UI message resulting in onDisplayActionUI listener call.
     * @param uri - document uri
     * @param actionID - actionID to execute.
     * @param position - optional position in the document.
     * If not provided, the last reported by positionChanged method will be used.
     */
    executeContextActionByID(uri: string, actionID: string, position?: number): Promise<IChangedDocument[]>;
    /**
     * Adds a listener to display action UI.
     * @param listener - accepts UI display request, should result in a promise
     * returning final UI state to be transferred to the server.
     */
    onDisplayActionUI(listener: (uiDisplayRequest: IUIDisplayRequest) => Promise<any>): void;
    DISPLAY_ACTION_UI(uiDisplayRequest: IUIDisplayRequest): Promise<any>;
}
