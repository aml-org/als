import {
    IServerConnection
} from "./connections";

import {
    IDisposableModule,
    isDisposableModule,
    IServerModule
} from "../modules/commonInterfaces";

import EditorManagerModule = require("../modules/editorManager");

import ASTManagerModule = require("../modules/astManager");

import ValidationManagerModule = require("../modules/validationManager");

import StructureManagerModule = require("../modules/structureManager");

import CompletionManagerModule = require("../modules/completionManager");

import FixedActionsManagerModule = require("../modules/fixedActionsManager");

import DetailsManagerModule = require("../modules/detailsManager");

import CustomActionsManagerModule = require("../modules/customActionsManager");

export class Server {

    /**
     * Map from module name to module.
     */
    private modules: {[moduleName: string]: IServerModule} = {};

    /**
     * Map from module name to its enablement state.
     */
    private modulesEnablementState: {[moduleName: string]: boolean} = {}

    constructor(private connection: IServerConnection) {

        const editorManagerModule = EditorManagerModule.createManager(connection);
        this.registerModule(editorManagerModule);

        const astManagerModule = ASTManagerModule.createManager(connection,
            editorManagerModule);
        this.registerModule(astManagerModule);

        this.registerModule(ValidationManagerModule.createManager(connection,
            astManagerModule, editorManagerModule));

        this.registerModule(StructureManagerModule.createManager(connection,
            astManagerModule, editorManagerModule));

        this.registerModule(CompletionManagerModule.createManager(connection,
            astManagerModule, editorManagerModule));

        this.registerModule(FixedActionsManagerModule.createManager(connection,
            astManagerModule, editorManagerModule));

        const customActionsManager = CustomActionsManagerModule.createManager(connection,
            astManagerModule, editorManagerModule);
        this.registerModule(customActionsManager, true);

        this.registerModule(DetailsManagerModule.createManager(connection,
            astManagerModule, editorManagerModule, customActionsManager), true);
    }

    public registerModule(module: IServerModule, defaultEnablementState = true): void {
        const moduleName = module.getModuleName();

        if (!moduleName) {
            this.connection.error("No name for module!", "server", "registerModule");
        }

        this.modules[moduleName] = module;
        this.modulesEnablementState[moduleName] = defaultEnablementState;
    }

    public enableModule(moduleName: string): void {

        this.connection.debugDetail("Changing module enablement of " + moduleName +
            " to true",
            "server", "onSetServerConfiguration");

        if (this.modulesEnablementState[moduleName]) {

            this.connection.debugDetail("Module already enabled " + moduleName ,
                                        "server", "onSetServerConfiguration");

            return;
        }

        const module = this.modules[moduleName];
        if (!module) {
            this.connection.error("Cant not enable unknown module " + moduleName,
                "server", "enableModule");
        }

        module.launch();
        this.modulesEnablementState[moduleName] = true;
    }

    public disableModule(moduleName: string): void {
        this.connection.debugDetail("Changing module enablement of " + moduleName +
            " to false",
            "server", "onSetServerConfiguration");

        if (!this.modulesEnablementState[moduleName]) {

            this.connection.debugDetail("Module already disabled " + moduleName ,
                "server", "onSetServerConfiguration");

            return;
        }

        const module = this.modules[moduleName];
        if (!module) {
            this.connection.error("Cant not enable unknown module " + moduleName,
                "server", "disableModule");
        }

        if (isDisposableModule(module)) {
            module.dispose();
        } else {
            this.connection.warning("Attempt to disable non-disposable module " + moduleName,
                "server", "disableModule");
        }

        this.modulesEnablementState[moduleName] = false;
    }

    public listen(): void {

        this.connection.onSetServerConfiguration((configuration) => {
            if (!configuration.modulesConfiguration) {
                return;
            }

            this.checkAndChangeEnablement(configuration.modulesConfiguration.enableCustomActionsModule,
                "CUSTOM_ACTIONS_MANAGER");

            this.checkAndChangeEnablement(configuration.modulesConfiguration.enableDetailsModule,
                "DETAILS_MANAGER");

            this.checkAndChangeEnablement(configuration.modulesConfiguration.enableASTManagerModule,
                "AST_MANAGER");

            this.checkAndChangeEnablement(configuration.modulesConfiguration.enableCompletionManagerModule,
                "COMPLETION_MANAGER");

            this.checkAndChangeEnablement(configuration.modulesConfiguration.enableEditorManagerModule,
                "EDITOR_MANAGER");

            this.checkAndChangeEnablement(configuration.modulesConfiguration.enableFixedActionsModule,
                "FIXED_ACTIONS_MANAGER");

            this.checkAndChangeEnablement(configuration.modulesConfiguration.enableStructureManagerModule,
                "STRUCTURE_MANAGER");

            this.checkAndChangeEnablement(configuration.modulesConfiguration.enableValidationManagerModule,
                "VALIDATION_MANAGER");

            if (configuration.modulesConfiguration.allModules != null) {

                this.connection.debugDetail("Changing module enablement of all modules" +
                    " to " + configuration.modulesConfiguration.allModules,
                    "server", "onSetServerConfiguration");

                for (const moduleName in this.modules) {
                    if (this.modules.hasOwnProperty(moduleName)) {
                        configuration.modulesConfiguration.allModules ?
                            this.enableModule(moduleName) : this.disableModule(moduleName);
                    }
                }
            }
        })

        for (const moduleName in this.modules) {
            if (this.modules.hasOwnProperty(moduleName)) {
                if (this.modulesEnablementState[moduleName]) {
                    this.modules[moduleName].launch();
                }
            }
        }
    }

    private checkAndChangeEnablement(enablementFlag: boolean, moduleId: string) {
        if (enablementFlag != null) {

            this.connection.debug("Changing module enablement of " + moduleId +
                " to " + enablementFlag,
                "server", "onSetServerConfiguration");

            enablementFlag ?
                this.enableModule(moduleId) : this.disableModule(moduleId);

        }
    }

}
