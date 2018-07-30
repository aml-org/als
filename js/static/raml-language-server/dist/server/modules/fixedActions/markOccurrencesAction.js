"use strict";
// This module provides a fixed action for finding occurrences of RAML node
Object.defineProperty(exports, "__esModule", { value: true });
var rp = require("raml-1-parser");
var search = rp.search;
var utils = require("../../../common/utils");
var fixedActionCommon = require("./fixedActionsCommon");
var openDeclarationsModule = require("./openDeclarationAction");
var selectionUtils = require("./selectionUtils");
function createManager(connection, astManagerModule, editorManagerModule) {
    return new MarkOccurrencesActionModule(connection, astManagerModule, editorManagerModule);
}
exports.createManager = createManager;
var MarkOccurrencesActionModule = /** @class */ (function () {
    function MarkOccurrencesActionModule(connection, astManagerModule, editorManagerModule) {
        this.connection = connection;
        this.astManagerModule = astManagerModule;
        this.editorManagerModule = editorManagerModule;
    }
    MarkOccurrencesActionModule.prototype.launch = function () {
        var _this = this;
        this.onMarkOccurrencesListener = function (uri, position) {
            return _this.markOccurrences(uri, position);
        };
        this.connection.onMarkOccurrences(this.onMarkOccurrencesListener);
    };
    MarkOccurrencesActionModule.prototype.dispose = function () {
        this.connection.onMarkOccurrences(this.onMarkOccurrencesListener, true);
    };
    /**
     * Returns unique module name.
     */
    MarkOccurrencesActionModule.prototype.getModuleName = function () {
        return "MARK_OCCURRENCES_ACTION";
    };
    MarkOccurrencesActionModule.prototype.markOccurrences = function (uri, position) {
        var _this = this;
        this.connection.debug("Called for uri: " + uri, "FixedActionsManager", "markOccurrences");
        this.connection.debugDetail("Uri extname: " + utils.extName(uri), "FixedActionsManager", "markOccurrences");
        if (utils.extName(uri) !== ".raml") {
            return Promise.resolve([]);
        }
        return this.astManagerModule.forceGetCurrentAST(uri).then(function (ast) {
            _this.connection.debugDetail("Found AST: " + (ast ? "true" : false), "FixedActionsManager", "markOccurrences");
            if (!ast) {
                return [];
            }
            var unit = ast.lowLevel().unit();
            // TODO both search and declaration unit filtering is better to be moved directly to the search module
            // in order to save CPU by not checking external units and just be flagged here
            var node = ast.findElementAtOffset(position);
            if (!node) {
                return [];
            }
            var selectionValue = selectionUtils.findSelection(node, position, unit.contents());
            if (!selectionValue) {
                _this.connection.debugDetail("Filtering out node, returning", "FixedActionsManager", "markOccurrences");
                _this.connection.debugDetail("Node:\n" + node.printDetails(), "FixedActionsManager", "markOccurrences");
                return [];
            }
            var findUsagesResult = search.findUsages(unit, position);
            _this.connection.debugDetail("Found usages: " + (findUsagesResult ? "true" : false), "FixedActionsManager", "markOccurrences");
            var unfiltered = [];
            if (findUsagesResult && findUsagesResult.results) {
                _this.connection.debugDetail("Number of found usages: " + findUsagesResult.results.length, "FixedActionsManager", "markOccurrences");
                unfiltered = unfiltered.concat(findUsagesResult.results.map(function (parseResult) {
                    return fixedActionCommon.lowLevelNodeToLocation(uri, parseResult.lowLevel(), _this.editorManagerModule, _this.connection, true);
                }));
            }
            var findUsagesLocations = unfiltered;
            return openDeclarationsModule.createManager(_this.connection, _this.astManagerModule, _this.editorManagerModule).openDeclaration(uri, position).then(function (declarations) {
                _this.connection.debugDetail("Number of found declarations: " + declarations.length, "FixedActionsManager", "markOccurrences");
                var locations = findUsagesLocations;
                if (declarations) {
                    locations = locations.concat(declarations);
                }
                var result = [];
                _this.connection.debugDetail("Unfiltered occurrences: " + JSON.stringify(locations), "FixedActionsManager", "markOccurrences");
                result = locations.filter(function (location) {
                    return location.uri === uri;
                }).filter(function (location) {
                    // excluding any mentions of whatever is located at the position itself
                    // as its not what user is interested with
                    return location.range.start > position || location.range.end < position;
                }).map(function (location) {
                    return location.range;
                }).map(function (range) { return selectionUtils.reduce(unit.contents(), selectionValue, range); }).filter(function (range) { return range; }).filter(function (range) {
                    return range.start > position || range.end < position;
                });
                _this.connection.debugDetail("Found occurrences result: " + JSON.stringify(result), "FixedActionsManager", "markOccurrences");
                var filtered = result[0] ? [result[0]] : [];
                result.forEach(function (range) {
                    for (var i = 0; i < filtered.length; i++) {
                        if (range.start === filtered[i].start && range.end === filtered[i].end) {
                            return;
                        }
                    }
                    filtered.push(range);
                });
                return filtered;
            });
        });
    };
    return MarkOccurrencesActionModule;
}());
//# sourceMappingURL=markOccurrencesAction.js.map