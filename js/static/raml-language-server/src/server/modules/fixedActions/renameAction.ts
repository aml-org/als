// This module provides a fixed action for renaming RAML node

import {
    IServerConnection
} from "../../core/connections";

import {
    IASTManagerModule
} from "../astManager";

import {
    IEditorManagerModule
} from "../editorManager";

import {
    IChangedDocument,
    ILocation,
    IRange
} from "../../../common/typeInterfaces";

import parserApi= require("raml-1-parser");
import search = parserApi.search;
import lowLevel= parserApi.ll;
import hl= parserApi.hl;
import universes= parserApi.universes;
import def= parserApi.ds;
import stubs= parserApi.stubs;

import {
    IDisposableModule
} from "../../modules/commonInterfaces";

import utils = require("../../../common/utils");
import fixedActionCommon = require("./fixedActionsCommon");

export function createManager(connection: IServerConnection,
                              astManagerModule: IASTManagerModule,
                              editorManagerModule: IEditorManagerModule)
                        : IDisposableModule {

    return new RenameActionModule(connection, astManagerModule, editorManagerModule);
}

class RenameActionModule implements IDisposableModule {

    private onRenameListener;

    constructor(private connection: IServerConnection, private astManagerModule: IASTManagerModule,
                private editorManagerModule: IEditorManagerModule) {
    }

    public launch() {

        this.onRenameListener = (uri: string, position: number, newName: string) => {
            const result = this.rename(uri, position, newName);

            this.connection.debugDetail("Renaming result for uri: " + uri,
                "RenameActionModule", "onRename");

            if (result.length >= 1) {
                this.connection.debugDetail("Text:\n" + result[0].text,
                    "RenameActionModule", "onRename");
            }

            return result;
        }
        this.connection.onRename(this.onRenameListener);
    }

    public dispose(): void {
        this.connection.onRename(this.onRenameListener, true);
    }

    /**
     * Returns unique module name.
     */
    public getModuleName(): string {
        return "RENAME_ACTION";
    }

    private rename(uri: string, position: number, newName: string): IChangedDocument[] {
        this.connection.debug("Called for uri: " + uri,
            "RenameActionModule", "rename");

        const editor = this.editorManagerModule.getEditor(uri);
        this.connection.debugDetail("Got editor: " + (editor ? "true" : "false"),
            "RenameActionModule", "rename");

        if (!editor) {
            return [];
        }

        const node = this.getAstNode(uri, editor.getText(), position, false);

        this.connection.debugDetail("Got node: " + (node ? "true" : "false"),
            "RenameActionModule", "rename");

        if (!node) {
            return [];
        }

        const kind = search.determineCompletionKind(editor.getText(), position);

        this.connection.debugDetail("Determined completion kind: " + kind,
            "RenameActionModule", "rename");

        if (kind === search.LocationKind.VALUE_COMPLETION) {
            const hlnode = node as hl.IHighLevelNode;

            let attr = null;
            for (const attribute of hlnode.attrs()) {
                if (attribute.lowLevel().start() < position
                    && attribute.lowLevel().end() >= position
                    && !attribute.property().getAdapter(def.RAMLPropertyService).isKey()) {

                    this.connection.debugDetail("Found attribute: " + attribute.name() +
                        " its key property is: " + attribute.property().getAdapter(def.RAMLPropertyService).isKey(),
                        "RenameActionModule", "rename");
                    attr = attribute;
                    break;
                }
            }

            this.connection.debugDetail("Found attribute: " + (attr ? "true" : "false"),
                "RenameActionModule", "rename");

            if (attr) {
                this.connection.debugDetail("Current attribute name is: " + attr.name(),
                    "RenameActionModule", "rename");

                this.connection.debugDetail("Current attribute value is: " + attr.value(),
                    "RenameActionModule", "rename");

                if (attr.value()) {
                    const p: hl.IProperty = attr.property();

                    const v = attr.value();
                    const targets = search.referenceTargets(p, hlnode);
                    let t: hl.IHighLevelNode = null;
                    for (const target of targets) {
                        if (target.name() === attr.value()) {
                            t = target;
                            break;
                        }
                    }

                    if (t) {
                        this.connection.debugDetail("Found target: " + t.printDetails(),
                            "RenameActionModule", "rename");

                        const findUsagesResult = search.findUsages(node.lowLevel().unit(), position);
                        if (findUsagesResult) {
                            const usages = findUsagesResult.results;

                            usages.reverse().forEach((usageAttribute) => {

                                this.connection.debugDetail("Renaming usage attribute: "
                                    + usageAttribute.name() + " of node:\n"
                                    + usageAttribute.parent().printDetails(),
                                    "RenameActionModule", "rename");

                                usageAttribute.asAttr().setValue(newName);
                            });

                            t.attr(
                                hlnode.definition().getAdapter(def.RAMLService).getKeyProp().nameId()
                            ).setValue(newName);

                            return [{
                                uri,
                                text: hlnode.lowLevel().unit().contents()
                            }];
                        }
                    }
                }
            }
        }
        if (kind === search.LocationKind.KEY_COMPLETION || kind === search.LocationKind.SEQUENCE_KEY_COPLETION) {
            const hlnode = node as hl.IHighLevelNode;

            const findUsagesResult = search.findUsages(node.lowLevel().unit(), position);
            if (findUsagesResult) {
                const oldValue = hlnode.attrValue(
                    hlnode.definition().getAdapter(def.RAMLService).getKeyProp().nameId());

                const filtered: hl.IParseResult[] = [];
                findUsagesResult.results.reverse().forEach((usage) => {

                    let hasConflicting = false;

                    for (const current of filtered) {
                        const currentLowLevel = current.lowLevel();
                        if (!currentLowLevel) {
                            continue;
                        }

                        const currentStart = currentLowLevel.start();
                        const currentEnd = currentLowLevel.end();

                        const usageLowLevel = usage.lowLevel();
                        if (!usageLowLevel) {
                            continue;
                        }

                        const usageStart = usageLowLevel.start();
                        const usageEnd = usageLowLevel.end();

                        if (usageStart <= currentEnd && usageEnd >= currentStart) {
                            hasConflicting = true;
                            break;
                        }
                    }

                    if (!hasConflicting) {
                        filtered.push(usage);
                    }
                });

                filtered.forEach((x) => {
                    this.renameInProperty(x.asAttr(), oldValue, newName);
                });
                hlnode.attr(
                    hlnode.definition().getAdapter(def.RAMLService).getKeyProp().nameId()
                ).setValue(newName);

                return [{
                    uri,
                    text: hlnode.lowLevel().unit().contents()
                }];
            }
        }

        return [];
    }

    private renameInProperty(property: hl.IAttribute, contentToReplace: string, replaceWith: string) {
        const oldPropertyValue = property.value();
        if (typeof oldPropertyValue === "string") {

            const oldPropertyStringValue = oldPropertyValue as string;

            const newPropertyStringValue = oldPropertyStringValue.replace(contentToReplace, replaceWith);
            property.setValue(newPropertyStringValue);
            if (oldPropertyStringValue.indexOf(contentToReplace) === -1) {
                if (property.name().indexOf(contentToReplace) !== -1) {
                    const newValue = (property.name() as string).replace(contentToReplace, replaceWith);
                    property.setKey(newValue);
                }
            }
            return;
        } else if (oldPropertyValue && (typeof oldPropertyValue === "object")) {
            const structuredValue = oldPropertyValue as hl.IStructuredValue;

            const oldPropertyStringValue = structuredValue.valueName();
            if (oldPropertyStringValue.indexOf(contentToReplace) !== -1) {
                const convertedHighLevel = structuredValue.toHighLevel();

                if (convertedHighLevel) {
                    let found = false;
                    if (convertedHighLevel.definition().isAnnotationType()) {
                        const prop = this.getKey(
                            (convertedHighLevel.definition() as def.AnnotationType), structuredValue.lowLevel());
                        prop.setValue("(" + replaceWith + ")");
                        return;
                    }
                    convertedHighLevel.attrs().forEach((attribute) => {
                        if (attribute.property().getAdapter(def.RAMLPropertyService).isKey()) {
                            const oldValue = attribute.value();
                            if (typeof oldValue === "string") {
                                found = true;
                                const newValue = (oldValue as string).replace(contentToReplace, replaceWith);
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
    }

    private getAstNode(uri: string, text: string, offset: number,
                       clearLastChar: boolean = true): parserApi.hl.IParseResult {
        const unitPath = utils.pathFromURI(uri);
        const newProjectId: string = utils.dirname(unitPath);

        const project = parserApi.project.createProject(newProjectId);

        const kind = search.determineCompletionKind(text, offset);

        if (kind === parserApi.search.LocationKind.KEY_COMPLETION && clearLastChar) {
            text = text.substring(0, offset) + "k:" + text.substring(offset);
        }

        const unit = project.setCachedUnitContent(unitPath, text);

        const ast = unit.highLevel() as parserApi.hl.IHighLevelNode;

        let actualOffset = offset;

        for (let currentOffset = offset - 1; currentOffset >= 0; currentOffset--) {
            const symbol = text[currentOffset];

            if (symbol === " " || symbol === "\t") {
                actualOffset = currentOffset - 1;

                continue;
            }

            break;
        }

        let astNode = ast.findElementAtOffset(actualOffset);

        if (astNode && search.isExampleNode(astNode)) {
            const exampleEnd = astNode.lowLevel().end();

            if (exampleEnd === actualOffset && text[exampleEnd] === "\n") {
                astNode = astNode.parent();
            }
        }

        return astNode;
    }

    private getKey(t: def.AnnotationType, n: lowLevel.ILowLevelASTNode) {
        const up = new def.UserDefinedProp("name", null);

        const ramlService: def.RAMLService = t.getAdapter(def.RAMLService);

        up.withRange(ramlService.universe().type(universes.Universe10.StringType.name));
        up.withFromParentKey(true);
        const node = ramlService.getDeclaringNode();
        return stubs.createASTPropImpl(n, node, up.range(), up, true);
    }

}
