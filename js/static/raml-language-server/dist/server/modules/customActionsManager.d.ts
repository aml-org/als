import { IServerConnection } from "../core/connections";
import { IASTManagerModule } from "./astManager";
import { IEditorManagerModule } from "./editorManager";
import { IExecutableAction } from "../../common/typeInterfaces";
import { IDisposableModule } from "./commonInterfaces";
export interface IActionManagerModule extends IDisposableModule {
    calculateEditorActions(uri: string, position?: number): Promise<IExecutableAction[]>;
    /**
     * Whether module is disposed.
     */
    isDisposed(): boolean;
}
export declare function createManager(connection: IServerConnection, astManagerModule: IASTManagerModule, editorManagerModule: IEditorManagerModule): IActionManagerModule;
export declare function initialize(): void;
