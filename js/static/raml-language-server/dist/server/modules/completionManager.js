"use strict";
// This module provides completion proposals
Object.defineProperty(exports, "__esModule", { value: true });
var utils_1 = require("../../common/utils");
var suggestions = require("raml-suggestions");
function createManager(connection, astManagerModule, editorManagerModule) {
    return new CompletionManagerModule(connection, astManagerModule, editorManagerModule);
}
exports.createManager = createManager;
function initialize() {
}
exports.initialize = initialize;
initialize();
var ASTProvider = /** @class */ (function () {
    function ASTProvider(uri, astManagerModule, logger) {
        this.uri = uri;
        this.astManagerModule = astManagerModule;
        this.logger = logger;
    }
    ASTProvider.prototype.getASTRoot = function () {
        var result = this.astManagerModule.getCurrentAST(this.uri);
        this.logger.debugDetail("Got AST from AST provider: " + (result ? "true" : "false"), "CompletionManagerModule", "ASTProvider#getASTRoot");
        return result;
    };
    ASTProvider.prototype.getSelectedNode = function () {
        return this.getASTRoot();
    };
    /**
     * Gets current AST root asynchronously.
     * Can return null.
     */
    ASTProvider.prototype.getASTRootAsync = function () {
        return Promise.resolve(this.getASTRoot());
    };
    /**
     * Gets current AST node asynchronously
     * Can return null.
     */
    ASTProvider.prototype.getSelectedNodeAsync = function () {
        return Promise.resolve(this.getSelectedNode());
    };
    return ASTProvider;
}());
/**
 * Editor state provider.
 */
var EditorStateProvider = /** @class */ (function () {
    function EditorStateProvider(uri, offset, editorManagerModule) {
        this.uri = uri;
        this.offset = offset;
        this.editorManagerModule = editorManagerModule;
        this.editor = editorManagerModule.getEditor(uri);
    }
    /**
     * Text of the document opened in the editor.
     */
    EditorStateProvider.prototype.getText = function () {
        if (!this.editor) {
            return "";
        }
        return this.editor.getText();
    };
    /**
     * Full path to the document opened in the editor.
     */
    EditorStateProvider.prototype.getPath = function () {
        if (!this.editor) {
            return "";
        }
        var editorPath = this.editor.getPath();
        if (utils_1.isFILEUri(editorPath)) {
            return utils_1.pathFromURI(editorPath);
        }
        else {
            return editorPath;
        }
    };
    /**
     * File name of the document opened in the editor.
     */
    EditorStateProvider.prototype.getBaseName = function () {
        if (!this.editor) {
            return "";
        }
        return utils_1.basename(this.getPath());
    };
    /**
     * Editor cursor offset.
     */
    EditorStateProvider.prototype.getOffset = function () {
        if (!this.editor) {
            return 0;
        }
        return this.offset;
    };
    return EditorStateProvider;
}());
var FSProvider = /** @class */ (function () {
    function FSProvider(logger, connection) {
        this.logger = logger;
        this.connection = connection;
    }
    /**
     * File contents by full path, synchronously.
     * @param fullPath
     */
    FSProvider.prototype.content = function (fullPath) {
        this.logger.debugDetail("Request for content: " + fullPath, "CompletionManagerModule", "FSProvider#content");
        this.logger.error("Should never be called", "CompletionManagerModule", "FSProvider#content");
        return null;
    };
    /**
     * File contents by full path, asynchronously.
     * @param fullPath
     */
    FSProvider.prototype.contentAsync = function (fullPath) {
        this.logger.debugDetail("Request for content: " + fullPath, "CompletionManagerModule", "FSProvider#contentAsync");
        return this.connection.content(fullPath);
    };
    FSProvider.prototype.contentDirName = function (content) {
        var contentPath = content.getPath();
        var converted = utils_1.pathFromURI(contentPath);
        var result = utils_1.dirname(converted);
        this.logger.debugDetail("contentDirName result: " + result, "CompletionManagerModule", "FSProvider#contentDirName");
        return result;
    };
    FSProvider.prototype.dirName = function (childPath) {
        this.logger.debugDetail("Dirname for path: " + childPath, "CompletionManagerModule", "FSProvider#dirName");
        var result = utils_1.dirname(childPath);
        this.logger.debugDetail("result: " + result, "CompletionManagerModule", "FSProvider#dirName");
        return result;
    };
    FSProvider.prototype.exists = function (checkPath) {
        this.logger.debugDetail("Request for existence: " + checkPath, "CompletionManagerModule", "FSProvider#exists");
        this.logger.error("Should never be called", "CompletionManagerModule", "FSProvider#exists");
        return false;
    };
    FSProvider.prototype.resolve = function (contextPath, relativePath) {
        return utils_1.resolve(contextPath, relativePath);
    };
    FSProvider.prototype.isDirectory = function (dirPath) {
        this.logger.debugDetail("Request for directory check: " + dirPath, "CompletionManagerModule", "FSProvider#isDirectory");
        this.logger.error("Should never be called", "CompletionManagerModule", "FSProvider#isDirectory");
        return false;
    };
    FSProvider.prototype.readDir = function (dirPath) {
        this.logger.debugDetail("Request for directory content: " + dirPath, "CompletionManagerModule", "FSProvider#readDir");
        this.logger.error("Should never be called", "CompletionManagerModule", "FSProvider#readDir");
        return [];
    };
    FSProvider.prototype.existsAsync = function (path) {
        this.logger.debugDetail("Request for existence: " + path, "CompletionManagerModule", "FSProvider#existsAsync");
        return this.connection.exists(path);
    };
    /**
     * Returns directory content list.
     * @param fullPath
     */
    FSProvider.prototype.readDirAsync = function (path) {
        this.logger.debugDetail("Request for directory content: " + path, "CompletionManagerModule", "FSProvider#readDirAsync");
        return this.connection.readDir(path);
    };
    /**
     * Check whether the path points to a directory.
     * @param fullPath
     */
    FSProvider.prototype.isDirectoryAsync = function (path) {
        this.logger.debugDetail("Request for directory check: " + path, "CompletionManagerModule", "FSProvider#isDirectoryAsync");
        return this.connection.isDirectory(path);
    };
    return FSProvider;
}());
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
var CompletionManagerModule = /** @class */ (function () {
    function CompletionManagerModule(connection, astManagerModule, editorManagerModule) {
        this.connection = connection;
        this.astManagerModule = astManagerModule;
        this.editorManagerModule = editorManagerModule;
    }
    CompletionManagerModule.prototype.launch = function () {
        var _this = this;
        this.onDocumentCompletionListener = function (uri, position) {
            return _this.getCompletion(uri, position);
        };
        this.connection.onDocumentCompletion(this.onDocumentCompletionListener);
    };
    CompletionManagerModule.prototype.dispose = function () {
        this.connection.onDocumentCompletion(this.onDocumentCompletionListener, true);
    };
    /**
     * Returns unique module name.
     */
    CompletionManagerModule.prototype.getModuleName = function () {
        return "COMPLETION_MANAGER";
    };
    CompletionManagerModule.prototype.getCompletion = function (uri, position) {
        this.connection.debug("Called getCompletion for position " + position, "CompletionManagerModule", "getCompletion");
        var astProvider = new ASTProvider(uri, this.astManagerModule, this.connection);
        var editorProvider = new EditorStateProvider(uri, position, this.editorManagerModule);
        var fsProvider = new FSProvider(this.connection, this.connection);
        // TODO remove after leaving prototype phase, only needed for logging
        var editorText = editorProvider.getText();
        this.connection.debugDetail("Current text:\n" + editorText, "CompletionManagerModule", "getCompletion");
        var cutStart = position - 10 >= 0 ? position - 10 : 0;
        var cutEnd = position + 10 < editorText.length ? position + 10 : editorText.length - 1;
        var cutText = editorText.substring(cutStart, position + 1) + "I" + editorText.substring(position + 1, cutEnd);
        this.connection.debugDetail("Completion position cutoff:" + cutText, "CompletionManagerModule", "getCompletion");
        var connection = this.connection;
        return this.astManagerModule.forceGetCurrentAST(uri).then(function (currentAST) {
            connection.debugDetail("Current AST found: " + (currentAST ? "true" : "false"), "CompletionManagerModule", "getCompletion");
            if (currentAST) {
                connection.debugDetail(currentAST.printDetails(), "CompletionManagerModule", "getCompletion");
            }
            suggestions.setDefaultASTProvider(astProvider);
            suggestions.setLogger(connection);
            return suggestions.suggestAsync(editorProvider, fsProvider).then(function (result) {
                connection.debug("Got suggestion results: " + JSON.stringify(result));
                connection.debug("Got suggestion results length: "
                    + (result ? result.length : 0), "CompletionManagerModule", "getCompletion");
                for (var _i = 0, result_1 = result; _i < result_1.length; _i++) {
                    var suggestion = result_1[_i];
                    connection.debug("Suggestion: text: " + suggestion.text, "CompletionManagerModule", "getCompletion");
                    connection.debug("Suggestion: displayText: " + suggestion.displayText, "CompletionManagerModule", "getCompletion");
                    connection.debug("Suggestion: prefix: " + suggestion.prefix, "CompletionManagerModule", "getCompletion");
                }
                return result;
            }, function (error) {
                connection.error("Failed to find suggestions: " + error, "CompletionManagerModule", "getCompletion");
                throw error;
            });
        }, function (rejection) {
            if (rejection.message && rejection.message.indexOf("Invalid first line") === 0) {
                return suggestions.suggest(editorProvider, fsProvider);
            }
            return Promise.reject(rejection);
        });
    };
    return CompletionManagerModule;
}());
//# sourceMappingURL=completionManager.js.map