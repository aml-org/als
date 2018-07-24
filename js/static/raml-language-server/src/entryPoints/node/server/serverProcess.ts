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

import fs = require("fs");

class NodeProcessServerConnection extends AbstractMSServerConnection {

    constructor() {
        super("NodeProcessServerConnection");
    }

    public sendMessage(message: ProtocolMessage<MessageToClientType>): void {

        try {
            this.debugDetail(JSON.stringify(message),
                "MessageDispatcher:NodeProcessServerConnection", "sendMessage")
        } catch (Error) {
            this.error(Error,
                "MessageDispatcher:NodeProcessServerConnection", "sendMessage")
        }

        process.send(message);
    }

    /**
     * Returns whether path/url exists.
     * @param fullPath
     */
    public exists(path: string): Promise<boolean> {
        return new Promise((resolve) => {
            fs.exists(path, (result) => {resolve(result); });
        });
    }

    /**
     * Returns directory content list.
     * @param fullPath
     */
    public readDir(path: string): Promise<string[]> {
        return new Promise((resolve) => {
            fs.readdir(path, (err, result) => {resolve(result); });
        });
    }

    /**
     * Returns whether path/url represents a directory
     * @param path
     */
    public isDirectory(path: string): Promise<boolean> {
        return new Promise((resolve) => {
            fs.stat(path, (err, stats) => {resolve(stats.isDirectory()); });
        });
    }

    /**
     * File contents by full path/url.
     * @param path
     */
    public content(path: string): Promise<string> {
        return new Promise(function(resolve, reject) {

            fs.readFile(path, (err, data) => {
                if (err != null) {
                    return reject(err);
                }

                const content = data.toString();
                resolve(content);
            });
        });
    }
}

const connection = new NodeProcessServerConnection();

const server = new Server(connection);
server.listen();

process.on("message", (data: ProtocolMessage<MessageToClientType>) => {

    try {
        connection.debugDetail(JSON.stringify(data),
            "MessageDispatcher:NodeProcessServerConnection", "recieveMessage");
    } catch (Error) {
        connection.error(Error,
            "MessageDispatcher:NodeProcessServerConnection", "recieveMessage");
    }

    connection.handleRecievedMessage(data);
});
