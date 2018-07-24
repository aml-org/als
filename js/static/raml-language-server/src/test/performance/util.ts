import index = require("../../index");

declare var require;
declare var __dirname;

var fs = require("fs");
var path = require("path");

var testCases: any[] = [];

var connection = null;

var srcPath = path.resolve(__dirname, '../../../resources/performance/report.html');
var dstPath = path.resolve(__dirname, '../../../performance_report/report.html');

var reportTemplate = fs.readFileSync(srcPath).toString();

export function addCase(relativeUri: string, caseName: string, testInfo?:string) {
    testCases.push({relativeUri, caseName});
}

export function runCases() {
    openConnection();
   
    handleCase(0);
}

function handleCase(caseNum: number) {
    if(caseNum >= testCases.length) {
        console.log("DONE.")

        generateReport();
        
        if(!connection.debug) {
            connection.debug = (message, arg2) => console.log("message");
        }
        
        connection.stop();
        
        return;
    }

    var currentCase = testCases[caseNum];

    runSequence(currentCase.relativeUri, currentCase.caseName, (previousResult, message) => {
        if(previousResult) {
            handleCase(caseNum + 1);
        } else {
            console.log(message, ", measurements stopped.")

            connection.stop()
        }
    });
}

function runSequence(relativeUri: string, caseName: string, nextTask: (previousResult, message) => void) {
    new SequenceRunner(relativeUri, caseName).run().then((result: boolean) => {
        nextTask(result, result ? "OK" : "SOMETHING WRONG");
    })
}

class RequestResult {
    passed: boolean;
}

class TimeTrackerEvent {
    eventName: string;
    message: string;
    timeSpend: number;
}

var timeData: any[] = [];

export function generateReport() {
    var apis = {};

    var table = [];

    var messages = {};

    timeData.forEach(record => {
        var tableRecord = null;

        messages[record.api] = record.message;

        var apiNum = apis[record.api];

        if(!apiNum) {
            apiNum = Object.keys(apis).length + 1;

            apis[record.api] = apiNum;
        }

        for(var i = 0; i < table.length; i++) {
            var foundRecord = table[i];

            if(foundRecord['api/case'] === apiNum) {
                tableRecord = foundRecord;

                break;
            }
        }

        if(!tableRecord) {
            tableRecord = {
                'api/case': apiNum
            }

            table.push(tableRecord);
        }

        tableRecord[record.caseId] = record.total + "ms";
    });

    console.log("Apis:");

    Object.keys(apis).forEach(key => {
        var messageString = messages[key] ? (':\n\t' + messages[key]) : '';

        console.log(apis[key] + ". " + key + messageString);
    });

    console.log();

    var jsonData: any = {
        apis: apis,
        table: table,
        messages: messages
    };

    fs.writeFileSync(dstPath, reportTemplate.replace('__jsonData__', JSON.stringify(jsonData)));
}

class TimeTracker {
    lastNoteTime: number;

    total: number = 0;

    constructor(private onEvent: (TimeTrackerEvent) => void) {

    }

    start(startMessage: string): void {
        this.onEvent({
            eventName: 'NOTE',
            
            message: startMessage,
            
            timeSpend: -1
        });

        this.lastNoteTime = this.time();
    }

    note(message: string): void {
        var timeSpend = this.diff();

        this.total+= timeSpend;

        this.onEvent({
            eventName: 'NOTE',
            message: message,
            timeSpend: timeSpend
        });

        this.lastNoteTime = this.time();
    }

    reset(): void {
        this.lastNoteTime = this.time();

        this.total = 0;
    }

    finish(message: string): void {
        this.onEvent({
            eventName: 'FINISH',
            message: message,
            timeSpend: this.total
        });
    }

    private diff(): number {
        return this.time() - this.lastNoteTime;
    }

    private time(): number {
        return new Date().getTime();
    }
}

class SequenceRunner {
    private absoluteUri: string;
    
    private sequence: string[];

    private tracker: TimeTracker;

    constructor(private relativeUri: string, private caseName) {
        this.absoluteUri = getAbsoluteUri(relativeUri);
        this.sequence = cases[caseName];
        
        this.tracker = new TimeTracker((event: TimeTrackerEvent) => this.handleEvent(event))
    }

    handleEvent(event: TimeTrackerEvent, testMessage?: string): void {
        var eventName = event.eventName;
        var message = event.message;
        var timeSpend = event.timeSpend;

        var indent = timeSpend === -1 ? "" : "\t";

        console.log(indent + (message || "total") + ": " + (timeSpend === -1 ? "" : (timeSpend + " ms")));

        if(eventName === 'FINISH') {
            timeData.push({
                api: this.relativeUri,
                caseId: this.caseName,
                total: timeSpend,
                message: testMessage
            });

            return;
        }
    }

    run(): Promise<boolean> {
        this.tracker.start("\nStart measures for '" + this.relativeUri + "'");
       
        return this.doNext(0).then(result => {
            connection.documentClosed(this.absoluteUri);

            this.tracker.finish(null);
           
            return result;
        });
    }

    private doNext(stepNum): Promise<any> {
        if(stepNum >= this.sequence.length) {
            return Promise.resolve({passed: true});
        }

        var step = this.sequence[stepNum];

        var result: Promise<any>;
       
        if(step === 'OPEN') {
            result = openDocument(this.absoluteUri);
        } else if(step === 'OPEN_WITH_VALIDATION') {
            result = openDocumentThenValidate(this.absoluteUri);
        } else if(step === 'GET_STRUCTURE') {
            result = structureReport(this.absoluteUri);
        } else if(step === 'SUGGEST') {
            result = getSuggestions(this.absoluteUri, suggestions[this.relativeUri]);
        }
       
        return result.then(res => {
            if(step !== 'OPEN') {
                this.tracker.note(step + " step: ")
            }
                                   
            return res.passed ? this.doNext(stepNum + 1) : Promise.resolve({passed: false});
        })
    }
}

var suggestions: any = {
   
}

function setSuggestionsInput(relativeUri: string, positions: number[]) {
    suggestions[relativeUri] = positions.reverse();
}

function getSuggestions(absoluteUri: string, positions): Promise<RequestResult> {
    var position = positions.pop();
   
    return connection.getSuggestions(absoluteUri, position).then(() => ({passed: true}), () => ({passed: false}));
}

function openDocumentThenValidate(absoluteUri: string): Promise<RequestResult> {
    return new Promise((resolve, reject) => {
        connection.onValidationReport(result => {
            if(result.pointOfViewUri !== absoluteUri) {
                return;
            }

            resolve({passed: true});
        });
        
        openDocument(absoluteUri);
    });
}

function structureReport(absoluteUri: string): Promise<RequestResult> {
    return connection.getStructure(absoluteUri).then(() => ({passed: true}), () => ({passed: false}));
}

function openDocument(absoluteUri: string): Promise<RequestResult> {
    var content = fs.readFileSync(absoluteUri).toString();

    connection.documentOpened({
        uri: absoluteUri,

        text: content
    });

    return Promise.resolve({
        passed: true
    })
}

function openConnection(): void {
    connection = index.getNodeClientConnection();

    // connection.setLoggerConfiguration({
    //     disabled: true
    // });
}

function getAbsoluteUri(relativePath: string): string {
    return path.resolve(__dirname, "../../../src/test/data/performance", relativePath);
}

var cases: any = {
    'FULL_LIFECYCLE': ['OPEN_WITH_VALIDATION', 'GET_STRUCTURE'],
    'LOADING': ['OPEN_WITH_VALIDATION'],
    'LOADING_THEN_STRUCTURE': ['OPEN', 'GET_STRUCTURE']
};