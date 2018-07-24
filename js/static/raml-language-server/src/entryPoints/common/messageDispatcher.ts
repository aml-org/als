import {
    MessageToClientType,
    MessageToServerType,
    ProtocolMessage
} from "./protocol";

import {
    ILogger,
    ILoggerSettings,
    MessageSeverity
} from "../../common/typeInterfaces";

import shortid = require("shortid");

interface CallBackHandle<ResultType> {
    resolve?: (value?: ResultType) => void;
    reject?: (error?: any) => void;
}

export abstract class MessageDispatcher<MessageType extends MessageToClientType | MessageToServerType>
    implements ILogger {
    private callBacks: {[messageId: string]: CallBackHandle<any>} = {};

    constructor(protected name: string) {}

    /**
     * Sends message to the counterpart.
     * @param message
     */
    public abstract sendMessage(message: ProtocolMessage<MessageType>): void;

    /**
     * Logs a message
     * @param message - message text
     * @param severity - message severity
     * @param component - component name
     * @param subcomponent - sub-component name
     */
    public abstract log(message: string, severity: MessageSeverity,
                        component?: string, subcomponent?: string): void;

    /**
     * Logs a DEBUG severity message.
     * @param message - message text
     * @param component - component name
     * @param subcomponent - sub-component name
     */
    public abstract debug(message: string,
                          component?: string, subcomponent?: string): void;

    /**
     * Logs a DEBUG_DETAIL severity message.
     * @param message - message text
     * @param component - component name
     * @param subcomponent - sub-component name
     */
    public abstract debugDetail(message: string,
                                component?: string, subcomponent?: string): void;

    /**
     * Logs a DEBUG_OVERVIEW severity message.
     * @param message - message text
     * @param component - component name
     * @param subcomponent - sub-component name
     */
    public abstract debugOverview(message: string,
                                  component?: string, subcomponent?: string): void;

    /**
     * Logs a WARNING severity message.
     * @param message - message text
     * @param component - component name
     * @param subcomponent - sub-component name
     */
    public abstract warning(message: string,
                            component?: string, subcomponent?: string): void;

    /**
     * Logs an ERROR severity message.
     * @param message - message text
     * @param component - component name
     * @param subcomponent - sub-component name
     */
    public abstract error(message: string,
                          component?: string, subcomponent?: string): void;

    /**
     * Sets logger configuration, both for the server and for the client.
     * @param loggerSettings
     */
    public abstract setLoggerConfiguration(loggerSettings: ILoggerSettings): void;

    /**
     * Sends message to the counterpart
     * @param message
     */
    public send(message: ProtocolMessage<MessageType>): void {
        this.debug("Sending message of type: " + message.type, "MessageDispatcher:" + this.name, "send");

        try {
            let strPayload: string = "";
            if (message.payload != null) {
                strPayload = (typeof(message.payload) === "string") ?
                    message.payload : JSON.stringify(message.payload, null, 2);
            }

            this.debugDetail("Message "
                + message.type + " , payload is:\n" + strPayload,
                "MessageDispatcher:" + this.name, "send");
        } catch (Error) {

        }

        this.sendMessage(message);
    }

    /**
     * Sends message to the counterpart and hooks for response.
     * When response comes back, calls the appropriate handler method.
     * @param message
     * @return promise, which will contain the result returned by the counterpart
     */
    public sendWithResponse<ResultType>(message: ProtocolMessage<MessageType>): Promise<ResultType> {

        this.debug("Sending message with response of type: " + message.type,
            "MessageDispatcher:" + this.name, "sendWithResonse");

        return new Promise((resolve: (value?: ResultType) => void, reject: (error?: any) => void) => {

            message.id = shortid.generate();

            this.callBacks[message.id] = {
                resolve,
                reject
            };

            try {
                let strPayload: string = "";
                try {
                    if (message.payload != null) {
                        strPayload = (typeof(message.payload) === "string") ?
                            message.payload : JSON.stringify(message.payload, null, 2);
                    }
                } catch (Error) {
                    this.error(Error, "MessageDispatcher:" + this.name, "sendWithResponse");
                }

                this.debugDetail("Message "
                    + message.type + " , payload is:\n" + strPayload,
                    "MessageDispatcher:" + this.name, "sendWithResponse");
            } catch (Error) {
            }

            this.sendMessage(message);
        });
    }

    /**
     * Finds a method in the current instance named as message type and calls it with
     * the message payload as an argument.
     *
     * If message assumes an answer, sends the results backwards.
     *
     * Is designed to be called by subclasses.
     * @param message
     */
    public handleRecievedMessage(message: ProtocolMessage<MessageType>) {
        this.debug("Recieved message of type: " + message.type + " and id: " + message.id,
            "MessageDispatcher:" + this.name, "handleRecievedMessage");

        if (message.id && this.callBacks[message.id]) {
            this.debugDetail("MessageDispatcher:handleRecievedMessage Message callback found",
                "MessageDispatcher:" + this.name, "handleRecievedMessage");
            // this is a response for a request sent earlier
            // lets find its resolve/error and call it

            const callBackHandle =  this.callBacks[message.id];

            try {

                if (message.errorMessage && callBackHandle) {

                    callBackHandle.reject(new Error(message.errorMessage));
                } else {

                    callBackHandle.resolve(message.payload);
                }
            } finally {
                delete this.callBacks[message.id];
            }
        } else {
            this.debugDetail("Looking for method " + message.type,
                "MessageDispatcher:" + this.name, "handleRecievedMessage");
            const method = this[message.type as string];
            if (!method) {
                this.debugDetail("Method NOT found: " + message.type,
                    "MessageDispatcher+" + this.name, "handleRecievedMessage");
                return;
            } else {
                this.debugDetail("Method found: " + message.type,
                    "MessageDispatcher:" + this.name, "handleRecievedMessage");
            }

            if (typeof(method) !== "function") {
                return;
            }

            // if this is not a response, just a direct message, lets call a handler
            let result = null;

            try {
                result = method.call(this, message.payload);
            } catch (error) {
                this.handleCommunicationError(error, message);
                return;
            }

            this.debugDetail("Called method " + message.type + " result is: " + result,
                "MessageDispatcher:" + this.name, "handleRecievedMessage");

            // if we've got some result and message has ID, so the answer is expected
            if (result && message.id) {
                if (result && (result as any).then && (result as any).catch) {
                    // TODO more precise instanceof

                    // looks like a promise, lets send the answer when its ready
                    result.then(
                        (resolvedResult) => {
                            this.debugDetail("Result promise resolved successfully",
                                "MessageDispatcher:" + this.name, "handleRecievedMessage");

                            try {
                                let strResult: string = "";
                                if (resolvedResult != null) {
                                    strResult = (typeof(resolvedResult) === "string") ?
                                        resolvedResult : JSON.stringify(resolvedResult, null, 2);
                                }

                                this.debugDetail("Message "
                                    + message.type + " , result is:\n" + strResult,
                                    "MessageDispatcher:" + this.name, "handleRecievedMessage");
                            } catch (Error) {

                            }

                            this.send({
                                type: message.type,
                                payload: resolvedResult,
                                id: message.id
                            });
                        },
                        (error) => {
                            this.debugDetail("Result promise failed of message "
                                + message.type + " , error message is:\n" + error.message,
                                "MessageDispatcher:" + this.name, "handleRecievedMessage");

                            this.send({
                                type: message.type,
                                payload: {},
                                id: message.id,
                                errorMessage: error.message
                            });
                        });
                } else {
                    // sending back immediatelly
                    const responseMessage = {
                        type: message.type,
                        payload: result,
                        id: message.id
                    };
                    this.sendMessage(responseMessage);
                }
            }
        }

    }

    protected handleCommunicationError(error: Error, originalMessage: ProtocolMessage<MessageType>) {
        this.error("Error on message handler execution: " + Error);

        if (originalMessage.id) {
            this.send({
                type: originalMessage.type,
                payload: {},
                id: originalMessage.id,
                errorMessage: error.message
            });
        }
    }
}
