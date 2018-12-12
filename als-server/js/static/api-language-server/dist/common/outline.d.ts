/**
 * Structure node JSON representation.
 */
export interface StructureNodeJSON {
    /**
     * Node label text to be displayed.
     */
    text: string;
    /**
     * Node type label, if any.
     */
    typeText: string;
    /**
     * Node icon. Structure module is not setting up, how icons are represented in the client
     * system, or what icons exist,
     * instead the client is responsible to configure the mapping from nodes to icon identifiers.
     */
    icon: string;
    /**
     * Text style of the node. Structure module is not setting up, how text styles are represented in the client
     * system, or what text styles exist,
     * instead the client is responsible to configure the mapping from nodes to text styles identifiers.
     */
    textStyle: string;
    /**
     * Unique node identifier.
     */
    key: string;
    /**
     * Node start position from the beginning of the document.
     */
    start: number;
    /**
     * Node end position from the beginning of the document.
     */
    end: number;
    /**
     * Whether the node is selected.
     */
    selected: boolean;
    /**
     * Node children.
     */
    children: StructureNodeJSON[];
    /**
     * Node category, if determined by a category filter.
     */
    category: string;
}

/**
 * The node of details tree converted to JSON
 */
export interface DetailsItemJSON {
    /**
     * Node title.
     */
    title: string;
    /**
     * Node description
     */
    description: string;
    /**
     * Node type name
     */
    type: string;
    /**
     * Error, associated with the node.
     */
    error: string;
    /**
     * Node children.
     */
    children: DetailsItemJSON[];
    /**
     * Node ID.
     */
    id: string;
}
/**
 * Details item having a value text.
 */
export interface DetailsValuedItemJSON extends DetailsItemJSON {
    /**
     * Value text.
     */
    valueText: string;
}
/**
 * Details item having potential value options
 */
export interface DetailsItemWithOptionsJSON extends DetailsValuedItemJSON {
    /**
     * Potential options.
     */
    options: string[];
}

/**
 * Details item pointing to an executable action.
 */
export interface DetailsActionItemJSON extends DetailsItemJSON {
    /**
     * Action item subtype.
     */
    subType: string;
}
/**
 * Type of details item
 */
export declare enum DetailsItemType {
    ROOT = 0,
    CATEGORY = 1,
    CHECKBOX = 2,
    JSONSCHEMA = 3,
    XMLSCHEMA = 4,
    MARKDOWN = 5,
    SELECTBOX = 6,
    MULTIEDITOR = 7,
    TREE = 8,
    STRUCTURED = 9,
    TYPEDISPLAY = 10,
    TYPESELECT = 11,
    JSONEXAMPLE = 12,
    XMLEXAMPLE = 13,
    ATTRIBUTETEXT = 14,
    DETAILS_ACTION = 15,
}
/**
 * Subtype for action items.
 */
export declare enum ActionItemSubType {
    INSERT = 0,
    INSERT_VALUE = 1,
    DELETE = 2,
}