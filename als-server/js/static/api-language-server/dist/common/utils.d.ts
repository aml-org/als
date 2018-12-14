import { ILoggerSettings, MessageSeverity } from "./typeInterfaces";
/**
 * Returns path from uri. If URI string is not a well-formed URI, but just an FS path, returns that path.
 * @param uri
 */
export declare function pathFromURI(uri: string): any;
/**
 * Returns whether URI has HTTP protocol.
 * If URI string is not a well-formed URI, but just an FS path, returns false
 * @param uri
 */
export declare function isHTTPUri(uri: string): boolean;
export declare function isFILEUri(uri: string): boolean;
export declare function extName(uri: string): string;
export declare function resolve(path1: string, path2: string): string;
export declare function basename(path1: string): string;
export declare function dirname(path1: string): string;
/**
 * If original format is well-formed FILE uri, and toTransform is simple path,
 * transforms toTransform to well-formed file uri
 * @param originalUri
 * @param toTransform
 */
export declare function transformUriToOriginalFormat(originalUri: string, toTransform: string): any;
export interface LogMessage {
    message: string;
    severity: MessageSeverity;
    component?: string;
    subcomponent?: string;
}
/**
 * Filters logger message based on logger settings.
 * @param message
 * @param settings
 * @returns transformed message or null if it was filtered out.
 */
export declare function filterLogMessage(message: LogMessage, settings: ILoggerSettings): LogMessage;
