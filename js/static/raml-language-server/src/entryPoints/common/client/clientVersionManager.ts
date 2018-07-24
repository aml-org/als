import clientTypeInterfaces = require("../../../client/typeInterfaces");
import {
    applyDocumentEdits
} from "../../../common/textEditProcessor";
import commonTypeInterfaces = require("../../../common/typeInterfaces");

export class VersionedDocument {

    constructor(private uri: string, private version: number, private text: string) {
        this.text = text;
    }

    /**
     * Gets document text
     * @returns {string}
     */
    public getText(): string {
        return this.text;
    }

    /**
     * Gets document uri.
     */
    public getUri() {
        return this.uri;
    }

    /**
     * Returns document version, if any.
     */
    public getVersion(): number {
        return this.version;
    }
}

export class VersionedDocumentManager {

    /**
     * Stores a mapping from document uri to a sorted list of versioned documents.
     *
     * @type {{}}
     */
    private documents: {[uri: string]: VersionedDocument[]} = {};

    constructor(private logger: commonTypeInterfaces.ILogger, private maxStoredVersions= 1) {
    }

    /**
     * Gets latest version of the document by uri, or null if unknown
     * @param uri
     */
    public getLatestDocumentVersion(uri: string): number {

        const latestDocument = this.getLatestDocument(uri);
        if (!latestDocument) {
            return null;
        }

        return latestDocument.getVersion();
    }

    public getLatestDocument(uri: string): VersionedDocument {
        const versionedDocuments = this.documents[uri];

        if (!versionedDocuments) {
            return null;
        }

        return versionedDocuments[0];
    }

    /**
     * Registers opened client document. Returns null if such a document is already registered,
     * or the newly registered document in common format.
     * @param proposal
     */
    public registerOpenedDocument(proposal: clientTypeInterfaces.IOpenedDocument):
        commonTypeInterfaces.IOpenedDocument {

        this.logger.debug("Open document called for uri " + proposal.uri,
            "VersionedDocumentManager", "registerOpenedDocument");

        this.logger.debugDetail("New text is:\n" + proposal.text,
            "VersionedDocumentManager", "registerOpenedDocument");

        const versionedDocuments = this.documents[proposal.uri];

        this.logger.debugDetail("Versioned documents for this uri found: " +
            (versionedDocuments ? "true" : "false"),
            "VersionedDocumentManager", "registerOpenedDocument");

        if (versionedDocuments) {

            return {
                uri: proposal.uri,
                text: proposal.text,
                version: 0
            };

        } else {
            const newDocument = new VersionedDocument(proposal.uri, 0, proposal.text);
            this.documents[proposal.uri] = [newDocument];

            return {
                uri: proposal.uri,
                text: proposal.text,
                version: 0
            };
        }
    }

    /**
     * Registers changed client document. Returns null if such a document is already registered,
     * or the newly registered document in common format.
     * @param proposal
     */
    public registerChangedDocument(proposal: clientTypeInterfaces.IChangedDocument):
        commonTypeInterfaces.IChangedDocument {

        this.logger.debug("Change document called for uri " + proposal.uri,
            "VersionedDocumentManager", "registerChangedDocument");

        this.logger.debugDetail("New text is:\n" + proposal.text,
            "VersionedDocumentManager", "registerChangedDocument");

        const versionedDocuments = this.documents[proposal.uri];

        this.logger.debugDetail("Versioned documents for this uri found: " +
            (versionedDocuments ? "true" : "false"),
            "VersionedDocumentManager", "registerChangedDocument");

        if (versionedDocuments) {

            const latestDocument = versionedDocuments[0];

            this.logger.debugDetail("Latest document version is " + latestDocument.getVersion(),
                "VersionedDocumentManager", "registerChangedDocument");

            const latestText = latestDocument.getText();

            this.logger.debugDetail("Latest document text is " + latestText,
                "VersionedDocumentManager", "registerChangedDocument");

            let newText = proposal.text;
            if (newText == null && proposal.textEdits && latestText !== null) {
                newText = applyDocumentEdits(latestText, proposal.textEdits);
            }

            this.logger.debugDetail("Calculated new text is: " + newText,
                "VersionedDocumentManager", "registerChangedDocument");

            if (newText == null) {
                return null;
            }

            if (newText === latestText) {

                this.logger.debugDetail("No changes of text found",
                    "VersionedDocumentManager", "registerChangedDocument");

                return null;
            }

            const newDocument = new VersionedDocument(proposal.uri,
                latestDocument.getVersion() + 1, newText);

            this.documents[proposal.uri] = [newDocument];

            return {
                uri: newDocument.getUri(),
                text: newDocument.getText(),
                version: newDocument.getVersion()
            };
        } else {

            const newDocument = new VersionedDocument(proposal.uri, 0, proposal.text);
            this.documents[proposal.uri] = [newDocument];

            this.logger.debugDetail("Registered new document, returning acceptance",
                "VersionedDocumentManager", "registerChangedDocument");

            return {
                uri: proposal.uri,
                text: proposal.text,
                version: 0
            };
        }
    }

    /**
     * Unregisters all document versions by uri.
     * @param uri
     */
    public unregisterDocument(uri: string): void {
        delete this.documents[uri];
    }
}
