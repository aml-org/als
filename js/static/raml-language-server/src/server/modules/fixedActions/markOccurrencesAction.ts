// This module provides a fixed action for finding occurrences of RAML node

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
    ILocation,
    IRange
} from "../../../common/typeInterfaces";

import rp= require("raml-1-parser");
import search = rp.search;
import lowLevel= rp.ll;
import hl= rp.hl;

import {
    IDisposableModule
} from "../../modules/commonInterfaces";

import utils = require("../../../common/utils");
import fixedActionCommon = require("./fixedActionsCommon");
import openDeclarationsModule = require("./openDeclarationAction");

import selectionUtils = require("./selectionUtils");

export function createManager(connection: IServerConnection,
                              astManagerModule: IASTManagerModule,
                              editorManagerModule: IEditorManagerModule)
                        : IDisposableModule {

    return new MarkOccurrencesActionModule(connection, astManagerModule, editorManagerModule);
}

class MarkOccurrencesActionModule implements IDisposableModule {

    private onMarkOccurrencesListener;

    constructor(private connection:IServerConnection, private astManagerModule:IASTManagerModule,
                private editorManagerModule:IEditorManagerModule) {
    }

    public launch() {

        this.onMarkOccurrencesListener = (uri:string, position:number) => {
            return this.markOccurrences(uri, position);
        }

        this.connection.onMarkOccurrences(this.onMarkOccurrencesListener);
    }

    public dispose():void {
        this.connection.onMarkOccurrences(this.onMarkOccurrencesListener, true);
    }

    /**
     * Returns unique module name.
     */
    public getModuleName():string {
        return "MARK_OCCURRENCES_ACTION";
    }

    public markOccurrences(uri:string, position:number):Promise<IRange[]> {
        this.connection.debug("Called for uri: " + uri,
            "FixedActionsManager", "markOccurrences");

        this.connection.debugDetail("Uri extname: " + utils.extName(uri),
            "FixedActionsManager", "markOccurrences");

        if (utils.extName(uri) !== ".raml") {
            return Promise.resolve([]);
        }

        return this.astManagerModule.forceGetCurrentAST(uri).then((ast) => {

            this.connection.debugDetail("Found AST: " + (ast ? "true" : false),
                "FixedActionsManager", "markOccurrences");

            if (!ast) {
                return [];
            }

            const unit = ast.lowLevel().unit();

            // TODO both search and declaration unit filtering is better to be moved directly to the search module
            // in order to save CPU by not checking external units and just be flagged here

            const node = ast.findElementAtOffset(position);
            if (!node) {
                return [];
            }

            var selectionValue = selectionUtils.findSelection(node, position, unit.contents());

            if (!selectionValue) {
                this.connection.debugDetail("Filtering out node, returning",
                    "FixedActionsManager", "markOccurrences");

                this.connection.debugDetail("Node:\n" + node.printDetails(),
                    "FixedActionsManager", "markOccurrences");

                return [];
            }

            const findUsagesResult = search.findUsages(unit, position);

            this.connection.debugDetail("Found usages: " + (findUsagesResult ? "true" : false),
                "FixedActionsManager", "markOccurrences");

            let unfiltered:ILocation[] = [];

            if (findUsagesResult && findUsagesResult.results) {

                this.connection.debugDetail("Number of found usages: " + findUsagesResult.results.length,
                    "FixedActionsManager", "markOccurrences");

                unfiltered = unfiltered.concat(findUsagesResult.results.map((parseResult) => {
                    return fixedActionCommon.lowLevelNodeToLocation(uri, parseResult.lowLevel(),
                        this.editorManagerModule, this.connection, true);
                }));
            }

            const findUsagesLocations = unfiltered;

            return openDeclarationsModule.createManager(
                this.connection, this.astManagerModule, this.editorManagerModule
            ).openDeclaration(uri, position).then((declarations) => {

                this.connection.debugDetail("Number of found declarations: " + declarations.length,
                    "FixedActionsManager", "markOccurrences");

                let locations = findUsagesLocations;
                if (declarations) {
                    locations = locations.concat(declarations);
                }

                let result: IRange[] = [];

                this.connection.debugDetail("Unfiltered occurrences: " + JSON.stringify(locations),
                    "FixedActionsManager", "markOccurrences");

                result = locations.filter(location => {
                    return location.uri === uri;
                }).filter(location => {
                    // excluding any mentions of whatever is located at the position itself
                    // as its not what user is interested with
                    return location.range.start > position || location.range.end < position;
                }).map(location => {
                    return location.range;
                }).map(range => selectionUtils.reduce(unit.contents(), selectionValue, range)).filter(range => range).filter(range => {
                    return range.start > position || range.end < position;
                });

                this.connection.debugDetail("Found occurrences result: " + JSON.stringify(result),
                    "FixedActionsManager", "markOccurrences");

                var filtered = result[0] ? [result[0]] : [];
                
                result.forEach(range => {
                    for(var i = 0; i < filtered.length; i++) {
                        if(range.start === filtered[i].start && range.end === filtered[i].end) {
                            return;
                        }
                    }

                    filtered.push(range);
                });
                

                return filtered;
            });

        });

    }
}