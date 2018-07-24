import clientInterfaces = require("../../../client/client");
import commonInterfaces = require("../../../common/typeInterfaces");

import {
    MessageToServerType,
    ProtocolMessage
} from "../../common/protocol";

import {
    AbstractClientConnection
} from "../../common/client/abstractClient";

export interface IWorker {

    onmessage: {(event: any): void};

    postMessage(message: any): void;

    terminate(): void;
}

export class RAMLClientConnection extends AbstractClientConnection
    implements clientInterfaces.IClientConnection {

    constructor(private worker: IWorker) {
        super("NodeProcessClientConnection");

        worker.onmessage = (event) => {
            this.handleRecievedMessage(event.data);
        };
    }

    public stop(): void {
        this.worker.terminate();
    }

    public sendMessage(message: ProtocolMessage<MessageToServerType>): void {

        this.worker.postMessage(message);
    }
}
