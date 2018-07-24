import ramlOutline = require("raml-outline");

import {
    IASTManagerModule
} from "./astManager";

import {
    IEditorManagerModule
} from "./editorManager";

import {
    ILogger
} from "../../common/typeInterfaces";

import rp= require("raml-1-parser");
import hl= rp.hl;

/**
 * Generates node key
 * @param node
 * @returns {any}
 */
export function keyProvider(node: hl.IParseResult): string {
    if (!node) {
        return null;
    }

    if (node && !node.parent()) {
        return node.name();
    } else {
        return node.name() + " :: " + keyProvider(node.parent());
    }
}

/**
 * Initializes module.
 */
export function initialize() {

    ramlOutline.initialize();
    ramlOutline.setKeyProvider(keyProvider as any);
}

initialize();

/**
 * AST provider for outline.
 */
class ASTProvider implements ramlOutline.IASTProvider {
    constructor(private uri: string, private astManagerModule: IASTManagerModule,
                private editorManagerModule: IEditorManagerModule,
                private logger: ILogger) {
    }

    public getASTRoot() {
        this.logger.debug("Asked for AST", "ASTProvider", "getASTRoot");
        const ast = this.astManagerModule.getCurrentAST(this.uri) as any;

        this.logger.debugDetail("AST found: " + (ast ? "true" : "false"), "ASTProvider", "getASTRoot");

        return ast;
    }

    public getSelectedNode() {
        this.logger.debug("Asked for selected node", "ASTProvider", "getSelectedNode");

        const editor = this.editorManagerModule.getEditor(this.uri);

        this.logger.debugDetail("Got editor" + (editor ? "true" : "false"),
                                "ASTProvider", "getSelectedNode");
        if (!editor) {
            return null;
        }

        const position = editor.getCursorPosition();

        this.logger.debugDetail("Got position: " + position,
            "ASTProvider", "getSelectedNode");

        if (position === null) {
            return null;
        }

        if (position === 0) {
            return this.getASTRoot();
        }

        const result = this.getASTRoot().findElementAtOffset(position);
        if (result) {
            this.logger.debugDetail("Result type is: " + (result.definition() ? result.definition().nameId() : ""),
                "ASTProvider", "getSelectedNode");
        } else {
            this.logger.debugDetail("Result not found",
                "ASTProvider", "getSelectedNode");
        }

        return result;
    }
}

/**
 * Sets AST provider for outline
 * @param uri
 * @param astManagerModule
 * @param logger
 */
export function setOutlineASTProvider(uri: string, astManagerModule: IASTManagerModule,
                                      editorManagerModule: IEditorManagerModule,
                                      logger: ILogger) {
    ramlOutline.setASTProvider(new ASTProvider(uri, astManagerModule, editorManagerModule, logger));
    ramlOutline.setLogger(logger);
}
