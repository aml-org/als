import { IASTManagerModule } from "./astManager";
import { IEditorManagerModule } from "./editorManager";
import { ILogger } from "../../common/typeInterfaces";
import rp = require("raml-1-parser");
import hl = rp.hl;
/**
 * Generates node key
 * @param node
 * @returns {any}
 */
export declare function keyProvider(node: hl.IParseResult): string;
/**
 * Initializes module.
 */
export declare function initialize(): void;
/**
 * Sets AST provider for outline
 * @param uri
 * @param astManagerModule
 * @param logger
 */
export declare function setOutlineASTProvider(uri: string, astManagerModule: IASTManagerModule, editorManagerModule: IEditorManagerModule, logger: ILogger): void;
