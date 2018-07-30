import { IServerConnection } from "./connections";
import { IServerModule } from "../modules/commonInterfaces";
export declare class Server {
    private connection;
    /**
     * Map from module name to module.
     */
    private modules;
    /**
     * Map from module name to its enablement state.
     */
    private modulesEnablementState;
    constructor(connection: IServerConnection);
    registerModule(module: IServerModule, defaultEnablementState?: boolean): void;
    enableModule(moduleName: string): void;
    disableModule(moduleName: string): void;
    listen(): void;
    private checkAndChangeEnablement(enablementFlag, moduleId);
}
