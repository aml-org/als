"use strict";
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
var index_1 = require("../../index");
var assert = require("assert");
var path = require("path");
var fs = require("fs");
function testEditorManager(testFileRelativePath, done) {
    var fullFilePath = getTestEditorManagerPath(testFileRelativePath);
    testWordBased(fullFilePath, new EditorManagerTester(fullFilePath), done);
}
exports.testEditorManager = testEditorManager;
function testValidationManager(testFileRelativePath, done) {
    var fullFilePath = getTestValidationManagerPath(testFileRelativePath);
    testWordBased(fullFilePath, new ValidationManagerTester(fullFilePath), done);
}
exports.testValidationManager = testValidationManager;
function testStructureManager(testFileRelativePath, done) {
    var fullFilePath = getTestStructureManagerPath(testFileRelativePath);
    testWordBased(fullFilePath, new StructureManagerTester(fullFilePath), done);
}
exports.testStructureManager = testStructureManager;
function testCompletionManager(testFileRelativePath, done) {
    var fullFilePath = getTestCompletionManagerPath(testFileRelativePath);
    testWordBased(fullFilePath, new CompletionManagerTester(fullFilePath), done);
}
exports.testCompletionManager = testCompletionManager;
function testDetailsManager(testFileRelativePath, done) {
    var fullFilePath = getTestDetailsManagerPath(testFileRelativePath);
    testWordBased(fullFilePath, new DetailsManagerTester(fullFilePath), done);
}
exports.testDetailsManager = testDetailsManager;
function testWordBased(filePath, tester, done) {
    var contentProducer = new WordBasedContentProducer(filePath);
    testGeneral(contentProducer, tester, done);
}
exports.testWordBased = testWordBased;
function testGeneral(contentProducer, tester, done) {
    tester.beforeAll();
    testGeneralRecursive(contentProducer, tester, done);
}
exports.testGeneral = testGeneral;
function testGeneralRecursive(contentProducer, tester, done) {
    var currentVariant = contentProducer.getNext();
    if (currentVariant === null) {
        tester.afterAll();
        done();
        return;
    }
    try {
        return tester.test(currentVariant, currentVariant.last).then(function (result) {
            assert(result);
            return testGeneralRecursive(contentProducer, tester, done);
        }, function (error) {
            done(error);
        });
    }
    catch (error) {
        done(error);
    }
}
exports.testGeneralRecursive = testGeneralRecursive;
var AbstractDocumentTester = (function () {
    function AbstractDocumentTester(apiPath) {
        this.apiPath = apiPath;
        this.connection = index_1.getNodeClientConnection();
    }
    /**
     * Is called before the start of the sequence.
     */
    AbstractDocumentTester.prototype.beforeAll = function () {
        this.connection.documentOpened({
            uri: this.apiPath,
            text: "#%RAML 1.0\n"
        });
        this.connection.onExists(function (fullPath) {
            return Promise.resolve(fs.existsSync(fullPath));
        });
        this.connection.onReadDir(function (fullPath) {
            return Promise.resolve(fs.readdirSync(fullPath));
        });
        this.connection.onIsDirectory(function (fullPath) {
            return Promise.resolve(fs.statSync(fullPath).isDirectory());
        });
        this.connection.onContent(function (fullPath) {
            return Promise.resolve(fs.readFileSync(fullPath).toString());
        });
    };
    /**
     * Is called after the end of the sequence.
     */
    AbstractDocumentTester.prototype.afterAll = function () {
        this.connection.documentClosed(this.apiPath);
    };
    return AbstractDocumentTester;
}());
var EditorManagerTester = (function (_super) {
    __extends(EditorManagerTester, _super);
    function EditorManagerTester() {
        var _this = _super !== null && _super.apply(this, arguments) || this;
        _this.callBack = null;
        return _this;
    }
    EditorManagerTester.prototype.beforeAll = function () {
        // this.connection.setServerConfiguration({
        //     modulesConfiguration: {
        //         allModules: false
        //     }
        // });
        //
        // this.connection.setServerConfiguration({
        //     modulesConfiguration: {
        //         enableEditorManagerModule: true
        //     }
        // });
        _super.prototype.beforeAll.call(this);
    };
    EditorManagerTester.prototype.test = function (contentVariant, last) {
        var _this = this;
        if (last) {
            // on the last change enabling ast and validation to check report
            // and make sure server is still alive
            // this.connection.setServerConfiguration({
            //     modulesConfiguration: {
            //         enableEditorManagerModule: true,
            //         enableASTManagerModule: true,
            //         enableValidationManagerModule: true
            //     }
            // });
            this.connection.onValidationReport(function (validationReport) {
                if (_this.callBack && _this.callBack.resolve) {
                    _this.callBack.resolve(true);
                }
                _this.callBack = null;
            });
        }
        this.connection.documentChanged({
            uri: contentVariant.path,
            text: contentVariant.content
        });
        if (last) {
            return new Promise(function (resolve, reject) {
                _this.callBack = {
                    resolve: resolve,
                    reject: reject
                };
            });
        }
        else {
            return Promise.resolve(true);
        }
    };
    return EditorManagerTester;
}(AbstractDocumentTester));
var ValidationManagerTester = (function (_super) {
    __extends(ValidationManagerTester, _super);
    function ValidationManagerTester() {
        var _this = _super !== null && _super.apply(this, arguments) || this;
        _this.callBack = null;
        return _this;
    }
    ValidationManagerTester.prototype.beforeAll = function () {
        // this.connection.setServerConfiguration({
        //     modulesConfiguration: {
        //         allModules: false
        //     }
        // });
        //
        // this.connection.setServerConfiguration({
        //     modulesConfiguration: {
        //         enableEditorManagerModule: true,
        //         enableASTManagerModule: true,
        //         enableValidationManagerModule: true
        //     }
        // });
        var _this = this;
        this.connection.onValidationReport(function (validationReport) {
            if (_this.callBack && _this.callBack.resolve) {
                _this.callBack.resolve(true);
            }
            _this.callBack = null;
        });
        _super.prototype.beforeAll.call(this);
    };
    ValidationManagerTester.prototype.test = function (contentVariant, last) {
        var _this = this;
        this.connection.documentChanged({
            uri: contentVariant.path,
            text: contentVariant.content
        });
        return new Promise(function (resolve, reject) {
            _this.callBack = {
                resolve: resolve,
                reject: reject
            };
        });
    };
    return ValidationManagerTester;
}(AbstractDocumentTester));
var StructureManagerTester = (function (_super) {
    __extends(StructureManagerTester, _super);
    function StructureManagerTester() {
        var _this = _super !== null && _super.apply(this, arguments) || this;
        _this.callBack = null;
        return _this;
    }
    StructureManagerTester.prototype.beforeAll = function () {
        // this.connection.setServerConfiguration({
        //     modulesConfiguration: {
        //         allModules: false
        //     }
        // });
        //
        // this.connection.setServerConfiguration({
        //     modulesConfiguration: {
        //         enableEditorManagerModule: true,
        //         enableASTManagerModule: true,
        //         enableStructureManagerModule: true
        //     }
        // });
        var _this = this;
        this.connection.onStructureReport(function (structureReport) {
            if (_this.callBack && _this.callBack.resolve) {
                _this.callBack.resolve(true);
            }
            _this.callBack = null;
        });
        _super.prototype.beforeAll.call(this);
    };
    StructureManagerTester.prototype.test = function (contentVariant, last) {
        var _this = this;
        this.connection.documentChanged({
            uri: contentVariant.path,
            text: contentVariant.content
        });
        return new Promise(function (resolve, reject) {
            _this.callBack = {
                resolve: resolve,
                reject: reject
            };
        });
    };
    return StructureManagerTester;
}(AbstractDocumentTester));
var CompletionManagerTester = (function (_super) {
    __extends(CompletionManagerTester, _super);
    function CompletionManagerTester() {
        var _this = _super !== null && _super.apply(this, arguments) || this;
        _this.callBack = null;
        return _this;
    }
    CompletionManagerTester.prototype.beforeAll = function () {
        _super.prototype.beforeAll.call(this);
    };
    CompletionManagerTester.prototype.test = function (contentVariant, last) {
        this.connection.documentChanged({
            uri: contentVariant.path,
            text: contentVariant.content
        });
        var position = contentVariant.content.length - 1;
        return this.connection.getSuggestions(contentVariant.path, position).then(function (result) {
            return true;
        });
    };
    return CompletionManagerTester;
}(AbstractDocumentTester));
var DetailsManagerTester = (function (_super) {
    __extends(DetailsManagerTester, _super);
    function DetailsManagerTester() {
        var _this = _super !== null && _super.apply(this, arguments) || this;
        _this.callBack = null;
        return _this;
    }
    DetailsManagerTester.prototype.beforeAll = function () {
        _super.prototype.beforeAll.call(this);
    };
    DetailsManagerTester.prototype.test = function (contentVariant, last) {
        this.connection.documentChanged({
            uri: contentVariant.path,
            text: contentVariant.content
        });
        var position = contentVariant.content.length - 1;
        return this.connection.getDetails(contentVariant.path, position).then(function (result) {
            return true;
        });
    };
    return DetailsManagerTester;
}(AbstractDocumentTester));
var WordBasedContentProducer = (function () {
    function WordBasedContentProducer(filePath) {
        this.filePath = filePath;
        this.currentIndex = 0;
        var fileContent = fs.readFileSync(filePath, "utf8");
        var headerIndex = fileContent.indexOf("#%RAML");
        if (headerIndex < 0) {
            throw new Error("Not a RAML file: " + filePath);
        }
        var lineIndex = fileContent.indexOf("\n", headerIndex);
        if (lineIndex < 0) {
            lineIndex = fileContent.length;
        }
        else {
            lineIndex += "\n".length;
        }
        this.contentBuffer = fileContent.substr(0, lineIndex);
        this.words = fileContent.substring(lineIndex).split(/(\s+)/);
        if (this.words.length > 0 && this.words[this.words.length - 1].length == 0) {
            this.words.splice(this.words.length - 1, 1);
        }
    }
    WordBasedContentProducer.prototype.getNext = function () {
        if (this.currentIndex >= this.words.length) {
            return null;
        }
        this.contentBuffer += this.words[this.currentIndex];
        this.currentIndex++;
        return {
            path: this.filePath,
            content: this.contentBuffer,
            last: this.currentIndex === this.words.length
        };
    };
    return WordBasedContentProducer;
}());
function getTestEditorManagerPath(originalPath) {
    return path.resolve(__dirname, "../../../src/test/data/longevity"
        + originalPath).replace(/\\/g, "/");
}
function getTestValidationManagerPath(originalPath) {
    return path.resolve(__dirname, "../../../src/test/data/longevity"
        + originalPath).replace(/\\/g, "/");
}
function getTestStructureManagerPath(originalPath) {
    return path.resolve(__dirname, "../../../src/test/data/longevity"
        + originalPath).replace(/\\/g, "/");
}
function getTestCompletionManagerPath(originalPath) {
    return path.resolve(__dirname, "../../../src/test/data/longevity"
        + originalPath).replace(/\\/g, "/");
}
function getTestDetailsManagerPath(originalPath) {
    return path.resolve(__dirname, "../../../src/test/data/longevity"
        + originalPath).replace(/\\/g, "/");
}
//# sourceMappingURL=utils.js.map