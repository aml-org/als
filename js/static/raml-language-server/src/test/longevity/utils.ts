import {
    getNodeClientConnection,
    IChangedDocument,
    IClientConnection,
    ILocation,
    IRange
} from "../../index";

import assert = require("assert");
import path = require("path");
import fs = require("fs");

/**
 * Variant of content the typer produces
 */
export interface IContentVariant {

    /**
     * Main RAML path
     */
    path: string;

    /**
     * Full RAML code of this variant
     */
    content: string;

    /**
     * Last variant in a sequence.
     */
    last?: boolean;
}

/**
 * Content producer.
 */
export interface IContentProducer {

    /**
     * Produces next content variant.
     *
     * Returns null when finished.
     */
    getNext(): IContentVariant;
}

/**
 * Tests provided content
 */
export interface IContentTester {

    /**
     * Is called before the start of the sequence.
     */
    beforeAll();

    /**
     * Is called after the end of the sequence.
     */
    afterAll();

    /**
     * Tests content variant.
     * @param contentVariant - variant to test
     * @param last - whether this variant is last in a sequence.
     * @return promise resulting in true if test succeeded, false otherwise.
     */
    test(contentVariant: IContentVariant, last: boolean): Promise<boolean>;
}

export function testEditorManager(testFileRelativePath: string,
                                  done: (error?: Error) => void ): void {

    const fullFilePath = getTestEditorManagerPath(testFileRelativePath);

    testWordBased(fullFilePath, new EditorManagerTester(fullFilePath), done);
}

export function testValidationManager(testFileRelativePath: string,
                                      done: (error?: Error) => void ): void {

    const fullFilePath = getTestValidationManagerPath(testFileRelativePath);

    testWordBased(fullFilePath, new ValidationManagerTester(fullFilePath), done);
}

export function testStructureManager(testFileRelativePath: string,
                                     done: (error?: Error) => void ): void {

    const fullFilePath = getTestStructureManagerPath(testFileRelativePath);

    testWordBased(fullFilePath, new StructureManagerTester(fullFilePath), done);
}

export function testCompletionManager(testFileRelativePath: string,
                                      done: (error?: Error) => void ): void {

    const fullFilePath = getTestCompletionManagerPath(testFileRelativePath);

    testWordBased(fullFilePath, new CompletionManagerTester(fullFilePath), done);
}

export function testDetailsManager(testFileRelativePath: string,
                                      done: (error?: Error) => void ): void {

    const fullFilePath = getTestDetailsManagerPath(testFileRelativePath);

    testWordBased(fullFilePath, new DetailsManagerTester(fullFilePath), done);
}

export function testWordBased(filePath: string, tester: IContentTester,
                              done: (error?: Error) => void ): void {

    const contentProducer = new WordBasedContentProducer(filePath);

    testGeneral(contentProducer, tester, done);
}

export function testGeneral(contentProducer: IContentProducer, tester: IContentTester,
                            done: (error?: Error) => void ): void {

    tester.beforeAll();

    testGeneralRecursive(contentProducer, tester, done);
}

export function testGeneralRecursive(contentProducer: IContentProducer, tester: IContentTester,
                                     done: (error?: Error) => void ): Promise<void> {

    const currentVariant = contentProducer.getNext();
    if (currentVariant === null) {
        tester.afterAll();

        done();

        return;
    }

    try {
        return tester.test(currentVariant, currentVariant.last).then(
            (result) => {
                assert(result);

                return testGeneralRecursive(contentProducer, tester, done);
            },
            (error) => {
                done(error);
            }
        );
    } catch (error) {
        done(error);
    }
}

abstract class AbstractDocumentTester implements IContentTester {

    protected connection: IClientConnection;

    constructor(protected apiPath: string) {
        this.connection = getNodeClientConnection();
    }

    /**
     * Is called before the start of the sequence.
     */
    public beforeAll() {

        this.connection.documentOpened({
            uri: this.apiPath,
            text: "#%RAML 1.0\n"
        });

        this.connection.onExists((fullPath: string) => {
            return Promise.resolve(fs.existsSync(fullPath));
        });

        this.connection.onReadDir((fullPath: string) => {

            return Promise.resolve(fs.readdirSync(fullPath));
        });


        this.connection.onIsDirectory((fullPath: string) => {
            return Promise.resolve(fs.statSync(fullPath).isDirectory());
        });


        this.connection.onContent((fullPath: string) => {
            return Promise.resolve(fs.readFileSync(fullPath).toString());
        });
    }

    /**
     * Is called after the end of the sequence.
     */
    public afterAll() {
        this.connection.documentClosed(this.apiPath);
    }

    /**
     * Tests content variant.
     * @param contentVariant - variant to test
     * @param last - whether this variant is last in a sequence.
     * @return promise resulting in true if test succeeded, false otherwise.
     */
    public abstract test(contentVariant: IContentVariant, last: boolean): Promise<boolean>;
}

class EditorManagerTester extends AbstractDocumentTester {

    private callBack: CallBackHandle<boolean> = null;

    public beforeAll() {
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

        super.beforeAll();
    }

    public test(contentVariant: IContentVariant, last: boolean): Promise<boolean> {

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

            this.connection.onValidationReport((validationReport) => {
                if (this.callBack && this.callBack.resolve) {
                    this.callBack.resolve(true);
                }

                this.callBack = null;
            })
        }

        this.connection.documentChanged({
            uri: contentVariant.path,
            text: contentVariant.content
        });

        if (last) {

            return new Promise((resolve: (value?: boolean) => void, reject: (error?: any) => void) => {

                this.callBack = {
                    resolve,
                    reject
                };
            });
        } else {

            return Promise.resolve(true);
        }
    }
}

interface CallBackHandle<ResultType> {
    resolve?: (value?: ResultType) => void;
    reject?: (error?: any) => void;
}

class ValidationManagerTester extends AbstractDocumentTester {

    private callBack: CallBackHandle<boolean> = null;

    public beforeAll() {
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

        this.connection.onValidationReport((validationReport) => {
            if (this.callBack && this.callBack.resolve) {
                this.callBack.resolve(true);
            }

            this.callBack = null;
        })

        super.beforeAll();
    }

    public test(contentVariant: IContentVariant, last: boolean): Promise<boolean> {

        this.connection.documentChanged({
            uri: contentVariant.path,
            text: contentVariant.content
        });

        return new Promise((resolve: (value?: boolean) => void, reject: (error?: any) => void) => {

            this.callBack = {
                resolve,
                reject
            };
        });
    }
}

class StructureManagerTester extends AbstractDocumentTester {

    private callBack: CallBackHandle<boolean> = null;

    public beforeAll() {
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

        this.connection.onStructureReport((structureReport) => {
            if (this.callBack && this.callBack.resolve) {
                this.callBack.resolve(true);
            }

            this.callBack = null;
        })

        super.beforeAll();
    }

    public test(contentVariant: IContentVariant, last: boolean): Promise<boolean> {

        this.connection.documentChanged({
            uri: contentVariant.path,
            text: contentVariant.content
        });

        return new Promise((resolve: (value?: boolean) => void, reject: (error?: any) => void) => {

            this.callBack = {
                resolve,
                reject
            };
        });
    }
}

class CompletionManagerTester extends AbstractDocumentTester {

    private callBack: CallBackHandle<boolean> = null;

    public beforeAll() {

        super.beforeAll();
    }

    public test(contentVariant: IContentVariant, last: boolean): Promise<boolean> {


        this.connection.documentChanged({
            uri: contentVariant.path,
            text: contentVariant.content
        });

        const position = contentVariant.content.length - 1;
        return this.connection.getSuggestions(contentVariant.path, position).then((result) => {
            return true;
        });
    }
}

class DetailsManagerTester extends AbstractDocumentTester {

    private callBack: CallBackHandle<boolean> = null;

    public beforeAll() {

        super.beforeAll();
    }

    public test(contentVariant: IContentVariant, last: boolean): Promise<boolean> {

        this.connection.documentChanged({
            uri: contentVariant.path,
            text: contentVariant.content
        });

        const position = contentVariant.content.length - 1;
        return this.connection.getDetails(contentVariant.path, position).then((result) => {
            return true;
        });
    }
}

class WordBasedContentProducer implements IContentProducer {

    private contentBuffer: string;
    private words: string[];
    private currentIndex = 0;

    constructor(private filePath: string) {
        const fileContent: string = fs.readFileSync(filePath, "utf8");
        const headerIndex = fileContent.indexOf("#%RAML");

        if (headerIndex < 0) {
            throw new Error(`Not a RAML file: ${filePath}`);
        }

        let lineIndex = fileContent.indexOf("\n", headerIndex);
        if (lineIndex < 0) {
            lineIndex = fileContent.length;
        } else {
            lineIndex += "\n".length;
        }

        this.contentBuffer = fileContent.substr(0, lineIndex);

        this.words = fileContent.substring(lineIndex).split(/(\s+)/);
        if (this.words.length > 0 && this.words[this.words.length - 1].length == 0) {
            this.words.splice(this.words.length - 1, 1);
        }
    }

    public getNext(): IContentVariant {

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
    }
}

function getTestEditorManagerPath(originalPath: string): string {
    return path.resolve(__dirname, "../../../src/test/data/longevity"
        + originalPath).replace(/\\/g, "/");
}

function getTestValidationManagerPath(originalPath: string): string {
    return path.resolve(__dirname, "../../../src/test/data/longevity"
        + originalPath).replace(/\\/g, "/");
}

function getTestStructureManagerPath(originalPath: string): string {
    return path.resolve(__dirname, "../../../src/test/data/longevity"
        + originalPath).replace(/\\/g, "/");
}

function getTestCompletionManagerPath(originalPath: string): string {
    return path.resolve(__dirname, "../../../src/test/data/longevity"
        + originalPath).replace(/\\/g, "/");
}

function getTestDetailsManagerPath(originalPath: string): string {
    return path.resolve(__dirname, "../../../src/test/data/longevity"
        + originalPath).replace(/\\/g, "/");
}