import {
    DetailsItemJSON,
    IChangedDocument,
    IDetailsReport,
    IExecutableAction,
    ILocation,
    ILogger,
    IOpenedDocument,
    IRange,
    IServerConnection,
    IUIDisplayRequest,
    IValidationIssue,
    IValidationReport,
    MessageSeverity,
    StructureNodeJSON,
    Suggestion
} from "../../../server/core/connections";

import {
    MessageToClientType,
    MessageToServerType, ProtocolMessage
} from "../../common/protocol";

import {
    MessageDispatcher
} from "../../common/messageDispatcher";

import {ILoggerSettings, IStructureReport} from "../../../common/typeInterfaces";

import {
    filterLogMessage
} from "../../../common/utils";
import {IServerConfiguration} from "../../../common/configuration";

export abstract class AbstractMSServerConnection extends MessageDispatcher<MessageToClientType>
    implements IServerConnection {

    private loggerSettings: ILoggerSettings;

    private openDocumentListeners: {(document: IOpenedDocument): void}[] = [];
    private changeDocumentListeners: {(document: IChangedDocument): void}[] = [];
    private closeDocumentListeners: {(uri: string): void}[] = [];
    private documentStructureListeners: {(uri: string): Promise<{[categoryName: string]: StructureNodeJSON}>}[] = [];
    private documentCompletionListeners: {(uri: string, position: number): Promise<Suggestion[]>}[] = [];
    private openDeclarationListeners: {(uri: string, position: number): Promise<ILocation[]>}[] = [];
    private findReferencesListeners: {(uri: string, position: number): Promise<ILocation[]>}[] = [];
    private markOccurrencesListeners: {(uri: string, position: number): Promise<IRange[]>}[] = [];
    private renameListeners: {(uri: string, position: number, newName: string): IChangedDocument[]}[] = [];
    private documentDetailsListeners: {(uri: string, position: number): Promise<DetailsItemJSON>}[] = [];
    private changeDetailValueListeners: {(uri: string, position: number, itemID: string,
                                          value: string | number| boolean): Promise<IChangedDocument[]>}[] = [];
    private changePositionListeners: {(uri: string, position: number): void}[] = [];
    private serverConfigurationListeners: {(configuration: IServerConfiguration): void}[] = [];

    private calculateEditorContextActionsListeners:
        {(uri: string, position?: number): Promise<IExecutableAction[]>}[] = [];
    private getAllEditorContextActionsListeners: {() : Promise<IExecutableAction[]>}[] = [];

    private executeContextActionListeners:
        {(uri: string, actionId: string,
          position?: number): Promise<IChangedDocument[]>}[] = [];

    private executeDetailsActionListeners:
        {(uri: string, actionId: string,
          position?: number): Promise<IChangedDocument[]>}[] = [];

    constructor(name: string) {
        super(name);
    }

    public abstract sendMessage(message: ProtocolMessage<MessageToClientType>): void;

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
                             unsubsribe = false): void {

        this.addListener(this.openDeclarationListeners, listener, unsubsribe);
    }

    /**
     * Adds a listener to document find references request.  Must notify listeners in order of registration.
     * @param listener
     */
    public onFindReferences(listener: (uri: string, position: number) => Promise<ILocation[]>,
                            unsubsribe = false) {

        this.addListener(this.findReferencesListeners, listener, unsubsribe);
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
     * Reports latest validation results
     * @param report
     */
    public validated(report: IValidationReport): void {
        this.send({
            type : "VALIDATION_REPORT",
            payload : report
        });
    }

    /**
     * Reports new calculated structure when available.
     * @param uri - document uri
     * @param structure - structure for the document
     */
    public structureAvailable(report: IStructureReport) {
        this.send({
            type : "STRUCTURE_REPORT",
            payload : report
        });
    }

    /**
     * Returns whether path/url exists.
     * @param fullPath
     */
    public exists(path: string): Promise<boolean> {
        return this.sendWithResponse({
            type: "EXISTS",
            payload: path
        });
    }

    /**
     * Returns directory content list.
     * @param fullPath
     */
    public readDir(path: string): Promise<string[]> {
        return this.sendWithResponse({
            type: "READ_DIR",
            payload: path
        });
    }

    /**
     * Returns whether path/url represents a directory
     * @param path
     */
    public isDirectory(path: string): Promise<boolean> {
        return this.sendWithResponse({
            type: "IS_DIRECTORY",
            payload: path
        });
    }

    /**
     * File contents by full path/url.
     * @param path
     */
    public content(path: string): Promise<string> {
        return this.sendWithResponse({
            type: "CONTENT",
            payload: path
        });
    }

    /**
     * Marks occurrences of a symbol under the cursor in the current document.
     * @param listener
     */
    public onMarkOccurrences(listener: (uri: string, position: number) => Promise<IRange[]>,
                             unsubsribe = false) {
        this.addListener(this.markOccurrencesListeners, listener, unsubsribe);
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
     * Adds a listener for specific details action execution.
     * @param uri - document uri
     * @param action - action to execute.
     * @param position - optional position in the document.
     * If not provided, the last reported by positionChanged method will be used.
     */
    public onExecuteDetailsAction(listener: (uri: string, actionId: string,
                                             position?: number)
                                      => Promise<IChangedDocument[]>,
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
     * Reports new calculated details when available.
     * @param report - details report.
     */
    public detailsAvailable(report: IDetailsReport) {
        this.send({
            type : "DETAILS_REPORT",
            payload : report
        });
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
     * Handler of OPEN_DOCUMENT message.
     * @param document
     * @constructor
     */
    public OPEN_DOCUMENT(document: IOpenedDocument): void {
        for (const listener of this.openDocumentListeners) {
            listener(document);
        }
    }

    /**
     * Handler of CHANGE_DOCUMENT message.
     * @param document
     * @constructor
     */
    public CHANGE_DOCUMENT(document: IChangedDocument): void {
        for (const listener of this.changeDocumentListeners) {
            listener(document);
        }
    }

    /**
     * Handler of CLOSE_DOCUMENT message.
     * @param uri
     * @constructor
     */
    public CLOSE_DOCUMENT(uri: string): void {
        for (const listener of this.closeDocumentListeners) {
            listener(uri);
        }
    }

    /**
     * Handler of GET_STRUCTURE message.
     * @param uri
     * @constructor
     */
    public GET_STRUCTURE(uri: string): Promise<{[categoryName: string]: StructureNodeJSON}> {
        if (this.documentStructureListeners.length === 0) {
            return Promise.resolve({});
        }

        return this.documentStructureListeners[0](uri);
    }

    /**
     * Handler of GET_SUGGESTIONS message.
     * @param uri - document uri
     * @param position - offset in the document starting from 0
     * @constructor
     */
    public GET_SUGGESTIONS(payload: {uri: string, position: number}): Promise<Suggestion[]> {
        if (this.documentCompletionListeners.length === 0) {
            return Promise.resolve([]);
        }

        const promises = [];
        for (const listener of this.documentCompletionListeners) {
            this.debugDetail("Calling a listener",
                "ProxyServerConnection", "getCompletion");

            const listenerResult = listener(payload.uri, payload.position);
            if (listenerResult) {
                promises.push(listenerResult);
            }
        }

        return Promise.all(promises).then((resolvedResults) => {

            let result = [];
            for (const currentPromiseResult of resolvedResults) {
                result = result.concat(currentPromiseResult);
            }

            return result;
        });

    }

    /**
     * Handler of OPEN_DECLARATION message.
     * @param uri - document uri
     * @param position - offset in the document starting from 0
     * @constructor
     */
    public OPEN_DECLARATION(payload: {uri: string, position: number}): Promise<ILocation[]> {
        if (this.openDeclarationListeners.length === 0) {
            return Promise.resolve([]);
        }

        return this.openDeclarationListeners[0](payload.uri, payload.position);
    }

    /**
     * Handler of FIND_REFERENCES message.
     * @param uri - document uri
     * @param position - offset in the document starting from 0
     * @constructor
     */
    public FIND_REFERENCES(payload: {uri: string, position: number}): Promise<ILocation[]> {
        if (this.findReferencesListeners.length === 0) {
            return Promise.resolve([]);
        }

        return this.findReferencesListeners[0](payload.uri, payload.position);
    }

    /**
     * Handler of MARK_OCCURRENCES message.
     * @param uri - document uri
     * @param position - offset in the document starting from 0
     * @constructor
     */
    public MARK_OCCURRENCES(payload: {uri: string, position: number}): Promise<IRange[]> {
        if (this.markOccurrencesListeners.length === 0) {
            return Promise.resolve([]);
        }

        return this.markOccurrencesListeners[0](payload.uri, payload.position);
    }

    /**
     * Handler of RENAME message.
     * @param uri - document uri
     * @param position - offset in the document starting from 0
     * @param newName - new name
     * @constructor
     */
    public RENAME(payload: {uri: string, position: number, newName: string}): IChangedDocument[] {
        if (this.renameListeners.length === 0) {
            return [];
        }

        let result = [];
        for (const listener of this.renameListeners) {
            result = result.concat(listener(payload.uri, payload.position, payload.newName));
        }

        return result;
    }

    public SET_LOGGER_CONFIGURATION(payload: ILoggerSettings): void {

        this.setLoggerConfiguration(payload);
    }

    /**
     * Handler of GET_STRUCTURE message.
     * @param uri
     * @constructor
     */
    public GET_DETAILS(payload: {uri: string, position: number}): Promise<DetailsItemJSON> {
        if (this.documentDetailsListeners.length === 0) {
            return Promise.resolve(null);
        }

        return this.documentDetailsListeners[0](payload.uri, payload.position);
    }

    /**
     * Handler of GET_STRUCTURE message.
     * @param uri
     * @constructor
     */
    public CHANGE_DETAIL_VALUE(payload: {uri: string, position: number, itemID: string,
                                   value: string | number| boolean}): Promise<IChangedDocument[]> {
        if (this.changeDetailValueListeners.length === 0) {
            return Promise.resolve(null);
        }

        return this.changeDetailValueListeners[0](payload.uri, payload.position,
                                                  payload.itemID, payload.value);
    }

    /**
     * Handler for CHANGE_POSITION message.
     * @param payload
     * @constructor
     */
    public CHANGE_POSITION(payload: {uri: string, position: number}): void {
        for (const listener of this.changePositionListeners) {
            listener(payload.uri, payload.position);
        }
    }

    /**
     * Handler for SET_SERVER_CONFIGURATION message.
     * @param payload
     * @constructor
     */
    public SET_SERVER_CONFIGURATION(payload: IServerConfiguration): void {

        for (const listener of this.serverConfigurationListeners) {
            listener(payload);
        }
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
                                                      position?: number)
                                           => Promise<IExecutableAction[]>,
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
                                             position?: number)
                                  => Promise<IChangedDocument[]>,
                                  unsubsribe = false): void {

        this.addListener(this.executeContextActionListeners, listener, unsubsribe);
    }

    /**
     * Adds a listener to display action UI.
     * @param uiDisplayRequest - display request
     * @return final UI state.
     */
    public displayActionUI(uiDisplayRequest: IUIDisplayRequest): Promise<any> {
        return this.sendWithResponse({
            type : "DISPLAY_ACTION_UI",
            payload : uiDisplayRequest
        });
    }

    public EXECUTE_DETAILS_ACTION(payload: {uri: string, actionId: string,
                              position?: number}): Promise<IChangedDocument[]> {

        this.debugDetail("Called",
            "ProxyServerConnection", "EXECUTE_DETAILS_ACTION");

        if (this.executeDetailsActionListeners.length === 0) {
            return Promise.resolve([]);
        }

        this.debugDetail("Before execution",
            "ProxyServerConnection", "EXECUTE_DETAILS_ACTION");

        try {
            const result = this.executeDetailsActionListeners[0](payload.uri, payload.actionId,
                payload.position);
            return result;
        } catch (Error) {
            this.debugDetail("Failed listener execution: " + Error.message,
                "ProxyServerConnection", "EXECUTE_DETAILS_ACTION");
        }
    }

    public CALCULATE_ACTIONS(payload: {uri: string, position?: number}): Promise<IExecutableAction[]> {
        if (this.calculateEditorContextActionsListeners.length === 0) {
            return Promise.resolve([]);
        }

        return this.calculateEditorContextActionsListeners[0](payload.uri, payload.position);
    }

    public ALL_ACTIONS(payload:{uri: string, position?: number}) : Promise<IExecutableAction[]> {
        if (this.getAllEditorContextActionsListeners.length === 0) {
            return Promise.resolve([]);
        }

        return this.getAllEditorContextActionsListeners[0]();
    }

    public EXECUTE_ACTION(payload: {uri: string, actionId: string,
                   position?: number}): Promise<IChangedDocument[]> {

        this.debugDetail("Called",
            "ProxyServerConnection", "EXECUTE_ACTION");

        if (this.executeContextActionListeners.length === 0) {
            return Promise.resolve([]);
        }

        this.debugDetail("Before execution",
            "ProxyServerConnection", "EXECUTE_ACTION");

        try {
            const result = this.executeContextActionListeners[0](payload.uri, payload.actionId,
                payload.position);
            return result;
        } catch (Error) {
            this.debugDetail("Failed listener execution: " + Error.message,
                "ProxyServerConnection", "EXECUTE_ACTION");
        }
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
