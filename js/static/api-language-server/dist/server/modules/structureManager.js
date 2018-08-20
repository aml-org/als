"use strict";
// This module provides RAML module structure
Object.defineProperty(exports, "__esModule", { value: true });
var typeInterfaces_1 = require("../../common/typeInterfaces");
var rp = require("raml-1-parser");
var ramlOutline = require("raml-outline");
var outlineManagerCommons = require("./outlineManagerCommons");
var universes = rp.universes;
function createManager(connection, astManagerModule, editorManagerModule) {
    return new StructureManager(connection, astManagerModule, editorManagerModule);
}
exports.createManager = createManager;
var prohibit = {
    resources: true,
    schemas: true,
    types: true,
    resourceTypes: true,
    traits: true
};
function isResource(p) {
    return (p.definition().key() === universes.Universe08.Resource ||
        p.definition().key() === universes.Universe10.Resource);
}
exports.isResource = isResource;
function isOther(p) {
    if (p.property()) {
        var nm = p.property().nameId();
        if (prohibit[nm]) {
            return false;
        }
    }
    return true;
}
exports.isOther = isOther;
function isResourceTypeOrTrait(p) {
    var pc = p.definition().key();
    return (pc === universes.Universe08.ResourceType
        || pc === universes.Universe10.ResourceType ||
        pc === universes.Universe08.Trait
        ||
            pc === universes.Universe10.Trait);
}
exports.isResourceTypeOrTrait = isResourceTypeOrTrait;
function isSchemaOrType(p) {
    if (p.parent() && p.parent().parent() == null) {
        var property = p.property();
        return property.nameId() === universes.Universe10.LibraryBase.properties.types.name ||
            property.nameId() === universes.Universe10.LibraryBase.properties.schemas.name ||
            property.nameId() === universes.Universe08.Api.properties.schemas.name;
    }
    return false;
}
exports.isSchemaOrType = isSchemaOrType;
function createCategories() {
    ramlOutline.addCategoryFilter(typeInterfaces_1.StructureCategories[typeInterfaces_1.StructureCategories.ResourcesCategory], isResource);
    ramlOutline.addCategoryFilter(typeInterfaces_1.StructureCategories[typeInterfaces_1.StructureCategories.SchemasAndTypesCategory], isSchemaOrType);
    ramlOutline.addCategoryFilter(typeInterfaces_1.StructureCategories[typeInterfaces_1.StructureCategories.ResourceTypesAndTraitsCategory], isResourceTypeOrTrait);
    ramlOutline.addCategoryFilter(typeInterfaces_1.StructureCategories[typeInterfaces_1.StructureCategories.OtherCategory], isOther);
}
function createDecorations() {
    ramlOutline.addDecoration(ramlOutline.NodeType.ATTRIBUTE, {
        icon: typeInterfaces_1.Icons[typeInterfaces_1.Icons.ARROW_SMALL_LEFT],
        textStyle: typeInterfaces_1.TextStyles[typeInterfaces_1.TextStyles.NORMAL]
    });
    ramlOutline.addDecoration(ramlOutline.NodeType.RESOURCE, {
        icon: typeInterfaces_1.Icons[typeInterfaces_1.Icons.PRIMITIVE_SQUARE],
        textStyle: typeInterfaces_1.TextStyles[typeInterfaces_1.TextStyles.HIGHLIGHT]
    });
    ramlOutline.addDecoration(ramlOutline.NodeType.METHOD, {
        icon: typeInterfaces_1.Icons[typeInterfaces_1.Icons.PRIMITIVE_DOT],
        textStyle: typeInterfaces_1.TextStyles[typeInterfaces_1.TextStyles.WARNING]
    });
    ramlOutline.addDecoration(ramlOutline.NodeType.SECURITY_SCHEME, {
        icon: typeInterfaces_1.Icons[typeInterfaces_1.Icons.FILE_SUBMODULE],
        textStyle: typeInterfaces_1.TextStyles[typeInterfaces_1.TextStyles.NORMAL]
    });
    ramlOutline.addDecoration(ramlOutline.NodeType.ANNOTATION_DECLARATION, {
        icon: typeInterfaces_1.Icons[typeInterfaces_1.Icons.TAG],
        textStyle: typeInterfaces_1.TextStyles[typeInterfaces_1.TextStyles.HIGHLIGHT]
    });
    ramlOutline.addDecoration(ramlOutline.NodeType.TYPE_DECLARATION, {
        icon: typeInterfaces_1.Icons[typeInterfaces_1.Icons.FILE_BINARY],
        textStyle: typeInterfaces_1.TextStyles[typeInterfaces_1.TextStyles.SUCCESS]
    });
    ramlOutline.addDecoration(ramlOutline.NodeType.DOCUMENTATION_ITEM, {
        icon: typeInterfaces_1.Icons[typeInterfaces_1.Icons.BOOK],
        textStyle: typeInterfaces_1.TextStyles[typeInterfaces_1.TextStyles.NORMAL]
    });
}
function initialize() {
    outlineManagerCommons.initialize();
    createCategories();
    createDecorations();
}
exports.initialize = initialize;
initialize();
var StructureManager = (function () {
    function StructureManager(connection, astManagerModule, editorManagerModule) {
        this.connection = connection;
        this.astManagerModule = astManagerModule;
        this.editorManagerModule = editorManagerModule;
        this.calculatingStructureOnDirectRequest = false;
        this.cachedStructures = {};
    }
    StructureManager.prototype.launch = function () {
        var _this = this;
        this.onDocumentStructureListener = function (uri) {
            return _this.getStructure(uri);
        };
        this.connection.onDocumentStructure(this.onDocumentStructureListener);
        this.onNewASTAvailableListener = function (uri, version, ast) {
            // we do not want reporting while performing the calculation
            if (_this.calculatingStructureOnDirectRequest) {
                return;
            }
            _this.connection.debug("Calculating structure due to new AST available", "StructureManager", "listen");
            _this.calculateStructure(uri).then(function (structureForUri) {
                _this.connection.debug("Calculation result is not null:" +
                    (structureForUri != null ? "true" : "false"), "StructureManager", "listen");
                if (structureForUri) {
                    _this.cachedStructures[uri] = structureForUri;
                    _this.connection.structureAvailable({
                        uri: uri,
                        version: version,
                        structure: structureForUri
                    });
                }
            });
        };
        this.astManagerModule.onNewASTAvailable(this.onNewASTAvailableListener);
        this.onCloseDocumentListener = function (uri) { return delete _this.cachedStructures[uri]; };
        this.connection.onCloseDocument(this.onCloseDocumentListener);
    };
    StructureManager.prototype.dispose = function () {
        this.connection.debugDetail("Disposing the module", "StructureManager", "dispose");
        this.connection.onDocumentStructure(this.onDocumentStructureListener, true);
        this.astManagerModule.onNewASTAvailable(this.onNewASTAvailableListener, true);
        this.connection.onCloseDocument(this.onCloseDocumentListener, true);
    };
    /**
     * Returns unique module name.
     */
    StructureManager.prototype.getModuleName = function () {
        return "STRUCTURE_MANAGER";
    };
    StructureManager.prototype.vsCodeUriToParserUri = function (vsCodeUri) {
        if (vsCodeUri.indexOf("file://") === 0) {
            return vsCodeUri.substring(7);
        }
        return vsCodeUri;
    };
    StructureManager.prototype.getStructure = function (uri) {
        var _this = this;
        this.connection.debug("Requested structure for uri " + uri, "StructureManager", "getStructure");
        var cached = this.cachedStructures[uri];
        this.connection.debug("Found cached structure: " + (cached ? "true" : "false"), "StructureManager", "getStructure");
        if (cached) {
            return Promise.resolve(cached);
        }
        this.connection.debug("Calculating structure due to getStructure request and no cached version found", "StructureManager", "getStructure");
        this.calculatingStructureOnDirectRequest = true;
        return this.calculateStructure(uri).then(function (calculated) {
            try {
                _this.connection.debug("Calculation result is not null:" +
                    (calculated != null ? "true" : "false"), "StructureManager", "getStructure");
                _this.cachedStructures[uri] = calculated;
                return calculated;
            }
            finally {
                _this.calculatingStructureOnDirectRequest = false;
            }
        }).catch(function (error) {
            _this.calculatingStructureOnDirectRequest = false;
            throw error;
        });
    };
    StructureManager.prototype.calculateStructure = function (uri) {
        var _this = this;
        this.connection.debug("Called for uri: " + uri, "StructureManager", "calculateStructure");
        // Forcing current AST to exist
        return this.astManagerModule.forceGetCurrentAST(uri).then(function (currentAST) {
            outlineManagerCommons.setOutlineASTProvider(uri, _this.astManagerModule, _this.editorManagerModule, _this.connection);
            var result = ramlOutline.getStructureForAllCategories();
            var jsonResult = {};
            if (result) {
                for (var categoryName in result) {
                    if (result.hasOwnProperty(categoryName)) {
                        var categoryJSON = result[categoryName];
                        jsonResult[categoryName] = categoryJSON.toJSON();
                        if (categoryJSON) {
                            _this.connection.debugDetail("Structure for category " + categoryName + "\n"
                                + JSON.stringify(categoryJSON, null, 2), "StructureManager", "calculateStructure");
                        }
                    }
                }
            }
            _this.connection.debug("Calculation result is not null:" +
                (result != null ? "true" : "false"), "StructureManager", "calculateStructure");
            return jsonResult;
        });
    };
    return StructureManager;
}());
//# sourceMappingURL=structureManager.js.map