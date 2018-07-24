export {
    IValidationReport,
    IStructureReport,
    StructureNodeJSON,
    Suggestion,
    ILogger,
    ILocation,
    IRange,
    MessageSeverity,
    ILoggerSettings,
    DetailsItemJSON,
    IDetailsReport,
    IExecutableAction,
    IUIDisplayRequest
} from "../common/typeInterfaces";

import {
    ITextEdit
} from "../common/typeInterfaces";

export interface IOpenedDocument {
    /**
     * Document URI
     */
    uri: string;

    /**
     * Document content
     */
    text?: string;
}

export interface IChangedDocument {
    /**
     * Document URI
     */
    uri: string;

    /**
     * Document content
     */
    text?: string;

    /**
     * Optional set of text edits instead of complete text replacement.
     * Is only taken into account if text is null.
     */
    textEdits?: ITextEdit[];
}
