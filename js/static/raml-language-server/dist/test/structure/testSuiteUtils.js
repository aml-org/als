"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var utils = require("../../common/utils");
var index = require("../../index");
var fs = require("fs");
var path = require("path");
var parser = require("raml-1-parser");
var assert = require("assert");
var _ = require("underscore");
function data(filepath) {
    var datadir = path.resolve(projectFolder(), 'src/test/data');
    return path.resolve(datadir, filepath).replace(/\\/g, '/');
}
exports.data = data;
var pathReplacer = function (str1, str2) {
    var l = str1.length;
    return function (key, value) {
        if (value) {
            if (typeof (value) == "object") {
                for (var _i = 0, _a = Object.keys(value); _i < _a.length; _i++) {
                    var k = _a[_i];
                    if (k.substring(0, l) == str1) {
                        var newKey = str2 + k.substring(l);
                        var val = value[k];
                        delete value[k];
                        value[newKey] = val;
                    }
                }
            }
            else if (typeof (value) == "string") {
                value = value.split(str1).join(str2);
            }
        }
        return value;
    };
};
var serializeTestJSON = function (outlineJsonPath, json) {
    var copy = JSON.parse(JSON.stringify(json));
    var rootPath = "file://" + data("").replace(/\\/g, "/");
    var replacer = pathReplacer(rootPath, "__$$ROOT_PATH__");
    fs.writeFileSync(outlineJsonPath, JSON.stringify(copy, replacer, 2));
};
var readTestJSON = function (outlineJsonPath) {
    var rootPath = "file://" + data("").replace(/\\/g, "/");
    var replacer = pathReplacer("__$$ROOT_PATH__", rootPath);
    return JSON.parse(fs.readFileSync(outlineJsonPath).toString(), replacer);
};
function compare(arg0, arg1, path) {
    if (path === void 0) { path = ''; }
    var diffs = [];
    if (arg0 == null) {
        if (arg1 != null) {
            diffs.push(new Diff(path, arg0, arg1, 'Defined/undefined mismatch'));
            return diffs;
        }
    }
    else if (arg1 == null) {
        diffs.push(new Diff(path, arg0, arg1, 'Defined/undefined mismatch'));
        return diffs;
    }
    else if (Array.isArray(arg0)) {
        if (!Array.isArray(arg1)) {
            diffs.push(new Diff(path, arg0, arg1, 'Array/' + typeof (arg1) + ' mismatch'));
            return diffs;
        }
        else {
            var l0 = arg0.length;
            var l1 = arg1.length;
            if (l1 != l0) {
                diffs.push(new Diff(path, arg0, arg1, 'Array lengths mismatch'));
                return diffs;
            }
            var l = Math.min(l0, l1);
            for (var i = 0; i < l; i++) {
                diffs = diffs.concat(compare(arg0[i], arg1[i], path + '[' + i + ']'));
            }
        }
    }
    else if (arg0 instanceof Object) {
        if (!(arg1 instanceof Object)) {
            diffs.push(new Diff(path, arg0, arg1, 'Object/' + typeof (arg1) + ' mismatch'));
            return diffs;
        }
        else {
            var keys0 = Object.keys(arg0);
            var keys1 = Object.keys(arg1);
            var map = {};
            for (var i = 0; i < keys0.length; i++) {
                var key = keys0[i];
                map[key] = true;
                var val0 = arg0[key];
                var val1 = arg1[key];
                diffs = diffs.concat(compare(val0, val1, path + '/' + key));
            }
            for (var i = 0; i < keys1.length; i++) {
                var key = keys1[i];
                if (map[key]) {
                    continue;
                }
                var val0 = arg0[key];
                var val1 = arg1[key];
                diffs = diffs.concat(compare(val0, val1, path + '/' + key));
            }
        }
    }
    else {
        if (arg0 !== arg1) {
            diffs.push(new Diff(path, arg0, arg1, 'Inequal values'));
        }
    }
    return diffs;
}
exports.compare = compare;
var Diff = /** @class */ (function () {
    function Diff(path, value0, value1, comment) {
        this.path = path;
        this.value0 = value0;
        this.value1 = value1;
        this.comment = comment;
        this.path = path;
        this.value0 = value0;
        this.value1 = value1;
        this.comment = comment;
    }
    Diff.prototype.message = function (label0, label1) {
        label0 = label0 || "value0";
        label1 = label1 || "value1";
        var strValue0 = "undefined";
        var strValue1 = "undefined";
        if (this.value0 != null) {
            try {
                strValue0 = JSON.stringify(this.value0, null, 2).trim();
            }
            catch (err) {
                strValue0 = this.value0.toString();
            }
        }
        if (this.value1 != null) {
            try {
                strValue1 = JSON.stringify(this.value1, null, 2).trim();
            }
            catch (err) {
                strValue1 = this.value1.toString();
            }
        }
        return "path: " + this.path + "\ncomment: " + this.comment + "\n" + label0 + ": " + strValue0 + "\n" + label1 + ": " + strValue1;
    };
    return Diff;
}());
exports.Diff = Diff;
var connection;
function stopConnection() {
    if (connection) {
        connection.stop();
    }
}
exports.stopConnection = stopConnection;
function getOutlineJSONAsync(apiPath, callback) {
    apiPath = resolve(apiPath);
    var content = fs.readFileSync(apiPath).toString();
    connection = index.getNodeClientConnection();
    connection.documentOpened({
        uri: apiPath,
        text: content
    });
    connection.positionChanged(apiPath, 0);
    connection.getStructure(apiPath).then(function (result) {
        connection.documentClosed(apiPath);
        callback(result, null);
    }, function (ee) {
        callback(null, ee);
    });
}
function resolve(testPath) {
    return utils.resolve(__dirname, '../../../src/test/data/' + testPath);
}
function testOutline(apiPath, done, extensions, outlineJsonPath, regenerateJSON, callTests) {
    if (regenerateJSON === void 0) { regenerateJSON = false; }
    if (callTests === void 0) { callTests = true; }
    getOutlineJSONAsync(apiPath, function (result, error) {
        if (error) {
            done(error);
            return;
        }
        try {
            assert(testOutlineStructure(apiPath, result, extensions, outlineJsonPath, regenerateJSON, callTests));
            done();
        }
        catch (exception) {
            done(exception);
        }
    });
}
exports.testOutline = testOutline;
function testOutlineStructure(apiPath, json, extensions, outlineJsonPath, regenerateJSON, callTests) {
    if (regenerateJSON === void 0) { regenerateJSON = false; }
    if (callTests === void 0) { callTests = true; }
    if (apiPath) {
        apiPath = data(apiPath);
    }
    if (extensions) {
        extensions = extensions.map(function (x) { return data(x); });
    }
    if (!outlineJsonPath) {
        outlineJsonPath = defaultJSONPath(apiPath);
    }
    else {
        outlineJsonPath = data(outlineJsonPath);
    }
    var api = parser.loadRAMLSync(apiPath, extensions);
    var expanded = api;
    expanded.setAttributeDefaults(true);
    if (!outlineJsonPath) {
        outlineJsonPath = defaultJSONPath(apiPath);
    }
    if (regenerateJSON) {
        serializeTestJSON(outlineJsonPath, json);
    }
    if (!fs.existsSync(outlineJsonPath)) {
        serializeTestJSON(outlineJsonPath, json);
        if (!callTests) {
            console.log("OUTLINE JSON GENERATED: " + outlineJsonPath);
            return;
        }
        console.warn("FAILED TO FIND OUTLINE JSON: " + outlineJsonPath);
    }
    if (!callTests) {
        return;
    }
    var outlineJson = readTestJSON(outlineJsonPath);
    var pathRegExp = new RegExp('/errors\\[\\d+\\]/path');
    var messageRegExp = new RegExp('/errors\\[\\d+\\]/message');
    var diff = compare(json, outlineJson).filter(function (x) {
        if (x.path.match(pathRegExp)) {
            return false;
        }
        return true;
    });
    var diffArr = [];
    if (diff.length == 0) {
        return true;
    }
    else {
        // console.log("DIFFERENCE DETECTED FOR " + outlineJsonPath);
        // console.log(diff.map(x=>x.message("actual","expected")).join("\n\n"));
        return false;
    }
}
function getRamlFirstLine(content) {
    return content.match(/^\s*#%RAML\s+(\d\.\d)\s*(\w*)\s*$/m);
}
exports.getRamlFirstLine = getRamlFirstLine;
function projectFolder() {
    var folder = __dirname;
    while (!fs.existsSync(path.resolve(folder, "package.json"))) {
        folder = path.resolve(folder, "../");
    }
    return folder;
}
exports.projectFolder = projectFolder;
;
var Test = /** @class */ (function () {
    function Test(_masterPath, _extensionsAndOverlays, _jsonPath) {
        this._masterPath = _masterPath;
        this._extensionsAndOverlays = _extensionsAndOverlays;
        this._jsonPath = _jsonPath;
    }
    Test.prototype.masterPath = function () { return this._masterPath; };
    Test.prototype.extensionsAndOverlays = function () { return this._extensionsAndOverlays; };
    Test.prototype.jsonPath = function () { return this._jsonPath; };
    return Test;
}());
exports.Test = Test;
var RamlFileKind;
(function (RamlFileKind) {
    RamlFileKind[RamlFileKind["API"] = 0] = "API";
    RamlFileKind[RamlFileKind["LIBRARY"] = 1] = "LIBRARY";
    RamlFileKind[RamlFileKind["EXTENSION"] = 2] = "EXTENSION";
    RamlFileKind[RamlFileKind["OVERLAY"] = 3] = "OVERLAY";
    RamlFileKind[RamlFileKind["FRAGMENT"] = 4] = "FRAGMENT";
})(RamlFileKind = exports.RamlFileKind || (exports.RamlFileKind = {}));
var RamlFile = /** @class */ (function () {
    function RamlFile(_absPath, _kind, _ver, _extends) {
        this._absPath = _absPath;
        this._kind = _kind;
        this._ver = _ver;
        this._extends = _extends;
    }
    RamlFile.prototype.absolutePath = function () {
        return this._absPath.replace(/\\/g, '/');
    };
    RamlFile.prototype.kind = function () {
        return this._kind;
    };
    RamlFile.prototype.version = function () {
        return this._ver;
    };
    RamlFile.prototype.extends = function () {
        return this._extends.replace(/\\/g, '/');
    };
    return RamlFile;
}());
exports.RamlFile = RamlFile;
var DirectoryContent = /** @class */ (function () {
    function DirectoryContent(dirAbsPath, files) {
        this.dirAbsPath = dirAbsPath;
        this.files = files;
    }
    DirectoryContent.prototype.absolutePath = function () {
        return this.dirAbsPath.replace(/\\/g, '/');
    };
    DirectoryContent.prototype.allRamlFiles = function () {
        return this.files;
    };
    DirectoryContent.prototype.extensionsAndOverlays = function () {
        return this.files.filter(function (x) { return x.kind() == RamlFileKind.EXTENSION || x.kind() == RamlFileKind.OVERLAY; });
    };
    DirectoryContent.prototype.masterAPIs = function () {
        return this.files.filter(function (x) { return x.kind() == RamlFileKind.API; });
    };
    DirectoryContent.prototype.fragments = function () {
        return this.files.filter(function (x) { return x.kind() == RamlFileKind.FRAGMENT; });
    };
    DirectoryContent.prototype.libraries = function () {
        return this.files.filter(function (x) { return x.kind() == RamlFileKind.LIBRARY; });
    };
    DirectoryContent.prototype.hasCleanAPIsOnly = function () {
        return this.extensionsAndOverlays().length == 0 && this.masterAPIs().length > 0;
    };
    DirectoryContent.prototype.hasSingleExtensionOrOverlay = function () {
        return this.extensionsAndOverlays().length == 1 && this.masterAPIs().length > 0;
    };
    DirectoryContent.prototype.hasExtensionsOrOverlaysAppliedToSingleAPI = function () {
        return this.extensionsAndOverlays().length > 0 && this.masterAPIs().length == 1;
    };
    DirectoryContent.prototype.hasFragmentsOnly = function () {
        return this.fragments().length == this.files.length;
    };
    DirectoryContent.prototype.hasLibraries = function () {
        return this.libraries().length > 0;
    };
    DirectoryContent.prototype.topExtensionOrOverlay = function () {
        var arr = this.extensionsAndOverlays();
        var map = {};
        for (var _i = 0, arr_1 = arr; _i < arr_1.length; _i++) {
            var x = arr_1[_i];
            map[x.absolutePath()] = x;
        }
        for (var _a = 0, arr_2 = arr; _a < arr_2.length; _a++) {
            var x = arr_2[_a];
            var ext = x.extends();
            delete map[ext];
        }
        var keys = Object.keys(map);
        if (keys.length != 1) {
            return null;
        }
        return map[keys[0]];
    };
    return DirectoryContent;
}());
exports.DirectoryContent = DirectoryContent;
function extractMasterRef(filePath) {
    var raml = parser.loadRAMLSync(filePath, null);
    var extendsStr = raml.highLevel().attrValue("extends");
    if (!extendsStr) {
        return null;
    }
    var result = path.resolve(path.dirname(filePath), extendsStr);
    return result;
}
function extractContent(folderAbsPath) {
    if (!fs.lstatSync(folderAbsPath).isDirectory()) {
        return null;
    }
    var ramlFileNames = fs.readdirSync(folderAbsPath).filter(function (x) { return path.extname(x).toLowerCase() == ".raml"; });
    if (ramlFileNames.length == 0) {
        return null;
    }
    var ramlFilesAbsPaths = ramlFileNames.map(function (x) { return path.resolve(folderAbsPath, x); });
    var ramlFiles = [];
    for (var _i = 0, ramlFilesAbsPaths_1 = ramlFilesAbsPaths; _i < ramlFilesAbsPaths_1.length; _i++) {
        var f = ramlFilesAbsPaths_1[_i];
        var content = fs.readFileSync(f).toString();
        var ramlFirstLine = getRamlFirstLine(content);
        if (!ramlFirstLine || ramlFirstLine.length < 2) {
            continue;
        }
        var verStr = ramlFirstLine[1];
        var version = (verStr == "0.8") ? "RAML08" : "RAML10";
        var ramlFileType = "API";
        if (ramlFirstLine.length > 2 && ramlFirstLine[2].trim().length > 0) {
            ramlFileType = ramlFirstLine[2].toUpperCase();
        }
        var kind = RamlFileKind[ramlFileType];
        if (kind == null) {
            kind = RamlFileKind.FRAGMENT;
        }
        var extendsPath = null;
        if (kind == RamlFileKind.EXTENSION || kind == RamlFileKind.OVERLAY) {
            extendsPath = extractMasterRef(f);
        }
        var ramlFile = new RamlFile(f, kind, version, extendsPath);
        ramlFiles.push(ramlFile);
    }
    if (ramlFiles.length == 0) {
        return null;
    }
    return new DirectoryContent(folderAbsPath, ramlFiles);
}
exports.extractContent = extractContent;
function iterateFolder(folderAbsPath, result) {
    if (result === void 0) { result = []; }
    if (!fs.lstatSync(folderAbsPath).isDirectory()) {
        return;
    }
    var dirContent = extractContent(folderAbsPath);
    if (dirContent != null) {
        result.push(dirContent);
        return result;
    }
    for (var _i = 0, _a = fs.readdirSync(folderAbsPath); _i < _a.length; _i++) {
        var ch = _a[_i];
        var childAbsPath = path.resolve(folderAbsPath, ch);
        if (fs.lstatSync(childAbsPath).isDirectory()) {
            iterateFolder(childAbsPath, result);
        }
    }
    return result;
}
exports.iterateFolder = iterateFolder;
function defaultJSONPath(apiPath) {
    var dir = path.dirname(apiPath);
    var fileName = path.basename(apiPath).replace(".raml", "-outline.json");
    var str = path.resolve(dir, fileName);
    return str;
}
exports.defaultJSONPath = defaultJSONPath;
;
function orderExtensionsAndOverlaysByIndex(ramlFiles) {
    var indToFileMap = {};
    var pathToIndMap = {};
    for (var _i = 0, ramlFiles_1 = ramlFiles; _i < ramlFiles_1.length; _i++) {
        var rf = ramlFiles_1[_i];
        var fPath = rf.absolutePath();
        var fName = path.basename(fPath);
        var indStr = fName.replace(/([a-zA-Z]*)(\d*)(\.raml)/, "$2");
        indStr = indStr == "" ? "0" : "" + parseInt(indStr);
        var ind = parseInt(indStr);
        if (indToFileMap[indStr]) {
            return null;
        }
        indToFileMap[indStr] = rf;
        pathToIndMap[rf.absolutePath()] = ind;
    }
    var sorted = _.sortBy(ramlFiles, function (x) {
        return pathToIndMap[x.absolutePath()];
    });
    return sorted;
}
function getTests(dirContent) {
    var result = [];
    if (dirContent.hasCleanAPIsOnly()) {
        result = dirContent.masterAPIs().map(function (x) { return new Test(x.absolutePath()); });
    }
    else if (dirContent.hasSingleExtensionOrOverlay()) {
        result = dirContent.extensionsAndOverlays().map(function (x) {
            var jsonPath = defaultJSONPath(x.extends());
            return new Test(x.absolutePath(), null, jsonPath);
        });
    }
    else if (dirContent.hasLibraries() && dirContent.masterAPIs().length == 0) {
        result = dirContent.libraries().map(function (x) { return new Test(x.absolutePath()); });
    }
    else if (dirContent.hasFragmentsOnly()) {
        result = dirContent.fragments().map(function (x) { return new Test(x.absolutePath()); });
    }
    else if (dirContent.hasExtensionsOrOverlaysAppliedToSingleAPI()) {
        var ordered = orderExtensionsAndOverlaysByIndex(dirContent.extensionsAndOverlays());
        if (ordered) {
            var apiPath = ordered[0].extends();
            var extensionsAndOverlays = ordered.map(function (x) { return x.absolutePath(); });
            result = [new Test(apiPath, extensionsAndOverlays)];
        }
        else {
            var topExt = dirContent.topExtensionOrOverlay();
            if (topExt != null) {
                result = [new Test(topExt.absolutePath())];
            }
        }
    }
    return result;
}
exports.getTests = getTests;
function suiteTitle(absPath, dataRoot) {
    var title = absPath.substring(dataRoot.length);
    if (title.length > 0 && title.charAt(0) == "/") {
        title = title.substring(1);
    }
    return title;
}
function dumpSuite(title, dataRoot, tests, testMethodName) {
    var dumpedTests = tests.map(function (x) { return dumpTest(x, dataRoot, testMethodName); });
    var testsStr = dumpedTests.join("\n\n");
    return "describe('" + title + "',function(){\n    \n" + testsStr + "\n    \n});";
}
function dumpTest(test, dataRoot, testMethod) {
    var relMasterPath = path.relative(dataRoot, test.masterPath()).replace(/\\/g, '/');
    ;
    var args = ["\"" + relMasterPath + "\""];
    if (test.extensionsAndOverlays()) {
        var relArr = test.extensionsAndOverlays().map(function (x) { return path.relative(dataRoot, x).replace(/\\/g, '/'); });
        if (relArr.length > 0) {
            args.push("[ " + relArr.map(function (x) { return "\"" + x + "\""; }).join(", ") + " ]");
        }
    }
    var jsonPath = test.jsonPath() ? path.relative(dataRoot, test.jsonPath()).replace(/\\/g, '/') : null;
    if (jsonPath != null) {
        if (!test.extensionsAndOverlays()) {
            args.push("null");
        }
        args.push("\"" + jsonPath + "\"");
    }
    // var testMethod = 'testOutline';
    return "    it(\"" + path.basename(path.dirname(test.masterPath())) + "/" + path.basename(test.masterPath()) + "\", function () {\n        this.timeout(15000);\n        testSuiteUtil." + testMethod + "(" + args.join(", ") + ");\n    });";
}
var toIncludePath = function (workingFolder, absPath) {
    var relPath = path.relative(workingFolder, absPath).replace(/\\/g, "/");
    if (!relPath || relPath.charAt(0) != ".") {
        relPath = "./" + relPath;
    }
    return relPath;
};
function fileContent(suiteStrings, filePath, title) {
    var folder = projectFolder();
    var dstFolder = path.dirname(filePath);
    var suiteUtilPath = path.resolve(folder, "./src/test/scripts/testSuiteUtils");
    var typingsPath = path.resolve(folder, "typings/main.d.ts");
    var relSuiteUtilPath = toIncludePath(dstFolder, suiteUtilPath);
    var relTypingsPath = toIncludePath(dstFolder, typingsPath);
    return "/**\n * The file is generated. Manual changes will be overridden by the next build.\n */\n/// <reference path=\"" + relTypingsPath + "\" />\nimport testSuiteUtil = require(\"" + relSuiteUtilPath + "\")\n\ndescribe('" + title + "',function(){\n\n" + suiteStrings.join("\n\n") + "\n\n});\n\n";
}
;
function generateSuite(folderAbsPath, dstPath, dataRoot, mochaSuiteTitle, testMethodName) {
    var dirs = iterateFolder(folderAbsPath);
    var map = {};
    for (var _i = 0, dirs_1 = dirs; _i < dirs_1.length; _i++) {
        var dir = dirs_1[_i];
        var tests = getTests(dir);
        if (tests.length > 0) {
            var suiteFolder = path.resolve(dir.absolutePath(), "../").replace(/\\/g, '/');
            var arr = map[suiteFolder];
            if (!arr) {
                arr = [];
                map[suiteFolder] = arr;
            }
            for (var _a = 0, tests_1 = tests; _a < tests_1.length; _a++) {
                var t = tests_1[_a];
                arr.push(t);
            }
        }
    }
    var suitePaths = Object.keys(map).sort();
    var suiteStrings = [];
    for (var _b = 0, suitePaths_1 = suitePaths; _b < suitePaths_1.length; _b++) {
        var suitePath = suitePaths_1[_b];
        var title = suiteTitle(suitePath, folderAbsPath);
        if (title == null) {
            continue;
        }
        var suiteStr = dumpSuite(title, dataRoot, map[suitePath], testMethodName);
        suiteStrings.push(suiteStr);
    }
    var content = fileContent(suiteStrings, dstPath, mochaSuiteTitle);
    fs.writeFileSync(dstPath, content);
}
exports.generateSuite = generateSuite;
function sleep(milliseconds) {
    var start = new Date().getTime();
    while (true) {
        if ((new Date().getTime() - start) > milliseconds) {
            break;
        }
    }
}
exports.sleep = sleep;
function detailsPositionByRAMLFile(path) {
    var originalContent = fs.readFileSync(path).toString();
    return originalContent.indexOf("*");
}
function fixedDetailsRAML(path) {
    if (!fs.existsSync(path)) {
        return null;
    }
    try {
        var originalContent = fs.readFileSync(path).toString();
        var markerPosition = originalContent.indexOf("*");
        if (markerPosition == -1)
            return originalContent;
        var contentsStart = originalContent.substring(0, markerPosition);
        var contentsEnd = markerPosition < originalContent.length - 1 ? originalContent.substring(markerPosition + 1) : "";
        var resultContents = contentsStart + contentsEnd;
        return resultContents;
    }
    catch (e) {
        return null;
    }
}
function getDetailsJSONAsync(apiPath, callback) {
    apiPath = resolve(apiPath);
    var content = fixedDetailsRAML(apiPath);
    var position = detailsPositionByRAMLFile(apiPath);
    connection = index.getNodeClientConnection();
    // connection.setLoggerConfiguration({
    //
    //     allowedComponents: [
    //         "NodeProcessServerConnection",
    //         "DetailsManager",
    //         "server"
    //     ],
    //     maxSeverity: 0,
    //     maxMessageLength: 500
    // });
    // connection.setLoggerConfiguration({
    //     maxSeverity: 4,
    //     maxMessageLength: 50
    // });
    connection.setServerConfiguration({
        modulesConfiguration: {
            enableDetailsModule: true,
            enableCustomActionsModule: false
        }
    });
    connection.documentOpened({
        uri: apiPath,
        text: content
    });
    connection.getDetails(apiPath, position).then(function (result) {
        connection.documentClosed(apiPath);
        callback(result, null);
    }, function (ee) {
        callback(null, ee);
    });
}
function testDetails(apiPath, done, extensions, detailsJsonPath, regenerateJSON, callTests) {
    if (regenerateJSON === void 0) { regenerateJSON = false; }
    if (callTests === void 0) { callTests = true; }
    getDetailsJSONAsync(apiPath, function (result, error) {
        if (error) {
            done(error);
            return;
        }
        try {
            assert(testDetailsStructure(apiPath, result, extensions, detailsJsonPath, regenerateJSON, callTests));
            done();
        }
        catch (exception) {
            done(exception);
        }
    });
}
exports.testDetails = testDetails;
function testDetailsStructure(apiPath, json, extensions, detailsJsonPath, regenerateJSON, callTests) {
    if (regenerateJSON === void 0) { regenerateJSON = false; }
    if (callTests === void 0) { callTests = true; }
    if (apiPath) {
        apiPath = data(apiPath);
    }
    if (extensions) {
        extensions = extensions.map(function (x) { return data(x); });
    }
    if (!detailsJsonPath) {
        detailsJsonPath = defaultJSONPath(apiPath);
    }
    else {
        detailsJsonPath = data(detailsJsonPath);
    }
    if (!detailsJsonPath) {
        detailsJsonPath = defaultJSONPath(apiPath);
    }
    if (regenerateJSON) {
        serializeTestJSON(detailsJsonPath, json);
    }
    if (!fs.existsSync(detailsJsonPath)) {
        serializeTestJSON(detailsJsonPath, json);
        if (!callTests) {
            console.log("OUTLINE JSON GENERATED: " + detailsJsonPath);
            return;
        }
        console.warn("FAILED TO FIND OUTLINE JSON: " + detailsJsonPath);
    }
    if (!callTests) {
        return;
    }
    var outlineJson = readTestJSON(detailsJsonPath);
    var pathRegExp = new RegExp('/errors\\[\\d+\\]/path');
    var messageRegExp = new RegExp('/errors\\[\\d+\\]/message');
    var diff = compare(json, outlineJson).filter(function (x) {
        if (x.path.match(pathRegExp)) {
            return false;
        }
        return true;
    });
    var diffArr = [];
    if (diff.length == 0) {
        assert(true);
        return true;
    }
    else {
        console.log("DIFFERENCE DETECTED FOR " + detailsJsonPath);
        console.log(diff.map(function (x) { return x.message("actual", "expected"); }).join("\n\n"));
        console.log("ORIGINAL:");
        console.log(JSON.stringify(outlineJson, null, 2));
        console.log("TEST RESULT:");
        console.log(JSON.stringify(json, null, 2));
        assert(false);
        return false;
    }
}
exports.testDetailsStructure = testDetailsStructure;
//# sourceMappingURL=testSuiteUtils.js.map