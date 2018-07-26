/**
 * Marker inside RAML text.
 */
export interface Marker {
    /**
     * Marker type name.
     */
    typeName: string;
    /**
     * Whether marker is range or single position.
     */
    isRange: boolean;
    /**
     * Marker start
     */
    start: number;
    /**
     * Marker end
     */
    end: number;
    /**
     * Schema that produced the marker.
     */
    schema: MarkerSchema;
}
/**
 * Abstract marker schema.
 */
export interface AbstractMarkerSchema {
    /**
     * Marker type name.
     */
    typeName: string;
}
/**
 * Marker schema for a single position marker.
 */
export interface SingleMarkerSchema extends AbstractMarkerSchema {
    /**
     * Marker sign regexp.
     */
    markerSign: string;
}
export interface RangeMarkerSchema extends AbstractMarkerSchema {
    /**
     * Marker start sign regexp.
     */
    markerStartSign: string;
    /**
     * Marker end sign regexp.
     */
    markerEndSign: any;
}
/**
 * Marker schema: single position or range.
 */
export declare type MarkerSchema = SingleMarkerSchema | RangeMarkerSchema;
/**
 * Instanceof for SingleMarkerSchema
 * @param schema
 * @return {boolean}
 */
export declare function isSingleMarkerSchema(schema: MarkerSchema): schema is SingleMarkerSchema;
/**
 * Instanceof for RangeMarkerSchema
 * @param schema
 * @return {boolean}
 */
export declare function isRangeMarkerSchema(schema: MarkerSchema): schema is RangeMarkerSchema;
/**
 * The result of marker parsing.
 */
export declare class ParseResult {
    /**
     * Markers.
     * @type {{}}
     */
    private markers;
    /**
     * Original RAML text
     */
    private originalText;
    /**
     * RAML text stripped from markers.
     */
    private strippedText;
    constructor(originalText: string, strippedText: string, markers: Marker[]);
    /**
     * Gets all markers of certain type.
     * @param markerType
     * @return {Marker[]}
     */
    getMarkersByType(markerType: string): Marker[];
    /**
     * Gets a single marker of type, returns the first one if there are many
     * @param markerType
     * @return {any}
     */
    getMarkerByType(markerType: string): Marker;
    /**
     * Returns position of the first marker of certain type.
     * @param markerType
     */
    getMarkerPosition(markerType: string): number;
    /**
     * Gets ranges for a certain marker type.
     * @param markerType
     * @return {Array}
     */
    getMarkerRanges(markerType: any): {
        start: number;
        end: number;
    }[];
    /**
     * Gets start positions for a certain type of marker.
     * @param markerType
     * @return {any}
     */
    getMarkerPositions(markerType: any): number[];
    getOriginalText(): string;
    getStrippedText(): string;
}
/**
 * Pares file.
 * @param filePath
 * @return {ParseResult}
 */
export declare function parseFileSync(filePath: string, schemas: MarkerSchema[]): ParseResult;
/**
 * Parses text.
 * @param originalText
 */
export declare function parseText(originalText: string, schemas: MarkerSchema[]): ParseResult;
