import { IServerConnection } from "../core/connections";
import { IASTManagerModule } from "./astManager";
import { IDisposableModule } from "./commonInterfaces";
import { IEditorManagerModule } from "./editorManager";
export declare function createManager(connection: IServerConnection, astManagerModule: IASTManagerModule, editorManagerModule: IEditorManagerModule): IDisposableModule;
