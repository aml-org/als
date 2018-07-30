"use strict";
// This module maintains AST for RAML units and provides AST contents and
// notifications to other server modules
Object.defineProperty(exports, "__esModule", { value: true });
var pathModule = require("path");
var parser = require("raml-1-parser");
var reconciler_1 = require("../../common/reconciler");
var PromiseConstructor = require("promise-polyfill");
if (typeof Promise === "undefined" && typeof window !== "undefined") {
    window.Promise = PromiseConstructor;
}
/**
 * Creates new AST manager
 * @param connection
 * @returns {ASTManager}
 */
function createManager(connection, editorManager) {
    return new ASTManager(connection, editorManager);
}
exports.createManager = createManager;
var ParseDocumentRunnable = /** @class */ (function () {
    function ParseDocumentRunnable(uri, version, editorManager, connection, logger) {
        this.uri = uri;
        this.version = version;
        this.editorManager = editorManager;
        this.connection = connection;
        this.logger = logger;
        this.canceled = false;
        // TODO maybe also accept pure content
    }
    ParseDocumentRunnable.isInstance = function (runnable) {
        return runnable.getTypeConst &&
            typeof (runnable.getTypeConst) === "function" &&
            ParseDocumentRunnable.TYPE_CONST === runnable.getTypeConst();
    };
    ParseDocumentRunnable.prototype.getTypeConst = function () {
        return ParseDocumentRunnable.TYPE_CONST;
    };
    ParseDocumentRunnable.prototype.toString = function () {
        return "[Runnable " + this.uri + ":" + this.version + "]";
    };
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
    ParseDocumentRunnable.prototype.run = function () {
        var options = this.prepareParserOptions();
        return this.parseAsynchronously(options);
    };
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
    ParseDocumentRunnable.prototype.conflicts = function (other) {
        if (ParseDocumentRunnable.isInstance(other)) {
            return other.getURI() === this.getURI();
        }
        return false;
    };
    /**
     * Cancels the runnable. run() method should do nothing if launched later,
     * if cancel is called during the run() method execution, run() should stop as soon as it can.
     */
    ParseDocumentRunnable.prototype.cancel = function () {
        this.canceled = true;
    };
    /**
     * Whether cancel() method was called at least once.
     */
    ParseDocumentRunnable.prototype.isCanceled = function () {
        return this.canceled;
    };
    ParseDocumentRunnable.prototype.getURI = function () {
        return this.uri;
    };
    ParseDocumentRunnable.prototype.prepareParserOptions = function () {
        // TODO think about sharing and storing the project
        this.logger.debug("Running the parsing", "ParseDocumentRunnable", "prepareParserOptions");
        var dummyProject = parser.project.createProject(pathModule.dirname(this.uri));
        var connection = this.connection;
        var logger = this.logger;
        var fsResolver = {
            content: function (path) {
                logger.debug("Request for path " + path, "ParseDocumentRunnable", "fsResolver#content");
                logger.error("Should never be called", "ParseDocumentRunnable", "fsResolver#content");
                return null;
            },
            contentAsync: function (path) {
                logger.debug("Request for path " + path, "ParseDocumentRunnable", "fsResolver#contentAsync");
                if (path.indexOf("file://") === 0) {
                    path = path.substring(7);
                    logger.debugDetail("Path changed to: " + path, "ParseDocumentRunnable", "fsResolver#contentAsync");
                }
                return connection.content(path);
            }
        };
        var documentUri = this.uri;
        this.logger.debugDetail("Parsing uri " + documentUri, "ParseDocumentRunnable", "prepareParserOptions");
        if (documentUri.indexOf("file://") === 0) {
            documentUri = documentUri.substring(7);
            this.logger.debugDetail("Parsing uri changed to: " + documentUri, "ParseDocumentRunnable", "prepareParserOptions");
        }
        return {
            filePath: documentUri,
            fsResolver: fsResolver,
            httpResolver: dummyProject._httpResolver,
            rejectOnErrors: false
        };
    };
    ParseDocumentRunnable.prototype.parseAsynchronously = function (parserOptions) {
        var _this = this;
        var editor = this.editorManager.getEditor(this.uri);
        this.logger.debugDetail("Got editor: " + (editor != null), "ParseDocumentRunnable", "parseAsynchronously");
        if (!editor) {
            return parser.loadRAML(parserOptions.filePath, [], parserOptions).then(function (api) {
                _this.logger.debug("Parsing finished, api: " + (api != null), "ParseDocumentRunnable", "parseAsynchronously");
                return api.highLevel();
            }, function (error) {
                _this.logger.debug("Parsing finished, ERROR: " + error, "ParseDocumentRunnable", "parseAsynchronously");
                throw error;
            });
        }
        else {
            this.logger.debugDetail("EDITOR text:\n" + editor.getText(), "ParseDocumentRunnable", "parseAsynchronously");
            return parser.parseRAML(editor.getText(), parserOptions).then(function (api) {
                _this.logger.debug("Parsing finished, api: " + (api != null), "ParseDocumentRunnable", "parseAsynchronously");
                return api.highLevel();
            }, function (error) {
                _this.logger.debug("Parsing finished, ERROR: " + error, "ParseDocumentRunnable", "parseAsynchronously");
                throw error;
            });
        }
    };
    ParseDocumentRunnable.TYPE_CONST = "astManager.ParseDocumentRunnable";
    return ParseDocumentRunnable;
}());
var ASTManager = /** @class */ (function () {
    function ASTManager(connection, editorManager) {
        this.connection = connection;
        this.editorManager = editorManager;
        this.astListeners = [];
        this.currentASTs = {};
        this.reconciler = new reconciler_1.Reconciler(connection, 250);
    }
    ASTManager.prototype.launch = function () {
        var _this = this;
        this.onOpenDocumentListener = function (document) { _this.onOpenDocument(document); };
        this.connection.onOpenDocument(this.onOpenDocumentListener);
        this.onChangeDocumentListener = function (document) { _this.onChangeDocument(document); };
        this.editorManager.onChangeDocument(this.onChangeDocumentListener);
        this.onCloseDocumentListener = function (uri) { _this.onCloseDocument(uri); };
        this.connection.onCloseDocument(this.onCloseDocumentListener);
    };
    ASTManager.prototype.dispose = function () {
        this.connection.onOpenDocument(this.onOpenDocumentListener, true);
        this.editorManager.onChangeDocument(this.onChangeDocumentListener, true);
        this.connection.onCloseDocument(this.onCloseDocumentListener, true);
    };
    /**
     * Returns unique module name.
     */
    ASTManager.prototype.getModuleName = function () {
        return "AST_MANAGER";
    };
    ASTManager.prototype.getCurrentAST = function (uri) {
        return this.currentASTs[uri];
    };
    ASTManager.prototype.forceGetCurrentAST = function (uri) {
        var _this = this;
        var current = this.currentASTs[uri];
        if (current) {
            return Promise.resolve(current);
        }
        var runner = new ParseDocumentRunnable(uri, null, this.editorManager, this.connection, this.connection);
        var newASTPromise = runner.run();
        if (!newASTPromise) {
            return null;
        }
        return newASTPromise.then(function (newAST) {
            var version = null;
            var editor = _this.editorManager.getEditor(uri);
            if (editor) {
                version = editor.getVersion();
            }
            _this.registerNewAST(uri, version, newAST);
            return newAST;
        });
    };
    ASTManager.prototype.onNewASTAvailable = function (listener, unsubscribe) {
        if (unsubscribe === void 0) { unsubscribe = false; }
        this.addListener(this.astListeners, listener, unsubscribe);
    };
    ASTManager.prototype.onOpenDocument = function (document) {
        var _this = this;
        this.reconciler.schedule(new ParseDocumentRunnable(document.uri, 0, this.editorManager, this.connection, this.connection))
            .then(function (newAST) { return _this.registerNewAST(document.uri, document.version, newAST); }, function (error) { return _this.registerASTParseError(document.uri, error); });
    };
    ASTManager.prototype.onChangeDocument = function (document) {
        var _this = this;
        this.connection.debug(" document is changed", "ASTManager", "onChangeDocument");
        this.reconciler.schedule(new ParseDocumentRunnable(document.uri, document.version, this.editorManager, this.connection, this.connection))
            .then(function (newAST) {
            _this.connection.debugDetail("On change document handler promise returned new ast", "ASTManager", "onChangeDocument");
            _this.registerNewAST(document.uri, document.version, newAST);
        }, function (error) {
            _this.connection.debugDetail("On change document handler promise returned new ast error", "ASTManager", "onChangeDocument");
            _this.registerASTParseError(document.uri, error);
        });
    };
    ASTManager.prototype.onCloseDocument = function (uri) {
        delete this.currentASTs[uri];
    };
    ASTManager.prototype.registerNewAST = function (uri, version, ast) {
        // cleaning ASTs
        // this.currentASTs = {};
        this.connection.debug("Registering new AST for URI: " + uri, "ASTManager", "registerNewAST");
        this.currentASTs[uri] = ast;
        this.notifyASTChanged(uri, version, ast);
    };
    ASTManager.prototype.registerASTParseError = function (uri, error) {
        // cleaning ASTs
        this.currentASTs = {};
        this.notifyASTChanged(uri, null, error);
    };
    ASTManager.prototype.notifyASTChanged = function (uri, version, ast, error) {
        this.connection.debug("Got new AST parser results, notifying the listeners", "ASTManager", "notifyASTChanged");
        for (var _i = 0, _a = this.astListeners; _i < _a.length; _i++) {
            var listener = _a[_i];
            listener(uri, version, ast);
        }
    };
    /**
     * Adds listener.
     * @param memberListeners - member containing array of listeners
     * @param listener - listener to add
     * @param unsubscribe - whether to unsubscribe this listener
     */
    ASTManager.prototype.addListener = function (memberListeners, listener, unsubscribe) {
        if (unsubscribe === void 0) { unsubscribe = false; }
        if (unsubscribe) {
            this.connection.debugDetail("Asked to remove listener", "ASTManager", "addListener");
            var index = memberListeners.indexOf(listener);
            this.connection.debugDetail("Found index: " + index, "ASTManager", "addListener");
            if (index !== -1) {
                this.connection.debugDetail("Removing listener", "ASTManager", "addListener");
                memberListeners.splice(index, 1);
            }
        }
        else {
            memberListeners.push(listener);
        }
    };
    return ASTManager;
}());
//# sourceMappingURL=astManager.js.map