// This module provides RAML module structure

import {
    IServerConnection
} from "../core/connections";

import {
    IASTManagerModule
} from "./astManager";

import {
    IEditorManagerModule
} from "./editorManager";

import {
    DetailsItemJSON,
    IChangedDocument,
    IExecutableAction,
    ILogger,
    IUIDisplayRequest,
    IValidationIssue
} from "../../common/typeInterfaces";

import {
    IDisposableModule,
    IServerModule
} from "./commonInterfaces";

import rp= require("raml-1-parser");
import lowLevel= rp.ll;
import hl= rp.hl;
import utils = rp.utils;
import ramlActions = require("raml-actions");

const universes = rp.universes;

export interface IActionManagerModule extends IDisposableModule {

    calculateEditorActions(uri: string, position?: number):
        Promise<IExecutableAction[]>;
    /**
     * Whether module is disposed.
     */
    isDisposed(): boolean;
}

export function createManager(connection: IServerConnection,
                              astManagerModule: IASTManagerModule,
                              editorManagerModule: IEditorManagerModule): IActionManagerModule {

    return new CustomActionsManager(connection, astManagerModule, editorManagerModule);
}

class EditorProvider implements ramlActions.IEditorProvider {

    constructor(private editorManagerModule: IEditorManagerModule,
                private uri: string) {

    }

    public getCurrentEditor() {
        return this.editorManagerModule.getEditor(this.uri);
    }
}

export function initialize() {

    ramlActions.intializeStandardActions();
}

class ASTProvider implements ramlActions.IASTProvider {
    constructor(private uri: string, private astManagerModule: IASTManagerModule,
                private logger: ILogger, private position: number) {
    }

    public getASTRoot() {

        const result =  this.astManagerModule.getCurrentAST(this.uri) as any;

        this.logger.debugDetail(
            "Got AST from AST provider: " + (result ? "true" : "false"),
            "CustomActionsManager", "ASTProvider#getASTRoot");

        return result;
    }

    public getSelectedNode() {
        const root = this.getASTRoot();
        if (!root) {
            return null;
        }

        return root.findElementAtOffset(this.position);
    }

    /**
     * Gets current AST root asynchronously.
     * Can return null.
     */
    public getASTRootAsync() {
        return Promise.resolve(this.getASTRoot());
    }

    /**
     * Gets current AST node asynchronously
     * Can return null.
     */
    public getSelectedNodeAsync() {
        return Promise.resolve(this.getSelectedNode());
    }
}

class CollectingDocumentChangeExecutor implements ramlActions.IDocumentChangeExecutor {
    private changes: IChangedDocument[] = [];

    constructor(private logger: ILogger) {
    }

    public changeDocument(change: IChangedDocument): Promise<void> {

        this.logger.debugDetail("Registering document change for document " + change.uri +
            ", text is:\n" + change.text,
            "CustomActionsManager", "CollectingDocumentChangeExecutor#changeDocument");

        this.changes.push(change);

        return Promise.resolve(null);
    }

    public getChanges() {
        return this.changes;
    }
}

class ASTModifier implements ramlActions.IASTModifier {

    constructor(private uri: string,
                private changeExecutor: CollectingDocumentChangeExecutor) {}

    public deleteNode(node: hl.IParseResult) {

        const parent = node.parent();
        if (parent) {
            parent.remove(node as any);
            parent.resetChildren();
        }
    }

    public updateText(node: lowLevel.ILowLevelASTNode) {
        const newText = node.unit().contents();

        this.changeExecutor.changeDocument({
            uri: this.uri,
            text: newText,
        });
    }
}

initialize();

class CustomActionsManager implements IDisposableModule {

    private changeExecutor = null;
    private enableUIActions: boolean = true;
    private onCalculateEditorContextActionsListener;
    private getAllActionsListener;
    private onExecuteContextActionListener;
    private onSetServerConfigurationListener;
    private disposed: boolean = false;

    constructor(private connection: IServerConnection, private astManagerModule: IASTManagerModule,
                private editorManager: IEditorManagerModule) {

        this.changeExecutor = new CollectingDocumentChangeExecutor(connection);
    }

    public launch() {
        this.disposed = false;

        this.onCalculateEditorContextActionsListener = (uri, position?) => {
            return this.calculateEditorActions(uri, position);
        }
        this.connection.onCalculateEditorContextActions(
            this.onCalculateEditorContextActionsListener
        );

        this.getAllActionsListener = () => this.getAllActions();
        this.connection.onAllEditorContextActions(this.getAllActionsListener);

        this.onExecuteContextActionListener = (uri, actionId, position?) => {
            this.connection.debug("onExecuteContextAction for uri " + uri,
                "CustomActionsManager",
                "calculateEditorActions");

            return this.executeAction(uri, actionId, position);
        }
        this.connection.onExecuteContextAction(
            this.onExecuteContextActionListener
        );

        this.onSetServerConfigurationListener = (configuration) => {
            if (configuration.actionsConfiguration) {
                if (configuration.actionsConfiguration.enableUIActions !== null) {
                    this.enableUIActions = configuration.actionsConfiguration.enableUIActions;
                }
            }
        }
        this.connection.onSetServerConfiguration(
            this.onSetServerConfigurationListener
        );
    }

    public dispose(): void {
        this.disposed = true;

        this.connection.onCalculateEditorContextActions(
            this.onCalculateEditorContextActionsListener, true
        );

        this.connection.onAllEditorContextActions(this.getAllActionsListener, true);

        this.connection.onExecuteContextAction(
            this.onExecuteContextActionListener, true
        );

        this.connection.onSetServerConfiguration(
            this.onSetServerConfigurationListener, true
        );
    }

    public isDisposed(): boolean {
        return this.disposed;
    }

    /**
     * Returns unique module name.
     */
    public getModuleName(): string {
        return "CUSTOM_ACTIONS_MANAGER";
    }

    public vsCodeUriToParserUri(vsCodeUri: string): string {
        if (vsCodeUri.indexOf("file://") === 0) {
            return vsCodeUri.substring(7);
        }

        return vsCodeUri;
    }

    private getAllActions(): Promise<IExecutableAction[]> {
        const result = ramlActions.allAvailableActions().filter((action) => {
            if (!this.enableUIActions && action.hasUI) {
                return false;
            }

            return true;
        }).map((action) => ({
            id: action.id,
            name : action.name,
            target : action.target,
            category : action.category,
            label : action.label,
            hasUI: action.hasUI
        }));

        return Promise.resolve(result);
    }

    public calculateEditorActions(uri: string, position?: number):
        Promise<IExecutableAction[]> {

        this.connection.debug("Requested actions for uri " + uri
            + " and position " + position, "CustomActionsManager",
            "calculateEditorActions");

        if (!position) {
            position = this.editorManager.getEditor(uri).getCursorPosition();
        }

        const connection = this.connection;

        return this.astManagerModule.forceGetCurrentAST(uri).then((ast) => {

            this.initializeActionsFramework(uri, position);

            connection.debugDetail("Starting to calculate actions",
                "CustomActionsManager", "calculateEditorActions");

            const actions = ramlActions.calculateCurrentActions("TARGET_RAML_EDITOR_NODE");

            connection.debugDetail("Calculated actions: " + actions ? actions.length.toString() : "0",
                "CustomActionsManager", "calculateEditorActions");

            return actions.filter((action) => {
                if (!this.enableUIActions && action.hasUI) {
                    connection.debugDetail("Filtering out action: " + action.id + " as UI actions are disabled",
                        "CustomActionsManager", "calculateEditorActions");
                    return false;
                }

                return true;
            }).map((action) => {
                return {
                    id: action.id,

                    name : action.name,

                    target : action.target,

                    category : action.category,

                    hasUI: action.hasUI,

                    label : action.label
                };
            });
        });
    }

    public executeAction(uri: string, actionId: string,
                         position?: number): Promise<IChangedDocument[]> {

        this.connection.debug("Requested action execution for uri " + uri
            + " and position " + position + " and action" + actionId,
            "CustomActionsManager", "executeAction");

        if (!position) {
            position = this.editorManager.getEditor(uri).getCursorPosition();
        }

        const connection = this.connection;

        const editorManager = this.editorManager;

        return this.astManagerModule.forceGetCurrentAST(uri)
            .then((ast) => {

            this.initializeActionsFramework(uri, position);



            connection.debugDetail("Starting to execute action " + actionId,
                "CustomActionsManager", "executeAction");

            const actionMeta = ramlActions.findActionById(actionId, true);

            if (actionMeta.hasUI) {

                const executeActionPromise =
                    (ramlActions.executeAction(actionId) as Promise<void>);

                connection.debugDetail("Got executeAction promise: " +
                    ((executeActionPromise) ? "true" : "false"),
                    "CustomActionsManager", "executeAction");

                return executeActionPromise.then(() => {
                    connection.debugDetail("Finished to execute action " + actionId,
                        "CustomActionsManager", "executeAction");

                    editorManager.setDocumentChangeExecutor(null);

                    const changes = this.getChangeExecutor().getChanges();

                    connection.debugDetail("Collected changes",
                        "CustomActionsManager", "executeAction");

                    connection.debugDetail("Number of changes: " + changes ? changes.length.toString() : "0",
                        "CustomActionsManager", "executeAction");

                    return changes;
                });

            } else {
                ramlActions.executeAction(actionId);

                connection.debugDetail("Finished to execute action " + actionId,
                    "CustomActionsManager", "executeAction");

                editorManager.setDocumentChangeExecutor(null);

                const changes = this.getChangeExecutor().getChanges();

                connection.debugDetail("Collected changes",
                    "CustomActionsManager", "executeAction");

                connection.debugDetail("Number of changes: " + changes ? changes.length.toString() : "0",
                    "CustomActionsManager", "executeAction");

                return changes;
            }

        }).catch((error) => {
            editorManager.setDocumentChangeExecutor(null);
            throw error;
        });
    }

    private initializeActionsFramework(uri: string, position: number) {

        ramlActions.setEditorProvider(
            new EditorProvider(this.editorManager, uri));

        ramlActions.setASTProvider(new ASTProvider(uri, this.astManagerModule,
            this.connection, position));

        this.changeExecutor = new CollectingDocumentChangeExecutor(this.connection);

        ramlActions.setASTModifier(new ASTModifier(uri, this.changeExecutor));

        ramlActions.setDocumentChangeExecutor(this.changeExecutor);

        this.editorManager.setDocumentChangeExecutor(this.changeExecutor);

        const connection = this.connection;

        ramlActions.setExternalUIDisplayExecutor(
            (actionId: string, externalDisplay: ramlActions.IExternalUIDisplay) => {

                connection.debugDetail("Requested to display UI for action ID " + actionId,
                    "CustomActionsManager", "executeAction#setExternalUIDisplayExecutor");

                const action = ramlActions.findActionById(actionId);

                connection.debugDetail("Action found: " + action ? "true" : "false",
                    "CustomActionsManager", "executeAction#setExternalUIDisplayExecutor");

                return (initialUIState?: any) => {

                    connection.debugDetail("Requested to display UI for action, got UI state",
                        "CustomActionsManager", "executeAction#setExternalUIDisplayExecutor");

                    const uiCode = externalDisplay.createUICode(initialUIState);

                    connection.debugDetail("UI code generated: " + (uiCode ? "true" : "false"),
                        "CustomActionsManager", "executeAction#setExternalUIDisplayExecutor");

                    connection.debugDetail("Requesting client for UI code display.",
                        "CustomActionsManager", "executeAction#setExternalUIDisplayExecutor");

                    connection.debugDetail("UI state is: " + JSON.stringify(initialUIState),
                        "CustomActionsManager", "executeAction#setExternalUIDisplayExecutor");

                    return connection.displayActionUI({

                        action: {
                            id: action.id,
                            name: action.name,
                            target: action.target,
                            hasUI : action.hasUI,
                            category: action.category,
                            label: action.label
                        },

                        uiCode,

                        initialUIState
                    }).then((finalUIState) => {
                        connection.debugDetail(
                            "Client answered with fina UI state for action " + actionId +
                            " , got final UI state: " + finalUIState ? "true" : "false",
                            "CustomActionsManager", "executeAction#setExternalUIDisplayExecutor");

                        return finalUIState;
                    });
                };

            });

        ramlActions.setLogger(this.connection);
    }

    private getChangeExecutor(): CollectingDocumentChangeExecutor {
        return this.changeExecutor;
    }
}
