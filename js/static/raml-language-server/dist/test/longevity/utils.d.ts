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
    beforeAll(): any;
    /**
     * Is called after the end of the sequence.
     */
    afterAll(): any;
    /**
     * Tests content variant.
     * @param contentVariant - variant to test
     * @param last - whether this variant is last in a sequence.
     * @return promise resulting in true if test succeeded, false otherwise.
     */
    test(contentVariant: IContentVariant, last: boolean): Promise<boolean>;
}
export declare function testEditorManager(testFileRelativePath: string, done: (error?: Error) => void): void;
export declare function testValidationManager(testFileRelativePath: string, done: (error?: Error) => void): void;
export declare function testStructureManager(testFileRelativePath: string, done: (error?: Error) => void): void;
export declare function testCompletionManager(testFileRelativePath: string, done: (error?: Error) => void): void;
export declare function testDetailsManager(testFileRelativePath: string, done: (error?: Error) => void): void;
export declare function testWordBased(filePath: string, tester: IContentTester, done: (error?: Error) => void): void;
export declare function testGeneral(contentProducer: IContentProducer, tester: IContentTester, done: (error?: Error) => void): void;
export declare function testGeneralRecursive(contentProducer: IContentProducer, tester: IContentTester, done: (error?: Error) => void): Promise<void>;
