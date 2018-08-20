"use strict";
// This module provides a fixed action for renaming RAML node
Object.defineProperty(exports, "__esModule", { value: true });
var parserApi = require("raml-1-parser");
var search = parserApi.search;
var universes = parserApi.universes;
var def = parserApi.ds;
var stubs = parserApi.stubs;
var utils = require("../../../common/utils");
function createManager(connection, astManagerModule, editorManagerModule) {
    return new RenameActionModule(connection, astManagerModule, editorManagerModule);
}
exports.createManager = createManager;
var RenameActionModule = (function () {
    function RenameActionModule(connection, astManagerModule, editorManagerModule) {
        this.connection = connection;
        this.astManagerModule = astManagerModule;
        this.editorManagerModule = editorManagerModule;
    }
    RenameActionModule.prototype.launch = function () {
        var _this = this;
        this.onRenameListener = function (uri, position, newName) {
            var result = _this.rename(uri, position, newName);
            _this.connection.debugDetail("Renaming result for uri: " + uri, "RenameActionModule", "onRename");
            if (result.length >= 1) {
                _this.connection.debugDetail("Text:\n" + result[0].text, "RenameActionModule", "onRename");
            }
            return result;
        };
        this.connection.onRename(this.onRenameListener);
    };
    RenameActionModule.prototype.dispose = function () {
        this.connection.onRename(this.onRenameListener, true);
    };
    /**
     * Returns unique module name.
     */
    RenameActionModule.prototype.getModuleName = function () {
        return "RENAME_ACTION";
    };
    RenameActionModule.prototype.rename = function (uri, position, newName) {
        var _this = this;
        this.connection.debug("Called for uri: " + uri, "RenameActionModule", "rename");
        var editor = this.editorManagerModule.getEditor(uri);
        this.connection.debugDetail("Got editor: " + (editor ? "true" : "false"), "RenameActionModule", "rename");
        if (!editor) {
            return [];
        }
        var node = this.getAstNode(uri, editor.getText(), position, false);
        this.connection.debugDetail("Got node: " + (node ? "true" : "false"), "RenameActionModule", "rename");
        if (!node) {
            return [];
        }
        var kind = search.determineCompletionKind(editor.getText(), position);
        this.connection.debugDetail("Determined completion kind: " + kind, "RenameActionModule", "rename");
        if (kind === search.LocationKind.VALUE_COMPLETION) {
            var hlnode = node;
            var attr = null;
            for (var _i = 0, _a = hlnode.attrs(); _i < _a.length; _i++) {
                var attribute = _a[_i];
                if (attribute.lowLevel().start() < position
                    && attribute.lowLevel().end() >= position
                    && !attribute.property().getAdapter(def.RAMLPropertyService).isKey()) {
                    this.connection.debugDetail("Found attribute: " + attribute.name() +
                        " its key property is: " + attribute.property().getAdapter(def.RAMLPropertyService).isKey(), "RenameActionModule", "rename");
                    attr = attribute;
                    break;
                }
            }
            this.connection.debugDetail("Found attribute: " + (attr ? "true" : "false"), "RenameActionModule", "rename");
            if (attr) {
                this.connection.debugDetail("Current attribute name is: " + attr.name(), "RenameActionModule", "rename");
                this.connection.debugDetail("Current attribute value is: " + attr.value(), "RenameActionModule", "rename");
                if (attr.value()) {
                    var p = attr.property();
                    var v = attr.value();
                    var targets = search.referenceTargets(p, hlnode);
                    var t = null;
                    for (var _b = 0, targets_1 = targets; _b < targets_1.length; _b++) {
                        var target = targets_1[_b];
                        if (target.name() === attr.value()) {
                            t = target;
                            break;
                        }
                    }
                    if (t) {
                        this.connection.debugDetail("Found target: " + t.printDetails(), "RenameActionModule", "rename");
                        var findUsagesResult = search.findUsages(node.lowLevel().unit(), position);
                        if (findUsagesResult) {
                            var usages = findUsagesResult.results;
                            usages.reverse().forEach(function (usageAttribute) {
                                _this.connection.debugDetail("Renaming usage attribute: "
                                    + usageAttribute.name() + " of node:\n"
                                    + usageAttribute.parent().printDetails(), "RenameActionModule", "rename");
                                usageAttribute.asAttr().setValue(newName);
                            });
                            t.attr(hlnode.definition().getAdapter(def.RAMLService).getKeyProp().nameId()).setValue(newName);
                            return [{
                                    uri: uri,
                                    text: hlnode.lowLevel().unit().contents()
                                }];
                        }
                    }
                }
            }
        }
        if (kind === search.LocationKind.KEY_COMPLETION || kind === search.LocationKind.SEQUENCE_KEY_COPLETION) {
            var hlnode = node;
            var findUsagesResult = search.findUsages(node.lowLevel().unit(), position);
            if (findUsagesResult) {
                var oldValue_1 = hlnode.attrValue(hlnode.definition().getAdapter(def.RAMLService).getKeyProp().nameId());
                var filtered_1 = [];
                findUsagesResult.results.reverse().forEach(function (usage) {
                    var hasConflicting = false;
                    for (var _i = 0, filtered_2 = filtered_1; _i < filtered_2.length; _i++) {
                        var current = filtered_2[_i];
                        var currentLowLevel = current.lowLevel();
                        if (!currentLowLevel) {
                            continue;
                        }
                        var currentStart = currentLowLevel.start();
                        var currentEnd = currentLowLevel.end();
                        var usageLowLevel = usage.lowLevel();
                        if (!usageLowLevel) {
                            continue;
                        }
                        var usageStart = usageLowLevel.start();
                        var usageEnd = usageLowLevel.end();
                        if (usageStart <= currentEnd && usageEnd >= currentStart) {
                            hasConflicting = true;
                            break;
                        }
                    }
                    if (!hasConflicting) {
                        filtered_1.push(usage);
                    }
                });
                filtered_1.forEach(function (x) {
                    _this.renameInProperty(x.asAttr(), oldValue_1, newName);
                });
                hlnode.attr(hlnode.definition().getAdapter(def.RAMLService).getKeyProp().nameId()).setValue(newName);
                return [{
                        uri: uri,
                        text: hlnode.lowLevel().unit().contents()
                    }];
            }
        }
        return [];
    };
    RenameActionModule.prototype.renameInProperty = function (property, contentToReplace, replaceWith) {
        var oldPropertyValue = property.value();
        if (typeof oldPropertyValue === "string") {
            var oldPropertyStringValue = oldPropertyValue;
            var newPropertyStringValue = oldPropertyStringValue.replace(contentToReplace, replaceWith);
            property.setValue(newPropertyStringValue);
            if (oldPropertyStringValue.indexOf(contentToReplace) === -1) {
                if (property.name().indexOf(contentToReplace) !== -1) {
                    var newValue = property.name().replace(contentToReplace, replaceWith);
                    property.setKey(newValue);
                }
            }
            return;
        }
        else if (oldPropertyValue && (typeof oldPropertyValue === "object")) {
            var structuredValue = oldPropertyValue;
            var oldPropertyStringValue = structuredValue.valueName();
            if (oldPropertyStringValue.indexOf(contentToReplace) !== -1) {
                var convertedHighLevel = structuredValue.toHighLevel();
                if (convertedHighLevel) {
                    var found_1 = false;
                    if (convertedHighLevel.definition().isAnnotationType()) {
                        var prop = this.getKey(convertedHighLevel.definition(), structuredValue.lowLevel());
                        prop.setValue("(" + replaceWith + ")");
                        return;
                    }
                    convertedHighLevel.attrs().forEach(function (attribute) {
                        if (attribute.property().getAdapter(def.RAMLPropertyService).isKey()) {
                            var oldValue = attribute.value();
                            if (typeof oldValue === "string") {
                                found_1 = true;
                                var newValue = oldValue.replace(contentToReplace, replaceWith);
                                attribute.setValue(newValue);
                            }
                        }
                    });
                    return;
                }
            }
        }
        // default case
        property.setValue(replaceWith);
    };
    RenameActionModule.prototype.getAstNode = function (uri, text, offset, clearLastChar) {
        if (clearLastChar === void 0) { clearLastChar = true; }
        var unitPath = utils.pathFromURI(uri);
        var newProjectId = utils.dirname(unitPath);
        var project = parserApi.project.createProject(newProjectId);
        var kind = search.determineCompletionKind(text, offset);
        if (kind === parserApi.search.LocationKind.KEY_COMPLETION && clearLastChar) {
            text = text.substring(0, offset) + "k:" + text.substring(offset);
        }
        var unit = project.setCachedUnitContent(unitPath, text);
        var ast = unit.highLevel();
        var actualOffset = offset;
        for (var currentOffset = offset - 1; currentOffset >= 0; currentOffset--) {
            var symbol = text[currentOffset];
            if (symbol === " " || symbol === "\t") {
                actualOffset = currentOffset - 1;
                continue;
            }
            break;
        }
        var astNode = ast.findElementAtOffset(actualOffset);
        if (astNode && search.isExampleNode(astNode)) {
            var exampleEnd = astNode.lowLevel().end();
            if (exampleEnd === actualOffset && text[exampleEnd] === "\n") {
                astNode = astNode.parent();
            }
        }
        return astNode;
    };
    RenameActionModule.prototype.getKey = function (t, n) {
        var up = new def.UserDefinedProp("name", null);
        var ramlService = t.getAdapter(def.RAMLService);
        up.withRange(ramlService.universe().type(universes.Universe10.StringType.name));
        up.withFromParentKey(true);
        var node = ramlService.getDeclaringNode();
        return stubs.createASTPropImpl(n, node, up.range(), up, true);
    };
    return RenameActionModule;
}());
//# sourceMappingURL=renameAction.js.map