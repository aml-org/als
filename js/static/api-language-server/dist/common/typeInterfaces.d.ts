import outline = require("./outline");
import suggestions = require("./suggestions");
export { MessageSeverity, ILoggerSettings, ILogger } from "./logger";
/**
 * Structure node JSON representation.
 */
export declare type StructureNodeJSON = outline.StructureNodeJSON;
/**
 * Code completion suggestion
 */
export declare type Suggestion = suggestions.Suggestion;
/**
 * Details node JSON representation.
 */
export declare type DetailsItemJSON = outline.DetailsItemJSON;
/**
 * Details item having a value text.
 */
export declare type DetailsValuedItemJSON = outline.DetailsValuedItemJSON;
/**
 * Details item having potential value options.
 */
export declare type DetailsItemWithOptionsJSON = outline.DetailsItemWithOptionsJSON;
/**
 * Type of details item
 */
export declare type DetailsItemType = outline.DetailsItemType;
/**
 * Subtype for action items.
 */
export declare type ActionItemSubType = outline.ActionItemSubType;
/**
 * Details item pointing to an executable action.
 */
export declare type DetailsActionItemJSON = outline.DetailsActionItemJSON;
/**
 * Range in the document.
 */
export interface IRange {
    /**
     * Range start position, counting from 0
     */
    start: number;
    /**
     * Range end position, counting from 0
     */
    end: number;
}
export interface IValidationIssue {
    /**
     * Error code
     */
    code: string;
    /**
     * Error type.
     */
    type: string;
    /**
     * To be renamed to uri.
     */
    filePath: string;
    text: string;
    range: IRange;
    trace: IValidationIssue[];
}
export interface IValidationReport {
    /**
     * This is the "point of view" uri, actual reported unit paths are located
     * in the particular issues.
     */
    pointOfViewUri: string;
    /**
     * Optional document version of the point of view.
     */
    version?: number;
    /**
     * Validation issues.
     */
    issues: IValidationIssue[];
}
export interface IStructureReport {
    /**
     * Document uri.
     */
    uri: string;
    /**
     * Optional document version.
     */
    version?: number;
    /**
     * Document structure.
     */
    structure: {
        [categoryName: string]: StructureNodeJSON;
    };
}
export interface IDetailsReport {
    /**
     * Document uri.
     */
    uri: string;
    /**
     * Cursor position in the document, starting from 0.
     */
    position: number;
    /**
     * Optional document version.
     */
    version?: number;
    /**
     * Details root item.
     */
    details: DetailsItemJSON;
}
export interface IOpenedDocument {
    /**
     * Document URI
     */
    uri: string;
    /**
     * Optional document version.
     */
    version?: number;
    /**
     * Document content
     */
    text?: string;
}
export interface ITextEdit {
    /**
     * Range to replace. Range start==end==0 => insert into the beginning of the document,
     * start==end==document end => insert into the end of the document
     */
    range: IRange;
    /**
     * Text to replace given range with.
     */
    text: string;
}
export interface IChangedDocument {
    /**
     * Document URI
     */
    uri: string;
    /**
     * Optional document version.
     */
    version?: number;
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
/**
 * Executes document changes.
 */
export interface IDocumentChangeExecutor {
    /**
     * Changes the document.
     * @param change
     */
    changeDocument(change: IChangedDocument): Promise<void>;
}
export declare enum StructureCategories {
    ResourcesCategory,
    SchemasAndTypesCategory,
    ResourceTypesAndTraitsCategory,
    OtherCategory,
}
export declare enum Icons {
    ARROW_SMALL_LEFT,
    PRIMITIVE_SQUARE,
    PRIMITIVE_DOT,
    FILE_SUBMODULE,
    TAG,
    FILE_BINARY,
    BOOK,
}
export declare enum TextStyles {
    NORMAL,
    HIGHLIGHT,
    WARNING,
    SUCCESS,
}
/**
 * Range in a particular document
 */
export interface ILocation {
    /**
     * Document uri
     */
    uri: string;
    /**
     * Optional document version.
     */
    version?: number;
    /**
     * Range in the document.
     */
    range: IRange;
}
/**
 * Actions are being exposed as this outer interface.
 */
export interface IExecutableAction {
    /**
     * Unique action ID.
     */
    id: string;
    /**
     * Displayed menu item name
     */
    name: string;
    /**
     * Action target (like editor node, tree viewer etc).
     * The value must be recognizable by action consumers.
     * Some of the standard values are defined in this module.
     */
    target: string;
    /**
     * Whether action has client-side UI part.
     */
    hasUI: boolean;
    /**
     * Action category and potential subcategories.
     * In example, item with a name "itemName" and categories ["cat1", "cat2"]
     * will be displayed as the following menu hierarchy: cat1/cat2/itemName
     */
    category?: string[];
    /**
     * Optional label, will be used instead of name for display purpose
     */
    label?: string;
}
/**
 * Server->Client request for UI display.
 */
export interface IUIDisplayRequest {
    /**
     * Action that requires UI to be displayed.
     */
    action: IExecutableAction;
    /**
     * JS code displaying UI.
     */
    uiCode: string;
    /**
     * Arbitrary JSON setting up initial UI state.
     */
    initialUIState: any;
}
