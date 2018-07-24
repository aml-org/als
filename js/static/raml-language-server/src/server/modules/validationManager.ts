// This module reports RAML warnings and errors

import {
    IServerConnection
} from "../core/connections";

import {
    IASTManagerModule
} from "./astManager";

import {
    ILogger,
    IValidationIssue
} from "../../common/typeInterfaces";

import {
    IDisposableModule
} from "./commonInterfaces";

import {
    IEditorManagerModule
} from "./editorManager";

import parser = require("raml-1-parser");
import utils = parser.utils;

type IHighLevelNode = parser.hl.IHighLevelNode;

export function createManager(connection: IServerConnection,
                              astManagerModule: IASTManagerModule,
                              editorManagerModule: IEditorManagerModule): IDisposableModule {

    return new ValidationManager(connection, astManagerModule, editorManagerModule);
}

class Acceptor extends utils.PointOfViewValidationAcceptorImpl {
    public buffers: {[path: string]: any} = {};

    private foundIssues: IValidationIssue[] = [];

    constructor(private ramlPath: string, primaryUnit: parser.hl.IParseResult,
                private logger: ILogger) {
        super([], primaryUnit);
    }

    public getErrors(): IValidationIssue[] {
        return this.foundIssues;
    }

    public accept(issue: parser.hl.ValidationIssue) {
        if (!issue) {
            return;
        }

        this.logger.debugDetail("Accepting issue: " + issue.message,
            "ValidationManager", "accept");

        this.transformIssue(issue);

        let issueType = issue.isWarning ? "Warning" : "Error";

        const issuesArray: parser.hl.ValidationIssue[] = [];

        while (issue) {
            issuesArray.push(issue);

            if (issue.extras && issue.extras.length > 0) {
                issue = issue.extras[0];
            } else {
                issue = null;
            }
        }

        const issues = issuesArray.reverse().map((x) => {
            const result = this.convertParserIssue(x, issueType);

            issueType = "Trace";

            return result;
        });

        for (let i = 0 ; i < issues.length - 1; i++) {
            issues[0].trace.push(issues[i + 1]);
        }

        const message = issues[0];

        this.foundIssues.push(message);
    }

    public acceptUnique(issue: parser.hl.ValidationIssue) {
        this.accept(issue);
    }

    public end() {

    }

    private convertParserIssue(originalIssue: parser.hl.ValidationIssue, issueType: string): IValidationIssue {
        const t = originalIssue.message;

        let ps = originalIssue.path;

        if (originalIssue.unit) {
            ps = originalIssue.unit.absolutePath();
        }

        const trace = {
            code: originalIssue.code,
            type: issueType,
            filePath: originalIssue.path ? ps : null,
            text: t,
            range: {
                start : originalIssue.start,
                end : originalIssue.end
            },
            trace: [],
        };

        return trace;
    }
}

class ValidationManager implements IDisposableModule {

    private onNewASTAvailableListener;

    constructor(private connection: IServerConnection, private astManagerModule: IASTManagerModule,
                private editorManagerModule: IEditorManagerModule) {
    }

    public launch() {

        this.onNewASTAvailableListener = (uri: string, version: number, ast: IHighLevelNode) => {
            this.newASTAvailable(uri, version, ast);
        }

        this.astManagerModule.onNewASTAvailable(this.onNewASTAvailableListener);
    }

    public dispose(): void {
        this.astManagerModule.onNewASTAvailable(this.onNewASTAvailableListener, true);
    }

    /**
     * Returns unique module name.
     */
    public getModuleName(): string {
        return "VALIDATION_MANAGER";
    }

    public newASTAvailable(uri: string, version: number, ast: IHighLevelNode): void {

        this.connection.debug("Got new AST:\n" + (ast != null ? ast.printDetails() : null),
            "ValidationManager", "newASTAvailable");

        const errors = this.gatherValidationErrors(ast, uri);

        this.connection.debug("Number of errors is:\n" + (errors ? errors.length : 0),
            "ValidationManager", "newASTAvailable");

        this.connection.validated({
            pointOfViewUri : uri,
            version,
            issues : errors
        });
    }

    public gatherValidationErrors(astNode: parser.IHighLevelNode, ramlPath: string): IValidationIssue[] {
        if (!astNode) {
            return;
        }

        const acceptor = new Acceptor(ramlPath, astNode.root(), this.connection);

        astNode.validate(acceptor);

        const acceptedErrors = acceptor.getErrors();

        const editor = this.editorManagerModule.getEditor(ramlPath);
        if (!editor) {
            return acceptedErrors;
        }

        const text = editor.getText();
        if (!text) {
            return acceptedErrors;
        }

        const tabErrors: IValidationIssue[] = [];

        let tab: number = 0;
        while (true) {
            tab = text.indexOf("\t", tab);
            if (tab !== -1) {
                const tabWarning = {
                    code : "TAB_WARNING",

                    type: "Warning",

                    filePath: ramlPath,

                    text: "Using tabs  can lead to unpredictable results",
                    range: {
                        start: tab,
                        end: tab + 1
                    },
                    trace: []
                };

                tabErrors.push(tabWarning);
                tab++;
            } else {
                break;
            }
        }

        return acceptedErrors.concat(tabErrors);
    }
}
