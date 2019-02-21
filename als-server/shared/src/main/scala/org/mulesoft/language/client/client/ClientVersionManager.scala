package org.mulesoft.language.client.client

import org.mulesoft.language.common.dtoTypes.{ChangedDocument, OpenedDocument}
import org.mulesoft.language.common.logger.Logger

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

class VersionedDocumentManager(val logger: Logger, val maxStoredVersions: Int = 1) {

  /**
    * Stores a mapping from document uri to a sorted list of versioned documents.
    *
    */
  val documents: mutable.Map[String, ListBuffer[VersionedDocument]] = mutable.Map()

  /**
    * Gets latest version of the document by uri, or null if unknown
    *
    * @param uri
    */
  def getLatestDocumentVersion(uri: String): Option[Int] = getLatestDocument(uri).map(_.version)

  def getLatestDocument(uri: String): Option[VersionedDocument] = documents.get(uri).flatMap(_.headOption)

  /**
    * Registers opened client document. Returns null if such a document is already registered,
    * or the newly registered document in common format.
    *
    * @param proposal
    */
  def registerOpenedDocument(proposal: OpenedDocument): Option[OpenedDocument] = {

    this.logger.debug("Open document called for uri " + proposal.uri,
      "VersionedDocumentManager", "registerOpenedDocument");

    this.logger.debugDetail("New text is:\n" + proposal.text,
      "VersionedDocumentManager", "registerOpenedDocument");

    val versionedDocuments = this.documents.get(proposal.uri)

    this.logger.debugDetail("Versioned documents for this uri found: " +
      (if (versionedDocuments.isDefined) "true" else "false"),
      "VersionedDocumentManager", "registerOpenedDocument");

    if (versionedDocuments.isDefined) {

      Some(OpenedDocument(proposal.uri, 0, proposal.text))

    } else {
      val newDocument = new VersionedDocument(proposal.uri, 0, proposal.text)
      this.documents.put(proposal.uri, ListBuffer() += newDocument)

      Some(OpenedDocument(proposal.uri, 0, proposal.text))
    }
  }

  /**
    * Registers changed client document. Returns null if such a document is already registered,
    * or the newly registered document in common format.
    *
    * @param proposal
    */
  def registerChangedDocument(proposal: ChangedDocument): Option[ChangedDocument] = {

    this.logger.debug("Change document called for uri " + proposal.uri,
      "VersionedDocumentManager", "registerChangedDocument");

    this.logger.debugDetail("New text is:\n" + proposal.text,
      "VersionedDocumentManager", "registerChangedDocument");

    val versionedDocuments = this.documents.get(proposal.uri)

    this.logger.debugDetail("Versioned documents for this uri found: " +
      (if (versionedDocuments.isDefined) "true" else "false"),
      "VersionedDocumentManager", "registerChangedDocument");

    if (versionedDocuments.isDefined && versionedDocuments.get.nonEmpty) {

      val latestDocument = versionedDocuments.get.head

      this.logger.debugDetail("Latest document version is " + latestDocument.getVersion,
        "VersionedDocumentManager", "registerChangedDocument")

      val latestText = latestDocument.getText

      this.logger.debugDetail("Latest document text is " + latestText,
        "VersionedDocumentManager", "registerChangedDocument")

      val newText = proposal.text
      if (newText == null && proposal.textEdits.isDefined) {
        //newText = applyDocumentEdits(latestText, proposal.textEdits);
      }

      this.logger.debugDetail("Calculated new text is: " + newText,
        "VersionedDocumentManager", "registerChangedDocument");

      if (newText.isEmpty) {
        return null
      }

      if (newText.contains(latestText)) {

        this.logger.debugDetail("No changes of text found",
          "VersionedDocumentManager", "registerChangedDocument")

        return None
      }

      val newDocument = new VersionedDocument(proposal.uri,
        latestDocument.getVersion + 1, newText.get);

      this.documents.put(proposal.uri, ListBuffer() += newDocument)

      Some(ChangedDocument(newDocument.getUri, newDocument.getVersion, Some(newDocument.getText), None))
    } else {

      val newDocument = new VersionedDocument(proposal.uri, 0, proposal.text.get)
      this.documents.put(proposal.uri, ListBuffer() += newDocument)

      this.logger.debugDetail("Registered new document, returning acceptance",
        "VersionedDocumentManager", "registerChangedDocument");

      Some(ChangedDocument(proposal.uri, 0, proposal.text, None))
    }
  }

  /**
    * Unregisters all document versions by uri.
    *
    * @param uri
    */
  def unregisterDocument(uri: String): Unit = documents.remove(uri)
}

class VersionedDocument(val uri: String, val version: Int, val text: String) {

  /**
    * Gets document text
    *
    * @return {String}
    */
  def getText: String = text

  /**
    * Gets document uri.
    */
  def getUri: String = uri

  /**
    * Returns document version, if any.
    */
  def getVersion: Int = this.version
}