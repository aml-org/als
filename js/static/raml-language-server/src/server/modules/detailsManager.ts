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
    IChangedDocument
} from "../../common/typeInterfaces";

import {
    IDisposableModule
} from "./commonInterfaces";

import {
    IActionManagerModule
} from "./customActionsManager";

import rp= require("raml-1-parser");
import lowLevel= rp.ll;
import hl= rp.hl;
import utils = rp.utils;
import ramlOutline = require("raml-outline");
import outlineManagerCommons = require("./outlineManagerCommons");

const universes = rp.universes;

export function createManager(connection: IServerConnection,
                              astManagerModule: IASTManagerModule,
                              editorManagerModule: IEditorManagerModule,
                              customActionsManager: IActionManagerModule): IDisposableModule {

    return new DetailsManager(connection, astManagerModule, editorManagerModule, customActionsManager);
}

export function initialize() {
    outlineManagerCommons.initialize();
}

initialize();

class DetailsManager implements IDisposableModule {

    /**
     * Whether direct calculation is on.
     * @type {boolean}
     */
    private calculatingDetailsOnDirectRequest = false;

    private onDocumentDetailsListener;
    private onNewASTAvailableListener;
    private onChangePositionListener;
    private onChangeDetailValueListener;
    private onExecuteDetailsActionListener;

    /**
     * Remembering positions for opened documents.
     * @type {{}}
     */
    private uriToPositions: {[uri: string]: number} = {};

    constructor(private connection: IServerConnection, private astManagerModule: IASTManagerModule,
                private editorManager: IEditorManagerModule, private actionManagerModule?: IActionManagerModule) {
    }

    public launch() {

        this.onDocumentDetailsListener = (uri, position) => {
            return this.getDetails(uri, position);
        };
        this.connection.onDocumentDetails(this.onDocumentDetailsListener);

        this.onNewASTAvailableListener = (uri: string, version: number, ast: hl.IHighLevelNode) => {

            this.connection.debug("Got new AST report for uri " + uri,
                "DetailsManager", "listen");

            this.calculateAndSendDetailsReport(uri, version);
        };
        this.astManagerModule.onNewASTAvailable(this.onNewASTAvailableListener);

        this.onChangePositionListener = (uri, position) => {

            this.connection.debug("Got new position report for uri " + uri + " : " + position,
                "DetailsManager", "listen");

            this.uriToPositions[uri] = position;

            const editor = this.editorManager.getEditor(uri);

            if (!editor) {
                return;
            }
            const version = editor.getVersion();

            this.calculateAndSendDetailsReport(uri, version);
        };
        this.connection.onChangePosition(this.onChangePositionListener);

        this.onChangeDetailValueListener = (uri, position, itemID, value) => {
            return this.changeDetailValue(uri, position, itemID, value);
        };
        this.connection.onChangeDetailValue(this.onChangeDetailValueListener);

        this.onExecuteDetailsActionListener = (uri, itemID, position) => {
            return this.onExecuteDetailsAction(uri, itemID, position);
        }
        this.connection.onExecuteDetailsAction(this.onExecuteDetailsActionListener);
    }

    public dispose(): void {
        this.connection.onDocumentDetails(this.onDocumentDetailsListener, true);
        this.astManagerModule.onNewASTAvailable(this.onNewASTAvailableListener, true);
        this.connection.onChangePosition(this.onChangePositionListener, true);
        this.connection.onChangeDetailValue(this.onChangeDetailValueListener, true);
        this.connection.onExecuteDetailsAction(this.onExecuteDetailsActionListener, true);
    }

    /**
     * Returns unique module name.
     */
    public getModuleName(): string {
        return "DETAILS_MANAGER";
    }

    public vsCodeUriToParserUri(vsCodeUri: string): string {
        if (vsCodeUri.indexOf("file://") === 0) {
            return vsCodeUri.substring(7);
        }

        return vsCodeUri;
    }

    public getDetails(uri: string, position: number): Promise<DetailsItemJSON> {
        this.connection.debug("Requested details for uri " + uri + " and position " + position, "DetailsManager",
            "getDetails");

        this.calculatingDetailsOnDirectRequest = true;

        return this.calculateDetails(uri, position).then((calculated) => {

            this.connection.debug("Calculation result is not null:" +
                (calculated != null ? "true" : "false"), "DetailsManager",
                "getDetails");

            this.calculatingDetailsOnDirectRequest = false;

            return calculated;

        }).catch((error) => {
            this.calculatingDetailsOnDirectRequest = false;
            throw error;
        });
    }

    public calculateDetails(uri: string, position: number): Promise<DetailsItemJSON> {

        this.connection.debug("Called for uri: " + uri,
            "DetailsManager", "calculateDetails");

        // Forcing current AST to exist
        return this.astManagerModule.forceGetCurrentAST(uri).then((currentAST) => {

            outlineManagerCommons.setOutlineASTProvider(uri, this.astManagerModule,
                                                        this.editorManager, this.connection);

            const result = ramlOutline.getDetailsJSON(position);

            this.connection.debug("Calculation result is not null:" +
                (result != null ? "true" : "false"), "DetailsManager",
                "calculateDetails");

            if (result) {
                this.connection.debugDetail("Calculation result: "
                    + JSON.stringify(result, null, 2), "DetailsManager", "calculateDetails");
            }

            return this.addActionsToDetails(result, uri, position);
        });
    }

    private calculateAndSendDetailsReport(uri: string, version: number) {

        // we do not want reporting while performing the calculation
        if (this.calculatingDetailsOnDirectRequest) {
            return;
        }

        this.connection.debug("Calculating details", "DetailsManager",
            "calculateAndSendDetailsReport");

        const knownPosition = this.uriToPositions[uri];
        this.connection.debug("Found position: " + knownPosition, "DetailsManager",
            "calculateAndSendDetailsReport");

        if (knownPosition != null) {
            this.calculateDetails(uri, knownPosition).then((detailsForUri) => {
                this.connection.debug("Calculation result is not null:" +
                    (detailsForUri != null ? "true" : "false"), "DetailsManager",
                    "calculateAndSendDetailsReport");

                if (detailsForUri) {
                    this.connection.detailsAvailable({
                        uri,
                        position: knownPosition,
                        version,
                        details: detailsForUri
                    });
                }
            });
        }
    }

    private addActionsToDetails(root: DetailsItemJSON, uri: string, position: number): Promise<DetailsItemJSON> {
        if (!this.actionManagerModule || this.actionManagerModule.isDisposed()) {
            return Promise.resolve(root);
        }

        return this.actionManagerModule.calculateEditorActions(uri, position).then((actions) => {
            const generalCategory = this.findOrCreateGeneralCategory(root);

            if (actions && actions.length > 0) {
                for (const action of actions) {

                    const actionItem: ramlOutline.DetailsActionItemJSON = {
                        title: action.name,
                        description: "Activate " + action.name,
                        type: "DETAILS_ACTION",
                        error: null,
                        children: [],
                        id: action.id,
                        subType: "CUSTOM_ACTION"
                    };

                    generalCategory.children.push(actionItem);
                }
            }

            return root;
        });
    }

    private findOrCreateGeneralCategory(root: DetailsItemJSON) : DetailsItemJSON {
        let foundCategory: ramlOutline.DetailsItemJSON = null;

        root.children.forEach((child) => {
            if (child.type === "CATEGORY" && child.title == "General") {
                foundCategory = child;
            }
        });

        if (!foundCategory) {
            foundCategory = {
                title: "General",
                description: "",
                type: "CATEGORY",
                error: null,
                children: [],
                id: "Category#General"
            };
        }

        return foundCategory;
    }

    private changeDetailValue(uri: string, position: number, itemID: string,
                              value: string | number| boolean): Promise<IChangedDocument[]> {

        return this.astManagerModule.forceGetCurrentAST(uri).then((currentAST) => {

            outlineManagerCommons.setOutlineASTProvider(uri, this.astManagerModule,
                this.editorManager, this.connection);

            const result = ramlOutline.changeDetailValue(position, itemID, value)

            this.connection.debug("Change documentn result is not null:" +
                (result != null ? "true" : "false"), "DetailsManager",
                "changeDetailValue");

            if (result) {
                this.connection.debugDetail("Calculation result: "
                    + JSON.stringify(result, null, 2), "DetailsManager", "changeDetailValue");
            }

            return [{
                uri,
                text: result.text
            }];
        });
    }

    private onExecuteDetailsAction(uri: string, itemID: string, position: number): Promise<IChangedDocument[]> {

        return this.astManagerModule.forceGetCurrentAST(uri).then((currentAST) => {

            outlineManagerCommons.setOutlineASTProvider(uri, this.astManagerModule,
                this.editorManager, this.connection);

            const result = ramlOutline.runDetailsAction(position, itemID)

            this.connection.debug("Change documentn result is not null:" +
                (result != null ? "true" : "false"), "DetailsManager",
                "changeDetailValue");

            if (result) {
                this.connection.debugDetail("Calculation result: "
                    + JSON.stringify(result, null, 2), "DetailsManager", "changeDetailValue");
            }

            return [{
                uri,
                text: result.text
            }];
        });
    }
}
