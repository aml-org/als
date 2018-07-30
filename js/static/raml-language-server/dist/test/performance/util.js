"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var index = require("../../index");
var fs = require("fs");
var path = require("path");
var testCases = [];
var connection = null;
var srcPath = path.resolve(__dirname, '../../../resources/performance/report.html');
var dstPath = path.resolve(__dirname, '../../../performance_report/report.html');
var reportTemplate = fs.readFileSync(srcPath).toString();
function addCase(relativeUri, caseName, testInfo) {
    testCases.push({ relativeUri: relativeUri, caseName: caseName });
}
exports.addCase = addCase;
function runCases() {
    openConnection();
    handleCase(0);
}
exports.runCases = runCases;
function handleCase(caseNum) {
    if (caseNum >= testCases.length) {
        console.log("DONE.");
        generateReport();
        if (!connection.debug) {
            connection.debug = function (message, arg2) { return console.log("message"); };
        }
        connection.stop();
        return;
    }
    var currentCase = testCases[caseNum];
    runSequence(currentCase.relativeUri, currentCase.caseName, function (previousResult, message) {
        if (previousResult) {
            handleCase(caseNum + 1);
        }
        else {
            console.log(message, ", measurements stopped.");
            connection.stop();
        }
    });
}
function runSequence(relativeUri, caseName, nextTask) {
    new SequenceRunner(relativeUri, caseName).run().then(function (result) {
        nextTask(result, result ? "OK" : "SOMETHING WRONG");
    });
}
var RequestResult = /** @class */ (function () {
    function RequestResult() {
    }
    return RequestResult;
}());
var TimeTrackerEvent = /** @class */ (function () {
    function TimeTrackerEvent() {
    }
    return TimeTrackerEvent;
}());
var timeData = [];
function generateReport() {
    var apis = {};
    var table = [];
    var messages = {};
    timeData.forEach(function (record) {
        var tableRecord = null;
        messages[record.api] = record.message;
        var apiNum = apis[record.api];
        if (!apiNum) {
            apiNum = Object.keys(apis).length + 1;
            apis[record.api] = apiNum;
        }
        for (var i = 0; i < table.length; i++) {
            var foundRecord = table[i];
            if (foundRecord['api/case'] === apiNum) {
                tableRecord = foundRecord;
                break;
            }
        }
        if (!tableRecord) {
            tableRecord = {
                'api/case': apiNum
            };
            table.push(tableRecord);
        }
        tableRecord[record.caseId] = record.total + "ms";
    });
    console.log("Apis:");
    Object.keys(apis).forEach(function (key) {
        var messageString = messages[key] ? (':\n\t' + messages[key]) : '';
        console.log(apis[key] + ". " + key + messageString);
    });
    console.log();
    var jsonData = {
        apis: apis,
        table: table,
        messages: messages
    };
    fs.writeFileSync(dstPath, reportTemplate.replace('__jsonData__', JSON.stringify(jsonData)));
}
exports.generateReport = generateReport;
var TimeTracker = /** @class */ (function () {
    function TimeTracker(onEvent) {
        this.onEvent = onEvent;
        this.total = 0;
    }
    TimeTracker.prototype.start = function (startMessage) {
        this.onEvent({
            eventName: 'NOTE',
            message: startMessage,
            timeSpend: -1
        });
        this.lastNoteTime = this.time();
    };
    TimeTracker.prototype.note = function (message) {
        var timeSpend = this.diff();
        this.total += timeSpend;
        this.onEvent({
            eventName: 'NOTE',
            message: message,
            timeSpend: timeSpend
        });
        this.lastNoteTime = this.time();
    };
    TimeTracker.prototype.reset = function () {
        this.lastNoteTime = this.time();
        this.total = 0;
    };
    TimeTracker.prototype.finish = function (message) {
        this.onEvent({
            eventName: 'FINISH',
            message: message,
            timeSpend: this.total
        });
    };
    TimeTracker.prototype.diff = function () {
        return this.time() - this.lastNoteTime;
    };
    TimeTracker.prototype.time = function () {
        return new Date().getTime();
    };
    return TimeTracker;
}());
var SequenceRunner = /** @class */ (function () {
    function SequenceRunner(relativeUri, caseName) {
        var _this = this;
        this.relativeUri = relativeUri;
        this.caseName = caseName;
        this.absoluteUri = getAbsoluteUri(relativeUri);
        this.sequence = cases[caseName];
        this.tracker = new TimeTracker(function (event) { return _this.handleEvent(event); });
    }
    SequenceRunner.prototype.handleEvent = function (event, testMessage) {
        var eventName = event.eventName;
        var message = event.message;
        var timeSpend = event.timeSpend;
        var indent = timeSpend === -1 ? "" : "\t";
        console.log(indent + (message || "total") + ": " + (timeSpend === -1 ? "" : (timeSpend + " ms")));
        if (eventName === 'FINISH') {
            timeData.push({
                api: this.relativeUri,
                caseId: this.caseName,
                total: timeSpend,
                message: testMessage
            });
            return;
        }
    };
    SequenceRunner.prototype.run = function () {
        var _this = this;
        this.tracker.start("\nStart measures for '" + this.relativeUri + "'");
        return this.doNext(0).then(function (result) {
            connection.documentClosed(_this.absoluteUri);
            _this.tracker.finish(null);
            return result;
        });
    };
    SequenceRunner.prototype.doNext = function (stepNum) {
        var _this = this;
        if (stepNum >= this.sequence.length) {
            return Promise.resolve({ passed: true });
        }
        var step = this.sequence[stepNum];
        var result;
        if (step === 'OPEN') {
            result = openDocument(this.absoluteUri);
        }
        else if (step === 'OPEN_WITH_VALIDATION') {
            result = openDocumentThenValidate(this.absoluteUri);
        }
        else if (step === 'GET_STRUCTURE') {
            result = structureReport(this.absoluteUri);
        }
        else if (step === 'SUGGEST') {
            result = getSuggestions(this.absoluteUri, suggestions[this.relativeUri]);
        }
        return result.then(function (res) {
            if (step !== 'OPEN') {
                _this.tracker.note(step + " step: ");
            }
            return res.passed ? _this.doNext(stepNum + 1) : Promise.resolve({ passed: false });
        });
    };
    return SequenceRunner;
}());
var suggestions = {};
function setSuggestionsInput(relativeUri, positions) {
    suggestions[relativeUri] = positions.reverse();
}
function getSuggestions(absoluteUri, positions) {
    var position = positions.pop();
    return connection.getSuggestions(absoluteUri, position).then(function () { return ({ passed: true }); }, function () { return ({ passed: false }); });
}
function openDocumentThenValidate(absoluteUri) {
    return new Promise(function (resolve, reject) {
        connection.onValidationReport(function (result) {
            if (result.pointOfViewUri !== absoluteUri) {
                return;
            }
            resolve({ passed: true });
        });
        openDocument(absoluteUri);
    });
}
function structureReport(absoluteUri) {
    return connection.getStructure(absoluteUri).then(function () { return ({ passed: true }); }, function () { return ({ passed: false }); });
}
function openDocument(absoluteUri) {
    var content = fs.readFileSync(absoluteUri).toString();
    connection.documentOpened({
        uri: absoluteUri,
        text: content
    });
    return Promise.resolve({
        passed: true
    });
}
function openConnection() {
    connection = index.getNodeClientConnection();
    // connection.setLoggerConfiguration({
    //     disabled: true
    // });
}
function getAbsoluteUri(relativePath) {
    return path.resolve(__dirname, "../../../src/test/data/performance", relativePath);
}
var cases = {
    'FULL_LIFECYCLE': ['OPEN_WITH_VALIDATION', 'GET_STRUCTURE'],
    'LOADING': ['OPEN_WITH_VALIDATION'],
    'LOADING_THEN_STRUCTURE': ['OPEN', 'GET_STRUCTURE']
};
//# sourceMappingURL=util.js.map