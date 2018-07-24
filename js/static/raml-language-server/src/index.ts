import {
    IClientConnection
} from "./client/client";

export {
    IClientConnection
} from "./client/client";

export {
    IValidationReport,
    IOpenedDocument,
    IChangedDocument,
    StructureNodeJSON,
    Suggestion,
    StructureCategories,
    ITextEdit,
    IRange,
    DetailsItemJSON,
    DetailsValuedItemJSON,
    DetailsItemWithOptionsJSON,
    DetailsActionItemJSON,
    DetailsItemType,
    ActionItemSubType,
    IDetailsReport,
    ILocation
} from "./common/typeInterfaces";

export {
    IServerConfiguration,
    IActionsConfiguration
} from "./common/configuration";

export {
    Runnable,
    Reconciler
} from "./common/reconciler";

import {
    getConnection
} from "./entryPoints/node/client/launch";

/**
 * Launches node entry point (separate node server process) and returns client connection.
 * @return {IClientConnection}
 */
export function getNodeClientConnection(): IClientConnection {
    return getConnection();
}

export import textEditProcessor = require("./common/textEditProcessor");

export {
    AbstractClientConnection
} from "./entryPoints/common/client/abstractClient";
