import clientInterfaces = require("../../../client/client");
import { MessageToServerType, ProtocolMessage } from "../../common/protocol";
import { AbstractClientConnection } from "../../common/client/abstractClient";
export interface IWorker {
    onmessage: {
        (event: any): void;
    };
    postMessage(message: any): void;
    terminate(): void;
}
export declare class RAMLClientConnection extends AbstractClientConnection implements clientInterfaces.IClientConnection {
    private worker;
    constructor(worker: IWorker);
    stop(): void;
    sendMessage(message: ProtocolMessage<MessageToServerType>): void;
}
