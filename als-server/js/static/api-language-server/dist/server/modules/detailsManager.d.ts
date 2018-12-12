import { IServerConnection } from "../core/connections";
import { IASTManagerModule } from "./astManager";
import { IEditorManagerModule } from "./editorManager";
import { IDisposableModule } from "./commonInterfaces";
import { IActionManagerModule } from "./customActionsManager";
export declare function createManager(connection: IServerConnection, astManagerModule: IASTManagerModule, editorManagerModule: IEditorManagerModule, customActionsManager: IActionManagerModule): IDisposableModule;
export declare function initialize(): void;
