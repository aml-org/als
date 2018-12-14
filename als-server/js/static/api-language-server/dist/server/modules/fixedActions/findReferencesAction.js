"use strict";
// This module provides a fixed action for finding references/usages of RAML node
Object.defineProperty(exports, "__esModule", { value: true });
var rp = require("raml-1-parser");
var search = rp.search;
var utils = require("../../../common/utils");
var fixedActionCommon = require("./fixedActionsCommon");
var selectionUtils = require("./selectionUtils");
function createManager(connection, astManagerModule, editorManagerModule) {
    return new FindReferencesActionModule(connection, astManagerModule, editorManagerModule);
}
exports.createManager = createManager;
var FindReferencesActionModule = (function () {
    function FindReferencesActionModule(connection, astManagerModule, editorManagerModule) {
        this.connection = connection;
        this.astManagerModule = astManagerModule;
        this.editorManagerModule = editorManagerModule;
    }
    FindReferencesActionModule.prototype.launch = function () {
        var _this = this;
        this.onFindReferencesListener = function (uri, position) {
            return _this.findReferences(uri, position);
        };
        this.connection.onFindReferences(this.onFindReferencesListener);
    };
    FindReferencesActionModule.prototype.dispose = function () {
        this.connection.onFindReferences(this.onFindReferencesListener, true);
    };
    /**
     * Returns unique module name.
     */
    FindReferencesActionModule.prototype.getModuleName = function () {
        return "FIND_REFERENCES_ACTION";
    };
    FindReferencesActionModule.prototype.findReferences = function (uri, position) {
        var _this = this;
        this.connection.debug("Called for uri: " + uri, "FixedActionsManager", "findReferences");
        this.connection.debugDetail("Uri extname: " + utils.extName(uri), "FixedActionsManager", "findReferences");
        if (utils.extName(uri) !== ".raml") {
            return Promise.resolve([]);
        }
        var connection = this.connection;
        return this.astManagerModule.forceGetCurrentAST(uri).then(function (ast) {
            connection.debugDetail("Found AST: " + (ast ? "true" : false), "FixedActionsManager", "findReferences");
            if (!ast) {
                return [];
            }
            var unit = ast.lowLevel().unit();
            var selection = selectionUtils.findSelection(ast.findElementAtOffset(position), position, unit.contents());
            var findUsagesResult = search.findUsages(unit, position);
            connection.debugDetail("Found usages: " + (findUsagesResult ? "true" : false), "FixedActionsManager", "findReferences");
            if (!findUsagesResult || !findUsagesResult.results) {
                return [];
            }
            connection.debugDetail("Number of found usages: " + findUsagesResult.results.length, "FixedActionsManager", "findReferences");
            var result = findUsagesResult.results.map(function (parseResult) {
                var resultUnit = parseResult.lowLevel().unit();
                var location = fixedActionCommon.lowLevelNodeToLocation(uri, parseResult.lowLevel(), _this.editorManagerModule, connection, true);
                var reducedRange = selectionUtils.reduce(resultUnit.contents(), selection, location.range);
                if (!reducedRange) {
                    return null;
                }
                return {
                    uri: location.uri,
                    range: reducedRange
                };
            }).filter(function (range) { return range; });
            var filtered = result[0] ? [result[0]] : [];
            result.forEach(function (location) {
                for (var i = 0; i < filtered.length; i++) {
                    if (location.range.start === filtered[i].range.start && location.range.end === filtered[i].range.end) {
                        return;
                    }
                }
                filtered.push(location);
            });
            connection.debugDetail("Usages are: " + JSON.stringify(result), "FixedActionsManager", "findReferences");
            return filtered;
        });
    };
    return FindReferencesActionModule;
}());
//# sourceMappingURL=findReferencesAction.js.map