import { ILocation, ILogger } from "../../../common/typeInterfaces";
import { IEditorManagerModule } from "../editorManager";
import rp = require("raml-1-parser");
import lowLevel = rp.ll;
/**
 * Converts low-level node position to location
 *
 * @param originalUri - uri of the document, initially reported by the client.
 * Is used to convert resulting location uri to the same format, if needed.
 *
 * @param node - node, which position to convert
 *
 * @param completeReference - whether to covert whole node contents range, or onlt they key range
 * (if applicable)
 *
 * @param editorManager - editor manager
 *
 * @param logger - logger
 *
 * @returns {{uri: (any|string), range: {start: number, end: number}}}
 */
export declare function lowLevelNodeToLocation(originalUri: string, node: lowLevel.ILowLevelASTNode, editorManager: IEditorManagerModule, logger: ILogger, completeReference?: boolean): ILocation;
