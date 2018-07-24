// This module provides RAML module structure

import {
    IServerConnection
} from "../core/connections";

import {
    IASTManagerModule
} from "./astManager";

import {
    IEditorManagerModule
} from "./editorManager";

import {
    Icons,
    ILogger,
    IValidationIssue,
    StructureCategories,
    StructureNodeJSON,
    TextStyles
} from "../../common/typeInterfaces";

import {
    IDisposableModule
} from "./commonInterfaces";

import rp= require("raml-1-parser");
import lowLevel= rp.ll;
import hl= rp.hl;
import utils = rp.utils;
import ramlOutline = require("raml-outline");
import outlineManagerCommons = require("./outlineManagerCommons");

const universes = rp.universes;

export function createManager(connection: IServerConnection,
                              astManagerModule: IASTManagerModule,
                              editorManagerModule: IEditorManagerModule): IDisposableModule {

    return new StructureManager(connection, astManagerModule, editorManagerModule);
}

const prohibit = {
    resources: true,
    schemas: true,
    types: true,
    resourceTypes: true,
    traits: true
};

export function isResource(p: hl.IHighLevelNode) {
    return (p.definition().key() === universes.Universe08.Resource ||
            p.definition().key() === universes.Universe10.Resource);
}

export function isOther(p: hl.IHighLevelNode) {
    if (p.property()) {
        const nm = p.property().nameId();
        if (prohibit[nm]) {
            return false;
        }
    }
    return true;
}
export function isResourceTypeOrTrait(p: hl.IHighLevelNode) {
    const pc = p.definition().key();

    return (pc === universes.Universe08.ResourceType
    || pc === universes.Universe10.ResourceType ||
    pc === universes.Universe08.Trait
    ||
    pc === universes.Universe10.Trait);
}

export function isSchemaOrType(p: hl.IHighLevelNode) {

    if (p.parent() && p.parent().parent() == null) {
        const property = p.property();

        return property.nameId() === universes.Universe10.LibraryBase.properties.types.name ||
            property.nameId() === universes.Universe10.LibraryBase.properties.schemas.name ||
            property.nameId() === universes.Universe08.Api.properties.schemas.name;
    }

    return false;
}

function createCategories(): void {
    ramlOutline.addCategoryFilter(
        StructureCategories[StructureCategories.ResourcesCategory], isResource as any);
    ramlOutline.addCategoryFilter(
        StructureCategories[StructureCategories.SchemasAndTypesCategory], isSchemaOrType as any);
    ramlOutline.addCategoryFilter(
        StructureCategories[StructureCategories.ResourceTypesAndTraitsCategory], isResourceTypeOrTrait as any);
    ramlOutline.addCategoryFilter(
        StructureCategories[StructureCategories.OtherCategory], isOther as any);
}

function createDecorations(): void {
    ramlOutline.addDecoration(ramlOutline.NodeType.ATTRIBUTE, {
        icon: Icons[Icons.ARROW_SMALL_LEFT],
        textStyle: TextStyles[TextStyles.NORMAL]
    });

    ramlOutline.addDecoration(ramlOutline.NodeType.RESOURCE, {
        icon: Icons[Icons.PRIMITIVE_SQUARE],
        textStyle: TextStyles[TextStyles.HIGHLIGHT]
    });

    ramlOutline.addDecoration(ramlOutline.NodeType.METHOD, {
        icon: Icons[Icons.PRIMITIVE_DOT],
        textStyle: TextStyles[TextStyles.WARNING]
    });

    ramlOutline.addDecoration(ramlOutline.NodeType.SECURITY_SCHEME, {
        icon: Icons[Icons.FILE_SUBMODULE],
        textStyle: TextStyles[TextStyles.NORMAL]
    });

    ramlOutline.addDecoration(ramlOutline.NodeType.ANNOTATION_DECLARATION, {
        icon: Icons[Icons.TAG],
        textStyle: TextStyles[TextStyles.HIGHLIGHT]
    });

    ramlOutline.addDecoration(ramlOutline.NodeType.TYPE_DECLARATION, {
        icon: Icons[Icons.FILE_BINARY],
        textStyle: TextStyles[TextStyles.SUCCESS]
    });

    ramlOutline.addDecoration(ramlOutline.NodeType.DOCUMENTATION_ITEM, {
        icon: Icons[Icons.BOOK],
        textStyle: TextStyles[TextStyles.NORMAL]
    });
}

export function initialize() {
    outlineManagerCommons.initialize();

    createCategories();

    createDecorations();
}

initialize();

class StructureManager implements IDisposableModule {

    private calculatingStructureOnDirectRequest = false;

    private cachedStructures: {[uri: string]: {[categoryName: string]: StructureNodeJSON}} = {};

    private onDocumentStructureListener;

    private onNewASTAvailableListener;

    private onCloseDocumentListener;

    constructor(private connection: IServerConnection,
                private astManagerModule: IASTManagerModule,
                private editorManagerModule: IEditorManagerModule) {
    }

    public launch() {
        this.onDocumentStructureListener = (uri) => {
            return this.getStructure(uri);
        }

        this.connection.onDocumentStructure(this.onDocumentStructureListener);

        this.onNewASTAvailableListener = (uri: string, version: number, ast: hl.IHighLevelNode) => {

            // we do not want reporting while performing the calculation
            if (this.calculatingStructureOnDirectRequest) {
                return;
            }

            this.connection.debug("Calculating structure due to new AST available", "StructureManager",
                "listen");

            this.calculateStructure(uri).then((structureForUri) => {
                this.connection.debug("Calculation result is not null:" +
                    (structureForUri != null ? "true" : "false"), "StructureManager",
                    "listen");

                if (structureForUri) {
                    this.cachedStructures[uri] = structureForUri;

                    this.connection.structureAvailable({
                        uri,
                        version,
                        structure: structureForUri
                    });
                }
            });

        }

        this.astManagerModule.onNewASTAvailable(this.onNewASTAvailableListener);

        this.onCloseDocumentListener = (uri) => delete this.cachedStructures[uri];
        this.connection.onCloseDocument(this.onCloseDocumentListener);
    }

    public dispose(): void {

        this.connection.debugDetail("Disposing the module", "StructureManager",
            "dispose");

        this.connection.onDocumentStructure(this.onDocumentStructureListener, true);

        this.astManagerModule.onNewASTAvailable(this.onNewASTAvailableListener, true);

        this.connection.onCloseDocument(this.onCloseDocumentListener, true);
    }

    /**
     * Returns unique module name.
     */
    public getModuleName(): string {
        return "STRUCTURE_MANAGER";
    }

    public vsCodeUriToParserUri(vsCodeUri: string): string {
        if (vsCodeUri.indexOf("file://") === 0) {
            return vsCodeUri.substring(7);
        }

        return vsCodeUri;
    }

    public getStructure(uri: string): Promise<{[categoryName: string]: StructureNodeJSON}> {
        this.connection.debug("Requested structure for uri " + uri, "StructureManager",
            "getStructure");

        const cached = this.cachedStructures[uri];

        this.connection.debug("Found cached structure: " + (cached ? "true" : "false"), "StructureManager",
            "getStructure");

        if (cached) {
            return Promise.resolve(cached);
        }

        this.connection.debug(
            "Calculating structure due to getStructure request and no cached version found",
            "StructureManager",
            "getStructure");

        this.calculatingStructureOnDirectRequest = true;

        return this.calculateStructure(uri).then((calculated) => {
            try {

                this.connection.debug("Calculation result is not null:" +
                    (calculated != null ? "true" : "false"), "StructureManager",
                    "getStructure");

                this.cachedStructures[uri] = calculated;

                return calculated;

            } finally {
                this.calculatingStructureOnDirectRequest = false;
            }
        }).catch((error) => {
            this.calculatingStructureOnDirectRequest = false;
            throw error;
        });
    }

    public calculateStructure(uri: string): Promise<{[categoryName: string]: StructureNodeJSON}> {

        this.connection.debug("Called for uri: " + uri,
            "StructureManager", "calculateStructure");

        // Forcing current AST to exist
        return this.astManagerModule.forceGetCurrentAST(uri).then((currentAST) => {

            outlineManagerCommons.setOutlineASTProvider(uri, this.astManagerModule,
                                                        this.editorManagerModule,
                                                        this.connection);

            const result = ramlOutline.getStructureForAllCategories();

            const jsonResult = {};
            if (result) {
                for (const categoryName in result) {
                    if (result.hasOwnProperty(categoryName)) {
                        const categoryJSON = result[categoryName];

                        jsonResult[categoryName] = categoryJSON.toJSON();

                        if (categoryJSON) {
                            this.connection.debugDetail("Structure for category " + categoryName + "\n"
                                + JSON.stringify(categoryJSON, null, 2), "StructureManager", "calculateStructure");
                        }
                    }
                }
            }

            this.connection.debug("Calculation result is not null:" +
                (result != null ? "true" : "false"), "StructureManager",
                "calculateStructure");

            return jsonResult;
        });
    }
}
