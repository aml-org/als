"use strict";
// This module reports RAML warnings and errors
var __extends = (this && this.__extends) || (function () {
    var extendStatics = Object.setPrototypeOf ||
        ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
        function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
Object.defineProperty(exports, "__esModule", { value: true });
var parser = require("raml-1-parser");
var utils = parser.utils;
function createManager(connection, astManagerModule, editorManagerModule) {
    return new ValidationManager(connection, astManagerModule, editorManagerModule);
}
exports.createManager = createManager;
var Acceptor = /** @class */ (function (_super) {
    __extends(Acceptor, _super);
    function Acceptor(ramlPath, primaryUnit, logger) {
        var _this = _super.call(this, [], primaryUnit) || this;
        _this.ramlPath = ramlPath;
        _this.logger = logger;
        _this.buffers = {};
        _this.foundIssues = [];
        return _this;
    }
    Acceptor.prototype.getErrors = function () {
        return this.foundIssues;
    };
    Acceptor.prototype.accept = function (issue) {
        var _this = this;
        if (!issue) {
            return;
        }
        this.logger.debugDetail("Accepting issue: " + issue.message, "ValidationManager", "accept");
        this.transformIssue(issue);
        var issueType = issue.isWarning ? "Warning" : "Error";
        var issuesArray = [];
        while (issue) {
            issuesArray.push(issue);
            if (issue.extras && issue.extras.length > 0) {
                issue = issue.extras[0];
            }
            else {
                issue = null;
            }
        }
        var issues = issuesArray.reverse().map(function (x) {
            var result = _this.convertParserIssue(x, issueType);
            issueType = "Trace";
            return result;
        });
        for (var i = 0; i < issues.length - 1; i++) {
            issues[0].trace.push(issues[i + 1]);
        }
        var message = issues[0];
        this.foundIssues.push(message);
    };
    Acceptor.prototype.acceptUnique = function (issue) {
        this.accept(issue);
    };
    Acceptor.prototype.end = function () {
    };
    Acceptor.prototype.convertParserIssue = function (originalIssue, issueType) {
        var t = originalIssue.message;
        var ps = originalIssue.path;
        if (originalIssue.unit) {
            ps = originalIssue.unit.absolutePath();
        }
        var trace = {
            code: originalIssue.code,
            type: issueType,
            filePath: originalIssue.path ? ps : null,
            text: t,
            range: {
                start: originalIssue.start,
                end: originalIssue.end
            },
            trace: [],
        };
        return trace;
    };
    return Acceptor;
}(utils.PointOfViewValidationAcceptorImpl));
var ValidationManager = /** @class */ (function () {
    function ValidationManager(connection, astManagerModule, editorManagerModule) {
        this.connection = connection;
        this.astManagerModule = astManagerModule;
        this.editorManagerModule = editorManagerModule;
    }
    ValidationManager.prototype.launch = function () {
        var _this = this;
        this.onNewASTAvailableListener = function (uri, version, ast) {
            _this.newASTAvailable(uri, version, ast);
        };
        this.astManagerModule.onNewASTAvailable(this.onNewASTAvailableListener);
    };
    ValidationManager.prototype.dispose = function () {
        this.astManagerModule.onNewASTAvailable(this.onNewASTAvailableListener, true);
    };
    /**
     * Returns unique module name.
     */
    ValidationManager.prototype.getModuleName = function () {
        return "VALIDATION_MANAGER";
    };
    ValidationManager.prototype.newASTAvailable = function (uri, version, ast) {
        this.connection.debug("Got new AST:\n" + (ast != null ? ast.printDetails() : null), "ValidationManager", "newASTAvailable");
        var errors = this.gatherValidationErrors(ast, uri);
        this.connection.debug("Number of errors is:\n" + (errors ? errors.length : 0), "ValidationManager", "newASTAvailable");
        this.connection.validated({
            pointOfViewUri: uri,
            version: version,
            issues: errors
        });
    };
    ValidationManager.prototype.gatherValidationErrors = function (astNode, ramlPath) {
        if (!astNode) {
            return;
        }
        var acceptor = new Acceptor(ramlPath, astNode.root(), this.connection);
        astNode.validate(acceptor);
        var acceptedErrors = acceptor.getErrors();
        var editor = this.editorManagerModule.getEditor(ramlPath);
        if (!editor) {
            return acceptedErrors;
        }
        var text = editor.getText();
        if (!text) {
            return acceptedErrors;
        }
        var tabErrors = [];
        var tab = 0;
        while (true) {
            tab = text.indexOf("\t", tab);
            if (tab !== -1) {
                var tabWarning = {
                    code: "TAB_WARNING",
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
            }
            else {
                break;
            }
        }
        return acceptedErrors.concat(tabErrors);
    };
    return ValidationManager;
}());
//# sourceMappingURL=validationManager.js.map