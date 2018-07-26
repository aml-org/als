import clientTypeInterfaces = require("../../../client/typeInterfaces");
import commonTypeInterfaces = require("../../../common/typeInterfaces");
export declare class VersionedDocument {
    private uri;
    private version;
    private text;
    constructor(uri: string, version: number, text: string);
    /**
     * Gets document text
     * @returns {string}
     */
    getText(): string;
    /**
     * Gets document uri.
     */
    getUri(): string;
    /**
     * Returns document version, if any.
     */
    getVersion(): number;
}
export declare class VersionedDocumentManager {
    private logger;
    private maxStoredVersions;
    /**
     * Stores a mapping from document uri to a sorted list of versioned documents.
     *
     * @type {{}}
     */
    private documents;
    constructor(logger: commonTypeInterfaces.ILogger, maxStoredVersions?: number);
    /**
     * Gets latest version of the document by uri, or null if unknown
     * @param uri
     */
    getLatestDocumentVersion(uri: string): number;
    getLatestDocument(uri: string): VersionedDocument;
    /**
     * Registers opened client document. Returns null if such a document is already registered,
     * or the newly registered document in common format.
     * @param proposal
     */
    registerOpenedDocument(proposal: clientTypeInterfaces.IOpenedDocument): commonTypeInterfaces.IOpenedDocument;
    /**
     * Registers changed client document. Returns null if such a document is already registered,
     * or the newly registered document in common format.
     * @param proposal
     */
    registerChangedDocument(proposal: clientTypeInterfaces.IChangedDocument): commonTypeInterfaces.IChangedDocument;
    /**
     * Unregisters all document versions by uri.
     * @param uri
     */
    unregisterDocument(uri: string): void;
}
