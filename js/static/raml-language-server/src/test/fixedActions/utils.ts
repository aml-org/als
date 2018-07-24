import path = require("path");
import assert = require("assert");
import {
    parseFileSync,
    Marker, ParseResult
} from "./markerParser";

import {
    getNodeClientConnection,
    ILocation,
    IRange,
    IChangedDocument
} from "../../index";

import {
    applyDocumentEdits
} from "../../common/textEditProcessor";

export function testOpenDeclaration(testFileRelativePath: string, done: any):void{

    const fullFilePath = getOpenDeclarationFullPath(testFileRelativePath);

    const parseResult = parseFileSync(fullFilePath, [
        {
            markerSign: "\\*",
            typeName: "SOURCE"
        },
        {
            markerSign: "\\!",
            typeName: "TARGET"
        }
    ]);

    if (!parseResult) {
        done(new Error("Can not parse file " + fullFilePath));
        return;
    }

    callOpenDeclaration(fullFilePath, parseResult, (result, error) => {
        if (error) {
            done(error);

            return;
        }

        try {
            assert(compareOpenDeclaration(fullFilePath, parseResult, result));
            done();
        } catch (exception) {
            done(exception);
        }
    });
}

function callOpenDeclaration(apiPath: string, parseResult: ParseResult,
                             callback: (result: ILocation[], error: any) => void): void {

    const content = parseResult.getStrippedText();

    const position = parseResult.getMarkerPosition("SOURCE");

    const connection = getNodeClientConnection();

    connection.documentOpened({
        uri: apiPath,
        text: content
    });

    connection.documentChanged({
        uri: apiPath,
        text: content
    });

    connection.openDeclaration(apiPath, position).then((result) => {
        connection.documentClosed(apiPath);
        callback(result, null);
    }, (err) => {
        callback(null, err);
    });
}

function compareOpenDeclaration(apiPath: string, parseResult: ParseResult, locations: ILocation[]): boolean {
    const targetPosition = parseResult.getMarkerPosition("TARGET");
    if (!targetPosition) {

        console.log("Can not determine target position")
        return false;
    }

    const modifiedTargetPosition = targetPosition;

    if (!locations || locations.length !== 1) {

        console.log("Expected to have a single location")
        return false;
    }

    if (locations[0].range.start > modifiedTargetPosition
        || locations[0].range.end < modifiedTargetPosition) {

        console.log("Modified location of " + modifiedTargetPosition +
            " does not fit into the recieved range of [" + locations[0].range.start +
            ":" + locations[0].range.end + "]");

        return false;
    }

    return true;
}

function getOpenDeclarationFullPath(originalPath: string): string {
    return path.resolve(__dirname, "../../../src/test/data/fixedActions/openDeclaration"
        + originalPath).replace(/\\/g, "/");
}

export function testFindReferences(testFileRelativePath: string, done: any): void {

    const fullFilePath = getFindReferencesFullPath(testFileRelativePath);

    const parseResult = parseFileSync(fullFilePath, [
        {
            markerSign: "\\*",
            typeName: "SOURCE"
        },
        {
            markerSign: "\\!",
            typeName: "USAGE"
        }
    ]);

    if (!parseResult) {
        done(new Error("Can not parse file " + fullFilePath));
        return;
    }

    callFindReferences(fullFilePath, parseResult, (result, error) => {
        console.log("HERE111: " + JSON.stringify(result))
        if (error) {
            done(error);

            return;
        }

        try {
            assert(assertRefLocations(fullFilePath, parseResult, result));
            done();
        } catch (exception) {
            done(exception);
        }
    });
}

function callFindReferences(apiPath: string, parseResult: ParseResult,
                             callback: (result: ILocation[], error: any) => void): void {

    const content = parseResult.getStrippedText();

    const position = parseResult.getMarkerPosition("SOURCE");

    const connection = getNodeClientConnection();

    connection.documentOpened({
        uri: apiPath,
        text: content
    });

    connection.documentChanged({
        uri: apiPath,
        text: content
    });

    connection.findReferences(apiPath, position).then((result) => {
        connection.documentClosed(apiPath);
        callback(result, null);
    }, (err) => {
        callback(null, err);
    });
}

function assertRefLocations(apiPath: string, parseResult: ParseResult, locations: ILocation[]): boolean {
    const sourcePosition = parseResult.getMarkerPosition("SOURCE");
    if (!sourcePosition) {

        console.log("Can not determine source position")
        return false;
    }

    return compareLocations(parseResult, "USAGE", locations);
}

function assertRefRanges(apiPath: string, parseResult: ParseResult, locations: IRange[]): boolean {
    const sourcePosition = parseResult.getMarkerPosition("SOURCE");
    if (!sourcePosition) {

        console.log("Can not determine source position")
        return false;
    }

    return compareRanges(parseResult, "USAGE", locations);
}

function compareLocations(parseResult: ParseResult,
                          markerName: string, locations: ILocation[]): boolean {

    return compareRanges(parseResult, markerName, locations.map((location) => {
        return location.range;
    }));
}

function compareRanges(parseResult: ParseResult,
                       markerName: string, locations: IRange[]): boolean {

    if (!locations || locations.length === 0) {

        console.log("Expected to have some location")
        return false;
    }

    const markerPositions = parseResult.getMarkerPositions(markerName);

    if (markerPositions.length !== locations.length) {

        console.log("Found " + markerPositions.length + " markers, but " +
            locations.length + " locations");
        return false;
    }

    for (const markerPosition of markerPositions) {
        let found = false;

        for (const location of locations) {
            if (location.start <= markerPosition &&
                location.end >= markerPosition) {

                found = true;
                break;
            }
        }

        if (!found) {

            console.log("Could not find a match for the marker at position "
                + markerPosition);
            return false;
        }
    }

    return true;
}


function getFindReferencesFullPath(originalPath: string): string {
    return path.resolve(__dirname, "../../../src/test/data/fixedActions/findReferences"
        + originalPath).replace(/\\/g, "/");
}

export function testMarkOccurrences(testFileRelativePath: string, done: any): void {

    const fullFilePath = getMarkOccurrencesFullPath(testFileRelativePath);

    const parseResult = parseFileSync(fullFilePath, [
        {
            markerSign: "\\*",
            typeName: "SOURCE"
        },
        {
            markerSign: "\\!",
            typeName: "USAGE"
        }
    ]);

    if (!parseResult) {
        done(new Error("Can not parse file " + fullFilePath));
        return;
    }

    callMarkOccurrences(fullFilePath, parseResult, (result, error) => {
        console.log("HERE111: " + JSON.stringify(result))
        if (error) {
            done(error);

            return;
        }

        try {
            assert(assertRefRanges(fullFilePath, parseResult, result));
            done();
        } catch (exception) {
            done(exception);
        }
    });
}

function callMarkOccurrences(apiPath: string, parseResult: ParseResult,
                            callback: (result: IRange[], error: any) => void): void {

    const content = parseResult.getStrippedText();

    const position = parseResult.getMarkerPosition("SOURCE");

    const connection = getNodeClientConnection();

    // connection.setLoggerConfiguration({
    //     allowedComponents: [
    //         "FixedActionsManager"
    //     ],
    //     maxMessageLength: 5000
    // });

    connection.documentOpened({
        uri: apiPath,
        text: content
    });

    connection.documentChanged({
        uri: apiPath,
        text: content
    });

    connection.markOccurrences(apiPath, position).then((result) => {
        connection.documentClosed(apiPath);
        callback(result, null);
    }, (err) => {
        callback(null, err);
    });
}

function getMarkOccurrencesFullPath(originalPath: string): string {
    return path.resolve(__dirname, "../../../src/test/data/fixedActions/markOccurrences"
        + originalPath).replace(/\\/g, "/");
}

export function testRename(testFileRelativePath: string, done: any):void{

    const fullFilePath = getRenameFullPath(testFileRelativePath);

    const parseResult = parseFileSync(fullFilePath, [
        {
            markerSign: "\\*",
            typeName: "SOURCE"
        },
        {
            markerSign: "\\!",
            typeName: "TARGET"
        }
    ]);

    if (!parseResult) {
        done(new Error("Can not parse file " + fullFilePath));
        return;
    }

    const sourcePosition = parseResult.getMarkerPosition("SOURCE");

    const nameToRename = findNameAtPosition(parseResult.getStrippedText(), sourcePosition);
    const newName = nameToRename.substring(0, nameToRename.length - 1) + "0";

    callRename(fullFilePath, parseResult, newName, (result, error) => {
        if (error) {
            done(error);

            return;
        }

        try {
            assert(compareRename(fullFilePath, parseResult, newName, result));
            done();
        } catch (exception) {
            done(exception);
        }
    });
}

function callRename(apiPath: string, parseResult: ParseResult, newName: string,
                             callback: (result: IChangedDocument[], error: any) => void): void {

    const content = parseResult.getStrippedText();

    const position = parseResult.getMarkerPosition("SOURCE");

    const connection = getNodeClientConnection();

    connection.documentOpened({
        uri: apiPath,
        text: content
    });

    connection.rename(apiPath, position, newName).then((result) => {
        connection.documentClosed(apiPath);
        callback(result, null);
    }, (err) => {
        callback(null, err);
    });
}

function findNameAtPosition(text: string, position: number): string {

    let start = position;
    for (let currentPos = position; currentPos >= 0; currentPos--) {
        const currentChar = text.charAt(currentPos);
        if (!isNameChar(currentChar)) {
            start = currentPos + 1;
            break;
        }
    }

    let end = position + 1;
    for (let currentPos = position; currentPos < text.length; currentPos++) {
        const currentChar = text.charAt(currentPos);
        if (!isNameChar(currentChar)) {
            end = currentPos;
            break;
        }
    }

    return text.substring(start, end);
}

const allowCharRegexp = /^[a-zA-Z0-9_]$/;
function isNameChar(char: string) {
    return allowCharRegexp.test(char);
}

function compareRename(apiPath: string, parseResult: ParseResult, expectedName: string, changes: IChangedDocument[]): boolean {
    const targetPosition = parseResult.getMarkerPosition("TARGET");
    if (!targetPosition) {

        console.log("Can not determine target position")
        return false;
    }

    let newText = parseResult.getStrippedText();

    changes.forEach((change) => {
        if (change.uri !== apiPath) {
            return;
        }

        if (change.text !== null) {
            newText = change.text;
        } else if (change.textEdits !== null) {
            newText = applyDocumentEdits(newText, change.textEdits);
        }
    })

    const markerPositions = parseResult.getMarkerPositions("TARGET");
    markerPositions.push(parseResult.getMarkerPosition("SOURCE"));

    let noMismatches = true;
    for (const markerPosition of markerPositions) {
        const nameAtPosition = findNameAtPosition(newText, markerPosition);

        if (expectedName !== nameAtPosition) {

            console.log("Resulting name at position " + markerPosition +
                " is " + nameAtPosition + " instead of " + expectedName);

            let start = markerPosition - 30;
            if (start < 0) {
                start = 0;
            }

            let end = markerPosition + 30;
            if (end >= newText.length)  {
                end = newText.length;
            }

            console.log("Code cut-off:\n-------------\n" + newText.substring(start, markerPosition) + "!" +
                newText.substring(markerPosition, end) + "\n-------------");

            noMismatches = false;
        }
    }

    return noMismatches;
}

function getRenameFullPath(originalPath: string): string {
    return path.resolve(__dirname, "../../../src/test/data/fixedActions/rename"
        + originalPath).replace(/\\/g, "/");
}