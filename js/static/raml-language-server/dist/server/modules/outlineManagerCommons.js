"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var ramlOutline = require("raml-outline");
/**
 * Generates node key
 * @param node
 * @returns {any}
 */
function keyProvider(node) {
    if (!node) {
        return null;
    }
    if (node && !node.parent()) {
        return node.name();
    }
    else {
        return node.name() + " :: " + keyProvider(node.parent());
    }
}
exports.keyProvider = keyProvider;
/**
 * Initializes module.
 */
function initialize() {
    ramlOutline.initialize();
    ramlOutline.setKeyProvider(keyProvider);
}
exports.initialize = initialize;
initialize();
/**
 * AST provider for outline.
 */
var ASTProvider = /** @class */ (function () {
    function ASTProvider(uri, astManagerModule, editorManagerModule, logger) {
        this.uri = uri;
        this.astManagerModule = astManagerModule;
        this.editorManagerModule = editorManagerModule;
        this.logger = logger;
    }
    ASTProvider.prototype.getASTRoot = function () {
        this.logger.debug("Asked for AST", "ASTProvider", "getASTRoot");
        var ast = this.astManagerModule.getCurrentAST(this.uri);
        this.logger.debugDetail("AST found: " + (ast ? "true" : "false"), "ASTProvider", "getASTRoot");
        return ast;
    };
    ASTProvider.prototype.getSelectedNode = function () {
        this.logger.debug("Asked for selected node", "ASTProvider", "getSelectedNode");
        var editor = this.editorManagerModule.getEditor(this.uri);
        this.logger.debugDetail("Got editor" + (editor ? "true" : "false"), "ASTProvider", "getSelectedNode");
        if (!editor) {
            return null;
        }
        var position = editor.getCursorPosition();
        this.logger.debugDetail("Got position: " + position, "ASTProvider", "getSelectedNode");
        if (position === null) {
            return null;
        }
        if (position === 0) {
            return this.getASTRoot();
        }
        var result = this.getASTRoot().findElementAtOffset(position);
        if (result) {
            this.logger.debugDetail("Result type is: " + (result.definition() ? result.definition().nameId() : ""), "ASTProvider", "getSelectedNode");
        }
        else {
            this.logger.debugDetail("Result not found", "ASTProvider", "getSelectedNode");
        }
        return result;
    };
    return ASTProvider;
}());
/**
 * Sets AST provider for outline
 * @param uri
 * @param astManagerModule
 * @param logger
 */
function setOutlineASTProvider(uri, astManagerModule, editorManagerModule, logger) {
    ramlOutline.setASTProvider(new ASTProvider(uri, astManagerModule, editorManagerModule, logger));
    ramlOutline.setLogger(logger);
}
exports.setOutlineASTProvider = setOutlineASTProvider;
//# sourceMappingURL=outlineManagerCommons.js.map