// This manager handles fixed actions as opposed to dynamic context-depended actions

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
    IDisposableModule
} from "./commonInterfaces";

import findReferencesModule = require("./fixedActions/findReferencesAction");
import markOccurrencesModule = require("./fixedActions/markOccurrencesAction");
import openDeclarationModule = require("./fixedActions/openDeclarationAction");
import renameModule = require("./fixedActions/renameAction");

import fixedActionsCommon = require("./fixedActions/fixedActionsCommon");

export function createManager(connection: IServerConnection,
                              astManagerModule: IASTManagerModule,
                              editorManagerModule: IEditorManagerModule): IDisposableModule {

    return new FixedActionsManager(connection, astManagerModule, editorManagerModule);
}

class FixedActionsManager implements IDisposableModule {

    private subModules: IDisposableModule[] = [];

    constructor(
        private connection: IServerConnection,
        private astManagerModule: IASTManagerModule,
        private editorManagerModule: IEditorManagerModule) {

        this.subModules.push(openDeclarationModule.createManager(
            this.connection, this.astManagerModule, this.editorManagerModule
        ));

        this.subModules.push(findReferencesModule.createManager(
            this.connection, this.astManagerModule, this.editorManagerModule
        ));

        this.subModules.push(markOccurrencesModule.createManager(
            this.connection, this.astManagerModule, this.editorManagerModule
        ));

        this.subModules.push(renameModule.createManager(
            this.connection, this.astManagerModule, this.editorManagerModule
        ));
    }

    public launch() {
        this.subModules.forEach((subModule) => subModule.launch());
    }

    public dispose(): void {
        this.subModules.forEach((subModule) => subModule.dispose());
    }

    /**
     * Returns unique module name.
     */
    public getModuleName(): string {
        return "FIXED_ACTIONS_MANAGER";
    }
}
