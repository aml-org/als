package org.mulesoft.lsp.feature.common

import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

/** Text documents are identified using a URI. On the protocol level, URIs are passed as strings.
  */
sealed trait DocumentIdentifier {

  /** The text document's URI.
    */
  val uri: String
}

/** Most simple Text Document Identifier
  *
  * @param uri
  *   The text document's URI.
  */
@JSExportAll
@JSExportTopLevel("TextDocumentIdentifier")
case class TextDocumentIdentifier(uri: String) extends DocumentIdentifier

/** An identifier to denote a specific version of a text document.
  *
  * @param uri
  *   The text document's URI.
  * @param version
  *   The version number of this document. If a versioned text document identifier is sent from the server to the client
  *   and the file is not open in the editor (the server has not received an open notification before) the server can
  *   send {{None}} to indicate that the version is known and the content on disk is the truth.
  *
  * The version number of a document will increase after each change, including undo/redo. The number doesn't need to be
  * consecutive.
  */
case class VersionedTextDocumentIdentifier(uri: String, version: Option[Int]) extends DocumentIdentifier
