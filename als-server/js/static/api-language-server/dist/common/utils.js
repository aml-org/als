"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var path = require("path");
var URI = require("urijs");
/**
 * Returns path from uri. If URI string is not a well-formed URI, but just an FS path, returns that path.
 * @param uri
 */
function pathFromURI(uri) {
    return (new URI(uri)).path();
}
exports.pathFromURI = pathFromURI;
/**
 * Returns whether URI has HTTP protocol.
 * If URI string is not a well-formed URI, but just an FS path, returns false
 * @param uri
 */
function isHTTPUri(uri) {
    var protocol = (new URI(uri)).protocol();
    return "http" === protocol || "HTTP" === protocol;
}
exports.isHTTPUri = isHTTPUri;
function isFILEUri(uri) {
    var protocol = (new URI(uri)).protocol();
    return "file" === protocol || "FILE" === protocol;
}
exports.isFILEUri = isFILEUri;
function extName(uri) {
    return path.extname(pathFromURI(uri));
}
exports.extName = extName;
function resolve(path1, path2) {
    return path.resolve(path1, path2).replace(/\\/g, '/');
}
exports.resolve = resolve;
function basename(path1) {
    return path.basename(path1);
}
exports.basename = basename;
function dirname(path1) {
    return path.dirname(path1);
}
exports.dirname = dirname;
/**
 * If original format is well-formed FILE uri, and toTransform is simple path,
 * transforms toTransform to well-formed file uri
 * @param originalUri
 * @param toTransform
 */
function transformUriToOriginalFormat(originalUri, toTransform) {
    if (isFILEUri(originalUri) && !isFILEUri(toTransform) && !isHTTPUri(toTransform)) {
        return (new URI(toTransform)).protocol("file").toString();
    }
    return toTransform;
}
exports.transformUriToOriginalFormat = transformUriToOriginalFormat;
/**
 * Filters logger message based on logger settings.
 * @param message
 * @param settings
 * @returns transformed message or null if it was filtered out.
 */
function filterLogMessage(message, settings) {
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
    var text = message.message;
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
exports.filterLogMessage = filterLogMessage;
//# sourceMappingURL=utils.js.map