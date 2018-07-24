// This module provides completion proposals

import {
    IServerConnection
} from "../core/connections";

import {
    IASTManagerModule
} from "./astManager";

import {
    IEditorManagerModule
} from "./editorManager";

import {
    IAbstractTextEditor,
    IDisposableModule
} from "./commonInterfaces";

import {
    Icons,
    ILogger,
    IValidationIssue,
    StructureCategories,
    StructureNodeJSON,
    Suggestion,
    TextStyles
} from "../../common/typeInterfaces";

import rp= require("raml-1-parser");
import lowLevel= rp.ll;
import hl= rp.hl;

import {
    basename,
    dirname,
    isFILEUri,
    pathFromURI,
    resolve
} from "../../common/utils";

import fs = require("fs");
import suggestions = require("raml-suggestions");

export function createManager(connection: IServerConnection,
                              astManagerModule: IASTManagerModule,
                              editorManagerModule: IEditorManagerModule): IDisposableModule {

    return new CompletionManagerModule(connection, astManagerModule, editorManagerModule);
}

export function initialize() {

}

initialize();

class ASTProvider implements suggestions.IASTProvider {
    constructor(private uri: string, private astManagerModule: IASTManagerModule,
                private logger: ILogger) {
    }

    public getASTRoot() {

        const result =  this.astManagerModule.getCurrentAST(this.uri) as any;

        this.logger.debugDetail(
            "Got AST from AST provider: " + (result ? "true" : "false"),
            "CompletionManagerModule", "ASTProvider#getASTRoot");

        return result;
    }

    public getSelectedNode() {
        return this.getASTRoot();
    }

    /**
     * Gets current AST root asynchronously.
     * Can return null.
     */
    public getASTRootAsync() {
        return Promise.resolve(this.getASTRoot());
    }

    /**
     * Gets current AST node asynchronously
     * Can return null.
     */
    public getSelectedNodeAsync() {
        return Promise.resolve(this.getSelectedNode());
    }
}

/**
 * Editor state provider.
 */
class EditorStateProvider implements suggestions.IEditorStateProvider {
    private editor: IAbstractTextEditor;

    constructor(private uri: string, private offset: number,
                private editorManagerModule: IEditorManagerModule) {
        this.editor = editorManagerModule.getEditor(uri);
    }

    /**
     * Text of the document opened in the editor.
     */
    public getText(): string {
        if (!this.editor) {
            return "";
        }

        return this.editor.getText();
    }

    /**
     * Full path to the document opened in the editor.
     */
    public getPath(): string {
        if (!this.editor) {
            return "";
        }

        const editorPath = this.editor.getPath();

        if (isFILEUri(editorPath)) {
            return pathFromURI(editorPath);
        } else {
            return editorPath;
        }
    }

    /**
     * File name of the document opened in the editor.
     */
    public getBaseName(): string {
        if (!this.editor) {
            return "";
        }

        return basename(this.getPath());
    }

    /**
     * Editor cursor offset.
     */
    public getOffset(): number {
        if (!this.editor) {
            return 0;
        }

        return this.offset;
    }
}

class FSProvider implements suggestions.IFSProvider {

    constructor(private logger: ILogger, private connection: IServerConnection) {

    }

    /**
     * File contents by full path, synchronously.
     * @param fullPath
     */
    public content(fullPath: string): string {
        this.logger.debugDetail("Request for content: " + fullPath,
            "CompletionManagerModule", "FSProvider#content");

        this.logger.error("Should never be called",
            "CompletionManagerModule", "FSProvider#content");

        return null;
    }
    /**
     * File contents by full path, asynchronously.
     * @param fullPath
     */
    public contentAsync(fullPath: string): Promise<string> {

        this.logger.debugDetail("Request for content: " + fullPath,
            "CompletionManagerModule", "FSProvider#contentAsync");

        return this.connection.content(fullPath);
    }

    public contentDirName(content: suggestions.IEditorStateProvider): string {
        const contentPath = content.getPath();

        const converted = pathFromURI(contentPath);

        const result = dirname(converted);

        this.logger.debugDetail("contentDirName result: " + result,
            "CompletionManagerModule", "FSProvider#contentDirName");

        return result;
    }

    public dirName(childPath: string): string {
        this.logger.debugDetail("Dirname for path: " + childPath,
            "CompletionManagerModule", "FSProvider#dirName");

        const result =  dirname(childPath);

        this.logger.debugDetail("result: " + result,
            "CompletionManagerModule", "FSProvider#dirName");

        return result;
    }

    public exists(checkPath: string): boolean {
        this.logger.debugDetail("Request for existence: " + checkPath,
            "CompletionManagerModule", "FSProvider#exists");

        this.logger.error("Should never be called",
            "CompletionManagerModule", "FSProvider#exists");
        return false;
    }

    public resolve(contextPath: string, relativePath: string): string {
        return resolve(contextPath, relativePath);
    }

    public isDirectory(dirPath: string): boolean {

        this.logger.debugDetail("Request for directory check: " + dirPath,
            "CompletionManagerModule", "FSProvider#isDirectory");

        this.logger.error("Should never be called",
            "CompletionManagerModule", "FSProvider#isDirectory");

        return false;
    }

    public readDir(dirPath: string): string[] {
        this.logger.debugDetail("Request for directory content: " + dirPath,
            "CompletionManagerModule", "FSProvider#readDir");

        this.logger.error("Should never be called",
            "CompletionManagerModule", "FSProvider#readDir");

        return [];
    }

    public existsAsync(path: string): Promise<boolean> {
        this.logger.debugDetail("Request for existence: " + path,
            "CompletionManagerModule", "FSProvider#existsAsync");

        return this.connection.exists(path);
    }

    /**
     * Returns directory content list.
     * @param fullPath
     */
    public readDirAsync(path: string): Promise<string[]> {
        this.logger.debugDetail("Request for directory content: " + path,
            "CompletionManagerModule", "FSProvider#readDirAsync");

        return this.connection.readDir(path);
    }

    /**
     * Check whether the path points to a directory.
     * @param fullPath
     */
    public isDirectoryAsync(path: string): Promise<boolean> {

        this.logger.debugDetail("Request for directory check: " + path,
            "CompletionManagerModule", "FSProvider#isDirectoryAsync");

        return this.connection.isDirectory(path);
    }
}
// class FSProvider implements suggestions.IFSProvider {
//
//     constructor(private logger: ILogger) {
//
//     }
//
//     /**
//      * File contents by full path, synchronously.
//      * @param fullPath
//      */
//     content(fullPath: string): string {
//         return fs.readFileSync(fullPath).toString();
//     }
//     /**
//      * File contents by full path, asynchronously.
//      * @param fullPath
//      */
//     contentAsync(fullPath: string): Promise<string> {
//         return new Promise(function(resolve, reject) {
//
//             fs.readFile(fullPath,(err,data)=>{
//                 if(err!=null){
//                     return reject(err);
//                 }
//
//                 let content = data.toString();
//                 resolve(content);
//             });
//         });
//     }
//
//     contentDirName(content: suggestions.IEditorStateProvider): string {
//         let contentPath = content.getPath();
//
//         let converted = pathFromURI(contentPath);
//
//         let result = dirname(converted);
//
//         this.logger.debugDetail("contentDirName result: " + result,
//             "CompletionManagerModule", "FSProvider#contentDirName")
//
//         return result;
//     }
//
//     dirName(childPath: string): string {
//         this.logger.debugDetail("Dirname for path: " + childPath,
//             "CompletionManagerModule", "FSProvider#dirName")
//
//         let result =  dirname(childPath);
//
//         this.logger.debugDetail("result: " + result,
//             "CompletionManagerModule", "FSProvider#dirName")
//
//         return result;
//     }
//
//     exists(checkPath: string): boolean {
//         this.logger.debugDetail("Request for existence: " + checkPath,
//             "CompletionManagerModule", "FSProvider#exists")
//
//         return fs.existsSync(checkPath);
//     }
//
//     resolve(contextPath: string, relativePath: string): string {
//         return resolve(contextPath, relativePath);
//     }
//
//     isDirectory(dirPath: string): boolean {
//
//         this.logger.debugDetail("Request for directory check: " + dirPath,
//             "CompletionManagerModule", "FSProvider#isDirectory")
//
//         var stat = fs.statSync(dirPath);
//
//         return stat && stat.isDirectory();
//     }
//
//     readDir(dirPath: string): string[] {
//         this.logger.debugDetail("Request for directory content: " + dirPath,
//             "CompletionManagerModule", "FSProvider#readDir")
//
//         return fs.readdirSync(dirPath);
//     }
//
//     existsAsync(path: string): Promise<boolean> {
//         this.logger.debugDetail("Request for existence: " + path,
//             "CompletionManagerModule", "FSProvider#existsAsync")
//
//         return new Promise(resolve => {
//             fs.exists(path, (result) => {resolve(result)})
//         });
//     }
//
//     /**
//      * Returns directory content list.
//      * @param fullPath
//      */
//     readDirAsync(path: string): Promise<string[]> {
//         this.logger.debugDetail("Request for directory content: " + path,
//             "CompletionManagerModule", "FSProvider#readDirAsync")
//
//         return new Promise(resolve => {
//             fs.readdir(path, (err, result) => {resolve(result)})
//         });
//     }
//
//     /**
//      * Check whether the path points to a directory.
//      * @param fullPath
//      */
//     isDirectoryAsync(path: string): Promise<boolean> {
//
//         this.logger.debugDetail("Request for directory check: " + path,
//             "CompletionManagerModule", "FSProvider#isDirectoryAsync")
//
//         return new Promise(resolve => {
//             fs.stat(path, (err, stats) => {resolve(stats.isDirectory())})
//         });
//     }
// }

class CompletionManagerModule implements IDisposableModule {

    private onDocumentCompletionListener;

    constructor(private connection: IServerConnection, private astManagerModule: IASTManagerModule,
                private editorManagerModule: IEditorManagerModule) {
    }

    public launch() {

        this.onDocumentCompletionListener = (uri, position) => {
            return this.getCompletion(uri, position);
        }

        this.connection.onDocumentCompletion(this.onDocumentCompletionListener);
    }

    public dispose(): void {
        this.connection.onDocumentCompletion(this.onDocumentCompletionListener, true);
    }

    /**
     * Returns unique module name.
     */
    public getModuleName(): string {
        return "COMPLETION_MANAGER";
    }

    public getCompletion(uri: string, position: number): Promise<Suggestion[]> {
        this.connection.debug("Called getCompletion for position " + position,
                              "CompletionManagerModule", "getCompletion")
        ;
        const astProvider = new ASTProvider(uri, this.astManagerModule, this.connection);
        const editorProvider = new EditorStateProvider(uri, position, this.editorManagerModule);
        const fsProvider = new FSProvider(this.connection, this.connection);

        // TODO remove after leaving prototype phase, only needed for logging
        const editorText = editorProvider.getText();
        this.connection.debugDetail("Current text:\n" + editorText, "CompletionManagerModule", "getCompletion");
        const cutStart = position - 10 >= 0 ? position - 10 : 0;
        const cutEnd = position + 10 < editorText.length ? position + 10 : editorText.length - 1;
        const cutText = editorText.substring(cutStart, position + 1) + "I" + editorText.substring(position + 1, cutEnd);
        this.connection.debugDetail("Completion position cutoff:" + cutText,
            "CompletionManagerModule", "getCompletion");

        const connection = this.connection;
        return this.astManagerModule.forceGetCurrentAST(uri).then((currentAST) => {

            connection.debugDetail("Current AST found: " + (currentAST ? "true" : "false"),
                "CompletionManagerModule", "getCompletion");
            if (currentAST) {
                connection.debugDetail(currentAST.printDetails(), "CompletionManagerModule", "getCompletion");
            }

            suggestions.setDefaultASTProvider(astProvider);
            suggestions.setLogger(connection);

            return suggestions.suggestAsync(editorProvider, fsProvider).then((result) => {
                connection.debug("Got suggestion results: " + JSON.stringify(result));
                connection.debug("Got suggestion results length: "
                    + (result ? result.length : 0), "CompletionManagerModule", "getCompletion");

                for (const suggestion of result) {
                    connection.debug("Suggestion: text: " + suggestion.text,
                        "CompletionManagerModule", "getCompletion");
                    connection.debug("Suggestion: displayText: " + suggestion.displayText,
                        "CompletionManagerModule", "getCompletion");
                    connection.debug("Suggestion: prefix: " + suggestion.prefix,
                        "CompletionManagerModule", "getCompletion");
                }

                return result;
            }, (error) => {
                connection.error("Failed to find suggestions: " + error,
                    "CompletionManagerModule", "getCompletion");

                throw error;
            });
        }, (rejection) => {
            if(rejection.message && rejection.message.indexOf("Invalid first line") === 0) {
                return suggestions.suggest(editorProvider, fsProvider);
            }
            
            return Promise.reject(rejection);
        });

    }
}
