export declare function stopConnection(): void;
export declare function data(relativePath: string): string;
export declare function getValidationReport(apiPath: string, callback: (result: Object, error: any, closeDocument: () => void) => void): void;
export declare function startTyping(apiPath: any, done: (error?: any) => void): void;
export declare function startTyping1(apiPath: any, done: (error?: any) => void): void;
export declare function testErrors(done: any, fullPath: any, errors?: any, ignoreWarnings?: any): void;
export declare function testErrorsByNumber(done: any, fullPath: any, count?: number, deviations?: number): void;
export declare function sleep(milliseconds: any): void;
