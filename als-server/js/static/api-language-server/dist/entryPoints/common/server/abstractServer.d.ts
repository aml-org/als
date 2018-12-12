import { DetailsItemJSON, IChangedDocument, IDetailsReport, IExecutableAction, ILocation, IOpenedDocument, IRange, IServerConnection, IUIDisplayRequest, IValidationReport, MessageSeverity, StructureNodeJSON, Suggestion } from "../../../server/core/connections";
import { MessageToClientType, ProtocolMessage } from "../protocol";
import { MessageDispatcher } from "../messageDispatcher";
import { ILoggerSettings, IStructureReport } from "../../../common/typeInterfaces";
import { IServerConfiguration } from "../../../common/configuration";
export declare abstract class AbstractMSServerConnection extends MessageDispatcher<MessageToClientType> implements IServerConnection {
    private loggerSettings;
    private openDocumentListeners;
    private changeDocumentListeners;
    private closeDocumentListeners;
    private documentStructureListeners;
    private documentCompletionListeners;
    private openDeclarationListeners;
    private findReferencesListeners;
    private markOccurrencesListeners;
    private renameListeners;
    private documentDetailsListeners;
    private changeDetailValueListeners;
    private changePositionListeners;
    private serverConfigurationListeners;
    private calculateEditorContextActionsListeners;
    private getAllEditorContextActionsListeners;
    private executeContextActionListeners;
    private executeDetailsActionListeners;
    constructor(name: string);
    abstract sendMessage(message: ProtocolMessage<MessageToClientType>): void;
    /**
     * Adds a listener to document open notification. Must notify listeners in order of registration.
     * @param listener
     */
    onOpenDocument(listener: (document: IOpenedDocument) => void, unsubsribe?: boolean): void;
    /**
     * Adds a listener to document change notification. Must notify listeners in order of registration.
     * @param listener
     */
    onChangeDocument(listener: (document: IChangedDocument) => void, unsubsribe?: boolean): void;
    /**
     * Adds a listener to document close notification. Must notify listeners in order of registration.
     * @param listener
     */
    onCloseDocument(listener: (uri: string) => void, unsubsribe?: boolean): void;
    /**
     * Adds a listener to document structure request. Must notify listeners in order of registration.
     * @param listener
     */
    onDocumentStructure(listener: (uri: string) => Promise<{
        [categoryName: string]: StructureNodeJSON;
    }>, unsubsribe?: boolean): void;
    /**
     * Adds a listener to document completion request. Must notify listeners in order of registration.
     * @param listener
     */
    onDocumentCompletion(listener: (uri: string, position: number) => Promise<Suggestion[]>, unsubsribe?: boolean): void;
    /**
     * Adds a listener to document open declaration request.  Must notify listeners in order of registration.
     * @param listener
     */
    onOpenDeclaration(listener: (uri: string, position: number) => Promise<ILocation[]>, unsubsribe?: boolean): void;
    /**
     * Adds a listener to document find references request.  Must notify listeners in order of registration.
     * @param listener
     */
    onFindReferences(listener: (uri: string, position: number) => Promise<ILocation[]>, unsubsribe?: boolean): void;
    /**
     * Finds the set of document (and non-document files) edits to perform the requested rename.
     * @param listener
     */
    onRename(listener: (uri: string, position: number, newName: string) => IChangedDocument[], unsubsribe?: boolean): void;
    /**
     * Reports latest validation results
     * @param report
     */
    validated(report: IValidationReport): void;
    /**
     * Reports new calculated structure when available.
     * @param uri - document uri
     * @param structure - structure for the document
     */
    structureAvailable(report: IStructureReport): void;
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
     * Marks occurrences of a symbol under the cursor in the current document.
     * @param listener
     */
    onMarkOccurrences(listener: (uri: string, position: number) => Promise<IRange[]>, unsubsribe?: boolean): void;
    /**
     * Adds a listener to document details request. Must notify listeners in order of registration.
     * @param listener
     */
    onDocumentDetails(listener: (uri: string, position: number) => Promise<DetailsItemJSON>, unsubsribe?: boolean): void;
    /**
     * Adds a listener for specific details action execution.
     * @param uri - document uri
     * @param action - action to execute.
     * @param position - optional position in the document.
     * If not provided, the last reported by positionChanged method will be used.
     */
    onExecuteDetailsAction(listener: (uri: string, actionId: string, position?: number) => Promise<IChangedDocument[]>, unsubsribe?: boolean): void;
    /**
     * Adds a listener to document details value change request.
     * @param listener
     * @param unsubscribe - if true, existing listener will be removed. False by default.
     */
    onChangeDetailValue(listener: (uri: string, position: number, itemID: string, value: string | number | boolean) => Promise<IChangedDocument[]>, unsubsribe?: boolean): void;
    /**
     * Adds a listener to document cursor position change notification.
     * Must notify listeners in order of registration.
     * @param listener
     */
    onChangePosition(listener: (uri: string, position: number) => void, unsubsribe?: boolean): void;
    /**
     * Reports new calculated details when available.
     * @param report - details report.
     */
    detailsAvailable(report: IDetailsReport): void;
    /**
     * Sets server configuration.
     * @param loggerSettings
     */
    onSetServerConfiguration(listener: (configuration: IServerConfiguration) => void, unsubsribe?: boolean): void;
    /**
     * Handler of OPEN_DOCUMENT message.
     * @param document
     * @constructor
     */
    OPEN_DOCUMENT(document: IOpenedDocument): void;
    /**
     * Handler of CHANGE_DOCUMENT message.
     * @param document
     * @constructor
     */
    CHANGE_DOCUMENT(document: IChangedDocument): void;
    /**
     * Handler of CLOSE_DOCUMENT message.
     * @param uri
     * @constructor
     */
    CLOSE_DOCUMENT(uri: string): void;
    /**
     * Handler of GET_STRUCTURE message.
     * @param uri
     * @constructor
     */
    GET_STRUCTURE(uri: string): Promise<{
        [categoryName: string]: StructureNodeJSON;
    }>;
    /**
     * Handler of GET_SUGGESTIONS message.
     * @param uri - document uri
     * @param position - offset in the document starting from 0
     * @constructor
     */
    GET_SUGGESTIONS(payload: {
        uri: string;
        position: number;
    }): Promise<Suggestion[]>;
    /**
     * Handler of OPEN_DECLARATION message.
     * @param uri - document uri
     * @param position - offset in the document starting from 0
     * @constructor
     */
    OPEN_DECLARATION(payload: {
        uri: string;
        position: number;
    }): Promise<ILocation[]>;
    /**
     * Handler of FIND_REFERENCES message.
     * @param uri - document uri
     * @param position - offset in the document starting from 0
     * @constructor
     */
    FIND_REFERENCES(payload: {
        uri: string;
        position: number;
    }): Promise<ILocation[]>;
    /**
     * Handler of MARK_OCCURRENCES message.
     * @param uri - document uri
     * @param position - offset in the document starting from 0
     * @constructor
     */
    MARK_OCCURRENCES(payload: {
        uri: string;
        position: number;
    }): Promise<IRange[]>;
    /**
     * Handler of RENAME message.
     * @param uri - document uri
     * @param position - offset in the document starting from 0
     * @param newName - new name
     * @constructor
     */
    RENAME(payload: {
        uri: string;
        position: number;
        newName: string;
    }): IChangedDocument[];
    SET_LOGGER_CONFIGURATION(payload: ILoggerSettings): void;
    /**
     * Handler of GET_STRUCTURE message.
     * @param uri
     * @constructor
     */
    GET_DETAILS(payload: {
        uri: string;
        position: number;
    }): Promise<DetailsItemJSON>;
    /**
     * Handler of GET_STRUCTURE message.
     * @param uri
     * @constructor
     */
    CHANGE_DETAIL_VALUE(payload: {
        uri: string;
        position: number;
        itemID: string;
        value: string | number | boolean;
    }): Promise<IChangedDocument[]>;
    /**
     * Handler for CHANGE_POSITION message.
     * @param payload
     * @constructor
     */
    CHANGE_POSITION(payload: {
        uri: string;
        position: number;
    }): void;
    /**
     * Handler for SET_SERVER_CONFIGURATION message.
     * @param payload
     * @constructor
     */
    SET_SERVER_CONFIGURATION(payload: IServerConfiguration): void;
    /**
     * Logs a message
     * @param message - message text
     * @param severity - message severity
     * @param component - component name
     * @param subcomponent - sub-component name
     */
    log(message: string, severity: MessageSeverity, component?: string, subcomponent?: string): void;
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
    onCalculateEditorContextActions(listener: (uri: string, position?: number) => Promise<IExecutableAction[]>, unsubsribe?: boolean): void;
    /**
     * Calculates the list of all available executable actions.
     */
    onAllEditorContextActions(listener: () => Promise<IExecutableAction[]>, unsubsribe?: boolean): void;
    /**
     * Adds a listener for specific action execution.
     * If action has UI, causes a consequent displayActionUI call.
     * @param uri - document uri
     * @param action - action to execute.
     * @param position - optional position in the document.
     * If not provided, the last reported by positionChanged method will be used.
     */
    onExecuteContextAction(listener: (uri: string, actionId: string, position?: number) => Promise<IChangedDocument[]>, unsubsribe?: boolean): void;
    /**
     * Adds a listener to display action UI.
     * @param uiDisplayRequest - display request
     * @return final UI state.
     */
    displayActionUI(uiDisplayRequest: IUIDisplayRequest): Promise<any>;
    EXECUTE_DETAILS_ACTION(payload: {
        uri: string;
        actionId: string;
        position?: number;
    }): Promise<IChangedDocument[]>;
    CALCULATE_ACTIONS(payload: {
        uri: string;
        position?: number;
    }): Promise<IExecutableAction[]>;
    ALL_ACTIONS(payload: {
        uri: string;
        position?: number;
    }): Promise<IExecutableAction[]>;
    EXECUTE_ACTION(payload: {
        uri: string;
        actionId: string;
        position?: number;
    }): Promise<IChangedDocument[]>;
    /**
     * Adds listener.
     * @param memberListeners - member containing array of listeners
     * @param listener - listener to add
     * @param unsubscribe - whether to unsubscribe this listener
     */
    private addListener<T>(memberListeners, listener, unsubscribe?);
}
