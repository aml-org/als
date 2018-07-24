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
    IValidationIssue,
    IValidationReport,
    MessageSeverity,
    StructureNodeJSON,
    Suggestion
} from "../../common/typeInterfaces";

import {
    IServerConfiguration
} from "../../common/configuration";

export {
    IRange,
    IValidationIssue,
    IValidationReport,
    IStructureReport,
    IOpenedDocument,
    IChangedDocument,
    StructureNodeJSON,
    Suggestion,
    ILogger,
    MessageSeverity,
    ILocation,
    ILoggerSettings,
    DetailsItemJSON,
    IDetailsReport,
    IExecutableAction,
    IUIDisplayRequest
} from "../../common/typeInterfaces";

export {
    IServerConfiguration
} from "../../common/configuration";

export interface IServerConnection extends ILogger {
    /**
     * Adds a listener to document open notification. Must notify listeners in order of registration.
     * @param listener
     * @param unsubscribe - if true, existing listener will be removed. False by default.
     */
    onOpenDocument(listener: (document: IOpenedDocument) => void, unsubscribe?: boolean);

    /**
     * Adds a listener to document change notification. Must notify listeners in order of registration.
     * @param listener
     * @param unsubscribe - if true, existing listener will be removed. False by default.
     */
    onChangeDocument(listener: (document: IChangedDocument) => void, unsubscribe?: boolean);

    /**
     * Adds a listener to document close notification. Must notify listeners in order of registration.
     * @param listener
     * @param unsubscribe - if true, existing listener will be removed. False by default.
     */
    onCloseDocument(listener: (uri: string) => void, unsubscribe?: boolean);

    /**
     * Adds a listener to document completion request. Must notify listeners in order of registration.
     * @param listener
     * @param unsubscribe - if true, existing listener will be removed. False by default.
     */
    onDocumentCompletion(listener: (uri: string, position: number) => Promise<Suggestion[]>, unsubscribe?: boolean);

    /**
     * Adds a listener to document structure request. Must notify listeners in order of registration.
     * @param listener
     * @param unsubscribe - if true, existing listener will be removed. False by default.
     */
    onDocumentStructure(listener: (uri: string) => Promise<{[categoryName: string]: StructureNodeJSON}>,
                        unsubscribe?: boolean);

    /**
     * Adds a listener to document open declaration request.  Must notify listeners in order of registration.
     * @param listener
     * @param unsubscribe - if true, existing listener will be removed. False by default.
     */
    onOpenDeclaration(listener: (uri: string, position: number) => Promise<ILocation[]>, unsubscribe?: boolean): void;

    /**
     * Adds a listener to document find references request.  Must notify listeners in order of registration.
     * @param listener
     * @param unsubscribe - if true, existing listener will be removed. False by default.
     */
    onFindReferences(listener: (uri: string, position: number) => Promise<ILocation[]>, unsubscribe?: boolean);

    /**
     * Reports latest validation results
     * @param report
     */
    validated(report: IValidationReport): void;

    /**
     * Reports new calculated structure when available.
     * @param report - structure report.
     */
    structureAvailable(report: IStructureReport);

    /**
     * Marks occurrences of a symbol under the cursor in the current document.
     * @param listener
     * @param unsubscribe - if true, existing listener will be removed. False by default.
     */
    onMarkOccurrences(listener: (uri: string, position: number) => Promise<IRange[]>, unsubscribe?: boolean);

    /**
     * Finds the set of document (and non-document files) edits to perform the requested rename.
     * @param listener
     * @param unsubscribe - if true, existing listener will be removed. False by default.
     */
    onRename(listener: (uri: string, position: number, newName: string) => IChangedDocument[], unsubscribe?: boolean);

    /**
     * Sets connection logger configuration.
     * @param loggerSettings
     */
    setLoggerConfiguration(loggerSettings: ILoggerSettings);

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
     * @param fullPath
     */
    content(fullPath: string): Promise<string>;

    /**
     * Adds a listener to document details request. Must notify listeners in order of registration.
     * @param listener
     * @param unsubscribe - if true, existing listener will be removed. False by default.
     */
    onDocumentDetails(listener: (uri: string, position: number) => Promise<DetailsItemJSON>, unsubscribe?: boolean);

    /**
     * Adds a listener to document details value change request.
     * @param listener
     * @param unsubscribe - if true, existing listener will be removed. False by default.
     */
    onChangeDetailValue(listener: (uri: string, position: number, itemID: string,
                                   value: string | number| boolean) => Promise<IChangedDocument[]>,
                        unsubscribe?: boolean);

    /**
     * Adds a listener to document cursor position change notification.
     * Must notify listeners in order of registration.
     * @param listener
     * @param unsubscribe - if true, existing listener will be removed. False by default.
     */
    onChangePosition(listener: (uri: string, position: number) => void, unsubscribe?: boolean);

    /**
     * Reports new calculated details when available.
     * @param report - details report.
     */
    detailsAvailable(report: IDetailsReport);

    /**
     * Adds a listener for specific details action execution.
     * @param uri - document uri
     * @param actionId - ID of the action to execute.
     * @param position - optional position in the document.
     * @param unsubscribe - if true, existing listener will be removed. False by default.
     * If not provided, the last reported by positionChanged method will be used.
     */
    onExecuteDetailsAction(listener: (uri: string, actionId: string,
                                      position?: number) => Promise<IChangedDocument[]>,
                           unsubscribe?: boolean): void;

    /**
     * Calculates the list of executable actions available in the current context.
     *
     * @param uri - document uri.
     * @param position - optional position in the document.
     * If not provided, the last reported by positionChanged method will be used.
     * @param target - option target argument.
     * @param unsubscribe - if true, existing listener will be removed. False by default.
     *
     * "TARGET_RAML_EDITOR_NODE" and "TARGET_RAML_TREE_VIEWER_NODE" are potential values
     * for actions based on the editor state and tree viewer state.
     * "TARGET_RAML_EDITOR_NODE" is default.
     */
    onCalculateEditorContextActions(listener: (uri: string,
                                               position?: number) => Promise<IExecutableAction[]>,
                                    unsubscribe?: boolean): void;

    /**
     * Calculates the list of all available executable actions.
     * @param unsubscribe - if true, existing listener will be removed. False by default.
     */
    onAllEditorContextActions(listener: () => Promise<IExecutableAction[]>, unsubscribe?: boolean): void;

    /**
     * Adds a listener for specific action execution.
     * If action has UI, causes a consequent displayActionUI call.
     * @param uri - document uri
     * @param action - action to execute.
     * @param position - optional position in the document.
     * @param unsubscribe - if true, existing listener will be removed. False by default.
     * If not provided, the last reported by positionChanged method will be used.
     */
    onExecuteContextAction(listener: (uri: string, actionId: string,
                                      position?: number) => Promise<IChangedDocument[]>,
                           unsubscribe?: boolean): void;

    /**
     * Adds a listener to display action UI.
     * @param uiDisplayRequest - display request
     * @return final UI state.
     */
    displayActionUI(uiDisplayRequest: IUIDisplayRequest): Promise<any>;

    /**
     * Sets server configuration.
     * @param loggerSettings
     * @param unsubscribe - if true, existing listener will be removed. False by default.
     */
    onSetServerConfiguration(listener: (configuration: IServerConfiguration) => void, unsubscribe?: boolean);
}
