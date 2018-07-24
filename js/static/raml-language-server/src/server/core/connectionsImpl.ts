import {
    IServerConnection
} from "./connections";

import {
    DetailsItemJSON,
    IChangedDocument,
    IDetailsReport,
    IExecutableAction,
    ILocation,
    IOpenedDocument,
    IRange,
    IStructureReport,
    IUIDisplayRequest,
    IValidationIssue,
    IValidationReport,
    MessageSeverity,
    StructureCategories,
    StructureNodeJSON,
    Suggestion
} from "../../common/typeInterfaces";

import utils = require("../../common/utils");
import {IServerConfiguration} from "../../common/configuration";

export abstract class AbstractServerConnection {

    protected openDocumentListeners: {(document: IOpenedDocument): void}[] = [];
    protected changeDocumentListeners: {(document: IChangedDocument): void}[] = [];
    protected closeDocumentListeners: {(uri: string): void}[] = [];
    protected documentStructureListeners: {(uri: string): Promise<{[categoryName: string]: StructureNodeJSON}>}[] = [];
    protected documentCompletionListeners: {(uri: string, position: number): Promise<Suggestion[]>}[] = [];
    protected openDeclarationListeners: {(uri: string, position: number): Promise<ILocation[]>}[] = [];
    protected findreferencesListeners: {(uri: string, position: number): Promise<ILocation[]>}[] = [];
    protected markOccurrencesListeners: {(uri: string, position: number): Promise<IRange[]>}[] = [];
    protected renameListeners: {(uri: string, position: number, newName: string): IChangedDocument[]}[] = [];
    protected documentDetailsListeners: {(uri: string, position: number): Promise<DetailsItemJSON>}[] = [];
    private changeDetailValueListeners: {(uri: string, position: number, itemID: string,
                                          value: string | number| boolean): Promise<IChangedDocument[]>}[] = [];
    protected changePositionListeners: {(uri: string, position: number): void}[] = [];
    protected serverConfigurationListeners: {(configuration: IServerConfiguration): void}[] = [];

    protected calculateEditorContextActionsListeners:
        {(uri: string, position?: number): Promise<IExecutableAction[]>}[] = [];
    protected getAllEditorContextActionsListeners: {():Promise<IExecutableAction[]>}[] = [];

    protected executeContextActionListeners:
        {(uri: string, actionId: string,
          position?: number): Promise<IChangedDocument[]>}[] = [];

    protected executeDetailsActionListeners:
        {(uri: string, actionId: string,
          position?: number): Promise<IChangedDocument[]>}[] = [];

    /**
     * Adds a listener to document open notification. Must notify listeners in order of registration.
     * @param listener
     */
    public onOpenDocument(listener: (document: IOpenedDocument) => void, unsubsribe = false) {

        this.addListener(this.openDocumentListeners, listener, unsubsribe);
    }

    /**
     * Adds a listener to document change notification. Must notify listeners in order of registration.
     * @param listener
     */
    public onChangeDocument(listener: (document: IChangedDocument) => void, unsubsribe = false) {

        this.addListener(this.changeDocumentListeners, listener, unsubsribe);
    }

    /**
     * Adds a listener to document close notification. Must notify listeners in order of registration.
     * @param listener
     */
    public onCloseDocument(listener: (uri: string) => void, unsubsribe = false) {

        this.addListener(this.closeDocumentListeners, listener, unsubsribe);
    }

    /**
     * Adds a listener to document structure request. Must notify listeners in order of registration.
     * @param listener
     */
    public onDocumentStructure(listener: (uri: string) => Promise<{[categoryName: string]: StructureNodeJSON}>,
                               unsubsribe = false) {

        this.addListener(this.documentStructureListeners, listener, unsubsribe);
    }

    /**
     * Adds a listener to document completion request. Must notify listeners in order of registration.
     * @param listener
     */
    public onDocumentCompletion(listener: (uri: string, position: number) => Promise<Suggestion[]>,
                                unsubsribe = false) {

        this.addListener(this.documentCompletionListeners, listener, unsubsribe);
    }

    /**
     * Adds a listener to document open declaration request.  Must notify listeners in order of registration.
     * @param listener
     */
    public onOpenDeclaration(listener: (uri: string, position: number) => Promise<ILocation[]>,
                             unsubsribe = false) {

        this.addListener(this.openDeclarationListeners, listener, unsubsribe);
    }

    /**
     * Adds a listener to document find references request.  Must notify listeners in order of registration.
     * @param listener
     */
    public onFindReferences(listener: (uri: string, position: number) => Promise<ILocation[]>, unsubsribe = false) {

        this.addListener(this.findreferencesListeners, listener, unsubsribe);
    }

    /**
     * Reports new calculated structure when available.
     * @param report - structure report.
     */
    public structureAvailable(report: IStructureReport) {
        // we dont need it
    }

    /**
     * Reports new calculated details when available.
     * @param report - details report.
     */
    public detailsAvailable(report: IDetailsReport) {
        // we dont need it
    }

    /**
     * Marks occurrences of a symbol under the cursor in the current document.
     * @param listener
     */
    public onMarkOccurrences(listener: (uri: string, position: number) => Promise<IRange[]>, unsubsribe = false) {

        this.addListener(this.markOccurrencesListeners, listener, unsubsribe);
    }

    /**
     * Finds the set of document (and non-document files) edits to perform the requested rename.
     * @param listener
     */
    public onRename(listener: (uri: string, position: number, newName: string) => IChangedDocument[],
                    unsubsribe = false) {

        this.addListener(this.renameListeners, listener, unsubsribe);
    }

    /**
     * Adds a listener to document details request. Must notify listeners in order of registration.
     * @param listener
     */
    public onDocumentDetails(listener: (uri: string, position: number) => Promise<DetailsItemJSON>,
                             unsubsribe = false) {

        this.addListener(this.documentDetailsListeners, listener, unsubsribe);
    }

    /**
     * Adds a listener for specific action execution.
     * @param uri - document uri
     * @param actionId - ID of the action to execute.
     * @param position - optional position in the document.
     * If not provided, the last reported by positionChanged method will be used.
     */
    public onExecuteDetailsAction(listener: (uri: string, actionId: string,
                                             position?: number) => Promise<IChangedDocument[]>,
                                  unsubsribe = false): void {

        this.addListener(this.executeDetailsActionListeners, listener, unsubsribe);
    }

    /**
     * Adds a listener to document details value change request.
     * @param listener
     * @param unsubscribe - if true, existing listener will be removed. False by default.
     */
    public onChangeDetailValue(listener: (uri: string, position: number, itemID: string,
                                          value: string | number| boolean) => Promise<IChangedDocument[]>,
                               unsubsribe?: boolean) {
        this.addListener(this.changeDetailValueListeners, listener, unsubsribe);
    }

    /**
     * Adds a listener to document cursor position change notification.
     * Must notify listeners in order of registration.
     * @param listener
     */
    public onChangePosition(listener: (uri: string, position: number) => void,
                            unsubsribe = false) {

        this.addListener(this.changePositionListeners, listener, unsubsribe);
    }

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
    public onCalculateEditorContextActions(listener: (uri: string,
                                                      position?: number) => Promise<IExecutableAction[]>,
                                           unsubsribe = false): void {

        this.addListener(this.calculateEditorContextActionsListeners, listener, unsubsribe);
    }

    /**
     * Calculates the list of all available executable actions.
     */
    public onAllEditorContextActions(listener: () => Promise<IExecutableAction[]>,
                                     unsubsribe = false): void {

        this.addListener(this.getAllEditorContextActionsListeners, listener, unsubsribe);
    }

    /**
     * Adds a listener for specific action execution.
     * If action has UI, causes a consequent displayActionUI call.
     * @param uri - document uri
     * @param action - action to execute.
     * @param position - optional position in the document.
     * If not provided, the last reported by positionChanged method will be used.
     */
    public onExecuteContextAction(listener: (uri: string, actionId: string,
                                             position?: number) => Promise<IChangedDocument[]>,
                                  unsubsribe = false): void {

        this.addListener(this.executeContextActionListeners, listener, unsubsribe);
    }

    /**
     * Sets server configuration.
     * @param loggerSettings
     */
    public onSetServerConfiguration(listener: (configuration: IServerConfiguration) => void,
                                    unsubsribe = false) {

        this.addListener(this.serverConfigurationListeners, listener, unsubsribe);
    }

    /**
     * Adds a listener to display action UI.
     * @param uiDisplayRequest - display request
     * @return final UI state.
     */
    public displayActionUI(uiDisplayRequest: IUIDisplayRequest): Promise<any> {
        return Promise.reject(new Error("displayActionUI not implemented"));
        // TODO implement this
    }

    /**
     * Adds listener.
     * @param memberListeners - member containing array of listeners
     * @param listener - listener to add
     * @param unsubscribe - whether to unsubscribe this listener
     */
    private addListener<T>(memberListeners: T[], listener: T, unsubscribe = false): void {
        if (unsubscribe) {
            const index = memberListeners.indexOf(listener);
            if (index !== -1) {
                memberListeners.splice(index, 1);
            }
        } else {
            memberListeners.push(listener);
        }
    }
}
