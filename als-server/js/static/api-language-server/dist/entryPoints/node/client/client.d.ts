/// <reference types="node" />
import clientInterfaces = require("../../../client/client");
import { MessageToServerType, ProtocolMessage } from "../../common/protocol";
import { AbstractClientConnection } from "../../common/client/abstractClient";
import childProcess = require("child_process");
export declare class NodeProcessClientConnection extends AbstractClientConnection implements clientInterfaces.IClientConnection {
    private serverProcess;
    constructor(serverProcess: childProcess.ChildProcess);
    stop(): void;
    sendMessage(message: ProtocolMessage<MessageToServerType>): void;
}
