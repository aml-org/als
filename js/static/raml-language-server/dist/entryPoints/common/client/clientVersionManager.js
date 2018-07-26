"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var textEditProcessor_1 = require("../../../common/textEditProcessor");
var VersionedDocument = /** @class */ (function () {
    function VersionedDocument(uri, version, text) {
        this.uri = uri;
        this.version = version;
        this.text = text;
        this.text = text;
    }
    /**
     * Gets document text
     * @returns {string}
     */
    VersionedDocument.prototype.getText = function () {
        return this.text;
    };
    /**
     * Gets document uri.
     */
    VersionedDocument.prototype.getUri = function () {
        return this.uri;
    };
    /**
     * Returns document version, if any.
     */
    VersionedDocument.prototype.getVersion = function () {
        return this.version;
    };
    return VersionedDocument;
}());
exports.VersionedDocument = VersionedDocument;
var VersionedDocumentManager = /** @class */ (function () {
    function VersionedDocumentManager(logger, maxStoredVersions) {
        if (maxStoredVersions === void 0) { maxStoredVersions = 1; }
        this.logger = logger;
        this.maxStoredVersions = maxStoredVersions;
        /**
         * Stores a mapping from document uri to a sorted list of versioned documents.
         *
         * @type {{}}
         */
        this.documents = {};
    }
    /**
     * Gets latest version of the document by uri, or null if unknown
     * @param uri
     */
    VersionedDocumentManager.prototype.getLatestDocumentVersion = function (uri) {
        var latestDocument = this.getLatestDocument(uri);
        if (!latestDocument) {
            return null;
        }
        return latestDocument.getVersion();
    };
    VersionedDocumentManager.prototype.getLatestDocument = function (uri) {
        var versionedDocuments = this.documents[uri];
        if (!versionedDocuments) {
            return null;
        }
        return versionedDocuments[0];
    };
    /**
     * Registers opened client document. Returns null if such a document is already registered,
     * or the newly registered document in common format.
     * @param proposal
     */
    VersionedDocumentManager.prototype.registerOpenedDocument = function (proposal) {
        this.logger.debug("Open document called for uri " + proposal.uri, "VersionedDocumentManager", "registerOpenedDocument");
        this.logger.debugDetail("New text is:\n" + proposal.text, "VersionedDocumentManager", "registerOpenedDocument");
        var versionedDocuments = this.documents[proposal.uri];
        this.logger.debugDetail("Versioned documents for this uri found: " +
            (versionedDocuments ? "true" : "false"), "VersionedDocumentManager", "registerOpenedDocument");
        if (versionedDocuments) {
            return {
                uri: proposal.uri,
                text: proposal.text,
                version: 0
            };
        }
        else {
            var newDocument = new VersionedDocument(proposal.uri, 0, proposal.text);
            this.documents[proposal.uri] = [newDocument];
            return {
                uri: proposal.uri,
                text: proposal.text,
                version: 0
            };
        }
    };
    /**
     * Registers changed client document. Returns null if such a document is already registered,
     * or the newly registered document in common format.
     * @param proposal
     */
    VersionedDocumentManager.prototype.registerChangedDocument = function (proposal) {
        this.logger.debug("Change document called for uri " + proposal.uri, "VersionedDocumentManager", "registerChangedDocument");
        this.logger.debugDetail("New text is:\n" + proposal.text, "VersionedDocumentManager", "registerChangedDocument");
        var versionedDocuments = this.documents[proposal.uri];
        this.logger.debugDetail("Versioned documents for this uri found: " +
            (versionedDocuments ? "true" : "false"), "VersionedDocumentManager", "registerChangedDocument");
        if (versionedDocuments) {
            var latestDocument = versionedDocuments[0];
            this.logger.debugDetail("Latest document version is " + latestDocument.getVersion(), "VersionedDocumentManager", "registerChangedDocument");
            var latestText = latestDocument.getText();
            this.logger.debugDetail("Latest document text is " + latestText, "VersionedDocumentManager", "registerChangedDocument");
            var newText = proposal.text;
            if (newText == null && proposal.textEdits && latestText !== null) {
                newText = textEditProcessor_1.applyDocumentEdits(latestText, proposal.textEdits);
            }
            this.logger.debugDetail("Calculated new text is: " + newText, "VersionedDocumentManager", "registerChangedDocument");
            if (newText == null) {
                return null;
            }
            if (newText === latestText) {
                this.logger.debugDetail("No changes of text found", "VersionedDocumentManager", "registerChangedDocument");
                return null;
            }
            var newDocument = new VersionedDocument(proposal.uri, latestDocument.getVersion() + 1, newText);
            this.documents[proposal.uri] = [newDocument];
            return {
                uri: newDocument.getUri(),
                text: newDocument.getText(),
                version: newDocument.getVersion()
            };
        }
        else {
            var newDocument = new VersionedDocument(proposal.uri, 0, proposal.text);
            this.documents[proposal.uri] = [newDocument];
            this.logger.debugDetail("Registered new document, returning acceptance", "VersionedDocumentManager", "registerChangedDocument");
            return {
                uri: proposal.uri,
                text: proposal.text,
                version: 0
            };
        }
    };
    /**
     * Unregisters all document versions by uri.
     * @param uri
     */
    VersionedDocumentManager.prototype.unregisterDocument = function (uri) {
        delete this.documents[uri];
    };
    return VersionedDocumentManager;
}());
exports.VersionedDocumentManager = VersionedDocumentManager;
//# sourceMappingURL=clientVersionManager.js.map