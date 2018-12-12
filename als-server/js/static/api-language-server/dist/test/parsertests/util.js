"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var index = require("../../index");
var fs = require("fs");
var path = require("path");
var chaiModule = require("chai");
var assert = chaiModule.assert;
var connection;
function stopConnection() {
    if (connection) {
        connection.stop();
    }
}
exports.stopConnection = stopConnection;
function data(relativePath) {
    return path.resolve(__dirname, "../../../src/test/data", relativePath);
}
exports.data = data;
function getValidationReport(apiPath, callback) {
    try {
        var content = fs.readFileSync(apiPath).toString();
        connection = index.getNodeClientConnection();
        // connection.setLoggerConfiguration({
        //     disabled: true
        // });
        connection.documentOpened({
            uri: apiPath,
            text: content
        });
        connection.onValidationReport(function (result) {
            callback(result, null, function () { return connection.documentClosed(apiPath); });
        });
    }
    catch (e) {
        callback(null, e, function () { return connection && connection.documentClosed(apiPath); });
    }
}
exports.getValidationReport = getValidationReport;
function startTyping(apiPath, done) {
    var content = fs.readFileSync(apiPath).toString();
    var expectedVersion = 0;
    connection = index.getNodeClientConnection();
    // connection.setLoggerConfiguration({
    //     disabled: true
    // });
    var changer = new TextChanger(content, true);
    var alreadyDone = false;
    connection.documentOpened({
        uri: apiPath,
        version: 0,
        text: content
    });
    connection.onValidationReport(function (result) {
        if (alreadyDone) {
            return;
        }
        if (result.pointOfViewUri !== apiPath) {
            return;
        }
        if (result.version !== expectedVersion) {
            return;
        }
        expectedVersion++;
        var nextContent = changer.nextContent();
        if (nextContent !== null) {
            //console.log(new Date().getTime() + ": " + changer.currentIndex + " of " + changer.maxIndex + " version " + result.version);
            connection.documentChanged({
                uri: apiPath,
                text: nextContent
            });
            return;
        }
        //console.log("document end reached");
        alreadyDone = true;
        done();
        connection.documentClosed(apiPath);
    });
}
exports.startTyping = startTyping;
function startTyping1(apiPath, done) {
    var content = fs.readFileSync(apiPath).toString();
    var expectedVersion = 0;
    connection = index.getNodeClientConnection();
    connection.loggingEnabled = false;
    var changer = new TextChanger(content, true);
    var alreadyDone = false;
    connection.documentOpened({
        uri: apiPath,
        version: 0,
        text: content
    });
    var versions = [{ version: expectedVersion++, done: false }];
    var nextContent = changer.nextContent();
    while (nextContent !== null) {
        versions.push({ version: expectedVersion++, done: false });
        connection.documentChanged({
            uri: apiPath,
            text: nextContent
        });
        nextContent = changer.nextContent();
    }
    connection.onValidationReport(function (result) {
        if (alreadyDone) {
            return;
        }
        if (result.pointOfViewUri !== apiPath) {
            return;
        }
        for (var i = 0; i < versions.length; i++) {
            if (result.version === versions[i].version) {
                if (!versions[i].done) {
                    versions[i].done = true;
                    console.log("version: " + versions[i].version + " is done");
                }
                break;
            }
        }
        for (var i = 0; i < versions.length; i++) {
            if (!versions[i].done) {
                return;
            }
        }
        alreadyDone = true;
        done();
        connection.documentClosed(apiPath);
    });
}
exports.startTyping1 = startTyping1;
var TextChanger = (function () {
    function TextChanger(initialContent, byWords) {
        if (byWords === void 0) { byWords = false; }
        this.initialContent = initialContent;
        this.byWords = byWords;
        this.currentIndex = 0;
        this.i1 = this.initialContent.indexOf("#%RAML");
        this.i2 = this.initialContent.indexOf("\n", this.i1);
        if (this.i2 < 0) {
            this.i2 = this.initialContent.length;
        }
        else {
            this.i2 += "\n".length;
        }
        this.contentBuffer = this.initialContent.substr(0, this.i2);
        if (this.byWords) {
            this.words = this.initialContent.substring(this.i2).split(/(\s+)/);
        }
        var last = this.words.length;
        if (this.words[last - 1] === "") {
            last = last - 1;
        }
        this.maxIndex = this.byWords ? last : this.initialContent.length;
        this.currentIndex = this.byWords ? 0 : this.contentBuffer.length;
    }
    TextChanger.prototype.nextContent = function () {
        if (this.currentIndex >= this.maxIndex) {
            return null;
        }
        if (this.byWords) {
            this.contentBuffer += this.words[this.currentIndex];
        }
        else {
            this.contentBuffer += this.initialContent.charAt(this.currentIndex);
        }
        this.currentIndex++;
        return this.contentBuffer;
    };
    return TextChanger;
}());
var ValidationReportDoner = (function () {
    function ValidationReportDoner(fullPath, done, onReport) {
        this.fullPath = fullPath;
        this.done = done;
        this.onReport = onReport;
        this.alreadyDone = false;
    }
    ValidationReportDoner.prototype.doDone = function (closeDocument, error) {
        if (this.alreadyDone) {
            return;
        }
        this.alreadyDone = true;
        closeDocument();
        error ? this.done(error) : this.done();
    };
    ValidationReportDoner.prototype.run = function () {
        var _this = this;
        getValidationReport(this.fullPath, function (result, reject, closeDocument) {
            if (!result) {
                _this.onReport(null, reject, function (error) {
                    _this.doDone(closeDocument, error);
                });
                return;
            }
            if (result.pointOfViewUri !== _this.fullPath) {
                return;
            }
            _this.onReport(result, reject, function (error) {
                _this.doDone(closeDocument, error);
            });
        });
    };
    return ValidationReportDoner;
}());
function testErrors(done, fullPath, errors, ignoreWarnings) {
    new ValidationReportDoner(fullPath, done, function (result, rejection, done) {
        if (result) {
            var receivedErrors = prepareErrors(result, ignoreWarnings);
            try {
                testErrorsSync(receivedErrors, errors, fullPath);
                done();
            }
            catch (assertErr) {
                done(assertErr);
            }
        }
        else {
            done(rejection);
        }
    }).run();
}
exports.testErrors = testErrors;
function testErrorsByNumber(done, fullPath, count, deviations) {
    if (count === void 0) { count = 0; }
    if (deviations === void 0) { deviations = 0; }
    new ValidationReportDoner(fullPath, done, function (result, rejection, done) {
        if (result) {
            var receivedErrors = prepareErrors(result, false);
            try {
                testErrorsByNumberSync(receivedErrors, count, deviations);
                done();
            }
            catch (assertErr) {
                done(assertErr);
            }
        }
        else {
            done(rejection);
        }
    }).run();
}
exports.testErrorsByNumber = testErrorsByNumber;
function prepareErrors(result, ignoreWarnings) {
    var receivedErrors = (result && result.issues) || [];
    if (ignoreWarnings) {
        receivedErrors = receivedErrors.filter(function (err) { return !(err.type === "Warning"); });
    }
    receivedErrors = receivedErrors.length === 0 ? receivedErrors : extractTraces({
        trace: receivedErrors
    });
    receivedErrors = removeDuplicates(receivedErrors);
    receivedErrors = receivedErrors.map(function (err) {
        return {
            message: err.text,
            range: {
                start: err.range.start,
                end: err.range.end
            }
        };
    });
    return receivedErrors;
}
function testErrorsByNumberSync(errors, count, deviations) {
    if (count === void 0) { count = 0; }
    if (deviations === void 0) { deviations = 0; }
    var condition = false;
    if (deviations == 0) {
        condition = errors.length == count;
    }
    else if (deviations > 0) {
        condition = errors.length >= count;
    }
    else {
        condition = errors.length <= count;
    }
    if (!condition) {
        if (errors.length > 0) {
            errors.forEach(function (error) {
                if (typeof error.message == 'string') {
                    console.warn(error.message);
                }
                else {
                    console.warn(error);
                }
                console.warn("\n");
            });
        }
    }
    if (deviations == 0) {
        assert.equal(errors.length, count);
    }
    else if (deviations > 0) {
        assert.equal(errors.length >= count, true);
    }
    else {
        assert.equal(errors.length <= count, true);
    }
}
function testErrorsSync(receivedErrors, expectedErrors, fullFilePath) {
    if (expectedErrors === void 0) { expectedErrors = []; }
    var testErrors;
    var hasUnexpectedErr = false;
    if (expectedErrors.length > 0) {
        testErrors = validateErrors(receivedErrors, expectedErrors);
        hasUnexpectedErr = testErrors.unexpected.length > 0 || testErrors.lostExpected.length > 0;
    }
    var condition = false;
    condition = receivedErrors.length == expectedErrors.length;
    var errorMsg = '';
    if (hasUnexpectedErr) {
        if (testErrors.unexpected.length > 0) {
            errorMsg += "\nUnexpected errors: \n\n";
            testErrors.unexpected.forEach(function (unexpectedError) {
                errorMsg += unexpectedError + "\n\n";
            });
        }
        if (testErrors.lostExpected.length > 0) {
            errorMsg += "\nDisappeared expected errors: \n\n";
            testErrors.lostExpected.forEach(function (lostExpected) {
                errorMsg += lostExpected + "\n\n";
            });
        }
    }
    var fileContents = null;
    if (fullFilePath) {
        try {
            fileContents = fs.readFileSync(fullFilePath).toString();
        }
        catch (Error) {
        }
    }
    if (hasUnexpectedErr || receivedErrors.length !== expectedErrors.length) {
        errorMsg += "\nActual errors:\n";
        for (var _i = 0, receivedErrors_1 = receivedErrors; _i < receivedErrors_1.length; _i++) {
            var currentError = receivedErrors_1[_i];
            errorMsg += (currentError.message ? currentError.message : "") + "\n";
            if (fileContents) {
                errorMsg += "---------\n" + unitCutOffForError(currentError, fileContents) + "\n-----------\n";
            }
        }
        errorMsg += "\nExpected errors:\n";
        for (var _a = 0, expectedErrors_1 = expectedErrors; _a < expectedErrors_1.length; _a++) {
            var currentError = expectedErrors_1[_a];
            errorMsg += currentError + "\n";
        }
    }
    assert.equal(hasUnexpectedErr, false, "Unexpected errors found\n" + errorMsg);
    assert.equal(receivedErrors.length, expectedErrors.length, "Wrong number of errors\n" + errorMsg);
}
function unitCutOffForError(issue, unitContents) {
    var start = issue.range.start;
    if (start < 0) {
        start = 0;
    }
    var end = issue.range.end;
    if (end > unitContents.length) {
        end = unitContents.length;
    }
    return "[" + start + ":" + end + "]\n" + unitContents.substring(start, end);
}
function validateErrors(realErrors, expectedErrors) {
    var errors = { unexpected: [], lostExpected: [] };
    if (realErrors.length > 0) {
        realErrors.forEach(function (error) {
            var realError;
            if (typeof error.message == 'string') {
                realError = error.message;
            }
            else {
                realError = error;
            }
            var isExpectedError = false;
            expectedErrors.forEach(function (expectedError) {
                var index = realError.search(new RegExp(expectedError, "mi"));
                if (index > -1) {
                    isExpectedError = true;
                }
                else {
                    index = realError.search(new RegExp(escapeRegexp(expectedError), "mi"));
                    if (index > -1)
                        isExpectedError = true;
                }
            });
            if (!isExpectedError)
                errors.unexpected.push(realError);
        });
        expectedErrors.forEach(function (expectedError) {
            var isLostError = true;
            realErrors.forEach(function (error) {
                var realError;
                if (typeof error.message == 'string') {
                    realError = error.message;
                }
                else {
                    realError = error;
                }
                var index = realError.search(new RegExp(expectedError, "i"));
                if (index > -1) {
                    isLostError = false;
                }
                else {
                    index = realError.search(new RegExp(escapeRegexp(expectedError), "i"));
                    if (index > -1)
                        isLostError = false;
                }
            });
            if (isLostError)
                errors.lostExpected.push(expectedError);
        });
    }
    return errors;
}
function removeDuplicates(errors) {
    var result = [];
    errors.forEach(function (error) {
        var found = false;
        for (var i = 0; i < result.length; i++) {
            if (compareErrors(error, result[i])) {
                found = true;
                break;
            }
        }
        if (found) {
            return;
        }
        result.push(error);
    });
    return result;
}
function compareErrors(e1, e2) {
    if (e1.range.start !== e2.range.start) {
        return false;
    }
    if (e1.range.end !== e2.range.end) {
        return false;
    }
    if (e1.code !== e2.code) {
        return false;
    }
    if (e1.filePath !== e2.filePath) {
        return false;
    }
    if (e1.text !== e2.text) {
        return false;
    }
    return true;
}
function extractTraces(error) {
    if (!error.trace || error.trace.length === 0) {
        return [error];
    }
    var result = [];
    error.trace.forEach(function (child) {
        if (child.type === "Trace") {
            child.type = error.type;
        }
        result = result.concat(extractTraces(child));
    });
    return result;
}
function escapeRegexp(regexp) {
    return regexp.replace(/[-\/\\^$*+?.()|[\]{}]/g, '\\$&');
}
function sleep(milliseconds) {
    var start = new Date().getTime();
    while (true) {
        if ((new Date().getTime() - start) > milliseconds) {
            break;
        }
    }
}
exports.sleep = sleep;
//# sourceMappingURL=util.js.map