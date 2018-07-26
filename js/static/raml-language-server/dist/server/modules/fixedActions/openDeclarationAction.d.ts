import { IServerConnection } from "../../core/connections";
import { IASTManagerModule } from "../astManager";
import { IEditorManagerModule } from "../editorManager";
import { ILocation } from "../../../common/typeInterfaces";
import { IDisposableModule } from "../../modules/commonInterfaces";
export interface IOpenDeclarationActionModule extends IDisposableModule {
    openDeclaration(uri: string, position: number): Promise<ILocation[]>;
}
export declare function createManager(connection: IServerConnection, astManagerModule: IASTManagerModule, editorManagerModule: IEditorManagerModule): IOpenDeclarationActionModule;
