import { IServerConnection } from "../../server/core/connections";
import { ILoggerSettings, IStructureReport, IValidationReport, MessageSeverity, IDetailsReport } from "../../common/typeInterfaces";
import { AbstractServerConnection } from "../../server/core/connectionsImpl";
import { CompletionList, DocumentHighlight, IConnection, Location, Position, SymbolInformation, WorkspaceEdit } from "vscode-languageserver";
export declare class ProxyServerConnection extends AbstractServerConnection implements IServerConnection {
    private vsCodeConnection;
    private loggerSettings;
    private documents;
    private extendedConnection;
    constructor(vsCodeConnection: IConnection);
    listen(): void;
    onChangePosition(listener: any): void;
    onChangeDetailValue(listener: any): void;
    onDocumentDetails(listener: any): void;
    onDocumentStructure(listener: any): void;
    detailsAvailable(report: IDetailsReport): void;
    structureAvailable(report: IStructureReport): void;
    onSetServerConfiguration(listener: any): void;
    onOpenDocument(listener: any): void;
    onChangeDocument(listener: any): void;
    onAllEditorContextActions(listener: any): void;
    onCalculateEditorContextActions(listener: any): void;
    onExecuteContextAction(listener: any): void;
    onExecuteDetailsAction(listener: any): void;
    displayActionUI(data: any): Promise<any>;
    /**
     * Reports latest validation results
     * @param report
     */
    validated(report: IValidationReport): void;
    getSymbols(uri: string): Promise<SymbolInformation[]>;
    getCompletion(uri: string, position: Position): Promise<CompletionList>;
    openDeclaration(uri: string, position: Position): Promise<Location[]>;
    findReferences(uri: string, position: Position): Promise<Location[]>;
    /**
     * Returns whether path/url exists.
     * @param fullPath
     */
    exists(path: string): Promise<boolean>;
    /**
     * Returns directory content list.
     * @param fullPath
     */
    readDir(path: string): Promise<string[]>;
    /**
     * Returns whether path/url represents a directory
     * @param path
     */
    isDirectory(path: string): Promise<boolean>;
    /**
     * File contents by full path/url.
     * @param path
     */
    content(path: string): Promise<string>;
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
     * Sets connection logger configuration.
     * @param loggerSettings
     */
    setLoggerConfiguration(loggerSettings: ILoggerSettings): void;
    documentHighlight(uri: string, position: Position): Promise<DocumentHighlight[]>;
    rename(uri: string, position: Position, newName: string): WorkspaceEdit;
    private removeCompletionPreviousLineIndentation(originalText);
}
