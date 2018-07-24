// Common interfaces and methods for fixed actions submodules

import {
    ILocation,
    ILogger
} from "../../../common/typeInterfaces";

import {
    IServerModule
} from "../../modules/commonInterfaces";

import {
    IEditorManagerModule
} from "../editorManager";

import utils = require("../../../common/utils");

import rp= require("raml-1-parser");
import lowLevel= rp.ll;

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
export function lowLevelNodeToLocation(
    originalUri: string,
    node: lowLevel.ILowLevelASTNode,
    editorManager: IEditorManagerModule,
    logger: ILogger,
    completeReference= true): ILocation {
    logger.debugDetail("Converting low level node to location",
        "FixedActionsManager", "lowLevelNodeToLocation");

    const unitNodePath = node.unit().absolutePath();

    let editor = editorManager.getEditor(unitNodePath);
    logger.debugDetail("Editor found: " + (editor ? "true" : "false"),
        "FixedActionsManager", "lowLevelNodeToLocation");

    const transformedPath = utils.transformUriToOriginalFormat(originalUri, unitNodePath);
    logger.debugDetail("Transformed path: " + transformedPath,
        "FixedActionsManager", "lowLevelNodeToLocation");

    if (!editor) {
        editor = editorManager.getEditor(transformedPath);
    }
    logger.debugDetail("Editor found from transfromed path: " + (editor ? "true" : "false"),
        "FixedActionsManager", "lowLevelNodeToLocation");

    if (!editor || completeReference) {
        return {
            uri: transformedPath,
            range: {
                start: node.start(),
                end: node.end()
            }
        };
    }

    logger.debugDetail("Initial declaration positions: [" + node.start() + ":" + node.end() + "]",
        "FixedActionsManager", "lowLevelNodeToLocation");

    const buffer = editor.getBuffer();
    logger.debugDetail("buffer found: " + (buffer ? "true" : "false"),
        "FixedActionsManager", "lowLevelNodeToLocation");

    const p1 = buffer.positionForCharacterIndex(node.start());
    logger.debugDetail("p1 found: " + (p1 ? "true" : "false"),
        "FixedActionsManager", "lowLevelNodeToLocation");

    const p2 = buffer.positionForCharacterIndex(node.end());
    logger.debugDetail("p2 found: " + (p2 ? "true" : "false"),
        "FixedActionsManager", "lowLevelNodeToLocation");

    p2.column = p1.column + node.key() ? node.key().length : 0;
    p2.row = p1.row;

    logger.debugDetail("Transformed p1: [" + p1.row + ":" + p1.column + "]",
        "FixedActionsManager", "lowLevelNodeToLocation");

    logger.debugDetail("Transformed p2: [" + p2.row + ":" + p2.column + "]",
        "FixedActionsManager", "lowLevelNodeToLocation");

    const resultStart = editor.getBuffer().characterIndexForPosition(p1);
    const resultEnd = editor.getBuffer().characterIndexForPosition(p2);

    logger.debugDetail("Transformed declaration positions: [" + resultStart + ":" + resultEnd + "]",
        "FixedActionsManager", "lowLevelNodeToLocation");

    return {
        uri: transformedPath,
        range: {
            start: resultStart,
            end: resultEnd
        }
    };
}
