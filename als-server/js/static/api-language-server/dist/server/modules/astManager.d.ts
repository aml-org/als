import parser = require("raml-1-parser");
import { IServerConnection } from "../core/connections";
import { IEditorManagerModule } from "./editorManager";
import { IDisposableModule } from "./commonInterfaces";
export declare type IHighLevelNode = parser.hl.IHighLevelNode;
export interface IASTListener {
    (uri: string, version: number, ast: IHighLevelNode, error?: Error): void;
}
/**
 * Manager of AST states.
 */
export interface IASTManagerModule extends IDisposableModule {
    /**
     * Start listening to the connection.
     */
    launch(): any;
    /**
     * Returns currently available AST for the document, if any
     * @param uri
     */
    getCurrentAST(uri: string): IHighLevelNode;
    /**
     * Gets current AST if there is any.
     * If not, performs immediate asynchronous parsing and returns the results.
     * @param uri
     */
    forceGetCurrentAST(uri: string): Promise<IHighLevelNode>;
    /**
     * Adds listener for new ASTs being parsed.
     * @param listener
     * @param unsubscribe - if true, existing listener will be removed. False by default.
     */
    onNewASTAvailable(listener: IASTListener, unsubscribe?: boolean): any;
}
/**
 * Creates new AST manager
 * @param connection
 * @returns {ASTManager}
 */
export declare function createManager(connection: IServerConnection, editorManager: IEditorManagerModule): IASTManagerModule;
