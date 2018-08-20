import { IServerConnection } from "../core/connections";
import { IChangedDocument, IDocumentChangeExecutor } from "../../common/typeInterfaces";
import { IAbstractTextEditorWithCursor, IServerModule } from "./commonInterfaces";
export interface IEditorManagerModule extends IServerModule {
    launch(): void;
    getEditor(uri: string): IAbstractTextEditorWithCursor;
    onChangeDocument(listener: (document: IChangedDocument) => void, unsubscribe?: boolean): any;
    /**
     * Sets document change executor to use when editor buffer text modification
     * methods are being called.
     * @param executor
     */
    setDocumentChangeExecutor(executor: IDocumentChangeExecutor): void;
}
export declare function createManager(connection: IServerConnection): IEditorManagerModule;
