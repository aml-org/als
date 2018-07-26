import { MessageToClientType, MessageToServerType, ProtocolMessage } from "./protocol";
import { ILogger, ILoggerSettings, MessageSeverity } from "../../common/typeInterfaces";
export declare abstract class MessageDispatcher<MessageType extends MessageToClientType | MessageToServerType> implements ILogger {
    protected name: string;
    private callBacks;
    constructor(name: string);
    /**
     * Sends message to the counterpart.
     * @param message
     */
    abstract sendMessage(message: ProtocolMessage<MessageType>): void;
    /**
     * Logs a message
     * @param message - message text
     * @param severity - message severity
     * @param component - component name
     * @param subcomponent - sub-component name
     */
    abstract log(message: string, severity: MessageSeverity, component?: string, subcomponent?: string): void;
    /**
     * Logs a DEBUG severity message.
     * @param message - message text
     * @param component - component name
     * @param subcomponent - sub-component name
     */
    abstract debug(message: string, component?: string, subcomponent?: string): void;
    /**
     * Logs a DEBUG_DETAIL severity message.
     * @param message - message text
     * @param component - component name
     * @param subcomponent - sub-component name
     */
    abstract debugDetail(message: string, component?: string, subcomponent?: string): void;
    /**
     * Logs a DEBUG_OVERVIEW severity message.
     * @param message - message text
     * @param component - component name
     * @param subcomponent - sub-component name
     */
    abstract debugOverview(message: string, component?: string, subcomponent?: string): void;
    /**
     * Logs a WARNING severity message.
     * @param message - message text
     * @param component - component name
     * @param subcomponent - sub-component name
     */
    abstract warning(message: string, component?: string, subcomponent?: string): void;
    /**
     * Logs an ERROR severity message.
     * @param message - message text
     * @param component - component name
     * @param subcomponent - sub-component name
     */
    abstract error(message: string, component?: string, subcomponent?: string): void;
    /**
     * Sets logger configuration, both for the server and for the client.
     * @param loggerSettings
     */
    abstract setLoggerConfiguration(loggerSettings: ILoggerSettings): void;
    /**
     * Sends message to the counterpart
     * @param message
     */
    send(message: ProtocolMessage<MessageType>): void;
    /**
     * Sends message to the counterpart and hooks for response.
     * When response comes back, calls the appropriate handler method.
     * @param message
     * @return promise, which will contain the result returned by the counterpart
     */
    sendWithResponse<ResultType>(message: ProtocolMessage<MessageType>): Promise<ResultType>;
    /**
     * Finds a method in the current instance named as message type and calls it with
     * the message payload as an argument.
     *
     * If message assumes an answer, sends the results backwards.
     *
     * Is designed to be called by subclasses.
     * @param message
     */
    handleRecievedMessage(message: ProtocolMessage<MessageType>): void;
    protected handleCommunicationError(error: Error, originalMessage: ProtocolMessage<MessageType>): void;
}
