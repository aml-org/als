import { DetailsItemJSON, IChangedDocument, IDetailsReport, IExecutableAction, ILocation, IOpenedDocument, IRange, IStructureReport, IUIDisplayRequest, StructureNodeJSON, Suggestion } from "../../common/typeInterfaces";
import { IServerConfiguration } from "../../common/configuration";
export declare abstract class AbstractServerConnection {
    protected openDocumentListeners: {
        (document: IOpenedDocument): void;
    }[];
    protected changeDocumentListeners: {
        (document: IChangedDocument): void;
    }[];
    protected closeDocumentListeners: {
        (uri: string): void;
    }[];
    protected documentStructureListeners: {
        (uri: string): Promise<{
            [categoryName: string]: StructureNodeJSON;
        }>;
    }[];
    protected documentCompletionListeners: {
        (uri: string, position: number): Promise<Suggestion[]>;
    }[];
    protected openDeclarationListeners: {
        (uri: string, position: number): Promise<ILocation[]>;
    }[];
    protected findreferencesListeners: {
        (uri: string, position: number): Promise<ILocation[]>;
    }[];
    protected markOccurrencesListeners: {
        (uri: string, position: number): Promise<IRange[]>;
    }[];
    protected renameListeners: {
        (uri: string, position: number, newName: string): IChangedDocument[];
    }[];
    protected documentDetailsListeners: {
        (uri: string, position: number): Promise<DetailsItemJSON>;
    }[];
    private changeDetailValueListeners;
    protected changePositionListeners: {
        (uri: string, position: number): void;
    }[];
    protected serverConfigurationListeners: {
        (configuration: IServerConfiguration): void;
    }[];
    protected calculateEditorContextActionsListeners: {
        (uri: string, position?: number): Promise<IExecutableAction[]>;
    }[];
    protected getAllEditorContextActionsListeners: {
        (): Promise<IExecutableAction[]>;
    }[];
    protected executeContextActionListeners: {
        (uri: string, actionId: string, position?: number): Promise<IChangedDocument[]>;
    }[];
    protected executeDetailsActionListeners: {
        (uri: string, actionId: string, position?: number): Promise<IChangedDocument[]>;
    }[];
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
     * Reports new calculated structure when available.
     * @param report - structure report.
     */
    structureAvailable(report: IStructureReport): void;
    /**
     * Reports new calculated details when available.
     * @param report - details report.
     */
    detailsAvailable(report: IDetailsReport): void;
    /**
     * Marks occurrences of a symbol under the cursor in the current document.
     * @param listener
     */
    onMarkOccurrences(listener: (uri: string, position: number) => Promise<IRange[]>, unsubsribe?: boolean): void;
    /**
     * Finds the set of document (and non-document files) edits to perform the requested rename.
     * @param listener
     */
    onRename(listener: (uri: string, position: number, newName: string) => IChangedDocument[], unsubsribe?: boolean): void;
    /**
     * Adds a listener to document details request. Must notify listeners in order of registration.
     * @param listener
     */
    onDocumentDetails(listener: (uri: string, position: number) => Promise<DetailsItemJSON>, unsubsribe?: boolean): void;
    /**
     * Adds a listener for specific action execution.
     * @param uri - document uri
     * @param actionId - ID of the action to execute.
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
     * Sets server configuration.
     * @param loggerSettings
     */
    onSetServerConfiguration(listener: (configuration: IServerConfiguration) => void, unsubsribe?: boolean): void;
    /**
     * Adds a listener to display action UI.
     * @param uiDisplayRequest - display request
     * @return final UI state.
     */
    displayActionUI(uiDisplayRequest: IUIDisplayRequest): Promise<any>;
    /**
     * Adds listener.
     * @param memberListeners - member containing array of listeners
     * @param listener - listener to add
     * @param unsubscribe - whether to unsubscribe this listener
     */
    private addListener<T>(memberListeners, listener, unsubscribe?);
}
