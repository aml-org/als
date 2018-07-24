import path = require("path");
import URI = require("urijs");
import {
    ILoggerSettings,
    MessageSeverity
} from "./typeInterfaces";

/**
 * Returns path from uri. If URI string is not a well-formed URI, but just an FS path, returns that path.
 * @param uri
 */
export function pathFromURI(uri: string) {
    return (new URI(uri)).path();
}

/**
 * Returns whether URI has HTTP protocol.
 * If URI string is not a well-formed URI, but just an FS path, returns false
 * @param uri
 */
export function isHTTPUri(uri: string) {
    const protocol =  (new URI(uri)).protocol();
    return "http" === protocol || "HTTP" === protocol;
}

export function isFILEUri(uri: string) {
    const protocol =  (new URI(uri)).protocol();
    return "file" === protocol || "FILE" === protocol;
}

export function extName(uri: string) {
    return path.extname(pathFromURI(uri));
}

export function resolve(path1: string, path2: string): string {
    return path.resolve(path1, path2).replace(/\\/g,'/');
}

export function basename(path1: string) {
    return path.basename(path1);
}

export function dirname(path1: string) {
    return path.dirname(path1);
}

/**
 * If original format is well-formed FILE uri, and toTransform is simple path,
 * transforms toTransform to well-formed file uri
 * @param originalUri
 * @param toTransform
 */
export function transformUriToOriginalFormat(originalUri: string, toTransform: string) {
    if (isFILEUri(originalUri) && !isFILEUri(toTransform) && !isHTTPUri(toTransform)) {
        return (new URI(toTransform)).protocol("file").toString();
    }

    return toTransform;
}

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
export function filterLogMessage(message: LogMessage, settings: ILoggerSettings): LogMessage {
    if (!settings) {
        return {
            message: message.message,
            severity: message.severity,
            component: message.component,
            subcomponent: message.subcomponent
        };
    }

    if (settings.disabled) {
        return null;
    }

    if (message.component && settings.allowedComponents) {
        if (settings.allowedComponents.indexOf(message.component) === -1) {
            return null;
        }
    }

    if (message.component && settings.deniedComponents) {
        if (settings.allowedComponents.indexOf(message.component) !== -1) {
            return null;
        }
    }

    if (settings.maxSeverity != null && message.severity != null) {
        if (message.severity < settings.maxSeverity) {
            return null;
        }
    }

    let text = message.message;
    if (settings.maxMessageLength && text && text.length > settings.maxMessageLength) {
        text = text.substring(0, settings.maxMessageLength - 1);
    }

    return {
        message: text,
        severity: message.severity,
        component: message.component,
        subcomponent: message.subcomponent
    };
}
