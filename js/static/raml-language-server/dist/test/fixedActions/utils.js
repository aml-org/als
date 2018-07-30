"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var path = require("path");
var assert = require("assert");
var markerParser_1 = require("./markerParser");
var index_1 = require("../../index");
var textEditProcessor_1 = require("../../common/textEditProcessor");
function testOpenDeclaration(testFileRelativePath, done) {
    var fullFilePath = getOpenDeclarationFullPath(testFileRelativePath);
    var parseResult = markerParser_1.parseFileSync(fullFilePath, [
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
    callOpenDeclaration(fullFilePath, parseResult, function (result, error) {
        if (error) {
            done(error);
            return;
        }
        try {
            assert(compareOpenDeclaration(fullFilePath, parseResult, result));
            done();
        }
        catch (exception) {
            done(exception);
        }
    });
}
exports.testOpenDeclaration = testOpenDeclaration;
function callOpenDeclaration(apiPath, parseResult, callback) {
    var content = parseResult.getStrippedText();
    var position = parseResult.getMarkerPosition("SOURCE");
    var connection = index_1.getNodeClientConnection();
    connection.documentOpened({
        uri: apiPath,
        text: content
    });
    connection.documentChanged({
        uri: apiPath,
        text: content
    });
    connection.openDeclaration(apiPath, position).then(function (result) {
        connection.documentClosed(apiPath);
        callback(result, null);
    }, function (err) {
        callback(null, err);
    });
}
function compareOpenDeclaration(apiPath, parseResult, locations) {
    var targetPosition = parseResult.getMarkerPosition("TARGET");
    if (!targetPosition) {
        console.log("Can not determine target position");
        return false;
    }
    var modifiedTargetPosition = targetPosition;
    if (!locations || locations.length !== 1) {
        console.log("Expected to have a single location");
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
function getOpenDeclarationFullPath(originalPath) {
    return path.resolve(__dirname, "../../../src/test/data/fixedActions/openDeclaration"
        + originalPath).replace(/\\/g, "/");
}
function testFindReferences(testFileRelativePath, done) {
    var fullFilePath = getFindReferencesFullPath(testFileRelativePath);
    var parseResult = markerParser_1.parseFileSync(fullFilePath, [
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
    callFindReferences(fullFilePath, parseResult, function (result, error) {
        console.log("HERE111: " + JSON.stringify(result));
        if (error) {
            done(error);
            return;
        }
        try {
            assert(assertRefLocations(fullFilePath, parseResult, result));
            done();
        }
        catch (exception) {
            done(exception);
        }
    });
}
exports.testFindReferences = testFindReferences;
function callFindReferences(apiPath, parseResult, callback) {
    var content = parseResult.getStrippedText();
    var position = parseResult.getMarkerPosition("SOURCE");
    var connection = index_1.getNodeClientConnection();
    connection.documentOpened({
        uri: apiPath,
        text: content
    });
    connection.documentChanged({
        uri: apiPath,
        text: content
    });
    connection.findReferences(apiPath, position).then(function (result) {
        connection.documentClosed(apiPath);
        callback(result, null);
    }, function (err) {
        callback(null, err);
    });
}
function assertRefLocations(apiPath, parseResult, locations) {
    var sourcePosition = parseResult.getMarkerPosition("SOURCE");
    if (!sourcePosition) {
        console.log("Can not determine source position");
        return false;
    }
    return compareLocations(parseResult, "USAGE", locations);
}
function assertRefRanges(apiPath, parseResult, locations) {
    var sourcePosition = parseResult.getMarkerPosition("SOURCE");
    if (!sourcePosition) {
        console.log("Can not determine source position");
        return false;
    }
    return compareRanges(parseResult, "USAGE", locations);
}
function compareLocations(parseResult, markerName, locations) {
    return compareRanges(parseResult, markerName, locations.map(function (location) {
        return location.range;
    }));
}
function compareRanges(parseResult, markerName, locations) {
    if (!locations || locations.length === 0) {
        console.log("Expected to have some location");
        return false;
    }
    var markerPositions = parseResult.getMarkerPositions(markerName);
    if (markerPositions.length !== locations.length) {
        console.log("Found " + markerPositions.length + " markers, but " +
            locations.length + " locations");
        return false;
    }
    for (var _i = 0, markerPositions_1 = markerPositions; _i < markerPositions_1.length; _i++) {
        var markerPosition = markerPositions_1[_i];
        var found = false;
        for (var _a = 0, locations_1 = locations; _a < locations_1.length; _a++) {
            var location_1 = locations_1[_a];
            if (location_1.start <= markerPosition &&
                location_1.end >= markerPosition) {
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
function getFindReferencesFullPath(originalPath) {
    return path.resolve(__dirname, "../../../src/test/data/fixedActions/findReferences"
        + originalPath).replace(/\\/g, "/");
}
function testMarkOccurrences(testFileRelativePath, done) {
    var fullFilePath = getMarkOccurrencesFullPath(testFileRelativePath);
    var parseResult = markerParser_1.parseFileSync(fullFilePath, [
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
    callMarkOccurrences(fullFilePath, parseResult, function (result, error) {
        console.log("HERE111: " + JSON.stringify(result));
        if (error) {
            done(error);
            return;
        }
        try {
            assert(assertRefRanges(fullFilePath, parseResult, result));
            done();
        }
        catch (exception) {
            done(exception);
        }
    });
}
exports.testMarkOccurrences = testMarkOccurrences;
function callMarkOccurrences(apiPath, parseResult, callback) {
    var content = parseResult.getStrippedText();
    var position = parseResult.getMarkerPosition("SOURCE");
    var connection = index_1.getNodeClientConnection();
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
    connection.markOccurrences(apiPath, position).then(function (result) {
        connection.documentClosed(apiPath);
        callback(result, null);
    }, function (err) {
        callback(null, err);
    });
}
function getMarkOccurrencesFullPath(originalPath) {
    return path.resolve(__dirname, "../../../src/test/data/fixedActions/markOccurrences"
        + originalPath).replace(/\\/g, "/");
}
function testRename(testFileRelativePath, done) {
    var fullFilePath = getRenameFullPath(testFileRelativePath);
    var parseResult = markerParser_1.parseFileSync(fullFilePath, [
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
    var sourcePosition = parseResult.getMarkerPosition("SOURCE");
    var nameToRename = findNameAtPosition(parseResult.getStrippedText(), sourcePosition);
    var newName = nameToRename.substring(0, nameToRename.length - 1) + "0";
    callRename(fullFilePath, parseResult, newName, function (result, error) {
        if (error) {
            done(error);
            return;
        }
        try {
            assert(compareRename(fullFilePath, parseResult, newName, result));
            done();
        }
        catch (exception) {
            done(exception);
        }
    });
}
exports.testRename = testRename;
function callRename(apiPath, parseResult, newName, callback) {
    var content = parseResult.getStrippedText();
    var position = parseResult.getMarkerPosition("SOURCE");
    var connection = index_1.getNodeClientConnection();
    connection.documentOpened({
        uri: apiPath,
        text: content
    });
    connection.rename(apiPath, position, newName).then(function (result) {
        connection.documentClosed(apiPath);
        callback(result, null);
    }, function (err) {
        callback(null, err);
    });
}
function findNameAtPosition(text, position) {
    var start = position;
    for (var currentPos = position; currentPos >= 0; currentPos--) {
        var currentChar = text.charAt(currentPos);
        if (!isNameChar(currentChar)) {
            start = currentPos + 1;
            break;
        }
    }
    var end = position + 1;
    for (var currentPos = position; currentPos < text.length; currentPos++) {
        var currentChar = text.charAt(currentPos);
        if (!isNameChar(currentChar)) {
            end = currentPos;
            break;
        }
    }
    return text.substring(start, end);
}
var allowCharRegexp = /^[a-zA-Z0-9_]$/;
function isNameChar(char) {
    return allowCharRegexp.test(char);
}
function compareRename(apiPath, parseResult, expectedName, changes) {
    var targetPosition = parseResult.getMarkerPosition("TARGET");
    if (!targetPosition) {
        console.log("Can not determine target position");
        return false;
    }
    var newText = parseResult.getStrippedText();
    changes.forEach(function (change) {
        if (change.uri !== apiPath) {
            return;
        }
        if (change.text !== null) {
            newText = change.text;
        }
        else if (change.textEdits !== null) {
            newText = textEditProcessor_1.applyDocumentEdits(newText, change.textEdits);
        }
    });
    var markerPositions = parseResult.getMarkerPositions("TARGET");
    markerPositions.push(parseResult.getMarkerPosition("SOURCE"));
    var noMismatches = true;
    for (var _i = 0, markerPositions_2 = markerPositions; _i < markerPositions_2.length; _i++) {
        var markerPosition = markerPositions_2[_i];
        var nameAtPosition = findNameAtPosition(newText, markerPosition);
        if (expectedName !== nameAtPosition) {
            console.log("Resulting name at position " + markerPosition +
                " is " + nameAtPosition + " instead of " + expectedName);
            var start = markerPosition - 30;
            if (start < 0) {
                start = 0;
            }
            var end = markerPosition + 30;
            if (end >= newText.length) {
                end = newText.length;
            }
            console.log("Code cut-off:\n-------------\n" + newText.substring(start, markerPosition) + "!" +
                newText.substring(markerPosition, end) + "\n-------------");
            noMismatches = false;
        }
    }
    return noMismatches;
}
function getRenameFullPath(originalPath) {
    return path.resolve(__dirname, "../../../src/test/data/fixedActions/rename"
        + originalPath).replace(/\\/g, "/");
}
//# sourceMappingURL=utils.js.map