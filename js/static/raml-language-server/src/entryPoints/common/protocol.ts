export type MessageToClientType =
    "VALIDATION_REPORT" |
    "STRUCTURE_REPORT" |
    "EXISTS" |
    "READ_DIR" |
    "IS_DIRECTORY" |
    "CONTENT" |
    "DETAILS_REPORT" |
    "DISPLAY_ACTION_UI";

export type MessageToServerType =
    "OPEN_DOCUMENT" |
    "CHANGE_DOCUMENT" |
    "CLOSE_DOCUMENT" |
    "GET_STRUCTURE" |
    "GET_SUGGESTIONS" |
    "OPEN_DECLARATION" |
    "FIND_REFERENCES" |
    "MARK_OCCURRENCES"|
    "RENAME" |
    "SET_LOGGER_CONFIGURATION" |
    "GET_DETAILS" |
    "CHANGE_POSITION" |
    "CALCULATE_ACTIONS" |
    "EXECUTE_ACTION" |
    "ALL_ACTIONS" |
    "SET_SERVER_CONFIGURATION" |
    "CHANGE_DETAIL_VALUE" |
    "EXECUTE_DETAILS_ACTION";

export interface ProtocolMessage<MessageType extends MessageToClientType | MessageToServerType> {
    type: MessageType;
    payload: any;
    id?: string;
    errorMessage?: string;
}
