import { IClientConnection } from "../../../client/client";
export { StructureCategories } from "../../../common/typeInterfaces";
export { Reconciler } from "../../../common/reconciler";
export declare function getConnection(): IClientConnection;
export import textEditProcessor = require("../../../common/textEditProcessor");
export declare function launch(workerFilePath?: string): IClientConnection;
