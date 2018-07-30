export declare function data(filepath: string): string;
export declare function compare(arg0: any, arg1: any, path?: string): Diff[];
export declare class Diff {
    path: string;
    value0: any;
    value1: any;
    comment: string;
    constructor(path: string, value0: any, value1: any, comment: string);
    message(label0?: string, label1?: string): string;
}
export declare function stopConnection(): void;
export declare function testOutline(apiPath: string, done: any, extensions?: string[], outlineJsonPath?: string, regenerateJSON?: boolean, callTests?: boolean): void;
export declare function getRamlFirstLine(content: string): RegExpMatchArray;
export declare function projectFolder(): string;
export declare class Test {
    _masterPath: string;
    _extensionsAndOverlays: string[];
    _jsonPath: string;
    constructor(_masterPath: string, _extensionsAndOverlays?: string[], _jsonPath?: string);
    masterPath(): string;
    extensionsAndOverlays(): string[];
    jsonPath(): string;
}
export declare enum RamlFileKind {
    API = 0,
    LIBRARY = 1,
    EXTENSION = 2,
    OVERLAY = 3,
    FRAGMENT = 4,
}
export declare class RamlFile {
    private _absPath;
    private _kind;
    private _ver;
    private _extends;
    constructor(_absPath: string, _kind: RamlFileKind, _ver: string, _extends?: string);
    absolutePath(): string;
    kind(): RamlFileKind;
    version(): string;
    extends(): string;
}
export declare class DirectoryContent {
    private dirAbsPath;
    private files;
    constructor(dirAbsPath: string, files: RamlFile[]);
    absolutePath(): string;
    allRamlFiles(): RamlFile[];
    extensionsAndOverlays(): RamlFile[];
    masterAPIs(): RamlFile[];
    fragments(): RamlFile[];
    libraries(): RamlFile[];
    hasCleanAPIsOnly(): boolean;
    hasSingleExtensionOrOverlay(): boolean;
    hasExtensionsOrOverlaysAppliedToSingleAPI(): boolean;
    hasFragmentsOnly(): boolean;
    hasLibraries(): boolean;
    topExtensionOrOverlay(): RamlFile;
}
export declare function extractContent(folderAbsPath: string): DirectoryContent;
export declare function iterateFolder(folderAbsPath: string, result?: DirectoryContent[]): DirectoryContent[];
export declare function defaultJSONPath(apiPath: string): string;
export declare function getTests(dirContent: DirectoryContent): Test[];
export declare function generateSuite(folderAbsPath: string, dstPath: string, dataRoot: string, mochaSuiteTitle: string, testMethodName: string): void;
export declare function sleep(milliseconds: any): void;
export declare function testDetails(apiPath: string, done: any, extensions?: string[], detailsJsonPath?: string, regenerateJSON?: boolean, callTests?: boolean): void;
export declare function testDetailsStructure(apiPath: string, json: any, extensions?: string[], detailsJsonPath?: string, regenerateJSON?: boolean, callTests?: boolean): boolean;
