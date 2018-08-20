import { IServerConnection } from "../../core/connections";
import { IASTManagerModule } from "../astManager";
import { IEditorManagerModule } from "../editorManager";
import { IDisposableModule } from "../../modules/commonInterfaces";
export declare function createManager(connection: IServerConnection, astManagerModule: IASTManagerModule, editorManagerModule: IEditorManagerModule): IDisposableModule;
