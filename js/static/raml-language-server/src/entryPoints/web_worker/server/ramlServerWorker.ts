import actions = require("raml-actions");

actions.setIsPackaged();

import {
    MessageToClientType,
    ProtocolMessage
} from "../../common/protocol";

import {
    AbstractMSServerConnection
} from "../../common/server/abstractServer";

import {
    Server
} from "../../../server/core/server";

declare function postMessage(message);

class WebWorkerServerConnection extends AbstractMSServerConnection {

    constructor() {
        super("WebServerConnection");
    }

    public sendMessage(message: ProtocolMessage<MessageToClientType>): void {
        postMessage(message);
    }
}

const connection = new WebWorkerServerConnection();

const server = new Server(connection);
server.listen();

self.addEventListener("message", function(e) {
    connection.handleRecievedMessage(e.data);
}, false);
