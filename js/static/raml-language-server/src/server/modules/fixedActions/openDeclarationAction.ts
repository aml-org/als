// This module provides a fixed action for finding declaration of RAML node

import {
    IServerConnection
} from "../../core/connections";

import {
    IASTManagerModule
} from "../astManager";

import {
    IEditorManagerModule
} from "../editorManager";

import {
    ILocation,
    IRange
} from "../../../common/typeInterfaces";

import rp= require("raml-1-parser");
import search = rp.search;
import lowLevel= rp.ll;
import hl= rp.hl;

import {
    IDisposableModule
} from "../../modules/commonInterfaces";

import utils = require("../../../common/utils");
import fixedActionCommon = require("./fixedActionsCommon");

export interface IOpenDeclarationActionModule extends IDisposableModule {
    openDeclaration(uri: string, position: number): Promise<ILocation[]>;
}

export function createManager(connection: IServerConnection,
                              astManagerModule: IASTManagerModule,
                              editorManagerModule: IEditorManagerModule)
                            : IOpenDeclarationActionModule {

    return new OpenDeclarationActionModule(connection, astManagerModule, editorManagerModule);
}

/**
 * Handles "open declaration" action.
 */
class OpenDeclarationActionModule implements IOpenDeclarationActionModule {

    private onOpenDeclarationListener;

    constructor(private connection: IServerConnection, private astManagerModule: IASTManagerModule,
                private editorManagerModule: IEditorManagerModule) {
    }

    public launch() {

        this.onOpenDeclarationListener = (uri: string, position: number) => {
            return this.openDeclaration(uri, position);
        }
        this.connection.onOpenDeclaration(this.onOpenDeclarationListener);
    }

    public dispose(): void {
        this.connection.onOpenDeclaration(this.onOpenDeclarationListener, true);
    }

    /**
     * Returns unique module name.
     */
    public getModuleName(): string {
        return "OPEN_DECLARATION_ACTION";
    }

    public openDeclaration(uri: string, position: number): Promise<ILocation[]> {

        this.connection.debug("Called for uri: " + uri,
            "FixedActionsManager", "openDeclaration");

        this.connection.debugDetail("Uri extname: " + utils.extName(uri),
            "FixedActionsManager", "openDeclaration");

        if (utils.extName(uri) !== ".raml") {
            return Promise.resolve([]);
        }

        const connection = this.connection;

        return this.astManagerModule.forceGetCurrentAST(uri).then((ast) => {
            connection.debugDetail("Found AST: " + (ast ? "true" : false),
                "FixedActionsManager", "openDeclaration");

            if (!ast) {
                return [];
            }

            const unit = ast.lowLevel().unit();

            const decl = search.findDeclaration(unit, position);

            connection.debugDetail("Found declaration: " + (decl ? "true" : false),
                "FixedActionsManager", "openDeclaration");

            if (!decl) {
                return [];
            }

            if (!(decl as any).absolutePath) {
                const location = fixedActionCommon.lowLevelNodeToLocation(uri,
                    (decl as hl.IParseResult).lowLevel(),
                    this.editorManagerModule, connection);
                if (!location) {
                    return [];
                }

                return [location];
            } else {
                const absolutePath = (decl as lowLevel.ICompilationUnit).absolutePath();

                if (utils.isHTTPUri(absolutePath)) {
                    return [];
                }

                return [{
                    uri: absolutePath,
                    range: {
                        start: 0,
                        end: 0
                    }
                }];
            }
        });


    }

}
