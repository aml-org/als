"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var shortid = require("shortid");
var MessageDispatcher = (function () {
    function MessageDispatcher(name) {
        this.name = name;
        this.callBacks = {};
    }
    /**
     * Sends message to the counterpart
     * @param message
     */
    MessageDispatcher.prototype.send = function (message) {
        this.debug("Sending message of type: " + message.type, "MessageDispatcher:" + this.name, "send");
        try {
            var strPayload = "";
            if (message.payload != null) {
                strPayload = (typeof (message.payload) === "string") ?
                    message.payload : JSON.stringify(message.payload, null, 2);
            }
            this.debugDetail("Message "
                + message.type + " , payload is:\n" + strPayload, "MessageDispatcher:" + this.name, "send");
        }
        catch (Error) {
        }
        this.sendMessage(message);
    };
    /**
     * Sends message to the counterpart and hooks for response.
     * When response comes back, calls the appropriate handler method.
     * @param message
     * @return promise, which will contain the result returned by the counterpart
     */
    MessageDispatcher.prototype.sendWithResponse = function (message) {
        var _this = this;
        this.debug("Sending message with response of type: " + message.type, "MessageDispatcher:" + this.name, "sendWithResonse");
        return new Promise(function (resolve, reject) {
            message.id = shortid.generate();
            _this.callBacks[message.id] = {
                resolve: resolve,
                reject: reject
            };
            try {
                var strPayload = "";
                try {
                    if (message.payload != null) {
                        strPayload = (typeof (message.payload) === "string") ?
                            message.payload : JSON.stringify(message.payload, null, 2);
                    }
                }
                catch (Error) {
                    _this.error(Error, "MessageDispatcher:" + _this.name, "sendWithResponse");
                }
                _this.debugDetail("Message "
                    + message.type + " , payload is:\n" + strPayload, "MessageDispatcher:" + _this.name, "sendWithResponse");
            }
            catch (Error) {
            }
            _this.sendMessage(message);
        });
    };
    /**
     * Finds a method in the current instance named as message type and calls it with
     * the message payload as an argument.
     *
     * If message assumes an answer, sends the results backwards.
     *
     * Is designed to be called by subclasses.
     * @param message
     */
    MessageDispatcher.prototype.handleRecievedMessage = function (message) {
        var _this = this;
        this.debug("Recieved message of type: " + message.type + " and id: " + message.id, "MessageDispatcher:" + this.name, "handleRecievedMessage");
        if (message.id && this.callBacks[message.id]) {
            this.debugDetail("MessageDispatcher:handleRecievedMessage Message callback found", "MessageDispatcher:" + this.name, "handleRecievedMessage");
            // this is a response for a request sent earlier
            // lets find its resolve/error and call it
            var callBackHandle = this.callBacks[message.id];
            try {
                if (message.errorMessage && callBackHandle) {
                    callBackHandle.reject(new Error(message.errorMessage));
                }
                else {
                    callBackHandle.resolve(message.payload);
                }
            }
            finally {
                delete this.callBacks[message.id];
            }
        }
        else {
            this.debugDetail("Looking for method " + message.type, "MessageDispatcher:" + this.name, "handleRecievedMessage");
            var method = this[message.type];
            if (!method) {
                this.debugDetail("Method NOT found: " + message.type, "MessageDispatcher+" + this.name, "handleRecievedMessage");
                return;
            }
            else {
                this.debugDetail("Method found: " + message.type, "MessageDispatcher:" + this.name, "handleRecievedMessage");
            }
            if (typeof (method) !== "function") {
                return;
            }
            // if this is not a response, just a direct message, lets call a handler
            var result = null;
            try {
                result = method.call(this, message.payload);
            }
            catch (error) {
                this.handleCommunicationError(error, message);
                return;
            }
            this.debugDetail("Called method " + message.type + " result is: " + result, "MessageDispatcher:" + this.name, "handleRecievedMessage");
            // if we've got some result and message has ID, so the answer is expected
            if (result && message.id) {
                if (result && result.then && result.catch) {
                    // TODO more precise instanceof
                    // looks like a promise, lets send the answer when its ready
                    result.then(function (resolvedResult) {
                        _this.debugDetail("Result promise resolved successfully", "MessageDispatcher:" + _this.name, "handleRecievedMessage");
                        try {
                            var strResult = "";
                            if (resolvedResult != null) {
                                strResult = (typeof (resolvedResult) === "string") ?
                                    resolvedResult : JSON.stringify(resolvedResult, null, 2);
                            }
                            _this.debugDetail("Message "
                                + message.type + " , result is:\n" + strResult, "MessageDispatcher:" + _this.name, "handleRecievedMessage");
                        }
                        catch (Error) {
                        }
                        _this.send({
                            type: message.type,
                            payload: resolvedResult,
                            id: message.id
                        });
                    }, function (error) {
                        _this.debugDetail("Result promise failed of message "
                            + message.type + " , error message is:\n" + error.message, "MessageDispatcher:" + _this.name, "handleRecievedMessage");
                        _this.send({
                            type: message.type,
                            payload: {},
                            id: message.id,
                            errorMessage: error.message
                        });
                    });
                }
                else {
                    // sending back immediatelly
                    var responseMessage = {
                        type: message.type,
                        payload: result,
                        id: message.id
                    };
                    this.sendMessage(responseMessage);
                }
            }
        }
    };
    MessageDispatcher.prototype.handleCommunicationError = function (error, originalMessage) {
        this.error("Error on message handler execution: " + Error);
        if (originalMessage.id) {
            this.send({
                type: originalMessage.type,
                payload: {},
                id: originalMessage.id,
                errorMessage: error.message
            });
        }
    };
    return MessageDispatcher;
}());
exports.MessageDispatcher = MessageDispatcher;
//# sourceMappingURL=messageDispatcher.js.map