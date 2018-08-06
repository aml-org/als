"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var fs = require("fs");
/**
 * Instanceof for SingleMarkerSchema
 * @param schema
 * @return {boolean}
 */
function isSingleMarkerSchema(schema) {
    return schema.markerSign != null;
}
exports.isSingleMarkerSchema = isSingleMarkerSchema;
/**
 * Instanceof for RangeMarkerSchema
 * @param schema
 * @return {boolean}
 */
function isRangeMarkerSchema(schema) {
    return schema.markerStartSign != null;
}
exports.isRangeMarkerSchema = isRangeMarkerSchema;
/**
 * The result of marker parsing.
 */
var ParseResult = (function () {
    function ParseResult(originalText, strippedText, markers) {
        /**
         * Markers.
         * @type {{}}
         */
        this.markers = {};
        this.originalText = originalText;
        this.strippedText = strippedText;
        for (var _i = 0, markers_1 = markers; _i < markers_1.length; _i++) {
            var marker = markers_1[_i];
            var markersOfType = this.markers[marker.typeName];
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
    ParseResult.prototype.getMarkersByType = function (markerType) {
        return this.markers[markerType];
    };
    /**
     * Gets a single marker of type, returns the first one if there are many
     * @param markerType
     * @return {any}
     */
    ParseResult.prototype.getMarkerByType = function (markerType) {
        var markersOfType = this.markers[markerType];
        if (!markersOfType) {
            return null;
        }
        return markersOfType[0];
    };
    /**
     * Returns position of the first marker of certain type.
     * @param markerType
     */
    ParseResult.prototype.getMarkerPosition = function (markerType) {
        var marker = this.getMarkerByType(markerType);
        if (!marker) {
            return null;
        }
        return marker.start;
    };
    /**
     * Gets ranges for a certain marker type.
     * @param markerType
     * @return {Array}
     */
    ParseResult.prototype.getMarkerRanges = function (markerType) {
        var markersOfType = this.markers[markerType];
        if (!markersOfType) {
            return [];
        }
        return markersOfType.map(function (marker) {
            return {
                start: marker.start,
                end: marker.end
            };
        });
    };
    /**
     * Gets start positions for a certain type of marker.
     * @param markerType
     * @return {any}
     */
    ParseResult.prototype.getMarkerPositions = function (markerType) {
        var markersOfType = this.markers[markerType];
        if (!markersOfType) {
            return [];
        }
        return markersOfType.map(function (marker) {
            return marker.start;
        });
    };
    ParseResult.prototype.getOriginalText = function () {
        return this.originalText;
    };
    ParseResult.prototype.getStrippedText = function () {
        return this.strippedText;
    };
    return ParseResult;
}());
exports.ParseResult = ParseResult;
/**
 * Pares file.
 * @param filePath
 * @return {ParseResult}
 */
function parseFileSync(filePath, schemas) {
    var text = fs.readFileSync(filePath).toString();
    return parseText(text, schemas);
}
exports.parseFileSync = parseFileSync;
/**
 * Parses text.
 * @param originalText
 */
function parseText(originalText, schemas) {
    var markers = findMarkers(originalText, schemas);
    var strippedText = stripTextFromMarkers(originalText, schemas);
    return new ParseResult(originalText, strippedText, markers);
}
exports.parseText = parseText;
function findMarkers(originalText, schemas) {
    var markers = [];
    var signs = [];
    for (var _i = 0, schemas_1 = schemas; _i < schemas_1.length; _i++) {
        var schema = schemas_1[_i];
        var regexp = regexpForSchema(schema);
        var match = regexp.exec(originalText);
        while (match) {
            var marker = {
                typeName: schema.typeName,
                isRange: isRangeMarkerSchema(schema),
                start: match.index,
                end: isSingleMarkerSchema(schema) ? match.index : match.index + match[0].length,
                schema: schema
            };
            markers.push(marker);
            if (isSingleMarkerSchema(schema)) {
                signs.push({
                    start: match.index,
                    length: match[0].length
                });
            }
            else if (isRangeMarkerSchema(schema)) {
                signs.push({
                    start: match.index,
                    length: match[1].length
                });
                signs.push({
                    start: match.index + match[1].length,
                    length: match[2].length
                });
            }
            match = regexp.exec(originalText);
        }
    }
    // correction of marker indexes basing on the shift due to marker signs cut-off
    signs = signs.sort(function (first, second) {
        return first.start - second.start;
    });
    markers.forEach(function (marker) {
        if (isSingleMarkerSchema(marker.schema)) {
            var offset = calculateOffsetForPosition(marker.start, signs);
            marker.start = marker.start + offset;
            marker.end = marker.end + offset;
        }
        else if (isRangeMarkerSchema(marker.schema)) {
            var startOffset = calculateOffsetForPosition(marker.start, signs);
            var endOffset = calculateOffsetForPosition(marker.end, signs);
            marker.start = marker.start + startOffset;
            marker.end = marker.end + endOffset;
        }
    });
    return markers;
}
function calculateOffsetForPosition(originalPosition, signs) {
    var offset = 0;
    for (var _i = 0, signs_1 = signs; _i < signs_1.length; _i++) {
        var sign = signs_1[_i];
        if (sign.start >= originalPosition) {
            break;
        }
        offset -= sign.length;
    }
    return offset;
}
function stripTextFromMarkers(originalText, schemas) {
    var text = originalText;
    for (var _i = 0, schemas_2 = schemas; _i < schemas_2.length; _i++) {
        var schema = schemas_2[_i];
        var regexp = regexpForSchema(schema);
        text = text.replace(regexp, "");
    }
    return text;
}
/**
 * Creates regexp for marker.
 * @param marker
 */
function regexpForSchema(schema) {
    var regexp = null;
    if (isSingleMarkerSchema(schema)) {
        regexp = new RegExp(schema.markerSign, "g");
    }
    else if (isRangeMarkerSchema(schema)) {
        regexp = new RegExp("(" + schema.markerStartSign + ").*(" + schema.markerEndSign + ")", "g");
    }
    // regexp.global = true;
    return regexp;
}
//# sourceMappingURL=markerParser.js.map