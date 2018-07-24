import clientInterfaces = require("../../../client/client");
import commonInterfaces = require("../../../common/typeInterfaces");

import {
    MessageToServerType,
    ProtocolMessage
} from "../../common/protocol";

import {
    AbstractClientConnection
} from "../../common/client/abstractClient";

import childProcess = require("child_process");

export class NodeProcessClientConnection extends AbstractClientConnection
    implements clientInterfaces.IClientConnection {

    constructor(private serverProcess: childProcess.ChildProcess) {
        super("NodeProcessClientConnection");

        serverProcess.on("message", (serverMessage: ProtocolMessage<MessageToServerType>) => {
            this.handleRecievedMessage(serverMessage);
        });

        const logger = this;

        serverProcess.stdout.on("data", (data) => {
            console.log(data.toString());
        });

        serverProcess.stderr.on("data", (data) => {
            console.log(data.toString());
        });

        serverProcess.on("close", function(code) {
            console.log("Validation process exited with code " + code, "NodeProcessClientConnection");
        });
    }

    public stop(): void {
        this.serverProcess.kill();
    }

    public sendMessage(message: ProtocolMessage<MessageToServerType>): void {

        this.serverProcess.send(message);
    }
}
