// This module maintains AST for RAML units and provides AST contents and
// notifications to other server modules

import pathModule = require("path");
import parser = require("raml-1-parser");
import {
    IServerConnection
} from "../core/connections";

import {
    IChangedDocument,
    ILogger,
    IOpenedDocument
} from "../../common/typeInterfaces";

import {
    Reconciler,
    Runnable
} from "../../common/reconciler";

import {
    IEditorManagerModule
} from "./editorManager";

import {
    IDisposableModule
} from "./commonInterfaces";

export type IHighLevelNode = parser.hl.IHighLevelNode;

import shortid = require("shortid");

import PromiseConstructor = require("promise-polyfill");
if (typeof Promise === "undefined" && typeof window !== "undefined") {
    (window as any).Promise = PromiseConstructor;
}

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
    launch();

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
    onNewASTAvailable(listener: IASTListener, unsubscribe?: boolean);
}

/**
 * Creates new AST manager
 * @param connection
 * @returns {ASTManager}
 */
export function createManager(connection: IServerConnection,
                              editorManager: IEditorManagerModule): IASTManagerModule {
    return new ASTManager(connection, editorManager);
}

/**
 * Copy of Options interface as it cant be referenced directly
 */
interface Options {
    /**
     * Module used for operations with file system
     */
    fsResolver?: any;
    /**
     * Module used for operations with web
     */
    httpResolver?: any;
    /**
     * Whether to return Api which contains errors.
     */
    rejectOnErrors?: boolean;
    /**
     * If true, attribute defaults will be returned if no actual vale is specified in RAML code.
     * Affects only attributes.
     */
    attributeDefaults?: boolean;
    /**
     * Absolute path of the RAML file. May be used when content is provided directly on
     * RAML parser method call instead of specifying file path and making the parser to
     * load the file.
     */
    filePath?: string;
}

class ParseDocumentRunnable implements Runnable<IHighLevelNode> {

    public static TYPE_CONST = "astManager.ParseDocumentRunnable";

    public static isInstance(runnable: Runnable<any>): runnable is ParseDocumentRunnable {
        return (runnable as any).getTypeConst &&
            typeof((runnable as any).getTypeConst) === "function" &&
            ParseDocumentRunnable.TYPE_CONST === (runnable as any).getTypeConst();
    }

    private canceled = false;

    constructor(private uri: string,
                private version: number,
                private editorManager: IEditorManagerModule,
                private connection: IServerConnection,
                private logger: ILogger) {
        // TODO maybe also accept pure content
    }

    public getTypeConst(): string {
        return ParseDocumentRunnable.TYPE_CONST;
    }

    public toString(): string {
        return "[Runnable " + this.uri + ":" + this.version + "]";
    }

    // Commented out as we do not allow to run parsing synhronously any more due to the connection,
    // which provides file system information does this only asynchronously
    // parseSynchronously(parserOptions: any) : IHighLevelNode {
    //
    //     let editor = this.editorManager.getEditor(this.uri);
    //
    //     this.logger.debugDetail("Got editor: " + (editor != null),
    //         "ParseDocumentRunnable", "parseSynchronously");
    //
    //     if (!editor) {
    //
    //         let api = parser.loadRAMLSync(parserOptions.filePath, [], parserOptions);
    //         this.logger.debug("Parsing finished, api: " + (api != null),
    //             "ParseDocumentRunnable", "parseSynchronously");
    //
    //         return api.highLevel();
    //     } else {
    //         this.logger.debugDetail("EDITOR text:\n" + editor.getText(),
    //             "ParseDocumentRunnable", "parseSynchronously")
    //
    //         let api = parser.parseRAMLSync(editor.getText(), parserOptions);
    //         this.logger.debug("Parsing finished, api: " + (api != null),
    //             "ParseDocumentRunnable", "parseSynchronously");
    //
    //         return api.highLevel();
    //     }
    // }

    /**
     * Performs the actual business logics.
     * Should resolve the promise when finished.
     */
    public run(): Promise<IHighLevelNode> {

        const options = this.prepareParserOptions();
        return this.parseAsynchronously(options);
    }

    // Commented out as we do not allow to run parsing synhronously any more due to the connection,
    //  provides file system information does this only asynchronously
    // public runSynchronously() : IHighLevelNode {
    //     let options = this.prepareParserOptions();
    //     return this.parseSynchronously(options);
    // }

    /**
     * Whether two runnable conflict with each other.
     * Must work fast as its called often.
     * @param other
     */
    public conflicts(other: Runnable<any>): boolean {
        if (ParseDocumentRunnable.isInstance(other)) {
            return other.getURI() === this.getURI();
        }

        return false;
    }

    /**
     * Cancels the runnable. run() method should do nothing if launched later,
     * if cancel is called during the run() method execution, run() should stop as soon as it can.
     */
    public cancel(): void {
        this.canceled = true;
    }

    /**
     * Whether cancel() method was called at least once.
     */
    public isCanceled(): boolean {
        return this.canceled;
    }

    public getURI() {
        return this.uri;
    }

    private prepareParserOptions(): Options {
        // TODO think about sharing and storing the project
        this.logger.debug("Running the parsing",
            "ParseDocumentRunnable", "prepareParserOptions");

        const dummyProject: any = parser.project.createProject(pathModule.dirname(this.uri));

        const connection = this.connection;

        const logger = this.logger;

        const fsResolver = {
            content(path) {

                logger.debug("Request for path " + path,
                    "ParseDocumentRunnable", "fsResolver#content");

                logger.error("Should never be called",
                    "ParseDocumentRunnable", "fsResolver#content");
                return null;
            },

            contentAsync(path) {

                logger.debug("Request for path " + path,
                    "ParseDocumentRunnable", "fsResolver#contentAsync");

                if (path.indexOf("file://") === 0) {
                    path = path.substring(7);
                    logger.debugDetail("Path changed to: " + path,
                        "ParseDocumentRunnable", "fsResolver#contentAsync");
                }

                return connection.content(path);
            }
        };

        let documentUri = this.uri;
        this.logger.debugDetail("Parsing uri " + documentUri,
            "ParseDocumentRunnable", "prepareParserOptions");

        if (documentUri.indexOf("file://") === 0) {
            documentUri = documentUri.substring(7);
            this.logger.debugDetail("Parsing uri changed to: " + documentUri,
                "ParseDocumentRunnable", "prepareParserOptions");
        }

        return {
            filePath: documentUri,
            fsResolver,
            httpResolver: dummyProject._httpResolver,
            rejectOnErrors: false
        };
    }

    private parseAsynchronously(parserOptions: any): Promise<IHighLevelNode> {
        const editor = this.editorManager.getEditor(this.uri);

        this.logger.debugDetail("Got editor: " + (editor != null),
            "ParseDocumentRunnable", "parseAsynchronously");

        if (!editor) {

            return parser.loadRAML(parserOptions.filePath, [], parserOptions).then((api: parser.hl.BasicNode) => {

                this.logger.debug("Parsing finished, api: " + (api != null),
                    "ParseDocumentRunnable", "parseAsynchronously");

                return api.highLevel();
            }, (error) => {

                this.logger.debug("Parsing finished, ERROR: " + error,
                    "ParseDocumentRunnable", "parseAsynchronously");

                throw error;
            });

        } else {
            this.logger.debugDetail("EDITOR text:\n" + editor.getText(),
                "ParseDocumentRunnable", "parseAsynchronously");

            return parser.parseRAML(editor.getText(), parserOptions).then((api: parser.hl.BasicNode) => {

                this.logger.debug("Parsing finished, api: " + (api != null),
                    "ParseDocumentRunnable", "parseAsynchronously");

                return api.highLevel();
            }, (error) => {

                this.logger.debug("Parsing finished, ERROR: " + error,
                    "ParseDocumentRunnable", "parseAsynchronously");
                throw error;
            });

        }
    }
}

class ASTManager implements IASTManagerModule {

    private astListeners: IASTListener[] = [];

    private currentASTs: {[uri: string]: IHighLevelNode} = {};

    private reconciler: Reconciler;

    private onOpenDocumentListener;

    private onChangeDocumentListener;

    private onCloseDocumentListener;

    constructor(private connection: IServerConnection,
                private editorManager: IEditorManagerModule) {

        this.reconciler = new Reconciler(connection, 250);
    }

    public launch(): void {

        this.onOpenDocumentListener = (document: IOpenedDocument) => {this.onOpenDocument(document); };
        this.connection.onOpenDocument(this.onOpenDocumentListener);

        this.onChangeDocumentListener = (document: IChangedDocument) => {this.onChangeDocument(document); };
        this.editorManager.onChangeDocument(this.onChangeDocumentListener);

        this.onCloseDocumentListener = (uri: string) => {this.onCloseDocument(uri);};
        this.connection.onCloseDocument(this.onCloseDocumentListener);
    }

    public dispose(): void {

        this.connection.onOpenDocument(this.onOpenDocumentListener, true);

        this.editorManager.onChangeDocument(this.onChangeDocumentListener, true);

        this.connection.onCloseDocument(this.onCloseDocumentListener, true);
    }

    /**
     * Returns unique module name.
     */
    public getModuleName(): string {
        return "AST_MANAGER";
    }

    public getCurrentAST(uri: string): IHighLevelNode {
        return this.currentASTs[uri];
    }

    public forceGetCurrentAST(uri: string): Promise<IHighLevelNode> {
        const current = this.currentASTs[uri];
        if (current) {
            return Promise.resolve(current);
        }

        const runner = new ParseDocumentRunnable(uri, null, this.editorManager,
            this.connection, this.connection);

        const newASTPromise = runner.run();
        if (!newASTPromise) {
            return null;
        }

        return newASTPromise.then((newAST) => {
            let version = null;
            const editor = this.editorManager.getEditor(uri);
            if (editor) {
                version = editor.getVersion();
            }

            this.registerNewAST(uri, version, newAST);

            return newAST;
        });
    }

    public onNewASTAvailable(listener: (uri: string, version: number,
                                        ast: IHighLevelNode, error?: Error) => void,
                             unsubscribe = false) {

        this.addListener(this.astListeners, listener, unsubscribe);
    }

    public onOpenDocument(document: IOpenedDocument): void {
        this.reconciler.schedule(new ParseDocumentRunnable(document.uri, 0, this.editorManager,
            this.connection, this.connection))
            .then(
                (newAST) => this.registerNewAST(document.uri, document.version, newAST),
                (error) => this.registerASTParseError(document.uri, error)
            );

    }

    public onChangeDocument(document: IChangedDocument): void {

        this.connection.debug(" document is changed", "ASTManager", "onChangeDocument");

        this.reconciler.schedule(new ParseDocumentRunnable(document.uri, document.version,
            this.editorManager, this.connection, this.connection))
            .then((newAST) => {

                    this.connection.debugDetail(
                        "On change document handler promise returned new ast",
                        "ASTManager", "onChangeDocument");

                    this.registerNewAST(document.uri, document.version, newAST);
                },
                (error) => {

                    this.connection.debugDetail(
                        "On change document handler promise returned new ast error",
                        "ASTManager", "onChangeDocument");

                    this.registerASTParseError(document.uri, error);
                }
            );
    }

    public onCloseDocument(uri: string): void {
        delete this.currentASTs[uri];
    }

    public registerNewAST(uri: string, version: number, ast: IHighLevelNode): void {
        // cleaning ASTs
        // this.currentASTs = {};

        this.connection.debug("Registering new AST for URI: " + uri,
            "ASTManager", "registerNewAST");

        this.currentASTs[uri] = ast;

        this.notifyASTChanged(uri, version, ast);
    }

    public registerASTParseError(uri: string, error: any) {
        // cleaning ASTs
        this.currentASTs = {};

        this.notifyASTChanged(uri, null, error);
    }

    private notifyASTChanged(uri: string, version: number, ast: IHighLevelNode, error?: Error) {

        this.connection.debug("Got new AST parser results, notifying the listeners",
            "ASTManager", "notifyASTChanged");

        for (const listener of this.astListeners) {
            listener(uri, version, ast);
        }
    }

    /**
     * Adds listener.
     * @param memberListeners - member containing array of listeners
     * @param listener - listener to add
     * @param unsubscribe - whether to unsubscribe this listener
     */
    private addListener<T>(memberListeners: T[], listener: T, unsubscribe = false): void {
        if (unsubscribe) {
            this.connection.debugDetail("Asked to remove listener",
                "ASTManager", "addListener");

            const index = memberListeners.indexOf(listener);

            this.connection.debugDetail("Found index: " + index,
                "ASTManager", "addListener");
            if (index !== -1) {
                this.connection.debugDetail("Removing listener",
                    "ASTManager", "addListener");
                memberListeners.splice(index, 1);
            }
        } else {
            memberListeners.push(listener);
        }
    }
}
