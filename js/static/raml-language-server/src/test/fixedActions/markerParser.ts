import fs = require("fs")

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
    markerEndSign;
}

/**
 * Marker schema: single position or range.
 */
export type MarkerSchema = SingleMarkerSchema | RangeMarkerSchema;

/**
 * Instanceof for SingleMarkerSchema
 * @param schema
 * @return {boolean}
 */
export function isSingleMarkerSchema(schema: MarkerSchema): schema is SingleMarkerSchema {
    return (schema as SingleMarkerSchema).markerSign != null;
}

/**
 * Instanceof for RangeMarkerSchema
 * @param schema
 * @return {boolean}
 */
export function isRangeMarkerSchema(schema: MarkerSchema): schema is RangeMarkerSchema {
    return (schema as RangeMarkerSchema).markerStartSign != null;
}

/**
 * The result of marker parsing.
 */
export class ParseResult {

    /**
     * Markers.
     * @type {{}}
     */
    private markers: {[markerType: string]: Marker[]} = {}

    /**
     * Original RAML text
     */
    private originalText: string;

    /**
     * RAML text stripped from markers.
     */
    private strippedText: string;

    constructor(originalText: string, strippedText: string, markers: Marker[]) {
        this.originalText = originalText;
        this.strippedText = strippedText;

        for (const marker of markers) {
            let markersOfType = this.markers[marker.typeName];
            if (!markersOfType) {
                markersOfType = [];
                this.markers[marker.typeName] = markersOfType;
            }

            markersOfType.push(marker);
        }
    }

    /**
     * Gets all markers of certain type.
     * @param markerType
     * @return {Marker[]}
     */
    public getMarkersByType(markerType: string) {
        return this.markers[markerType];
    }

    /**
     * Gets a single marker of type, returns the first one if there are many
     * @param markerType
     * @return {any}
     */
    public getMarkerByType(markerType: string) {
        const markersOfType = this.markers[markerType];
        if (!markersOfType) {
            return null;
        }

        return markersOfType[0];
    }

    /**
     * Returns position of the first marker of certain type.
     * @param markerType
     */
    public getMarkerPosition(markerType: string) {
        const marker = this.getMarkerByType(markerType);

        if (!marker) {
            return null;
        }

        return marker.start;
    }

    /**
     * Gets ranges for a certain marker type.
     * @param markerType
     * @return {Array}
     */
    public getMarkerRanges(markerType): {start: number, end: number}[] {
        const markersOfType = this.markers[markerType];
        if (!markersOfType) {
            return [];
        }

        return markersOfType.map((marker) => {
            return {
                start: marker.start,
                end: marker.end
            };
        });
    }

    /**
     * Gets start positions for a certain type of marker.
     * @param markerType
     * @return {any}
     */
    public getMarkerPositions(markerType): number[] {
        const markersOfType = this.markers[markerType];
        if (!markersOfType) {
            return [];
        }

        return markersOfType.map((marker) => {
            return marker.start;
        });
    }

    public getOriginalText() {
        return this.originalText;
    }

    public getStrippedText() {
        return this.strippedText;
    }
}

/**
 * Pares file.
 * @param filePath
 * @return {ParseResult}
 */
export function parseFileSync(filePath: string, schemas: MarkerSchema[]): ParseResult {
    const text = fs.readFileSync(filePath).toString();

    return parseText(text, schemas);
}

/**
 * Parses text.
 * @param originalText
 */
export function parseText(originalText: string, schemas: MarkerSchema[]) : ParseResult {

    const markers = findMarkers(originalText, schemas);

    const strippedText = stripTextFromMarkers(originalText, schemas);

    return new ParseResult(originalText, strippedText, markers);
}

function findMarkers(originalText: string, schemas: MarkerSchema[]): Marker[] {
    const markers: Marker[] = [];

    let signs: {start: number, length: number}[] = [];

    for (const schema of schemas) {

        const regexp = regexpForSchema(schema)

        let match = regexp.exec(originalText);
        while (match) {

            const marker: Marker = {
                typeName: schema.typeName,
                isRange: isRangeMarkerSchema(schema),
                start: match.index,
                end: isSingleMarkerSchema(schema) ? match.index : match.index + match[0].length,
                schema
            }

            markers.push(marker);

            if (isSingleMarkerSchema(schema)) {
                signs.push({
                    start: match.index,
                    length: match[0].length
                });
            } else if (isRangeMarkerSchema(schema)) {
                signs.push({
                    start: match.index,
                    length: match[1].length
                });

                signs.push({
                    start: match.index + match[1].length,
                    length:  match[2].length
                });
            }

            match = regexp.exec(originalText);
        }
    }

    // correction of marker indexes basing on the shift due to marker signs cut-off

    signs = signs.sort((first, second) => {
        return first.start - second.start;
    })

    markers.forEach((marker) => {
        if (isSingleMarkerSchema(marker.schema)) {

            const offset = calculateOffsetForPosition(marker.start, signs);
            marker.start = marker.start + offset;
            marker.end = marker.end + offset;

        } else if (isRangeMarkerSchema(marker.schema)) {

            const startOffset = calculateOffsetForPosition(marker.start, signs);
            const endOffset = calculateOffsetForPosition(marker.end, signs);
            marker.start = marker.start + startOffset;
            marker.end = marker.end + endOffset;

        }
    });

    return markers;
}

function calculateOffsetForPosition(originalPosition: number,
                                    signs: {start: number, length: number}[]): number {

    let offset = 0;
    for (const sign of signs) {
        if (sign.start >= originalPosition) {
            break;
        }

        offset -= sign.length;
    }

    return offset;
}

function stripTextFromMarkers(originalText: string, schemas: MarkerSchema[]): string {

    let text = originalText;

    for (const schema of schemas) {

        const regexp = regexpForSchema(schema)

        text = text.replace(regexp, "");
    }

    return text;
}

/**
 * Creates regexp for marker.
 * @param marker
 */
function regexpForSchema(schema: MarkerSchema): RegExp {
    let regexp: RegExp = null;

    if (isSingleMarkerSchema(schema)) {

        regexp = new RegExp(schema.markerSign, "g");

    } else if (isRangeMarkerSchema(schema)) {

        regexp = new RegExp("(" + schema.markerStartSign + ").*(" + schema.markerEndSign + ")", "g");
    }

    // regexp.global = true;

    return regexp;
}
