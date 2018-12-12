import { IClientConnection } from "./client/client";
export { IClientConnection } from "./client/client";
export { IValidationReport, IOpenedDocument, IChangedDocument, StructureNodeJSON, Suggestion, StructureCategories, ITextEdit, IRange, DetailsItemJSON, DetailsValuedItemJSON, DetailsItemWithOptionsJSON, DetailsActionItemJSON, DetailsItemType, ActionItemSubType, IDetailsReport, ILocation } from "./common/typeInterfaces";
export { IServerConfiguration, IActionsConfiguration } from "./common/configuration";
export { Runnable, Reconciler } from "./common/reconciler";
/**
 * Launches node entry point (separate node server process) and returns client connection.
 * @return {IClientConnection}
 */
export declare function getNodeClientConnection(): IClientConnection;
export import textEditProcessor = require("./common/textEditProcessor");
export { AbstractClientConnection } from "./entryPoints/common/client/abstractClient";
