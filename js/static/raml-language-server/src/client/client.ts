import {
    DetailsItemJSON,
    IChangedDocument,
    IDetailsReport,
    IExecutableAction,
    ILocation,
    ILogger,
    ILoggerSettings,
    IOpenedDocument,
    IRange,
    IStructureReport,
    IUIDisplayRequest,
    IValidationReport,
    MessageSeverity,
    StructureNodeJSON,
    Suggestion
} from "./typeInterfaces";

import {
    IActionsConfiguration,
    IServerConfiguration
} from "../common/configuration";

export {
    IValidationReport,
    IStructureReport,
    IOpenedDocument,
    IChangedDocument,
    StructureNodeJSON,
    Suggestion,
    ILogger,
    ILocation,
    IRange,
    ILoggerSettings,
    MessageSeverity,
    DetailsItemJSON,
    IDetailsReport,
    IExecutableAction,
    IUIDisplayRequest
} from "./typeInterfaces";

export {
    IServerConfiguration,
    IActionsConfiguration
} from "../common/configuration";

export interface IClientConnection extends ILogger {

    /**
     * Stops the server.
     */
    stop(): void;

    /**
     * Adds a listener for validation report coming from the server.
     * @param listener
     */
    onValidationReport(listener: (report: IValidationReport) => void);

    /**
     * Instead of calling getStructure to get immediate structure report for the document,
     * this method allows to launch to the new structure reports when those are available.
     * @param listener
     */
    onStructureReport(listener: (report: IStructureReport) => void);

    /**
     * Notifies the server that document is opened.
     * @param document
     */
    documentOpened(document: IOpenedDocument);

    /**
     * Notified the server that document is closed.
     * @param uri
     */
    documentClosed(uri: string);

    /**
     * Notifies the server that document is changed.
     * @param document
     */
    documentChanged(document: IChangedDocument);

    /**
     * Requests server for the document structure.
     * @param uri
     */
    getStructure(uri: string): Promise<{[categoryName: string]: StructureNodeJSON}>;

    /**
     * Requests server for the suggestions.
     * @param uri - document uri
     * @param position - offset in the document, starting from 0
     */
    getSuggestions(uri: string, position: number): Promise<Suggestion[]>;

    /**
     * Requests server for the positions of the declaration of the element defined
     * at the given document position.
     * @param uri - document uri
     * @param position - position in the document
     */
    openDeclaration(uri: string, position: number): Promise<ILocation[]>;

    /**
     * Requests server for the positions of the references of the element defined
     * at the given document position.
     * @param uri - document uri
     * @param position - position in the document
     */
    findReferences(uri: string, position: number): Promise<ILocation[]>;

    /**
     * Requests server for the occurrences of the element defined
     * at the given document position.
     * @param uri - document uri
     * @param position - position in the document
     */
    markOccurrences(uri: string, position: number): Promise<IRange[]>;

    /**
     * Requests server for rename of the element
     * at the given document position.
     * @param uri - document uri
     * @param position - position in the document
     */
    rename(uri: string, position: number, newName: string): Promise<IChangedDocument[]>;

    /**
     * Gets latest document version.
     * @param uri
     */
    getLatestVersion(uri: string): Promise<number>;

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
     * Requests server for the document+position details.
     * @param uri
     */
    getDetails(uri: string, position: number): Promise<DetailsItemJSON>;

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
    onDetailsReport(listener: (IDetailsReport) => void);

    /**
     * Executes the specified details action.
     * @param uri - document uri
     * @param actionID - ID of the action to execute.
     * @param position - optional position in the document.
     * If not provided, the last reported by positionChanged method will be used.
     */
    executeDetailsAction(uri: string,
                         actionID: string, position?: number): Promise<IChangedDocument[]>;

    /**
     * Calculates the list of executable actions avilable in the current context.
     *
     * @param uri - document uri.
     * @param position - optional position in the document.
     * If not provided, the last reported by positionChanged method will be used.
     */
    calculateEditorContextActions(uri: string, position?: number) : Promise<IExecutableAction[]>;

    /**
     * Calculates the list of all available actions.
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
    executeContextAction(uri: string,
                         action: IExecutableAction, position?: number): Promise<IChangedDocument[]>;

    /**
     * Executes the specified action. If action has UI, causes a consequent
     * server->client UI message resulting in onDisplayActionUI listener call.
     * @param uri - document uri
     * @param actionID - actionID to execute.
     * @param position - optional position in the document.
     * If not provided, the last reported by positionChanged method will be used.
     */
    executeContextActionByID(uri: string,
                             actionID: string, position?: number): Promise<IChangedDocument[]>;

    /**
     * Adds a listener to display action UI.
     * @param listener - accepts UI display request, should result in a promise
     * returning final UI state to be transferred to the server.
     */
    onDisplayActionUI(
        listener: (uiDisplayRequest: IUIDisplayRequest) => Promise<any>
    );

    /**
     * Sets server configuration.
     * @param serverSettings
     */
    setServerConfiguration(serverSettings: IServerConfiguration): void;

    /**
     * Changes value of details item.
     * @param uri
     * @param position
     * @param itemID
     * @param value
     */
    changeDetailValue(uri: string, position: number, itemID: string,
                      value: string | number| boolean): Promise<IChangedDocument[]>;
}
